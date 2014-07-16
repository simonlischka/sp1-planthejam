package de.bht.comanche.server;

import java.util.ArrayList;
import java.util.List;

import de.bht.comanche.logic.DbObject;

public class ResponseObject<E> {
	protected List<E> data;
	protected int responseCode;
	
	public ResponseObject() {
		responseCode = 200;
		data = new ArrayList<E>();
	}
	
	public void addData(E d) {
		data.add(d);
	}

	public void addAll(List<E> l) {
		data.addAll(l);
	}
	
	public List<E> getData() {
		return data;
	}
	
	public void setData(List<E> data) {
		this.data = data;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	public boolean hasError() {
		return responseCode != 200;
	}
}
