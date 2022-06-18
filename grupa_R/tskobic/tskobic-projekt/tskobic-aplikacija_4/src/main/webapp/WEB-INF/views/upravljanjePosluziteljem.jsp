<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>T.Škobić - aplikacija 4 - upravljanje poslužiteljem</title>
</head>
<body>
	<h1>Upravljanje poslužiteljem prve aplikacije</h1>
	<a
		href="${pageContext.servletContext.contextPath}/mvc/korisnici/pocetak">Početak</a>
	<br>
	<h3>Status: ${requestScope.status}</h3>
	<form
		action="${pageContext.servletContext.contextPath}/mvc/serveri/upravljanjePosluziteljem/rezultat"
		method="POST">
		<label for="komanda">Komande:</label><br>
		<select id="komanda" name="komanda">
			<option value="INIT">Inicijalizacija poslužitelja</option>
			<option value="QUIT">Prekid rada poslužitelja</option>
			<option value="LOAD">Učitavanje podataka</option>
			<option value="CLEAR">Brisanje podataka</option>
		</select>
		<input type="submit" value="Izvrši">
	</form>
</body>
</html>