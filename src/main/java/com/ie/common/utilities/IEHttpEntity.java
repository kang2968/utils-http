package com.ie.common.utilities;

/**
 * Http response message entity
 * @author bradly
 * @version 1.0
 */
public class IEHttpEntity {

    /**
     * http response status
     */
    private int status;

    /**
     * http response content
     */
    private String response;

    private IEHttpEntity() {
    }

    public IEHttpEntity(int status) {
        this.status = status;
    }

    public IEHttpEntity(int status, String response) {
        this.status = status;
        this.response = response;
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

    @Override
    public String toString() {
        return "IEHttpEntity{" +
                "status='" + status + '\'' +
                ", response='" + response + '\'' +
                '}';
    }

}
