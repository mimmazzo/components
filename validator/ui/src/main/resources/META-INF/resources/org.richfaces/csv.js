(function($, rf) {

	rf.csv = rf.csv || {};
	

	var RE_MESSAGE_PATTERN = /\{(\d+)\}/g;
	
	function _interpolateMessage(customMessage, values) {
		var message = customMessage ||  "";
		if (message) {
			var msgObject = message.replace(RE_MESSAGE_PATTERN,"\n$1\n").split("\n");
			var value;
			for (var i=1; i<msgObject.length; i+=2) {
				value = values[msgObject[i]];
				msgObject[i] = typeof value == "undefined" ? "" : value;
			}
			message = msgObject.join('');
		}
		return message;
	}
	
	$.extend(rf.csv, {
		RE_DIGITS: /^-?\d+$/,
		RE_FLOAT: /^(-?\d+)?(\.(\d+)?(e[+-]?\d+)?)?$/,
		MESSAGE_EVENT_NAME: "onmessage."+rf.Event.RICH_NAMESPACE,
		// Messages API
		getMessage :function(facesMessage,values){
			return {detail:_interpolateMessage(facesMessage.detail,values),summary:_interpolateMessage(facesMessage.summary,values)};
		},
		sendMessage: function (componentId, message) {
			rf.Event.fireById(document, rf.csv.MESSAGE_EVENT_NAME, message);
			rf.Event.fireById(componentId, rf.csv.MESSAGE_EVENT_NAME, message);
		},
		clearMessage: function (componentId) {
			rf.Event.fireById(document, rf.csv.MESSAGE_EVENT_NAME, message);
			rf.Event.fireById(componentId, rf.csv.MESSAGE_EVENT_NAME, message);
		},
		getValue: function (clientId, element){
			var value;
			var element = element || rf.getDomElement(id);
			if (element.value) {
				value = element.value;
			} else {
				var component = rf.$(element);
				// TODO: add getValue to baseComponent and change jsdocs
				value = component && typeof component["getValue"] == "function" ? component.getValue() : "";
			}
			return value;
		},
		validate: function (event, id, converter, validators) {
			var value = getValue(id);
			if (converter) {
				try {
					converter.options.componentId = id;
					value = getConverter([converter.name])(value, converter.options);
				} catch (e){
					sendMessage(id, e.message);
					return false;
				}
			}
			if (validators) {
				var validatorFunction;
				try {
					for (i=0;i<validators.length;i++) {
						validatorFunction = getValidator(validators[i].type);
						if (validatorFunction) {
							validatorFunction(id, value, validators[i]);
						}
					}
				} catch (e) {
					sendMessage(id, result);
					return false;
				}
			}
			return true;
		},
		// Converters
		convertBoolean: function(value,message,options){
			options = options || {};
			var result; 
			value = $.trim(value).toLowerCase();
			result = value=='true' ? true : value.length<1 ? null : false;
			return result;
		},
		// Validators
		validateLength: function(value,message,params){
			if (params.maximum && value.length > params.maximum) {
				throw rf.csv.getMessage(message, [params.minimum,params.maximum]);
			}
			if (params.minimum && value.length < params.minimum) {
				throw rf.csv.getMessage(message, [params.minimum,params.maximum]);
			}

		},
		addFormValidators: function (formId, callValidatorFunctions) {
			
		}
	});
	
	/*
	// component ids hash that can send messages
	// each hash item contains array of message component id that receive messages from the component
	_componentIds = {};
	// array of message component id that will receive messages from all components
	_messageComponentIds = {};
	
	var messageDispatchers = {};
	var addDispatcher = function (dispatcherId) {
	};
	var removeDispatcher: function (dispatcherId) {
	};

	rf.MessageDispatcher = function(id) {
		this.id = id;
	};
	rf.BaseComponent.extend(rf.MessageDispatcher);
	
	$.extend(rf.MessageDispatcher.prototype, {
		register: function (messageComponentId, componentIds) {
			if (!componentIds || componentIds.length==0) {
				// global message listener
				_messageComponents.push(messageComponentId);
			}
			var messageComponents;
			for (var i=0;i<componentIds.length;i++) {
				messageComponents = _components[componentIds[i]];
				if (!messageComponents) {
					messageComponents = _components[componentIds[i]] = [];
				}
				messageComponents.push(messageComponentId);
			}
		},
		unregister: function (messageComponentId) {
			var messageComponents;
			for (var i=0;i<_components.length;i++) {
				messageComponents = _components[i];
				if (!messageComponents) {
					messageComponents = _components[componentIds[i]] = [];
				}
				messageComponents.push(messageComponentId);
			}
		},
		send: function (componentId, message) {
			var messageComponents = _components[componentId];
			if (messageComponents) {
				for (var i=0;i<messageComponents.length;i++) {
					rf.$(messageComponents[id]).update(message);
				}
			}
		}
	});
	*/
	
	/*
	 * message.constructor () {
	 * 		rf.Event.bindById(componentId, "onMessage.RichFaces", onMessage );
	 * 		rf.Event.bindById(document, "onMessage.RichFaces", onMessage );
	 * }
	 * 
	 */
	
})(jQuery, window.RichFaces || (window.RichFaces={}));