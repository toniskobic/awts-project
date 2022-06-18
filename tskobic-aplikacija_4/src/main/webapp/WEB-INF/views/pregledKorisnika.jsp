<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>T.Škobić - aplikacija 4 - pregled korisnika</title>
</head>
<body>
	<h1>Pregled korisnika</h1>
	<a
		href="${pageContext.servletContext.contextPath}/mvc/korisnici/pocetak">Početak</a>
	<br>
	<a
		href="${pageContext.servletContext.contextPath}/mvc/korisnici/brisanjeZetona">Brisanje trenutnog žetona</a>
	<br>
	<table border="1">
		<tr>
			<th>Korisničko ime</th>
			<th>Ime</th>
			<th>Prezime</th>
			<th>Lozinka</th>
			<th>E-mail</th>
			<c:choose>
				<c:when test="${requestScope.admin == true}">
					<th>Obriši žetone</th>
				</c:when>
				<c:otherwise>
				</c:otherwise>
			</c:choose>
		</tr>
		<c:forEach var="k" items="${requestScope.korisnici}">
			<tr>
				<td>${k.korIme}</td>
				<td>${k.ime}</td>
				<td>${k.prezime}</td>
				<td>${k.lozinka}</td>
				<td>${k.email}</td>
				<c:choose>
					<c:when test="${requestScope.admin == true}">
						<td><form
								action="${pageContext.servletContext.contextPath}/mvc/korisnici/brisanjeZetona/${k.korIme}"
								method="POST">
								<input type="submit" value="Obriši žetone">
							</form></td>
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:forEach>
	</table>
</body>
</html>