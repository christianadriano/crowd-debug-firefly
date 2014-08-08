<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@ page
	import="edu.uci.ics.sdcl.firefly.*, java.util.*, java.util.Map.Entry"
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Firefly - Question-based Crowd Debugging</title>
<style type="text/css" media="screen">
#editor {
	position: relative;
	height: 200px;
	width: 680px;
}

.callers {
	position: absolute;
	background: rgba(27, 132, 249, 0.3);
	z-index: 20
}

.callees {
	position: absolute;
	background: rgba(27, 132, 249, 0.3);
	z-index: 20
}


#failurePrompt {
	max-width: 700px;
	background-color: #D1EEEE;
	 text-align: justify;
	 margin: 0 auto;
	 text-justify: distribute-all-lines;
}

#internalText {
	 margin-left: 10px;
	margin-right: 10px;
	
}

#questionPrompt {
	max-width: 700px;
	background-color: #B4CDCD;
	 margin: 0 auto;
	 text-align: justify;
	  text-justify: distribute-all-lines;
}

#questionCode {
	max-width: 700px;
	background-color: #D1EEEE;
	margin: 0 auto;
	 text-align: justify;
}

#buttons { 
background-color: #B4CDCD;
margin: 0 auto;
max-width: 700px;
}
 
#thumbs {
	width: 700px;
	background-color: #B4CDCD;
	text-align: justify;
	text-justify: distribute-all-lines;
	 margin: 0 auto;
}

#thumbs a {
	vertical-align: top;
	display: inline-block;
	*display: inline;
	zoom: 1;
}

.stretch {
	width: 100%;
	display: inline-block;
	font-size: 12;
	line-height: 0
}
</style>
</head>

