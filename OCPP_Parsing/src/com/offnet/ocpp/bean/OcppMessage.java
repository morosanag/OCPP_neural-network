/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.bean;

import com.offnet.ocpp.general.RequestType;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gabi
 */
public class OcppMessage {
    
    private long time;
    private String stationId;
    private RequestType requestType;
    private int messageType;
    private String messageId;
    private JSONObject messageContent;
    
    public OcppMessage() {
        
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public JSONObject getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(JSONObject messageContent) {
        this.messageContent = messageContent;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.time ^ (this.time >>> 32));
        hash = 53 * hash + Objects.hashCode(this.stationId);
        hash = 53 * hash + Objects.hashCode(this.requestType);
        hash = 53 * hash + this.messageType;
        hash = 53 * hash + Objects.hashCode(this.messageId);
        hash = 53 * hash + Objects.hashCode(this.messageContent);
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
        final OcppMessage other = (OcppMessage) obj;
        if (this.time != other.time) {
            return false;
        }
        if (this.messageType != other.messageType) {
            return false;
        }
        if (!Objects.equals(this.stationId, other.stationId)) {
            return false;
        }
        if (!Objects.equals(this.messageId, other.messageId)) {
            return false;
        }
        if (this.requestType != other.requestType) {
            return false;
        }
        if (!Objects.equals(this.messageContent, other.messageContent)) {
            return false;
        }
        return true;
    }

    public boolean isErrorResponse() throws JSONException {
        // generic
        if(messageType == 4) {
            return true;
        }
        
        // StartTransaction
        if(messageContent == null) {
            return false;
        }
        
        if(messageContent.has("transactionId")) {
            if(messageContent.getInt("transactionId") == -1) {
                return true;
            }
        }
        
        // Authorize & StopTransaction
        if(messageContent.has("idTagInfo")) {
            if(messageContent.getJSONObject("idTagInfo").has("status")) {
                if(!messageContent.getJSONObject("idTagInfo").getString("status").equalsIgnoreCase("accepted")) {
                    return true;
                }
            }
        }
         
        // BootNotification & Data Transfer
        if(messageContent.has("status")) {
            if(!messageContent.getString("status").equalsIgnoreCase("accepted")) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return "OcppMessage{" + "time=" + time + ", stationId=" + stationId + ", requestType=" + requestType + ", messageType=" + messageType + ", messageId=" + messageId + ", messageContent=" + messageContent + '}';
    }    
    
}
