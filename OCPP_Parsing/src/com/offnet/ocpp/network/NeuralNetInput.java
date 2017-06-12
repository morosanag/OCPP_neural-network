/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;

import com.offnet.ocpp.bean.OcppPairComparator;
import com.offnet.ocpp.bean.RequestResponsePair;
import com.offnet.ocpp.general.RequestType;
import com.offnet.ocpp.parsing.LogReader;

/**
 *
 * @author gabi
 */
public class NeuralNetInput {
    
    // input values
    private double lastTimeRequestPerc;
    private double lastTimeStationRequestPerc;
    //private double requestPriority;// sample 
    
    // target values
    private double target;
    
    // last N requests
    private LinkedList<RequestType> requestSequence;
    
    // constants
    private static final long MAX_STATION_TIME = 10 * 60 * 1000; // 10 minutes
    private static final long MAX_STATION_REQUEST_TIME = 10 * 60 * 1000; // 10 minutes
    
    // neural network constants
    public static final int INPUT_SIZE = 4;
    public static int HIDDEN_SIZE = 4;
    public static final int OUTPUT_SIZE = 1;
    
    public static HashMap<LinkedList<RequestType>, Double> sequencesMap = new HashMap<LinkedList<RequestType>, Double>();
    
    static int count;
    static double sum;
    
    public NeuralNetInput() {
    	requestSequence = new LinkedList<RequestType>();
    	sequencesMap.clear();
    	count = 0;
    	sum = 0;
        
    }
    
    public NeuralNetInput(RequestResponsePair lastPair, RequestResponsePair currentPair, long lastTimeRequest, LogReader logReader) {
        OcppPairComparator ocppPairComparator = new OcppPairComparator(lastPair, currentPair);
        target = ocppPairComparator.getSimilarityPercentage() ;
        
       /* if(target < 0.3) {
            target = 0;
        } else {
            target = 1;
        }
       */ 
        try {
            if(currentPair.getResponse().isErrorResponse()) {
                target /= 2;
            } else {
                target = (target + 1) / 2;
            }
            
            count++;
            sum += target;
            
        //    //System.out.println("#" + target + " - " + currentPair.getResponse().isErrorResponse());
        } catch (JSONException ex) {
            Logger.getLogger(NeuralNetInput.class.getName()).log(Level.SEVERE, null, ex);
        }
       // //System.out.println(sum + " " + count);
       
       
       
       //requestPriority = percentageValue(currentPair.getRequest().getStationId(), logReader.getStationIds());
       ////System.out.println("requestPriority: " + requestPriority);
       //requestPriority = currentPair.getRequest().getRequestType().getPriority();
       ////System.out.println("lastTimeRequest: " +lastTimeRequest);
       // //System.out.println(lastTimeRequest + " " +MAX_STATION_TIME );
        
      // //System.out.println("--------------------------");
     //  //System.out.println(lastPair);
     //  //System.out.println(currentPair);
       
        
        lastTimeRequestPerc = lastTimeRequest > MAX_STATION_TIME ? 1 : ((double)lastTimeRequest / MAX_STATION_TIME);
        
        long timeDiff = Math.abs(currentPair.getRequest().getTime() - lastPair.getRequest().getTime());
       // //System.out.println(timeDiff);
        lastTimeStationRequestPerc = timeDiff > MAX_STATION_REQUEST_TIME ? 1 : ((double)timeDiff / MAX_STATION_REQUEST_TIME);           
        
        
        ////System.out.println("a: " + lastTimeRequest + " - " + MAX_STATION_TIME);
        ////System.out.println("b: " + timeDiff + " - " + MAX_STATION_REQUEST_TIME);
    }

    public static double percentageValue(String value, Set<String> arr) {
    
        List<String> list = new ArrayList(arr);
        int index = list.indexOf(value);
        int size = list.size();
        
        double bucketSize = (double) 1 / (size - 1);
        return bucketSize * index;
    
    }
    
    public double getSequenceIndex(LinkedList<RequestType> sequence) {
    	if(sequencesMap.containsKey(sequence)) {
    		return sequencesMap.get(sequence);
    	}
    	double value = Math.random();
    	sequencesMap.put(sequence, value);
    	
    	return value;
    	
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

    /*public double getRequestPriority() {
        return requestPriority;
    }

    public void setRequestPriority(double requestPriority) {
        this.requestPriority = requestPriority;
    }*/

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public LinkedList<RequestType> getRequestSequence() {
		return requestSequence;
	}
 
	public void setRequestSequence(LinkedList<RequestType> requestSequence) {
		this.requestSequence = requestSequence;
	}

	@Override
	public String toString() {
		return "NeuralNetInput [lastTimeRequestPerc=" + lastTimeRequestPerc + ", lastTimeStationRequestPerc="
				+ lastTimeStationRequestPerc + ", target=" + target
				+ ", requestSequence=" + requestSequence + "]";
	}    
}
