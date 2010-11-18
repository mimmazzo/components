/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.richfaces.renderkit;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseWriter;

import org.richfaces.component.AbstractTreeNode;
import org.richfaces.component.MetaComponentResolver;
import org.richfaces.component.SwitchType;
import org.richfaces.event.TreeToggleEvent;

import com.google.common.base.Strings;

/**
 * @author Nick Belaevski
 * 
 */
public class TreeNodeRendererBase extends RendererBase implements MetaComponentRenderer {

    private static final String NEW_NODE_TOGGLE_STATE = "__NEW_NODE_TOGGLE_STATE";
    
    private static final String TRIGGER_NODE_AJAX_UPDATE = "__TRIGGER_NODE_AJAX_UPDATE";
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);

        final Map<String, String> map = context.getExternalContext().getRequestParameterMap();
        String newToggleState = map.get(component.getClientId(context) + NEW_NODE_TOGGLE_STATE);
        if (newToggleState != null) {

            AbstractTreeNode treeNode = (AbstractTreeNode) component;
            
            boolean expanded = Boolean.valueOf(newToggleState);
            if (treeNode.isExpanded() ^ expanded) {
                new TreeToggleEvent(treeNode, expanded).queue();
            }

            PartialViewContext pvc = context.getPartialViewContext();
            if (pvc.isAjaxRequest() && map.get(component.getClientId(context) + TRIGGER_NODE_AJAX_UPDATE) != null) {
                pvc.getRenderIds().add(treeNode.getClientId(context) + MetaComponentResolver.META_COMPONENT_SEPARATOR_CHAR + AbstractTreeNode.SUBTREE_META_COMPONENT_ID);
            }
        }
    }
    
    public void decodeMetaComponent(FacesContext context, UIComponent component, String metaComponentId) {
        throw new UnsupportedOperationException();
    }
    
    public void encodeMetaComponent(FacesContext context, UIComponent component, String metaComponentId)
        throws IOException {

        if (AbstractTreeNode.SUBTREE_META_COMPONENT_ID.equals(metaComponentId)) {
            AbstractTreeNode treeNode = (AbstractTreeNode) component;
            new TreeEncoderPartial(context, treeNode).encode();
        } else {
            throw new IllegalArgumentException(metaComponentId);
        }
    }
    
    protected TreeNodeState getNodeState(FacesContext context) {
        return (TreeNodeState) context.getAttributes().get(TreeEncoderBase.TREE_NODE_STATE_ATTRIBUTE);
    }
    
    protected void encodeDefaultHandle(FacesContext context, UIComponent component, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement(HtmlConstants.SPAN_ELEM, component);
        writer.writeAttribute(HtmlConstants.CLASS_ATTRIBUTE, styleClass, null);
        writer.endElement(HtmlConstants.SPAN_ELEM);
    }
    
    protected void encodeCustomHandle(FacesContext context, UIComponent component, String styleClass, String iconSource) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement(HtmlConstants.IMG_ELEMENT, component);
        writer.writeAttribute(HtmlConstants.CLASS_ATTRIBUTE, styleClass, null);
        writer.writeAttribute(HtmlConstants.ALT_ATTRIBUTE, "", null);
        writer.writeURIAttribute(HtmlConstants.SRC_ATTRIBUTE, RenderKitUtils.getResourceURL(iconSource, context), null);
        writer.endElement(HtmlConstants.IMG_ELEMENT);
    }
    
    
    
    protected void encodeHandle(FacesContext context, UIComponent component) throws IOException {
        TreeNodeState nodeState = getNodeState(context);
        
        AbstractTreeNode treeNode = (AbstractTreeNode) component;
        
        if (nodeState.isLeaf()) {
            String iconLeaf = (String) treeNode.getAttributes().get("iconLeaf");
            encodeHandleForNodeState(context, treeNode, nodeState, iconLeaf);
        } else {
            String iconExpanded = (String) treeNode.getAttributes().get("iconExpanded");
            String iconCollapsed = (String) treeNode.getAttributes().get("iconCollapsed");
            
            if (Strings.isNullOrEmpty(iconCollapsed) && Strings.isNullOrEmpty(iconExpanded)) {
                encodeDefaultHandle(context, component, nodeState.getDefaultHandleClass());
            } else {
                SwitchType toggleType = treeNode.findTreeComponent().getToggleType();

                if (toggleType == SwitchType.client || nodeState == TreeNodeState.collapsed) {
                    encodeHandleForNodeState(context, treeNode, TreeNodeState.collapsed, iconCollapsed);
                }
                
                if (toggleType == SwitchType.client || nodeState == TreeNodeState.expanded) {
                    encodeHandleForNodeState(context, treeNode, TreeNodeState.expanded, iconExpanded);
                }
            }
        }
    }

    protected void encodeHandleForNodeState(FacesContext context, AbstractTreeNode treeNode, TreeNodeState nodeState, String cutomIcon) throws IOException {
        if (Strings.isNullOrEmpty(cutomIcon)) {
            encodeDefaultHandle(context, treeNode, nodeState.getDefaultHandleClass());
        } else {
            encodeCustomHandle(context, treeNode, nodeState.getCustomHandleClass(), cutomIcon);
        }
    }
}