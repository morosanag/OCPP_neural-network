/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.parsing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author gabi
 */
public class LogWriter {
    
    private static final String ORIGINAL_FILENAME = "http://serverstore.ro/OCPP_Logs/ocpp_rest_02-10-2017.log";
    private static final String BOT_FILENAME = "file:///var/www/serverstore.ro/web/OCPP_Logs/ocpp_rest.log";
    private static final String RESULT_FILENAME = "/var/www/serverstore.ro/web/OCPP_Logs/ocpp_rest_result2.log";
    private static final int BOT_STEP = 10;
    
    private List<String> originalLog = new ArrayList<String>();
    private List<String> botLog = new ArrayList<String>();
    
    public List<String> getOriginalLog() {
        return originalLog;
    }

    public void setOriginalLog(List<String> originalLog) {
        this.originalLog = originalLog;
    }

    public List<String> getBotLog() {
        return botLog;
    }

    public void setBotLog(List<String> botLog) {
        this.botLog = botLog;
    }

    public static void main(String [] args) throws IOException {
        LogWriter logWriter = new LogWriter();
        LogReader logReader = new LogReader();
        
        logWriter.setOriginalLog(logReader.readFromLink(ORIGINAL_FILENAME));
        logWriter.setBotLog(logReader.readFromLink(BOT_FILENAME));
        
        System.out.println(logWriter.getOriginalLog().size());
        System.out.println(logWriter.getBotLog().size());
        
        logWriter.writeToFile(logWriter.mergeLists(
                logWriter.getOriginalLog(),
                logWriter.getBotLog()),
                RESULT_FILENAME);
        
    }
    
    public List<String> mergeLists(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<String>();
        Iterator<String> iterList1 = list1.iterator();
        Iterator<String> iterList2 = list2.iterator();
        
        while(iterList1.hasNext()) {
            String row1 = iterList1.next();
            if(!iterList1.hasNext()) {
                return result;
            }
            String row2 = iterList1.next();
            
            result.add(row1);
            result.add(row2);
            
            int rand = (int)(Math.random() * BOT_STEP);
            if(rand == 0) {
                String rowBot1, rowBot2;
                
                if(!iterList2.hasNext()) {
                    return result;
                }
                rowBot1 = iterList2.next();
                
                if(!iterList2.hasNext()) {
                    return result;
                }
                rowBot2 = iterList2.next();
                
                String dateToReplace1 = extractDate(row1);
                System.out.println(dateToReplace1);
                
                String dateToReplace2 = extractDate(row2);
                System.out.println(dateToReplace2);
                
                String dateToBeReplace = extractDate(rowBot1);
                System.out.println(dateToBeReplace);
                
                String dateToBeReplace2 = extractDate(rowBot2);
                System.out.println(dateToBeReplace2);
                result.add(rowBot1.replace(dateToBeReplace, dateToReplace1));
                result.add(rowBot2.replace(dateToBeReplace2, dateToReplace2));
            }
        }
        
        return result;
    }
    
    public String extractDate(String row) {
        String[] words = row.split("\t");
        return words[0];
    }
    
    
    public void writeToFile(List<String> lines, String filename) {
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            
            for(String line : lines) {
                if(!line.contains("SERVER -> SERVER")) {
                    bw.write(line + "\n");
                }
            }
            
	} catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
}
