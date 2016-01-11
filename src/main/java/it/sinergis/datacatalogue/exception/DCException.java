package it.sinergis.datacatalogue.exception;

import it.sinergis.datacatalogue.common.PropertyReader;

public class DCException extends Exception {

	/** Error messages reader. */
	private PropertyReader pr;	
	
	/** Error code. */
	private String errorCode;
	
	/** Error message. */
	private String errorMessage;
	
	public DCException(String errorCode) {
		super(errorCode);
		pr = new PropertyReader("error_messages.properties");
		this.setErrorCode(errorCode);
		this.setErrorMessage(pr.getValue(errorCode));	
	}
	
	/**
	 * Returns an error as json in the following format: {"Status":"Error","Description": ????}
	 * 
	 * @return
	 */
	public String returnErrorString() {
		return "{\"Status\":\"Error\",\"Description\":\""+this.getErrorMessage()+"\"}";
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
	
	
}
