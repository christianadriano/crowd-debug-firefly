package edu.uci.ics.sdcl.firefly;

import java.io.Serializable;
import java.util.List;

import org.eclipse.jdt.core.dom.Expression;

public class MyMethodCall extends CodeElement implements Serializable
{
	private String name;
	private String expression;
	private String parameterList;
	
	public MyMethodCall(String nameArg, String expressionArg, String parametersArg, 
			Integer elementStartingLineArg, Integer elementStartingColumnArg,
			Integer elementEndingLineArg, Integer elementEndingColumnArg)
	{
		super(CodeElement.METHOD_INVOCATION, elementStartingLineArg, elementStartingColumnArg,
				elementEndingLineArg, elementEndingColumnArg);
		this.setName(nameArg);
		this.setExpression(expressionArg);
		this.parameterList = parametersArg;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getExpression()
	{
		return expression;
	}

	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	public String getParameterList()
	{
		return parameterList;
	}

	public void setParameterList(String parametersArg)
	{
		this.parameterList = parametersArg;
	}

} // test