/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.offnet.ocpp.general.Constants;
import com.offnet.ocpp.bean.OcppMessage;
import com.offnet.ocpp.bean.RequestResponsePair;
import com.offnet.ocpp.general.RequestType;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gabi
 */
public class LogReader {
   
    private Map<String, OcppMessage> pendingRequests = new HashMap<String, OcppMessage>();
    private LinkedList<RequestResponsePair> ocppLogPairs = new LinkedList<RequestResponsePair>();
    private String urlString;
    private Set<String> stationIds = new HashSet<String>();
    
    public LogReader(String urlString) {
        this.urlString = urlString;
    }
    
    public LogReader() {
    }
    
    
    
    public static void main(String [] args) throws ParseException, JSONException, IOException {
        /*String line = "2017-01-03 13:47:20 	LMS-11702190 -> SERVER : [2,\"11939\",\"StartTransaction\",{\"timestamp\":\"2016-12-31T16:11:35+00:00\",\"connectorId\":1,\"meterStart\":0,\"idTag\":\"DCEAFEC9\"}]";
        String line2 = "2017-01-03 13:47:20 	SERVER -> LMS-11702190 : [3, \"11939\", {\"transactionId\":-1,\"idTagInfo\":{\"status\":\"ConcurrentTx\",\"expiryDate\":\"2017-10-10T12:00:00Z\",\"parentIdTag\":\"-1\"}}]";
        
        LogReader logReader = new LogReader();
        logReader.parseLine(line);
        logReader.parseLine(line2);
        */
        
        /*LogReader logReader = new LogReader();
                
        logReader.parseLineFromLink("http://serverstore.ro/OCPP_Logs/ocpp_rest_01-03-2017.log");
        System.out.println(logReader.pendingRequests.size());
        System.out.println(logReader.ocppLogPairs.size());
        
        System.out.println("DONE");
        */
    }
    
    public void read() {
        try {
        //    parseLineFromLink("http://serverstore.ro/OCPP_Logs/ocpp_rest_01-03-2017.log");
            parseLineFromLink(urlString);
        } catch (ParseException ex) {
            Logger.getLogger(LogReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LogReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(LogReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static long convertStringDateToLong(String string) throws ParseException {
        DateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH);
        Date date = format.parse(string);
        return date.getTime();
    }
    
    public List<String> readFromLink(String link) throws MalformedURLException, IOException {

        List<String> responseList = new ArrayList<String>();
        
        URL urlObj = new URL(link);
        URLConnection con = urlObj.openConnection();

        con.setDoOutput(true);
        con.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            responseList.add(inputLine);
        }

        in.close();
        
        return responseList;
    }
    
    public void parseLineFromLink(String link) throws ParseException, IOException, JSONException {
        List<String> linesList = readFromLink(link);
        for(String line : linesList) {
            parseLine(line);
        }
    }
    
    
    
    private void parseLine(String line) throws ParseException, JSONException {
        
        OcppMessage ocppMessage = new OcppMessage();
        
        // get time
        Pattern patternObj = Pattern.compile(Constants.DATE_PATTERN);
        Matcher matherObj = patternObj.matcher(line);
        
        if(matherObj.find()) {
            ocppMessage.setTime(convertStringDateToLong(matherObj.group(0)));
        }
        
        // get station
        patternObj = Pattern.compile(Constants.STATION_PATTERN);
        matherObj = patternObj.matcher(line);
        
        if(matherObj.find()) {
            ocppMessage.setStationId(matherObj.group(0).replace("->", "").trim());
        }
        stationIds.add(ocppMessage.getStationId());
        
        // get message type (request/response/error) + id + request type
        patternObj = Pattern.compile(Constants.MESSAGE_PATTERN);
        matherObj = patternObj.matcher(line);
        
        
        if(matherObj.find()) {
            String messageContent = matherObj.group(0);
            String[] messageFragments = messageContent.replace("[","").replace("]","").split(",");
            try {
                ocppMessage.setMessageType(Integer.parseInt(messageFragments[0].trim()));
            } catch (NumberFormatException ex) {
                return;
            }
            ocppMessage.setMessageId(messageFragments[1].trim().replace("\"", ""));
            
            if(ocppMessage.getMessageType() == 2) {
                try {
                    ocppMessage.setRequestType(RequestType.valueOf(messageFragments[2].replace("\"", "").trim()));
                } catch (IllegalArgumentException ex) {
                    return;
                }
            }
        }
        
        // get the message content
        
        patternObj = Pattern.compile(Constants.MESSAGE_CONTENT_PATTERN);
        matherObj = patternObj.matcher(line);
        
        if(matherObj.find()) {
        	//System.out.println(matherObj.group(0));
            ocppMessage.setMessageContent(new JSONObject(matherObj.group(0)));
        }
        
        // check if the message is in pending requests list
        if(pendingRequests.containsKey(ocppMessage.getMessageId())) {
            RequestResponsePair messagePair = new RequestResponsePair(pendingRequests.get(ocppMessage.getMessageId()), ocppMessage);
            ocppLogPairs.push(messagePair);
            pendingRequests.remove(ocppMessage.getMessageId());
        } else {
            pendingRequests.put(ocppMessage.getMessageId(), ocppMessage);
        }
    }
    
    public void clearPendingMap() {
        this.pendingRequests.clear();
    }

    
    public LinkedList<RequestResponsePair> getOcppLogPairs() {
        return ocppLogPairs;
    }

    public Set<String> getStationIds() {
        return stationIds;
    }

}
