package test;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.sdcl.firefly.CodeElement;
import edu.uci.ics.sdcl.firefly.CodeSnippet;
import edu.uci.ics.sdcl.firefly.FileDebugSession;
import edu.uci.ics.sdcl.firefly.MethodParameter;
import edu.uci.ics.sdcl.firefly.MethodSignature;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.memento.MicrotaskMemento;

public class MicrotaskMementoTest {

	private  HashMap<String, FileDebugSession> debugSessionMap = new HashMap<String, FileDebugSession>();
	private HashMap<Integer, Microtask> microtaskMap;
	private String fileName = "SimpleSampleCode.java";

	@Before
	public void setUp() throws Exception {
		MethodSignature signature = new MethodSignature("factorial", "public", new Integer(12));
		MethodParameter arg1, arg2;
		arg1 = new MethodParameter("Integer", "Seed");
		arg2 = new MethodParameter("Integer", "Iterations");
		signature.addMethodParameters(arg1);
		signature.addMethodParameters(arg2);

		StringBuffer buffer = new StringBuffer("public Integer factorial(Integer Seed, Integer Iterations){");
		buffer.append("\n");
		buffer.append("if(Seed!=null){");
		buffer.append("\n");
		buffer.append("int aux=1;");
		buffer.append("\n");
		buffer.append("for (int i=0;i<Iterations.intValue();i++){");
		buffer.append("\n");
		buffer.append("aux =  aux * Seed;");
		buffer.append("\n");
		buffer.append("	}");
		buffer.append("\n");
		buffer.append("	return new Integer(aux);");
		buffer.append("\n");
		buffer.append("}");
		buffer.append("\n");
		buffer.append("else return null;");
		buffer.append("\n");
		buffer.append("}");

		String questionArg = "Is there maybe something wrong in the declaration of function 'factorial' at line 12 " 
				+ "(e.g., requires a parameter that is not listed, needs different parameters to produce the correct result, specifies the wrong or no return type, etc .)?";
		
		CodeSnippet codeSnippetFactorial=new CodeSnippet("sample","SimpleSampleCode", buffer.toString(), new Integer (1),
				new Boolean (true), signature);
		Microtask mtask = new Microtask(CodeElement.METHOD_DECLARARION, codeSnippetFactorial, questionArg, new Integer(1));

		//Create the data structure
		this.microtaskMap =  new HashMap<Integer,Microtask>();
		microtaskMap.put(new Integer(1),mtask);
		FileDebugSession debugMap = new FileDebugSession(fileName,microtaskMap);
		
		this.debugSessionMap.put(fileName, debugMap);
	}

	@Test
	public void testCreateNewPersistentFile() {

		MicrotaskMemento memento = new MicrotaskMemento();
		memento.insert(fileName, this.debugSessionMap.get(fileName));

		 FileDebugSession debugMap = memento.read(fileName);
		 if((debugMap!=null) && (debugMap.getMicrotaskMap()!=null)){
			HashMap<Integer, Microtask> mMap = debugMap.getMicrotaskMap();
			Integer key = new Integer (1);
			Microtask expectedTask = this.microtaskMap.get(key);
			Microtask actualTask = mMap.get(key);
			
			assertEquals(expectedTask.getMethod().getClassName(),actualTask.getMethod().getClassName().toString());
		}
		else
			fail("review test setup");
	}

	@Test
	public void testRemoveDebugSession() {
		MicrotaskMemento memento = new MicrotaskMemento();
		memento.insert(fileName, this.debugSessionMap.get(fileName));
		
		 memento.remove(fileName);
		 FileDebugSession debugMap = memento.read(fileName);
		 assertNull(debugMap);
	}
}