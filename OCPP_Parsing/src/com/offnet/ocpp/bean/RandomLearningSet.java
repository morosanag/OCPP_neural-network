package com.offnet.ocpp.bean;

public class RandomLearningSet {

	public int i, j, k, l;

	public RandomLearningSet(int i, int j, int k, int l) {
		super();
		this.i = i;
		this.j = j;
		this.k = k;
		this.l = l;
	}

	@Override
	public String toString() {
		return i + ", " + j + ", " + k + ", " + l;
	}
	
	

}
