/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.network;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.offnet.ocpp.bean.RequestResponsePair;
import com.offnet.ocpp.bean.StationRequestTypePair;
import com.offnet.ocpp.general.Constants;
import com.offnet.ocpp.general.RequestType;
import com.offnet.ocpp.general.Utils;
import com.offnet.ocpp.parsing.LogReader;

/**
 *
 * @author gabi
 */
public class NeuralNetworkController {
    
    private LogReader logReader;
    private Map<StationRequestTypePair, RequestResponsePair> lastRequestTypeStation;
    private Map<String, Long> lastRequestStation;
    private HashMap<String, LinkedList<RequestType>> lastSeqRequestsStation;
    private NeuralNetwork neuralNetwork = new NeuralNetwork();
    
    public HashMap<String, Integer> countAllPerStation;
    public HashMap<String, Integer> countAllWrittenPerStation;
    
    private Writer writerAll;
    private Writer writerFiltered;
    
    //private final String chargePoint = "RENOVA_0007";
    private final String chargePoint = "BMX LMS-11702190";
    
    public NeuralNetworkController(String urlString) throws UnsupportedEncodingException, FileNotFoundException {
        this.logReader = new LogReader(urlString);
        lastRequestTypeStation = new HashMap<StationRequestTypePair, RequestResponsePair>();
        lastRequestStation = new HashMap<String, Long>();
        lastSeqRequestsStation = new HashMap<String, LinkedList<RequestType>>();
        countAllPerStation = new HashMap<String, Integer>();
        countAllWrittenPerStation = new HashMap<String, Integer>();
        
        
        createNewLogs();
        
        initialize();

    }
    
