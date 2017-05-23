/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.general;

import java.util.LinkedList;

/**
 *
 * @author gabi
 */
public class Utils {
    
    public static RequestType[][] invalidSequences = {{RequestType.StartTransaction, RequestType.Authorize},
        {RequestType.StopTransaction, RequestType.Authorize},
        {RequestType.StartTransaction, RequestType.StopTransaction},
        {RequestType.StopTransaction, RequestType.StartTransaction},
        {RequestType.Authorize, RequestType.MeterValues},
        {RequestType.MeterValues, RequestType.StartTransaction},
        {RequestType.StopTransaction, RequestType.MeterValues},
        {RequestType.Authorize, RequestType.StartTransaction},
        {RequestType.StartTransaction, RequestType.StartTransaction},
        {RequestType.StopTransaction, RequestType.StopTransaction}     
    };
    
    
    public static RequestType[] allRequestTypes = {RequestType.Authorize, RequestType.BootNotification, RequestType.Heartbeat, RequestType.MeterValues,
        RequestType.StartTransaction, RequestType.StatusNotification, RequestType.StopTransaction};

    // return true is it's valid and false otherwise
    public static boolean checkRequestSequence(LinkedList<RequestType> sequence, int index) {
        
        for(int i = 0; i < invalidSequences.length; i++) {
            if(sequence.get(index) == null) {
                return true;
            }
            if(sequence.get(index).equals(invalidSequences[i][0]) && sequence.get(index + 1).equals(invalidSequences[i][1])) {
                return false;
            }
        }
        
        if(sequence.get(index).equals(RequestType.BootNotification) && !sequence.get(index + 1).equals(RequestType.StatusNotification)) {
            return false;
        }
        
        return true;
    }
    
    public static boolean checkRequestSequence(LinkedList<RequestType> sequence) { 
        if(sequence.size() < 3) return false;
        return checkRequestSequence(sequence, 0) || checkRequestSequence(sequence, 1);
    }
}
