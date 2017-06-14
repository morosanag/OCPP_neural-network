package com.offnet.ocpp.network;

import java.util.HashMap;
import java.util.LinkedList;

import com.offnet.ocpp.general.RequestType;

public class PermutationsTest {
/*
	public static final int size = 3;
	public static final int no_requests = 7;
	public static double current_index = 0;
	public static final double step = 1 / (double)(factorial(no_requests) / factorial(no_requests - size));
	
	static class Value {
		private double index;
		private double target;
		public double getIndex() {
			return index;
		}
		public void setIndex(double index) {
			this.index = index;
		}
		public double getTarget() {
			return target;
		}
		public void setTarget(double target) {
			this.target = target;
		}
	}
	
	public static HashMap<LinkedList<RequestType>, Value> map = null;
	
	public static final LinkedList<RequestType> list = new LinkedList<RequestType>();
	
	/*public static void main(String [] args) {
		list.add(RequestType.Authorize);
		list.add(RequestType.BootNotification);
		list.add(RequestType.Heartbeat);
		list.add(RequestType.MeterValues);
		list.add(RequestType.StartTransaction);
		list.add(RequestType.StopTransaction);
		list.add(RequestType.StatusNotification);
		
		System.out.println(step);
		
		permutations(new LinkedList<RequestType>(), list);
	}
	
	 static int factorial(int n){    
		  if (n == 0)    
		    return 1;    
		  else    
		    return(n * factorial(n-1));    
		 }    

	public static RequestType[][] requestTypesBad = {{RequestType.StopTransaction, RequestType.Authorize},
			{RequestType.Authorize, RequestType.MeterValues},
			{RequestType.MeterValues, RequestType.StartTransaction},
			{RequestType.StopTransaction, RequestType.MeterValues},
			{RequestType.Authorize, RequestType.BootNotification},
			{RequestType.StartTransaction, RequestType.StartTransaction},
			{RequestType.StartTransaction, RequestType.MeterValues},
			{RequestType.StopTransaction, RequestType.StopTransaction},
			{RequestType.MeterValues, RequestType.MeterValues},
			{RequestType.StartTransaction, RequestType.StopTransaction},
			{RequestType.StopTransaction, RequestType.StartTransaction}};
	 
	public static boolean ibBadTraffic(LinkedList<RequestType> list, int index) {
		
		for(int i = 0; i < requestTypesBad.length; i++) {
			if(list.get(index).equals(requestTypesBad[i][0]) && list.get(index + 1).equals(requestTypesBad[i][1])) {
				return true;
			}
			
		}
		
		if(list.get(index).equals(RequestType.BootNotification) && !list.get(index + 1).equals(RequestType.StatusNotification)) {
			return true;
		}
		
		return false;
		
	}
	
	public static boolean ibBadTraffic(LinkedList<RequestType> list) {
		
		return ibBadTraffic(list, 0) || ibBadTraffic(list, 1);
		
	}
	
	public static void permutations(LinkedList<RequestType> current, LinkedList<RequestType> others) {
		if(current.size() == 3) {
			Value value = new Value();
			value.setIndex(current_index += step);
			value.setTarget(ibBadTraffic(current) ? 0 : 1);
			map.put(current, value);
			
			return;
		}
		
		for(int i = 0; i < others.size(); i++) {
			LinkedList<RequestType> othersTemp = (LinkedList<RequestType>) others.clone();
			LinkedList<RequestType> currentTemp = (LinkedList<RequestType>) current.clone();
			RequestType requestType = othersTemp.get(i);
			othersTemp.remove(i);
			currentTemp.add(requestType);
			permutations(currentTemp, othersTemp);
		}
		
		
	}
	
	public void populateMap() {
		map = new HashMap<LinkedList<RequestType>, Value>();
		
		
	}
	
        
        public static String[] arr = { "BootNotification",
"StartTransaction",
"Authorize",
"Heartbeat",
"Authorize",
"StartTransaction",
"MeterValues",
"Authorize",
"StartTransaction",
"MeterValues",
"StartTransaction",
"Heartbeat",
"StopTransaction",
"MeterValues",
"BootNotification",
"Heartbeat",
"Heartbeat",
"Heartbeat",
"StartTransaction",
"StopTransaction",
"BootNotification",
"StopTransaction",
"StatusNotification",
"Authorize",
"MeterValues",
"StopTransaction",
"Authorize",
"BootNotification",
"Authorize",
"StopTransaction",
"BootNotification",
"StopTransaction",
"StartTransaction",
"StartTransaction",
"BootNotification",
"MeterValues",
"BootNotification",
"StatusNotification",
"StopTransaction",
"StopTransaction",
"Heartbeat",
"BootNotification",
"Heartbeat",
"Authorize",
"StopTransaction",
"MeterValues",
"StartTransaction",
"StartTransaction",
"Authorize",
"Heartbeat",
"MeterValues",
"MeterValues",
"StopTransaction",
"MeterValues",
"StopTransaction",
"StartTransaction",
"Heartbeat",
"Heartbeat",
"MeterValues",
"Authorize",
"MeterValues",
"StopTransaction",
"Authorize",
"StartTransaction",
"StopTransaction",
"Authorize",
"MeterValues",
"StatusNotification",
"StopTransaction",
"StartTransaction",
"StartTransaction",
"MeterValues",
"Heartbeat",
"StopTransaction",
"StopTransaction",
"Authorize",
"StartTransaction",
"MeterValues",
"MeterValues",
"StartTransaction",
"StartTransaction",
"Authorize",
"Heartbeat",
"StartTransaction",
"StopTransaction",
"StopTransaction",
"MeterValues",
"StopTransaction",
"StartTransaction",
"Authorize",
"Authorize",
"MeterValues",
"BootNotification",
"Authorize",
"BootNotification",
"StatusNotification",
"MeterValues",
"StatusNotification",
"BootNotification",
"Authorize",
"StopTransaction",
"StartTransaction",
"BootNotification",
"StatusNotification",
"StopTransaction",
"Authorize",
"StopTransaction",
"StopTransaction",
"Authorize",
"StartTransaction",
"StatusNotification",
"Heartbeat",
"Authorize",
"BootNotification",
"StartTransaction",
"BootNotification",
"StatusNotification",
"BootNotification",
"MeterValues",
"MeterValues",
"StopTransaction",
"StatusNotification",
"StartTransaction",
"StopTransaction",
"Authorize",
"Authorize",
"Heartbeat",
"Authorize",
"StartTransaction",
"MeterValues",
"StatusNotification",
"StopTransaction",
"StatusNotification",
"Heartbeat",
"Heartbeat",
"StartTransaction",
"MeterValues",
"Authorize",
"Authorize",
"StartTransaction",
"MeterValues",
"MeterValues",
"Authorize",
"StartTransaction",
"StopTransaction",
"StopTransaction",
"StartTransaction",
"StartTransaction",
"StartTransaction",
"StartTransaction",
"StartTransaction",
"Heartbeat",
"Authorize",
"StartTransaction",
"StopTransaction",
"StatusNotification",
"StartTransaction",
"Heartbeat",
"Authorize",
"MeterValues",
"StatusNotification",
"StopTransaction",
"StatusNotification",
"MeterValues",
"StopTransaction",
"StartTransaction",
"StopTransaction",
"StatusNotification",
"StopTransaction",
"StartTransaction",
"StatusNotification",
"Authorize",
"StartTransaction",
"StartTransaction",
"Heartbeat",
"Authorize",
"StatusNotification",
"MeterValues",
"MeterValues",
"MeterValues",
"StatusNotification",
"StopTransaction",
"StopTransaction",
"StartTransaction",
"StartTransaction",
"Heartbeat",
"Authorize"};
        
        
        public static void check() {
            int count = 0;
            for(int i = 1; i < arr.length; i++) {
                for(int j = 0; j < requestTypesBad.length; j++) {
                    if((arr[i - 1].equalsIgnoreCase(requestTypesBad[j][0].toString()) && arr[i].equalsIgnoreCase(requestTypesBad[j][1].toString()))) {
                        count++;
                    }
                }
                    
             
            }
            System.out.println(arr.length - count);
        }
        
        public static void main(String [] args) {
            check();
        }
	*/
}
