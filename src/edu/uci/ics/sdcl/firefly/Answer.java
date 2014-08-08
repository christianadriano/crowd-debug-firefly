package edu.uci.ics.sdcl.firefly;

import java.io.Serializable;

public class Answer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String YES= "YES";
	public static final String PROBABLY_YES= "PROBABLY_YES";
	public static final String I_CANT_TELL= "I_CANT_TELL";
	public static final String PROBABLY_NOT= "PROBABLY_NOT";
	public static final String NO= "NO";
	public static final String SKIPPED = "SKIPPED";
	
	private String option;
	private String explanation;
	
	public Answer(String option, String explanation){
		this.option = option;
		this.explanation = explanation;
	}

	public String getOption() {
		return option;
	}

	public String getExplanation() {
		return explanation;
	}
	
	public static String mapToString(int number){
		switch(number){
		case 1: return Answer.YES; 
		case 2: return Answer.PROBABLY_YES; 
		case 3: return Answer.I_CANT_TELL; 		
		case 4: return Answer.PROBABLY_NOT; 
		case 5: return Answer.NO; 
		case 6: return Answer.SKIPPED;
		default: return null;
		}
	}

	public static int mapNumber(String option){
		switch(option){
		case  Answer.YES: return 1; 
		case Answer.PROBABLY_YES: return 2; 
		case Answer.I_CANT_TELL: return 3; 		
		case Answer.PROBABLY_NOT: return 4; 
		case Answer.NO: return 5; 
		case Answer.SKIPPED: return 6;
		default: return 0;
		}
	}
	
	
}
