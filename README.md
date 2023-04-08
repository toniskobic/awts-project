# AWTS Project


The AWTS Project is a Java 17 Jakarta EE 9 Web system composed of six applications 
that communicate with each other via network sockets, JAXWS calls, REST calls and via WebSocket 
for providing information about airports, locations and meteorological data made as part of a college project. 

Project contains comprehensive documentation generated via Javadoc.

This project was made as part of the faculty graduate studies course "Advanced Web Technologies and Services".

## System description

Payara Web Server uses a HSQLDB database for data management and Glassfish EE Sever uses a MySQL database for data manegement.

 - First application is a distance calculation service between airports that is used by other applications and is available on a network socket.
 - Second web application is deployed on a Payara Web Server and downloads data about planes departures and arrivals from certain airports.
 - Third web application is deployed on a Payara Web Server and represents a central authentication, authorization 
 and monitoring RESTful (JAX-RS) web service.
 - Fourth applications is deployed on a Payara Web Server and deals with administrative tasks through user interface implemented with Jakarta MVC.
 - Fifth application is deploy on a Glassfish EE Server and provides a JAX-WS (SOAP) service and a WebSocket endpoint. The application sends 
 requests to the third application, i.e. the RESTful web service and the OpenWeatherMap service.
- Sixth application is deployed on a Glassfish EE Server and provides views for 
logging in and syncing missing users in its own MySQL database with the third's application RESTful Web Service users stored in a MySQL database. 
Views are implemented with Jakarta Server Faces and PrimeFaces. Application implements JPA Entities and uses Criteria API for building queries.
