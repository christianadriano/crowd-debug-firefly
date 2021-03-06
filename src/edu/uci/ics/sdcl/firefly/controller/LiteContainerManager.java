package edu.uci.ics.sdcl.firefly.controller;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.WorkerSession;
import edu.uci.ics.sdcl.firefly.servlet.SkillTestServlet;
import edu.uci.ics.sdcl.firefly.storage.WorkerSessionStorage;

public class LiteContainerManager extends StorageStrategy{

	/** Table with Session ID and session */
	private Hashtable<String, WorkerSession> activeSessionTable;

	/** Vector of closed Sessions */
	private Vector<WorkerSession> closedSessionVector;

	/** List of available sessions */
	private Stack<WorkerSession> newSessionStack;

	/** Keeps track of Workers*/
	private Hashtable<String, Worker> workerTable;

	/** Logger for register consent, skilltest, and survey */
	private Logger consentLogger;

	/** Logger for sessions and microtasks  */
	private Logger sessionLogger;


	private static LiteContainerManager container;

	public synchronized static LiteContainerManager initializeSingleton(){
		if(container == null)
			container = new LiteContainerManager();
		return container;
	}

	public void cleanUpRepositories(){
		StorageManager storageManager = new StorageManager();
		storageManager.cleanUpRepositories();
		container = new LiteContainerManager();		
	}

	private LiteContainerManager(){
		//Initialize Sessions
		WorkerSessionStorage storage = WorkerSessionStorage.initializeSingleton();
		newSessionStack = storage.retrieveNewSessionStorage();
		activeSessionTable = new Hashtable<String, WorkerSession>();
		closedSessionVector = new Vector<WorkerSession>();

		//Initialize Worker table
		workerTable = new Hashtable<String, Worker>();

		//Initialize Loggers
		consentLogger = LoggerFactory.getLogger("consent");
		sessionLogger = LoggerFactory.getLogger("session");
	}

	public synchronized  Microtask getNextMicrotask(String sessionId){
		if(this.activeSessionTable.containsKey(sessionId)){
			WorkerSession session = this.activeSessionTable.get(sessionId);	
			return session.getCurrentMicrotask();
		}
		else return null;
	}

	/** 
	 * @param workerId is used to associate the WorkerSession with a unique anonymous worker
	 * @param hitIT is used to associate the WorkerSession with the Mechanical Turk HIT
	 * @return a new session, if there aren't new sessions available return null
	 */
	public synchronized WorkerSession readNewSession(String workerId){	
		WorkerSession session;
		if(!newSessionStack.isEmpty()){
			session = this.newSessionStack.pop();
			session.setWorkerId(workerId);
			Worker worker = this.workerTable.get(workerId);
			sessionLogger.info("EVENT%OPEN SESSION% workerId%"+ workerId+"% sessionId%"+ session.getId());
			worker.setSessionId(session.getId());
			this.workerTable.put(workerId, worker);
			this.activeSessionTable.put(session.getId(), session); 
			return session;
		}
		else
			return null;
	}

	public synchronized boolean areThereMicrotasksAvailable(){
		return !newSessionStack.isEmpty();
	}


	/**
	 * @return true if all three operations succeeded, false if any of them failed.
	 */
	public synchronized boolean updateMicrotaskAnswer(String sessionId, Integer microtaskId, Answer answer){
		//set answer to the microtask in the WorkerSession 
		WorkerSession session = this.activeSessionTable.get(sessionId);

		if(session!=null){
			session.insertMicrotaskAnswer(microtaskId,answer); 

			Microtask microtask = session.getPreviousMicrotask();
			
			String explanation = answer.getExplanation().replaceAll("[\n]"," ").replaceAll("[\r]"," ");
			
			sessionLogger.info("EVENT%MICROTASK% workerId%"+ answer.getWorkerId()+"% sessionId%"+ sessionId+
					"% microtaskId%"+microtaskId+"% fileName%"+microtask.getFileName()+
					"% question%"+ microtask.getQuestion()+"% answer%"+answer.getOption()+
					"% duration%"+answer.getElapsedTime()+"% explanation%"+explanation);

			if(session.isClosed()){//Move session to closed //EVENT
				this.closedSessionVector.add(session);
				sessionLogger.info("EVENT%CLOSE SESSION% workerId%"+ session.getWorkerId()+"% sessionId%"+ session.getId());
				this.activeSessionTable.remove(session.getId());
			}
			return true;	
		} 
		else
			return false;
	}

	public synchronized String getSessionIdForWorker(String workerId) {
		return workerTable.get(workerId).getSessionId();
	}


	public synchronized boolean insertSkillTest(Worker worker) {
		if(worker!=null){
			consentLogger.info("EVENT%SKILLTEST% workerId%"+worker.getWorkerId()
					+"% test1%"+worker.getGradeMap().get(SkillTestServlet.QUESTION1)
					+"% test2%"+worker.getGradeMap().get(SkillTestServlet.QUESTION2)
					+"% test3%"+worker.getGradeMap().get(SkillTestServlet.QUESTION3)
					+"% test4%"+worker.getGradeMap().get(SkillTestServlet.QUESTION4)
					+"% grade%"+worker.getGrade()
					+"% testDuration%"+worker.getSkillTestDuration());
			this.workerTable.put(worker.getWorkerId(), worker);
			return true;
		}
		else{
			consentLogger.error("EVENT%ERROR% could not store worker SKILL TEST.");
			return false;
		}
	}

	public synchronized Worker insertConsent(String consentDateStr) {
		Worker worker = new Worker(new Integer(this.workerTable.size()).toString(),consentDateStr);
			consentLogger.info("EVENT%CONSENT% workerId%"+worker.getWorkerId()+ "% consentDate%" + worker.getConsentDate().toString());	
			this.workerTable.put(worker.getWorkerId(), worker);
			return worker;
	}

	public synchronized boolean insertSurvey(Worker worker) {
		if(worker!=null){
			consentLogger.info("EVENT%SURVEY% workerId%"+worker.getWorkerId()+ "% sessionId%"+worker.getSessionId()+
					"% "+worker.getSurveyAnswersToString());
			return true;
		}
		else{
			consentLogger.error("EVENT%ERROR% could not store worker SURVEY.");
			return false;
		}
	}

	public synchronized Worker readExistingWorker(String workerId) {
		return this.workerTable.get(workerId);
	}
}