<body>

	<script>
		function checkAnswer() {

			var radios = document.getElementsByName('answer');

			var option = -1;
			var i = 0;

			for (i = 0; i < radios.length; i++) {
				if (radios[i].checked) {
					option = i;
					break;
				}
			}

			if (option == -1) {
				alert("Please select an answer.");
				return -1;
			} else {
				if ((radios[0].checked) || (radios[1].checked)) {//yes and probably yes must provide an explanation
					if (document.getElementById("explanation").value == '') {
						alert("Please provide an explanation for your answer.");
						return -1;
					} else
						return option;
				} else
					return option;
			}
		}

		function submitAnswer() {
			var checked = checkAnswer();
			if (checked != -1) {
				var subAction = document.getElementById("subAction");
				subAction.value = "loadNext";
				document.forms["answerForm"].submit();
			} else {
				//nothing to do.
			}
		}

		function skipAnswer() {
			if(confirm('Confirm skipping this question ?')){
				var subAction = document.getElementById("subAction");
				subAction.value = "skip";
				document.forms["answerForm"].submit();				
			}
		}
		
	</script>

		<br>
		<!-- Hidden fields -->
		<input type="hidden" id="startLine" value=${requestScope["startLine"]}>
		<input type="hidden" id="startColumn" value=${requestScope["startColumn"]}> 
		<input type="hidden" id="endLine" value=${requestScope["endLine"]}> 
		<input type="hidden" id="endColumn" value=${requestScope["endColumn"]}>
		<input type="hidden" id="methodStartingLine" value=${requestScope["methodStartingLine"]}>
		<input type="hidden" id="positionsCaller" value=${requestScope["positionsCaller"]}>
		<input type="hidden" id="positionsCallee" value=${requestScope["positionsCallee"]}>
		<input type="hidden" id="calleesOnMain" value=${requestScope["calleesOnMain"]}>
		
 
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	<script
		src="https://rawgithub.com/ajaxorg/ace-builds/master/src-noconflict/ace.js"></script>

	<div id="failurePrompt"><br>
		 	<div id="internalText">
		 	<br>
			Thanks for using FireFly! By answering the question below, you will help us debug software from all over the world. 
   			The bug we specifically could use your help with today is the following: 
      	<b>${requestScope["bugReport"]}</b><br>
      	<br>
   		</div>
	</div>
	
	
	<div id="questionPrompt">
		<div id="internalText">
		<br>
		${requestScope["question"]}<br>
		</div>
	</div>

	
	<div id="thumbs">
		<div id="internalText">
		<br>
		<form name="answerForm" action="microtask" method="get">

			<a id="option1"> <input type="radio" name="answer" value="1">Yes</a> 
			<a id="option2"> <input type="radio" name="answer" value="2">Probably yes</a> 
			<a id="option3"> <input type="radio" name="answer" value="3">I can't tell</a> 
			<a id="option4"> <input type="radio" name="answer" value="4">Probably not</a> 
			<a id="option5"> <input type="radio" name="answer" value="5">No</a> 
			<span class="stretch"></span> 
			
			<!-- Hidden fields -->
			<input type="hidden" name="fileName" value=${requestScope["fileName"]}> 
			<input type="hidden" name="sessionId" value=${requestScope["sessionId"]}> 
			<input type="hidden" name="userId" value=${requestScope["userId"]}> 
			<input type="hidden" name="hitId" value=${requestScope["hitId"]}> 
			<input type="hidden" name="microtaskId" value=${requestScope["microtaskId"]}> 
			<input type="hidden" id="subAction" name="subAction" value=${requestScope["subAction"]}> 
			
			<center><br>Please provide an explanation for your answer: <br>
			<textarea name="explanation" id="explanation" rows="3" cols="82"></textarea>
			</center>
	 		<br>
			
		</form>
	 	</div>
	 </div>
	 
	 <div id="buttons">
	 	<center>
			<INPUT TYPE="button" VALUE="Skip this"	onclick="skipAnswer()"> 
			<INPUT TYPE="button" VALUE="Submit answer" onclick="submitAnswer(event)">
		</center>
		<br>
	</div>

	
	<div id="questionCode">
		<div id="internalText">
		<br> 
		<b>The source code:</b> 
		<div id="editor"><xmp>${requestScope["source"]}</xmp></div>
		
		<br>
		
		<div id="context"></div>
		
		<div id="editorCaller"><xmp>${requestScope["caller"]}</xmp></div>
		
		<div id="space"></div>
		
		<div id="editorCallee"><xmp>${requestScope["callee"]}</xmp></div>

		<script>
			/* First and main ACE Editor */
			var editor = ace.edit('editor');
			editor.setReadOnly(true);
			editor.setTheme("ace/theme/github");
			editor.getSession().setMode("ace/mode/java");
			editor.setBehavioursEnabled(false);
			editor.setOption("highlightActiveLine", false); // disable highligthing on the active line
			editor.setShowPrintMargin(false); 				// disable printing margin
	
			var startLine = document.getElementById("startLine").value;
			var startColumn = document.getElementById("startColumn").value;
			var endLine = document.getElementById("endLine").value;
			var endColumn = document.getElementById("endColumn").value;
			var Range = ace.require("ace/range").Range;
	
			var codeSnippetStartingLine = parseInt(document.getElementById("methodStartingLine").value);
			editor.setOption("firstLineNumber", codeSnippetStartingLine); // set the starting line to <second parameter>	
			
			// parameters for the others AceEditor
	        var highlightCaller = document.getElementById("positionsCaller").value;
	        var highlightCallee = document.getElementById("positionsCallee").value;
	        var calleesOnMain = document.getElementById("calleesOnMain").value;
	        
			setTimeout(function() {
				// highlight regarding main method
				editor.session.addMarker(new Range(startLine - codeSnippetStartingLine, startColumn, 
						endLine	- codeSnippetStartingLine, endColumn), "ace_active-line", "line");
				editor.gotoLine(startLine - codeSnippetStartingLine + 1);
				if (calleesOnMain){		// highlighting callees
					var numbersCalleesOnMain = calleesOnMain.split("#");
					var lnStart = 0.0;
					var clStart = 0.0;
					var lnEnd = 0.0;
					var clEnd = 0.0;
					//document.write("Callee length: " + numbersCallee.length + "<br>");
					for (i=0; i < numbersCalleesOnMain.length; i+=4){
						lnStart = numbersCalleesOnMain[i]-1;
						clStart = numbersCalleesOnMain[i+1];
						lnEnd = numbersCalleesOnMain[i+2]-1;
						clEnd = numbersCalleesOnMain[i+3];
						editor.session.addMarker(new Range(lnStart, clStart, lnEnd, clEnd), "callees", "line");
						//document.write("positions: " + lnStart + ", " + clStart + ", " + lnEnd + ", " + clEnd +"<br>");
					}	
				}
				
				// other ACE Editor highlights
				if (highlightCaller){
					/* setting properties of the div caller */
					var divCaller = document.getElementById('editorCaller');
					divCaller.style.position='relative';
					divCaller.style.height='200px';
					divCaller.style.width='680px';
					 
					/* Second and caller ACE Editor */
					var editorCaller = ace.edit('editorCaller');
					editorCaller.setReadOnly(true);
					editorCaller.setTheme("ace/theme/github");
					editorCaller.getSession().setMode("ace/mode/java"); 
					editorCaller.setBehavioursEnabled(false);
					editorCaller.setOption("highlightActiveLine", false); 	// disable highligthing on the active line
					editorCaller.setShowPrintMargin(false);					// disable printing margin
					
					var numbersCaller = highlightCaller.split("#");
					var lnStart = 0.0;
					var clStart = 0.0;
					var lnEnd = 0.0;
					var clEnd = 0.0;
					//document.write("Caller length: " + numbersCaller.length + "<br>");
					for (i=0; i < numbersCaller.length; i+=4){
						lnStart = numbersCaller[i]-1;
						clStart = numbersCaller[i+1];
						lnEnd = numbersCaller[i+2]-1;
						clEnd = numbersCaller[i+3];
						editorCaller.session.addMarker(new Range(lnStart, clStart, lnEnd, clEnd), "ace_active-line", "line");
						//document.write("positions: " + lnStart + ", " + clStart + ", " + lnEnd + ", " + clEnd +"<br>");
					}
					//document.write("<br>");
				}
				
				if (highlightCallee){
					/* setting properties of the div caller */
					var divCallee = document.getElementById('editorCallee');
					divCallee.style.position='relative';
					divCallee.style.height='200px';
					divCallee.style.width='680px';
					
					/* Third and callee ACE Editor */
					var editorCallee = ace.edit('editorCallee');
					editorCallee.setReadOnly(true);
					editorCallee.setTheme("ace/theme/github");
					editorCallee.getSession().setMode("ace/mode/java"); 
					editorCallee.setBehavioursEnabled(false);
					editorCallee.setOption("highlightActiveLine", false); 	// disable highligthing on the active line
					editorCallee.setShowPrintMargin(false);					// disable printing margin
		    
					var numbersCallee = highlightCallee.split("#");
					var lnStart = 0.0;
					var clStart = 0.0;
					var lnEnd = 0.0;
					var clEnd = 0.0;
					//document.write("Callee length: " + numbersCallee.length + "<br>");
					for (i=0; i < numbersCallee.length; i+=4){
						lnStart = numbersCallee[i]-1;
						clStart = numbersCallee[i+1];
						lnEnd = numbersCallee[i+2]-1;
						clEnd = numbersCallee[i+3];
						editorCallee.session.addMarker(new Range(lnStart, clStart, lnEnd, clEnd), "callees", "line");
						//document.write("positions: " + lnStart + ", " + clStart + ", " + lnEnd + ", " + clEnd +"<br>");
					}	
				}
				// just do make a space between Editors
				if (highlightCaller && highlightCallee)
					document.getElementById('space').innerHTML = '<br>';
				// just to fill the label about the Editors
				if (highlightCaller || highlightCallee)
					document.getElementById('context').innerHTML = '<b>Functions that call and are called by this method:</b>';
					
			}, 100); 
			
		</script>
		<br>
		</div>

	</div>
</body>
</html>