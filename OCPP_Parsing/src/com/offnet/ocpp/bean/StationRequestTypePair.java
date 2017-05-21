/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.bean;

import com.offnet.ocpp.general.RequestType;
import java.util.Objects;

/**
 *
 * @author gabi
 */
public class StationRequestTypePair {
    
    private RequestType requestType;
    private String station;

    public StationRequestTypePair(RequestType requestType, String station) {
        this.requestType = requestType;
        this.station = station;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.requestType);
        hash = 97 * hash + Objects.hashCode(this.station);
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
        final StationRequestTypePair other = (StationRequestTypePair) obj;
        if (!Objects.equals(this.station, other.station)) {
            return false;
        }
        if (this.requestType != other.requestType) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StationRequestTypePair{" + "requestType=" + requestType + ", station=" + station + '}';
    }
}