    public void createNewLogs() throws UnsupportedEncodingException, FileNotFoundException {
    	
    	Date date = new Date();
    	Timestamp timestamp = new Timestamp(date.getTime());

    	writerAll = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("traffic_all_" + timestamp.getTime() + ".txt"), "utf-8"));
        writerFiltered = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("traffic_filtered_" + timestamp.getTime() + ".txt"), "utf-8"));
    	
    }
    
    public void testSet(String url) throws IOException {
    	this.logReader = new LogReader(url);
    	this.logReader.read();
    	
        System.out.println("size: " + this.logReader.getStationIds().size());
        this.computeDifferences(false);
        this.writerAll.close();
        this.writerFiltered.close();
    }
    
    public static void main(String [] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {     
        NeuralNetworkController networkController = new NeuralNetworkController("http://serverstore.ro/OCPP_Logs/ocpp_rest_01-03-2017.log");
        networkController.computeDifferences(true);
        
        networkController.runAdditionalLeaningSets();
        
        networkController.testSet("file:///var/www/serverstore.ro/web/OCPP_Logs/ocpp_rest_result2.log");
        //networkController.createNewLogs();
        //networkController.testSet("file:///C:/Users/IBM_ADMIN/Desktop/ocpp_neural/OCPP_neural-network/OCPP_Parsing/ocpp_rest_edited.txt");
        
        /*networkController.logReader = new LogReader("http://serverstore.ro/OCPP_Logs/ocpp_rest_01-02-2017.log");
        */
       // networkController.logReader = new LogReader("file:///var/www/serverstore.ro/web/OCPP_Logs/ocpp_rest_result.log");
        //networkController.logReader = new LogReader("http://serverstore.ro/OCPP_Logs/ocpp_rest_01-02-2017.log");
       /* networkController.logReader.read();
        System.out.println("size: " + networkController.logReader.getStationIds().size());
        networkController.computeDifferences(false);
        networkController.writerAll.close();
        networkController.writerFiltered.close();
        */
      /*  networkController.logReader = new LogReader("file:///C:/Users/IBM_ADMIN/Desktop/ocpp_neural/OCPP_neural-network/OCPP_Parsing/ocpp_rest_edited.txt");
        networkController.logReader.read();
        networkController.createNewLogs();
        networkController.computeDifferences(false);
        networkController.writerAll.close();
        networkController.writerFiltered.close();
        */
        System.out.println("Results: ");
        for(String station : networkController.countAllPerStation.keySet()) {
            if(networkController.countAllWrittenPerStation.containsKey(station)) {
                System.out.println(station + ": " + ((double) networkController.countAllWrittenPerStation.get(station) / networkController.countAllPerStation.get(station)));
            }
        }
      
    }
    
    public void initialize() {
        logReader.read();
    }
    
    private void writeToFile(RequestResponsePair currentPair, Writer writer) throws IOException {
        
        writer.write(currentPair.getRequest() + "\n");
        writer.write(currentPair.getResponse() + "\n");
        
    }
    /*
    if(level1 == null) {
                initialize(neuralNetInput);
            }
            
            double[] inputs = new double[NeuralNetInput.INPUT_SIZE];
            double[] targets = new double[NeuralNetInput.OUTPUT_SIZE];
            
            inputs[0] = neuralNetInput.getLastTimeRequestPerc();
            inputs[1] = neuralNetInput.getLastTimeStationRequestPerc();
            inputs[2] = neuralNetInput.getSequenceIndex(neuralNetInput.getRequestSequence());
            
            for(int i = 0; i < neuralNetInput.getRequestSequence().size(); i++) {
            	inputs[2 + i] = neuralNetInput.getRequestSequence().get(i).getPriority();
            }
            
            for(int i = 0; i < inputs.length; i++) {
            	System.out.print(inputs[i] + " ");
            }
            System.out.println();
            
                targets[0] = neuralNetInput.getTarget();
            
            if(!Utils.checkRequestSequence(neuralNetInput.getRequestSequence())) {
                targets[0] /= 2;
            }
    */
    
    public void runAdditionalLeaningSets() {
        
        for(int i = 1; i <= 10; i++) {
            for(int j = 0; j < Utils.invalidSequences.length; j++) {
                for(int k = 0; k < Utils.allRequestTypes.length; k++) {
                    NeuralNetInput netInput = new NeuralNetInput();
                    netInput.setLastTimeRequestPerc((double) 1 / i);
                    netInput.setLastTimeStationRequestPerc((double) 1 / i);
                    
                    LinkedList<RequestType> requestSeq = new LinkedList<RequestType>();
                    requestSeq.add(Utils.invalidSequences[j][0]);
                    requestSeq.add(Utils.invalidSequences[j][1]);
                    requestSeq.add(Utils.allRequestTypes[k]);
                    
                    netInput.setRequestSequence(requestSeq);
                    netInput.setTarget(1 - i * 0.1);
                    //System.out.println("1:" + netInput);
                    neuralNetwork.processNode(netInput);
                    
                    netInput = new NeuralNetInput();
                    netInput.setLastTimeRequestPerc((double) 1 / i);
                    netInput.setLastTimeStationRequestPerc((double) 1 / i);
                    
                    requestSeq = new LinkedList<RequestType>();
                    requestSeq.add(Utils.allRequestTypes[k]);
                    requestSeq.add(Utils.invalidSequences[j][0]);
                    requestSeq.add(Utils.invalidSequences[j][1]);
                    
                    netInput.setRequestSequence(requestSeq);
                    netInput.setTarget(1 - i * 0.1);
                    //System.out.println("2:" + netInput);
                    neuralNetwork.processNode(netInput);
                }
            }
        }
    }
    
    public void computeDifferences(boolean isLearning) throws IOException {
        while(!logReader.getOcppLogPairs().isEmpty()) {
            
            RequestResponsePair currentPair = logReader.getOcppLogPairs().removeLast();
            if(!isLearning /*&& currentPair.getRequest().getStationId().equals(chargePoint)*/)  {
                if(!countAllPerStation.containsKey(currentPair.getRequest().getStationId())) {
                    countAllPerStation.put(currentPair.getRequest().getStationId(), 1);
                } else {
                    countAllPerStation.put(currentPair.getRequest().getStationId(), countAllPerStation.get(currentPair.getRequest().getStationId()) + 1);
                }
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
                
                LinkedList<RequestType> seqRequests = lastSeqRequestsStation.get(currentPair.getRequest().getStationId());
                if(seqRequests.size() >= 3) {
                	seqRequests.removeLast();
                }
                seqRequests.addFirst(currentPair.getRequest().getRequestType());
                lastSeqRequestsStation.put(currentPair.getRequest().getStationId(), seqRequests);
                
                if(currentPair.getRequest().getStationId().equals(chargePoint)) {
                //	System.out.println(lastSeqRequestsStation.get(currentPair.getRequest().getStationId()));
                }
                
            } else {
                stationTimeDiff = currentPair.getRequest().getTime();
                lastRequestStation.put(currentPair.getRequest().getStationId(), stationTimeDiff);
                
                LinkedList<RequestType> seqRequests = new LinkedList<RequestType>();
                seqRequests.addFirst(currentPair.getRequest().getRequestType());
                lastSeqRequestsStation.put(currentPair.getRequest().getStationId(), seqRequests);
                
            }
            
            
            
            // get last time diff from the last request having the same time from the same station
            StationRequestTypePair currentStationRequestTypePair = new StationRequestTypePair(currentPair.getRequest().getRequestType(), 
                    currentPair.getRequest().getStationId());
            if(lastRequestTypeStation.containsKey(currentStationRequestTypePair)) {
                RequestResponsePair lastRequestResponsePair = lastRequestTypeStation.get(currentStationRequestTypePair);
                NeuralNetInput neuralNetInput = new NeuralNetInput(lastRequestResponsePair, currentPair, stationTimeDiff, logReader);
                neuralNetInput.setRequestSequence(lastSeqRequestsStation.get(currentPair.getRequest().getStationId()));
                
                //System.out.println(neuralNetInput.getLastTimeRequestPerc() + " " + neuralNetInput.getLastTimeStationRequestPerc() + " " + neuralNetInput.getRequestPriority());
                    
                
                if(isLearning) {
                  //  System.out.println("isLearnt");
                    ///if(neuralNetInput.getLastTimeRequestPerc() > 0.01 && neuralNetInput.getLastTimeStationRequestPerc() > 0.01) {
                  //  System.out.println(neuralNetInput);
                        neuralNetwork.processNode(neuralNetInput);
                        
                        
                        
                   // }
                } else {
                  //  System.out.println("!isLearnt");
                    if(chargePoint.contains(lastRequestResponsePair.getRequest().getStationId())) {
                    //    System.out.println(lastRequestResponsePair.getRequest().getStationId() + "- " + neuralNetInput);
                    }
                  
                    neuralNetwork.computeNode(neuralNetInput);
                    
                    long start = (long) 1483318133000.0;
                    
                    if(lastRequestResponsePair.getRequest().getStationId().equalsIgnoreCase(chargePoint)) {
                    //    System.out.println((lastRequestResponsePair.getRequest().getTime() - start) + ", " + neuralNetInput.getTarget());
                    }
                    if(chargePoint.contains(lastRequestResponsePair.getRequest().getStationId())) {
                    	System.out.println(lastRequestResponsePair.getRequest().getStationId() + ": " + lastRequestResponsePair.getRequest().getRequestType().getValue() + " " + neuralNetInput.getTarget());
                    } /*else if(lastRequestResponsePair.getRequest().getStationId().equalsIgnoreCase(chargePoint)) {
                    	System.out.println("+++++++++: " + neuralNetInput.getTarget());
                        	
                    }*/
                    
                    if(neuralNetInput.getTarget() > Constants.THRESHOLD) {
                    //    neuralNetwork.processNode(neuralNetInput);
                    //    if(lastRequestResponsePair.getRequest().getStationId().equalsIgnoreCase(chargePoint)) {
                    //        System.out.println("2: " + lastRequestResponsePair.getRequest().getStationId() + " " + lastRequestResponsePair.getRequest().getTime() + ":\t" + neuralNetInput.getTarget());
                    //    }
                    }
                    
                    if(neuralNetInput.getTarget() > Constants.THRESHOLD /*&& currentPair.getRequest().getStationId().equals(chargePoint)*/) {
                        if(!countAllWrittenPerStation.containsKey(currentPair.getRequest().getStationId())) {
                            countAllWrittenPerStation.put(currentPair.getRequest().getStationId(), 1);
                        } else {
                            countAllWrittenPerStation.put(currentPair.getRequest().getStationId(), countAllWrittenPerStation.get(currentPair.getRequest().getStationId()) + 1);
                        }
                        writeToFile(currentPair, writerFiltered);
                    }
                }
                lastRequestTypeStation.put(currentStationRequestTypePair, currentPair);
                
            } else {
                lastRequestTypeStation.put(currentStationRequestTypePair, currentPair);
                if(!isLearning /*&& currentPair.getRequest().getStationId().equals(chargePoint)*/) {
                    if(!countAllWrittenPerStation.containsKey(currentPair.getRequest().getStationId())) {
                        countAllWrittenPerStation.put(currentPair.getRequest().getStationId(), 1);
                    } else {
                        countAllWrittenPerStation.put(currentPair.getRequest().getStationId(), countAllWrittenPerStation.get(currentPair.getRequest().getStationId()) + 1);
                    }
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
