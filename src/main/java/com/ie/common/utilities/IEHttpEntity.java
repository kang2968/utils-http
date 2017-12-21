package com.ie.common.utilities;

/**
 * Http response message entity
 * @author bradly
 * @version 1.0
 */
public class IEHttpEntity {

    public static final int error_code = -1;

    /**
     * http response status
     */
    private int status;

    /**
     * http response content
     */
    private String response;

    /**
     * request failed, status = -1
     */
    private Throwable error;

    public IEHttpEntity(int status, String response) {
        this.status = status;
        this.response = response;
    }

    public IEHttpEntity(Throwable error){
        this.status = error_code;
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "IEHttpEntity{" +
                "status='" + status + '\'' +
                ", response='" + response + '\'' +
                '}';
    }

}
