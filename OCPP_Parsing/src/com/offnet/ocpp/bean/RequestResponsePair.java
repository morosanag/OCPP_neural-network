/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.bean;

import com.offnet.ocpp.bean.OcppMessage;
import java.util.Objects;

/**
 *
 * @author gabi
 */
public class RequestResponsePair {
    
    private OcppMessage request;
    private OcppMessage response;

    public RequestResponsePair(OcppMessage request, OcppMessage response) {
        this.request = request;
        this.response = response;
    }
    
    public OcppMessage getRequest() {
        return request;
    }

    public void setRequest(OcppMessage request) {
        this.request = request;
    }

    public OcppMessage getResponse() {
        return response;
    }

    public void setResponse(OcppMessage response) {
        this.response = response;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.request);
        hash = 29 * hash + Objects.hashCode(this.response);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RequestResponsePair other = (RequestResponsePair) obj;
        if (!Objects.equals(this.request, other.request)) {
            return false;
        }
        if (!Objects.equals(this.response, other.response)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RequestResponsePair{" + "request=" + request + ", response=" + response + '}';
    }
}
