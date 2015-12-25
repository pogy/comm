package com.vipkid.service.exception;

/**
 * 业务异常类
 */
public abstract class ServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ServiceException(int status, String template, Object... params) {
        super(String.format(template,params));
        this.status = status;
        this.message = String.format(template, params);
	}
    public ServiceException(int status) {
        super();
        this.status = status;
    }
    public ServiceException(int status, Throwable cause,String template, Object... params) {
        super(String.format(template,params),cause);
        this.status = status;
        this.message = String.format(template,params);
    }
	
}
