'use strict';
var gscDatacat = gscDatacat || {};

/**
 * Create a new Response object
 *
 * @class
 */
gscDatacat.Response = function() {

    /**
     * Object to hold this context
     * @type gscDatacat.Response
     */
    var _self = this;

    /**
     * Status of the response
     */
    this.success = true;

    /**
     * Array of messages
     */
    this.messages = [];

    /**
     * Place holder for response data
     */
    this.data = null;

    /**
     * Set the status of the operation to success (true) or error (false)
     *
     * @param {Boolean} status
     * @return {gscDatacat.Response}
     */
    this.setStatus = function(status) {
        if (status === undefined) {
            return;
        }
        this.success = status;
        return _self;
    };

    /**
     * Set data content of response
     *
     * @param {Object} data
     * @param {Boolean} [status=true]
     * @return {gscDatacat.Response}
     */
    this.setData = function(data, status) {
        if (data === undefined) {
            return;
        }
        if (status === undefined) {
            status = true;
        }
        this.setStatus(status);
        return _self;
    };

    /**
     * Add a message to the response object
     *
     * @param {type} message
     * @param {type} [status=true]
     * @returns {undefined}
     */
    this.addMessage = function(message, status) {
        if (message === undefined) {
            return;
        }
        if (status === undefined) {
            status = true;
        }
        this.messages.push(message);
        return _self;
    };

};

/**
 * Create a new response success object
 *
 * @param {Object} [data] - Optional data object
 * @param {String} [message] - Optional message
 * @returns {gscDatacat.Response}
 */
gscDatacat.Response.getSuccess = function(data, message) {
    var r = new gscDatacat.Response();
    if (message !== undefined) {
        r.addMessage(message);
    }
    if (data !== undefined) {
        r.setData(data);
    }
    r.setStatus(true);
    return r;
};

/**
 * Create a new response error object
 *
 * @param {Object} [data] - Optional data object
 * @param {String} [message] - Optional message
 * @returns {gscDatacat.Response}
 */
gscDatacat.Response.getError = function(data, message) {
    var r = new gscDatacat.Response();
    if (message !== undefined) {
        r.addMessage(message);
    }
    if (data !== undefined) {
        r.setData(data);
    }
    r.setStatus(false);
    return r;
};
