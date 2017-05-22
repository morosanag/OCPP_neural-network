/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.general;

/**
 *
 * @author gabi
 */
public class Constants {
    
    public static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
    public static final String STATION_PATTERN = "[a-zA-Z0-9_-]+ ->";
    public static final String MESSAGE_PATTERN = "\\[.+\\]";
    public static final String MESSAGE_CONTENT_PATTERN = "\\{.*\\}";
    
    public static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    
    // only for LMS-11702190
    //public static double THRESHOLD = 0.31;
    
    public static double THRESHOLD = 0.8;
}
