/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.network;

import com.offnet.ocpp.bean.OcppPairComparator;
import com.offnet.ocpp.parsing.LogReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.offnet.ocpp.bean.RequestResponsePair;
import org.json.JSONException;

/**
 *
 * @author gabi
 */
public class NeuralNetInput {
    
    // input values
    private double lastTimeRequestPerc;
    private double lastTimeStationRequestPerc;
    private double requestPriority;
    
    // target values
    private double target;
    
    // constants
    private static final long MAX_STATION_TIME = 10 * 60 * 1000; // 10 minutes
    private static final long MAX_STATION_REQUEST_TIME = 10 * 60 * 1000; // 10 minutes
    
    // neural network constants
    public static final int INPUT_SIZE = 3;
    public static final int HIDDEN_SIZE = 3;
    public static final int OUTPUT_SIZE = 1;
    
    static int count;
    static double sum;
    
    public NeuralNetInput(RequestResponsePair lastPair, RequestResponsePair currentPair, long lastTimeRequest, LogReader logReader) {
        OcppPairComparator ocppPairComparator = new OcppPairComparator(lastPair, currentPair);
        target = ocppPairComparator.getSimilarityPercentage() * 10;
        
        try {
            if(currentPair.getResponse().isErrorResponse()) {
                target /= 2;
            } else {
                target = (target + 1) / 2;
            }
            
            count++;
            sum += target;
            
        //    System.out.println("#" + target + " - " + currentPair.getResponse().isErrorResponse());
        } catch (JSONException ex) {
            Logger.getLogger(NeuralNetInput.class.getName()).log(Level.SEVERE, null, ex);
        }
       // System.out.println(sum + " " + count);
       
       
       
       requestPriority = percentageValue(currentPair.getRequest().getStationId(), logReader.getStationIds());
       //System.out.println("requestPriority: " + requestPriority);
       //requestPriority = currentPair.getRequest().getRequestType().getPriority();
       //System.out.println("lastTimeRequest: " +lastTimeRequest);
       // System.out.println(lastTimeRequest + " " +MAX_STATION_TIME );
        
      // System.out.println("--------------------------");
     //  System.out.println(lastPair);
     //  System.out.println(currentPair);
       
        
        lastTimeRequestPerc = lastTimeRequest > MAX_STATION_TIME ? 1 : ((double)lastTimeRequest / MAX_STATION_TIME);
        
        long timeDiff = Math.abs(currentPair.getRequest().getTime() - lastPair.getRequest().getTime());
       // System.out.println(timeDiff);
        lastTimeStationRequestPerc = timeDiff > MAX_STATION_REQUEST_TIME ? 1 : ((double)timeDiff / MAX_STATION_REQUEST_TIME);           
        
        
        //System.out.println("a: " + lastTimeRequest + " - " + MAX_STATION_TIME);
        //System.out.println("b: " + timeDiff + " - " + MAX_STATION_REQUEST_TIME);
    }

    public static double percentageValue(String value, Set<String> arr) {
    
        List<String> list = new ArrayList(arr);
        int index = list.indexOf(value);
        int size = list.size();
        
        double bucketSize = (double) 1 / (size - 1);
        return bucketSize * index;
    
    }
    
    public double getLastTimeRequestPerc() {
        return lastTimeRequestPerc;
    }

    public void setLastTimeRequestPerc(double lastTimeRequestPerc) {
        this.lastTimeRequestPerc = lastTimeRequestPerc;
    }

    public double getLastTimeStationRequestPerc() {
        return lastTimeStationRequestPerc;
    }

    public void setLastTimeStationRequestPerc(double lastTimeStationRequestPerc) {
        this.lastTimeStationRequestPerc = lastTimeStationRequestPerc;
    }

    public double getRequestPriority() {
        return requestPriority;
    }

    public void setRequestPriority(double requestPriority) {
        this.requestPriority = requestPriority;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "NeuralNetInput{" + "lastTimeRequestPerc=" + lastTimeRequestPerc + ", lastTimeStationRequestPerc=" + lastTimeStationRequestPerc + ", requestPriority=" + requestPriority + ", target=" + target + '}';
    }
    
}