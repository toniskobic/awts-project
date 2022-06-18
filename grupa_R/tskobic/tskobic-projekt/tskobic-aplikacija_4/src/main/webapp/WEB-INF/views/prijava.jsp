<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>T.Škobić - aplikacija 4 - prijava</title>
</head>
<body>
	<h1>Prijava</h1>
	<a
		href="${pageContext.servletContext.contextPath}/mvc/korisnici/pocetak">Početak</a>
	<br>
	<form
		action="${pageContext.servletContext.contextPath}/mvc/korisnici/prijava/rezultat"
		method="POST">
		<label for="korIme">Korisničko ime:</label><br>
		<input type="text" id="korIme" name="korIme"> <br>
		<label for="lozinka">Lozinka:</label><br>
		<input type="password" id="lozinka" name="lozinka"><br>
		<input type="submit" value="Prijavi se">
	</form>
</body>
</html>