<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false"%>
<html>
<head>
<title>Home</title>
<link href="<c:url value="/css/ontStyle.css" />" rel="stylesheet">
<link href="<c:url value="/css/w3schoolTab.css" />" rel="stylesheet">
</head>
<body>
	<div id="web_container">
		<div id="web_header">
			<h1>Web based ontology summarisation</h1>
			<hr width="100%" size="4" color="#00BFFF">
			<fieldset>
				<legend>Input ontology file</legend>
				<form method="POST" action="" enctype="multipart/form-data">
					Ontology file to upload: <input type="file" name="file">&emsp;
					Provide a File name:     <input type="text" name="name">
					<input type="submit" value="Upload">
				</form>
				<P>${uploadStatus}</p>
		</fieldset>
		</div>
		<div id="web_maincontent">
			<div class="portion">
				<div class="section">
					<h2>Ontology Axioms</h2>
					<hr width="100%" size="3" color="#00BFFF">
				</div>
				<div class="vertical-menu">
					<c:forEach items="${axioms}" var="axiom">
								<p>${axiom}</p>							
						</c:forEach>
				</div>
			</div>
			
			<form:form method="post" action="execute" modelAttribute="performOperation">
			<div class="portion">	
				<div id="portionSignature">
					<div class="section">
						<h2>Signature</h2>
						<hr width="100%" size="3" color="#00BFFF">
					</div>
					<div class="vertical-menu">
							<form:select multiple="true" path="symbols" style="width: 100%; height: 100%">
								<form:options items="${allSymbols}" />
							</form:select>
					</div>
				</div>
				<div id="portionOption">
					<div class="section">
						<h2>Options</h2>
						<hr width="100%" size="3" color="#00BFFF">
					</div>
						<p>Please select an approach:</p>
						<div>
							Forgetting <form:radiobutton path="forgetting" value="forgetting"/> 
							Interpolation <form:radiobutton path="forgetting" value="interpolation"/> 
						</div>
						<p>Please select method:</p>
						<div>
							ALCH TBoxes <form:radiobutton path="method" value="alch"/> 
							ALC with ABoxes <form:radiobutton path="method" value="alc"/> 
						</div>
						<div style="float: right;padding-top: 10px">
							<input type="submit" name="addOptions" value="Process Summarisation">
						</div>
				</div>
				</div>
				</form:form>
			
			<div class="portion">
				<div class="section">
					<h2>Summarisation Output</h2>
					<hr width="100%" size="3" color="#00BFFF">
				</div>
				<%-- <c:forEach items="${interpolants}" var="interpolant"> 
				 		<p>${interpolant}</p> 							
					</c:forEach> --%>
					<div class="tab">
						<button class="tablinks" onclick="openResult(event, 'Lethe')">Lethe</button>
						<button class="tablinks" onclick="openResult(event, 'Fame')">Fame</button>
						<button class="tablinks" onclick="openResult(event, 'Modularisation')">Modularisation</button>
					</div>
					
					<div id="Lethe" class="tabcontent">
						<div class="vertical-menu" style="width: 100%; height: 85%">
								<c:forEach items="${interpolants}" var="interpolant">
									<p>${interpolant}</p>
								</c:forEach>
						</div>
						<div style="float: right;padding-top: 10px">
							<a href="<c:url value='/downloadLethe'/>">Save Lethe Results</a>
							<a href="<c:url value='javascript:visualiseLethe()'/>">Visualise Lethe Results</a>
						</div>
					</div>
					<div id="Fame" class="tabcontent">
						<div class="vertical-menu" style="width: 100%; height: 85%">
								<c:forEach items="${fameResults}" var="fameEntry">
									<p>${fameEntry}</p>
								</c:forEach>
						</div>
						<div style="float: right;padding-top: 10px">
							<a href="<c:url value='/downloadFame'/>">Save Fame Results</a>
							<a href="<c:url value='javascript:visualiseFame()'/>">Visualise Fame Results</a>
						</div>
					</div>

					<div id="Modularisation" class="tabcontent">
						<div class="vertical-menu" style="width: 100%; height: 85%">
								<c:forEach items="${modularisationResults}" var="modEntry">
									<p>${modEntry}</p>
								</c:forEach>
						</div>
						<div style="float: right;padding-top: 10px">
							<a href="<c:url value='/downloadMod'/>">Save Modularisation Results</a>
							<a href="<c:url value='javascript:visualiseModularisation()'/>">Visualise Modularisation Results</a>
						</div>
					</div>
				
			</div>
		</div>
	</div>
	<script>
		function openResult(evt, resName) {
			var i, tabcontent, tablinks;
			tabcontent = document.getElementsByClassName("tabcontent");
			for (i = 0; i < tabcontent.length; i++) {
				tabcontent[i].style.display = "none";
			}
			tablinks = document.getElementsByClassName("tablinks");
			for (i = 0; i < tablinks.length; i++) {
				tablinks[i].className = tablinks[i].className.replace(
						" active", "");
			}
			document.getElementById(resName).style.display = "block";
			evt.currentTarget.className += " active";
		}
		function visualiseLethe() {
		    window.open("http://localhost:8080/webvowl_1.1.2/#iri=http://localhost:8080/WebOntSummarisation/ontologies/lethe_modified.owl");
		}
		function visualiseFame() {
		    window.open("http://localhost:8080/webvowl_1.1.2/#iri=http://localhost:8080/WebOntSummarisation/ontologies/fame_modified.owl");
		}
		function visualiseModularisation() {
		    window.open("http://localhost:8080/webvowl_1.1.2/#iri=http://localhost:8080/WebOntSummarisation/ontologies/mod_modified.owl");
		}
	</script>
</body>
</html>