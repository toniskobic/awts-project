<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>T.Škobić - aplikacija 4 - registracija</title>
</head>
<body>
	<h1>Registracija</h1>
	<a
		href="${pageContext.servletContext.contextPath}/mvc/korisnici/pocetak">Početak</a>
	<br>
	<form
		action="${pageContext.servletContext.contextPath}/mvc/korisnici/registracija/rezultat"
		method="POST">
		<label for="korIme">Korisničko ime:</label><br>
		<input type="text" id="korIme" name="korIme"> <br>
		<label for="ime">Ime:</label><br> 
		<input type="text" id="ime" name="ime"> <br>
		<label for="prezime">Prezime:</label><br>
		<input type="text" id="prezime" name="prezime"><br>
		<label for="lozinka">Lozinka:</label><br>
		<input type="password" id="lozinka" name="lozinka"><br>
		<label for="email">E-mail:</label><br>
		<input type="text" id="email"name="email"><br>
		<input type="submit" value="Registriraj">
	</form>
</body>
</html>