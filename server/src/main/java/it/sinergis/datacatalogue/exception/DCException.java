package it.sinergis.datacatalogue.exception;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.common.PropertyReader;

public class DCException extends Exception {

	/** Error messages reader. */
	private PropertyReader pr;

	/** Logger. */
	private static Logger logger;

	/** Error code. */
	private String errorCode;

	/** Error message. */
	private String errorMessage;

	/** JSon request. */
	private String jsonRequest;

	public DCException(String errorCode, String jsonRequest) {
		super(errorCode);
		pr = new PropertyReader("error_messages.properties");
		this.setErrorCode(errorCode);
		this.setErrorMessage(pr.getValue(errorCode));
		this.jsonRequest = jsonRequest;
		logger = Logger.getLogger(this.getClass());
	}

	public DCException(String errorCode) {
		super(errorCode);
		pr = new PropertyReader("error_messages.properties");
		this.setErrorCode(errorCode);
		this.setErrorMessage(pr.getValue(errorCode));
		logger = Logger.getLogger(this.getClass());
	}

	/**
	 * Returns an error as json in the following format:
	 * {"status":"Error","description": "description","request": {jsonRequest}}
	 * 
	 * @return
	 */
	public String returnErrorString() {
		StringBuilder errorStringBuilder = new StringBuilder();
		errorStringBuilder.append("{");
		errorStringBuilder.append("\"");
		errorStringBuilder.append(Constants.STATUS_FIELD);
		errorStringBuilder.append("\":\"");
		errorStringBuilder.append(Constants.STATUS_ERROR);
		errorStringBuilder.append("\",\"");
		errorStringBuilder.append(Constants.DESCRIPTION_FIELD);
		errorStringBuilder.append("\":\"");
		errorStringBuilder.append(this.getErrorMessage());
		errorStringBuilder.append("\"");

		if (StringUtils.isNotEmpty(this.getJsonRequest())) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				String request = mapper.readTree(this.getJsonRequest()).toString();
				errorStringBuilder.append(",\"");
				errorStringBuilder.append(Constants.REQUEST);
				errorStringBuilder.append("\":");
				errorStringBuilder.append(request);
			} catch (Exception e) {
				logger.error("Error in reading the request");
			}
		}
		errorStringBuilder.append("}");

		return errorStringBuilder.toString();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getJsonRequest() {
		return jsonRequest;
	}

	public void setJsonRequest(String jsonRequest) {
		this.jsonRequest = jsonRequest;
	}

}
