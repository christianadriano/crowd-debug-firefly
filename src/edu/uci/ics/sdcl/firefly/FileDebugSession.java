package edu.uci.ics.sdcl.firefly;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Class to persist the information of debugging one file
 * 
 * @author Christian Adriano
 *
 */
public class FileDebugSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fileName;
	
	private HashMap<Integer, Microtask> microtaskMap;
	
	private Integer maximumAnswerCount; 
	
	public FileDebugSession(String fileName, HashMap<Integer,Microtask> microtaskMap){
		this.microtaskMap =  microtaskMap;
		this.fileName = fileName;
	}

	public HashMap<Integer, Microtask> getMicrotaskMap() {
		return microtaskMap;
	}

	public void setMicrotaskMap(HashMap<Integer, Microtask> microtaskMap) {
		this.microtaskMap = microtaskMap;
	}

	public String getFileName() {
		return fileName;
	}
	
	public Integer getMaximumAnswerCount() {
		return maximumAnswerCount;
	}
	
	/** 
	 * If the provided number is smaller then maximumAnswerCount, then 
	 * increment maximumAnswerCount, otherwise, ignore 
	 * @param microtaskAnswersCount the total number of answers a certain microtask
	 *  received (within this debugging session)
	 *  @return true if maximumAnswerCount was actually incremented, otherwise false
	 */
	public boolean incrementAnswersReceived(int microtaskAnswersCount){
		if((maximumAnswerCount==null) || (maximumAnswerCount.intValue()<microtaskAnswersCount)){
			this.maximumAnswerCount = new Integer(microtaskAnswersCount);
			return true;
		}
		else
			return false;
	}
	
	
	
}