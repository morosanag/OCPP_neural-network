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
public enum RequestType {
 
    Authorize ("Authorize", 1.0),
    StartTransaction ("StartTransaction", 0.5),
    StopTransaction("StopTransaction", 0.8),
    Heartbeat("Heartbeat", 0.3),
    HeartBeat("Heartbeat", 0.3),
    MeterValues("MeterValues", 0.7),
    BootNotification("BootNotification", 0.4),
    StatusNotification("StatusNotification", 0.1),
    FirmwareStatusNotification("FirmwareStatusNotification", 0.2),
    DiagnosticsStatusNotification("DiagnosticsStatusNotification", 0.6),
    DataTransfer("DataTransfer", 0.9);
    /*UnlockConnector("UnlockConnector"),
    Reset("Reset"),
    ChangeAvailability("ChangeAvailability"),
    GetDiagnostics("GetDiagnostics"),
    ClearCache("ClearCache"),
    UpdateFirmware("UpdateFirmware"),
    ChangeConfiguration("ChangeConfiguration"),
    RemoteStartTransaction("RemoteStartTransaction"),
    RemoteStopTransaction("RemoteStopTransaction"),
    CancelReservation("CancelReservation"),
    GetConfiguration("GetConfiguration"),
    GetLocalListVersion("GetLocalListVersion"),
    ReserveNow("ReserveNow"),
    SendLocalList("SendLocalList")*/;
    
    private String value;
    private double priority;
	
    private RequestType(String value, double priority){
	this.value = value;
        this.priority = priority;
    }
	
    public String getValue(){
	return this.value;
    }

    public double getPriority() {
        return priority;
    }    
}
