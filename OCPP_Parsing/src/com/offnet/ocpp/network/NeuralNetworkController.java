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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
    
    public static double best_threshold = 0;
    public static double best_accuracy = 0;
    
    public static int count_BMX_hearbeats = 0;
    
    private final String chargePoint = "BMX";
    //private final String chargePoint = "LMS-11702190";
    
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

    	writerAll = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("traffic_all" + ".txt"), "utf-8"));
        writerFiltered = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("traffic_filtered" + ".txt"), "utf-8"));
        
    	
//    	writerAll = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("traffic_all_" + timestamp.getTime() + ".txt"), "utf-8"));
//        writerFiltered = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("traffic_filtered_" + timestamp.getTime() + ".txt"), "utf-8"));
    	
    }
    
    public void testSet(String url) throws IOException {
    	this.logReader = new LogReader(url);
    	this.logReader.read();
    	
      //  System.out.println("size: " + this.logReader.getStationIds().size());
        this.computeDifferences(false);
        this.writerAll.close();
        this.writerFiltered.close();
    }
    
    public static  void runEntireWorkflow() throws IOException {
    	  NeuralNetworkController networkController = new NeuralNetworkController("http://serverstore.ro/OCPP_Logs/ocpp_rest_01-03-2017.log");
    	  networkController.neuralNetwork = new NeuralNetwork();
        //  networkController.computeDifferences(true);
          
          networkController.runAdditionalLeaningSets();
          
         // networkController.testSet("file:///C:/Users/IBM_ADMIN/Desktop/ocpp_neural/OCPP_neural-network/OCPP_Parsing/ocpp_rest_result2.log");

          //int COUNT_LEARNING_RATE = 10;
          //double[] learning_rates = {0, 0.01, 0.001, 0.0001, 0.00001};
          //for(int j = 0; j < learning_rates.length; j++) {
          
          	//Constants.LEARN_RATE = learning_rates[j];
          	
  	        int COUNT = 1;
  	        for(int i = 1; i <= COUNT; i++) {
  	        	Constants.THRESHOLD = i * (double) 1 / COUNT;
  	
  	        	networkController.runTest();
  	        }
  	        
  	       // System.out.println(Constants.LEARN_RATE + " " +  "best_threshold: " + best_threshold + " " + "best_accuracy: " + best_accuracy);
          //}
  	        
    }
    
    public static void main(String [] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {     
    	
    	//int LEARNING_RATE_STEP = 15;
    	// 0.22
    	//double[] learning_rates = {0.2};
    	//for(int i = 0; i < learning_rates.length; i++) {
    	//	Constants.LEARN_RATE = learning_rates[i];//(double) i * 0.3 / LEARNING_RATE_STEP;
    		runEntireWorkflow();
    	//}
    	
    	//runEntireWorkflow();
        
        //networkController.runTest();
    }
    
    public void runTest() throws IOException {
    	
    	createNewLogs();
    	
    	countAllPerStation.clear();
    	countAllWrittenPerStation.clear();
    	
    	 testSet("file:///C:/Users/IBM_ADMIN/Desktop/ocpp_neural/OCPP_neural-network/OCPP_Parsing/ocpp_rest_result2.log");
    	
         countAllPerStation.remove("SERVER");
         countAllWrittenPerStation.remove("SERVER");
         
         List<String> allStations = new ArrayList<String>();
         for(String station : countAllPerStation.keySet()) {
        	 allStations.add(station);
         }
         
         
         String pickedStation = "RENOVA_0007";
         
         for(String station : allStations) {
        	 if(!station.equals(pickedStation)) {
        		 countAllPerStation.remove(station);
                 countAllWrittenPerStation.remove(station);
        	 }
         }
       
         //System.out.println("count: " + count_BMX_hearbeats);
         
         //countAllPerStation.remove("LMS-11702190");
         //countAllWrittenPerStation.remove("LMS-11702190");
         
         
         int TP_TN = 0;
         int TOTAL = 0;
         
         HashMap<String, Integer> truePositive = new HashMap<String, Integer>();
         for(String station : countAllPerStation.keySet()) {
         	truePositive.put(station, countAllPerStation.get(station));
         }
         
         truePositive.put("RENOVA_0007", 0);
       //  truePositive.put("BMX", 20);
         //truePositive.put("RENOVA_0013", 91);
         
        // System.out.println("Results: ");
         for(String station : countAllPerStation.keySet()) {
             if(countAllWrittenPerStation.containsKey(station)) {
             	TOTAL += countAllPerStation.get(station);
             	TP_TN += countAllPerStation.get(station) - Math.abs(countAllWrittenPerStation.get(station) - truePositive.get(station));
             //    System.out.println(station + ": " + ((double) countAllWrittenPerStation.get(station) / countAllPerStation.get(station)) + " " + countAllWrittenPerStation.get(station) + " / " + countAllPerStation.get(station));
             }
         }
       
         
         if(((double) TP_TN / TOTAL) > best_accuracy) {
        	 best_accuracy = (double) TP_TN / TOTAL;
        	 best_threshold = Constants.THRESHOLD;
         }
         
        /* if(TOTAL == 0) {
         	 System.out.println(Constants.THRESHOLD + " " + 0);
         } else {
        	 System.out.println(Constants.THRESHOLD + " " + ((double) TP_TN / TOTAL));
         }*/
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
    	/*
    	for(int i = 1; i <= 10; i++) {
            for(int j = 0; j < Utils.repetitiveTypes.length; j++) {
            	for(int k = 0; k < Utils.allRequestTypes.length; k++) {
            		NeuralNetInput netInput = new NeuralNetInput();
            		 netInput.setLastTimeRequestPerc((double) 1 / i);
                     netInput.setLastTimeStationRequestPerc((double) 1 / i);
                     
                     LinkedList<RequestType> requestSeq = new LinkedList<RequestType>();
                     requestSeq.add(Utils.repetitiveTypes[j]);
                     requestSeq.add(Utils.repetitiveTypes[j]);
                     requestSeq.add(Utils.allRequestTypes[k]);
                     
                     netInput.setRequestSequence(requestSeq);
                     netInput.setTarget(1);
                     //System.out.println("1:" + netInput);
                     neuralNetwork.processNode(netInput);
                     
                     
                     netInput.setLastTimeRequestPerc((double) 1 / i);
                     netInput.setLastTimeStationRequestPerc((double) 1 / i);
                     
                     requestSeq = new LinkedList<RequestType>();
                     requestSeq.add(Utils.allRequestTypes[k]);
                     requestSeq.add(Utils.repetitiveTypes[j]);
                     requestSeq.add(Utils.repetitiveTypes[j]);
                     
                     netInput.setRequestSequence(requestSeq);
                     netInput.setTarget(1);
                     //System.out.println("1:" + netInput);
                     neuralNetwork.processNode(netInput);
            	}
            }
    	}
        */
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
    	long _15_min = 15 * 60 * 1000;
        long min = 1486677962000l;
        long max = 1486763598000l;
        
        int[] trafficTimeFrame = new int[96];
        
        while(!logReader.getOcppLogPairs().isEmpty()) {
            
            
            RequestResponsePair currentPair = logReader.getOcppLogPairs().removeLast();
            if(!isLearning /*&& currentPair.getRequest().getStationId().equals(chargePoint)*/)  {
            	if(currentPair.getRequest().getStationId().equals(chargePoint)) {
            		if(min > currentPair.getRequest().getTime() ) { 
            			min = currentPair.getRequest().getTime();
            		} 
            		if(max < currentPair.getRequest().getTime() ) { 
            			max = currentPair.getRequest().getTime();
            		} 
            		int index = (int) ((currentPair.getRequest().getTime() - min) / _15_min);
            		trafficTimeFrame[index]++;
            		//System.out.println(currentPair.getRequest().getTime() - min);
            	}
            	/*if(currentPair.getRequest().getStationId().equals("RENOVA_0013") && currentPair.getRequest().getRequestType().equals(RequestType.Heartbeat)) {
            		count_BMX_hearbeats++;
            	}*/
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
                 //   	System.out.println(lastRequestResponsePair.getRequest().getStationId() + ": " + lastRequestResponsePair.getRequest().getRequestType().getValue() + " " + neuralNetInput.getTarget());
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
        
        for(int i = 0; i < trafficTimeFrame.length; i++) {
        	System.out.println((_15_min * i) + " " + trafficTimeFrame[i]);
        }
        
   //     neuralNetwork.printCurrentState();
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
