/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offnet.ocpp.bean;

/**
 *
 * @author gabi
 */
public class OcppPairComparator {
    
    private RequestResponsePair requestResponsePair1;
    private RequestResponsePair requestResponsePair2;
    
    private static final long maxTimeDiffInMillis = 30 * 1000;
    
    public OcppPairComparator(RequestResponsePair requestResponsePair1, RequestResponsePair requestResponsePair2) {
        this.requestResponsePair1 = requestResponsePair1;
        this.requestResponsePair2 = requestResponsePair2;
    }
    
    private int computeEditDistance(String word1, String word2) {
	int len1 = word1.length();
	int len2 = word2.length();
 
	// len1+1, len2+1, because finally return dp[len1][len2]
	int[][] dp = new int[len1 + 1][len2 + 1];
 
	for (int i = 0; i <= len1; i++) {
		dp[i][0] = i;
	}
 
	for (int j = 0; j <= len2; j++) {
		dp[0][j] = j;
	}
 
	//iterate though, and check last char
	for (int i = 0; i < len1; i++) {
		char c1 = word1.charAt(i);
		for (int j = 0; j < len2; j++) {
			char c2 = word2.charAt(j);
 
			//if last two chars equal
			if (c1 == c2) {
				//update dp value for +1 length
				dp[i + 1][j + 1] = dp[i][j];
			} else {
				int replace = dp[i][j] + 1;
				int insert = dp[i][j + 1] + 1;
				int delete = dp[i + 1][j] + 1;
 
				int min = replace > insert ? insert : replace;
				min = delete > min ? min : delete;
				dp[i + 1][j + 1] = min;
			}
		}
	}
 
	return dp[len1][len2];
    }
    
    private double getStringSimilarity(OcppMessage message1, OcppMessage message2) {
        if(message1 == null || message2 == null || message1.getMessageContent() == null || message2.getMessageContent() == null) {
            return 0;
        }
        String str1 = message1.getMessageContent().toString();
        String str2 = message2.getMessageContent().toString();
        
        int editDistance = computeEditDistance(str1, str2);
        int maxLength = Math.max(str1.length(), str2.length());
        
        return ((double) editDistance) / maxLength;
    }
    
    public double getSimilarityPercentage() {
        double diffPercRequest = getStringSimilarity(requestResponsePair1.getRequest(), requestResponsePair2.getRequest());
        double diffPercResponse = getStringSimilarity(requestResponsePair1.getResponse(), requestResponsePair2.getResponse());
        
        double length_prop = 0;
        
        try {
        
        length_prop = (double)(requestResponsePair1.getRequest().getMessageContent().toString().length() + 
                requestResponsePair2.getRequest().getMessageContent().toString().length() + 
                requestResponsePair1.getResponse().getMessageContent().toString().length() + 
                requestResponsePair2.getResponse().getMessageContent().toString().length()) / 400; 
                
        } catch (NullPointerException ex) {
            return 0;
        }
        
       // double diffPercTime = Math.abs(requestResponsePair1.getRequest().getTime() - requestResponsePair2.getRequest().getTime()) / maxTimeDiffInMillis;
       // diffPercTime = diffPercTime > 1 ? 1 : diffPercTime;
        
       
       
        return (diffPercRequest + diffPercResponse)  / length_prop;
    }
    
}
