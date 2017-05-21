/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.network;

import com.offnet.ocpp.general.Constants;
import com.offnet.ocpp.parsing.LogReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import com.offnet.ocpp.network.NeuralNetwork;
import com.offnet.ocpp.bean.RequestResponsePair;
import com.offnet.ocpp.bean.StationRequestTypePair;

/**
 *
 * @author gabi
 */
public class NeuralNetworkController {
    
    private LogReader logReader;
    private Map<StationRequestTypePair, RequestResponsePair> lastRequestTypeStation;
    private Map<String, Long> lastRequestStation;
    private NeuralNetwork neuralNetwork = new NeuralNetwork();
    
    private Writer writerAll;
    private Writer writerFiltered;
    
    private final String chargePoint = "RENOVA_0007";
    //private final String chargePoint = "LMS-11702190";
    
    public NeuralNetworkController(String urlString) throws UnsupportedEncodingException, FileNotFoundException {
        this.logReader = new LogReader(urlString);
        lastRequestTypeStation = new HashMap<StationRequestTypePair, RequestResponsePair>();
        lastRequestStation = new HashMap<String, Long>();
        
        writerAll = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("traffic_all.txt"), "utf-8"));
        writerFiltered = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("traffic_filtered.txt"), "utf-8"));
        
        initialize();
        
        
    }
    
    public static void main(String [] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {     
        NeuralNetworkController networkController = new NeuralNetworkController("http://serverstore.ro/OCPP_Logs/ocpp_rest_01-01-2017.log");
        networkController.computeDifferences(true);
        networkController.logReader = new LogReader("file:///var/www/serverstore.ro/web/OCPP_Logs/ocpp_rest_result.log");
        //networkController.logReader = new LogReader("file:///var/www/serverstore.ro/web/OCPP_Logs/ocpp_rest_result.log");
        //networkController.logReader = new LogReader("http://serverstore.ro/OCPP_Logs/ocpp_rest_01-02-2017.log");
        networkController.logReader.read();
        System.out.println("size: " + networkController.logReader.getStationIds().size());
        networkController.computeDifferences(false);
        networkController.writerAll.close();
        networkController.writerFiltered.close();
    }
    
    public void initialize() {
        logReader.read();
    }
    
    private void writeToFile(RequestResponsePair currentPair, Writer writer) throws IOException {
        
        writer.write(currentPair.getRequest() + "\n");
        writer.write(currentPair.getResponse() + "\n");
        
    }
    
    public void computeDifferences(boolean isLearning) throws IOException {
        while(!logReader.getOcppLogPairs().isEmpty()) {
            
            RequestResponsePair currentPair = logReader.getOcppLogPairs().removeLast();
            if(!isLearning && !currentPair.getRequest().getStationId().equals(chargePoint))  {
                writeToFile(currentPair, writerAll);
            }
            
            //if(!currentPair.getRequest().getStationId().equals(chargePoint)) {
                   
             
            //System.out.println("---------------------");
            // get last time diff from the last request the same station
            long stationTimeDiff = 0;
            if(lastRequestStation.containsKey(currentPair.getRequest().getStationId())) {
                long lastRequestTime = lastRequestStation.get(currentPair.getRequest().getStationId());
                stationTimeDiff = Math.abs(currentPair.getRequest().getTime() - lastRequestTime);
                //System.out.println("ok: " + currentPair.getRequest().getTime() + " - " + lastRequestTime + " = " + stationTimeDiff);
                lastRequestStation.put(currentPair.getRequest().getStationId(), currentPair.getRequest().getTime());
            } else {
                stationTimeDiff = currentPair.getRequest().getTime();
                lastRequestStation.put(currentPair.getRequest().getStationId(), stationTimeDiff);
            }
            
            // get last time diff from the last request having the same time from the same station
            StationRequestTypePair currentStationRequestTypePair = new StationRequestTypePair(currentPair.getRequest().getRequestType(), 
                    currentPair.getRequest().getStationId());
            if(lastRequestTypeStation.containsKey(currentStationRequestTypePair)) {
                RequestResponsePair lastRequestResponsePair = lastRequestTypeStation.get(currentStationRequestTypePair);
                NeuralNetInput neuralNetInput = new NeuralNetInput(lastRequestResponsePair, currentPair, stationTimeDiff, logReader);
                
                
                //System.out.println(neuralNetInput.getLastTimeRequestPerc() + " " + neuralNetInput.getLastTimeStationRequestPerc() + " " + neuralNetInput.getRequestPriority());
                    
                
                if(isLearning) {
                  //  System.out.println("isLearnt");
                    ///if(neuralNetInput.getLastTimeRequestPerc() > 0.01 && neuralNetInput.getLastTimeStationRequestPerc() > 0.01) {
                  //  System.out.println(neuralNetInput);
                        neuralNetwork.processNode(neuralNetInput);
                        
                        
                        
                   // }
                } else {
                  //  System.out.println("!isLearnt");
                    neuralNetwork.computeNode(neuralNetInput);
                    
                    long start = (long) 1483318133000.0;
                    
                    if(lastRequestResponsePair.getRequest().getStationId().equalsIgnoreCase(chargePoint)) {
                        System.out.println((lastRequestResponsePair.getRequest().getTime() - start) + ", " + neuralNetInput.getTarget());
                    }
                    
                    if(neuralNetInput.getTarget() > Constants.THRESHOLD) {
                        neuralNetwork.processNode(neuralNetInput);
                    //    if(lastRequestResponsePair.getRequest().getStationId().equalsIgnoreCase(chargePoint)) {
                    //        System.out.println("2: " + lastRequestResponsePair.getRequest().getStationId() + " " + lastRequestResponsePair.getRequest().getTime() + ":\t" + neuralNetInput.getTarget());
                    //    }
                    }
                    
                    if(neuralNetInput.getTarget() > Constants.THRESHOLD && !currentPair.getRequest().getStationId().equals(chargePoint)) {
                        writeToFile(currentPair, writerFiltered);
                    }
                }
                lastRequestTypeStation.put(currentStationRequestTypePair, currentPair);
                
            } else {
                lastRequestTypeStation.put(currentStationRequestTypePair, currentPair);
                if(!isLearning && !currentPair.getRequest().getStationId().equals(chargePoint)) {
                    writeToFile(currentPair, writerFiltered);
                }
            }
            
            
            //}
            
        }
        
        neuralNetwork.printCurrentState();
    }
    
    /*
    public void computeDifferences() {
        while(!logReader.getOcppLogPairs().isEmpty()) {
            RequestResponsePair currentPair = logReader.getOcppLogPairs().pop();
            if(currentPair.getRequest().getStationId().equals(chargePoint)) {
                if(lastPairs.containsKey(currentPair.getRequest().getRequestType())) {
                    RequestResponsePair lastPair = lastPairs.get(currentPair.getRequest().getRequestType());
                    OcppPairComparator ocppPairComparator = new OcppPairComparator(lastPair, currentPair);
                    lastPairs.put(currentPair.getRequest().getRequestType(), currentPair);
                    System.out.println(currentPair.getRequest().getRequestType() + ": " + ocppPairComparator.getSimilarityPercentage());
                    //System.out.println("diff: " + ocppPairComparator.getSimilarityPercentage());
                } else {
                    lastPairs.put(currentPair.getRequest().getRequestType(), currentPair);
                }
            }
        }
        
    }*/
    
}