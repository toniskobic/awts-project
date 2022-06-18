<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>T.Škobić - aplikacija 4 - brisanje žetona - rezultat</title>
</head>
<body>
<h1>${requestScope.brisanjeZetona}</h1>
	<a
		href="${pageContext.servletContext.contextPath}/mvc/korisnici/pocetak">Početak</a>
</body>
</html>