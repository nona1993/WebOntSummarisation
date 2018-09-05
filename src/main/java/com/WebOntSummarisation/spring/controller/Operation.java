package com.WebOntSummarisation.spring.controller;

import java.util.Collections;
import java.util.List;

public class Operation {
	private List<String> symbols = Collections.emptyList();
	private String forgetting="forgetting";
	private String method="alch";
	//private String representation;
	//private boolean dis;
	//private String approximateFixPoint;
	
	public List<String> getSymbols() {
		return symbols;
	}

	public void setSymbols(List<String> symbols) {
		this.symbols = symbols;
	}

	public String getForgetting() {
		return forgetting;
	}

	public void setForgetting(String forgetting) {
		this.forgetting = forgetting;
	}

	//public String getRepresentation() {
	//	return representation;
//	}

	//public void setRepresentation(String representation) {
	//	this.representation = representation;
	//}

	//public boolean isDis() {
	//	return dis;
	//}

//	public void setDis(boolean dis) {
//		this.dis = dis;
//	}
//
//	public String getApproximateFixPoint() {
//		return approximateFixPoint;
//	}
//
//	public void setApproximateFixPoint(String approximateFixPoint) {
//		this.approximateFixPoint = approximateFixPoint;
//	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}
