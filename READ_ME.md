# Restaurants & Tourism: TheCork -> Group T48

TheCork is a service that allows its users to reserve tables at their favorites restaurants. Our goal was to make TheCork a totally safe platform for users by using security protocols and procedures to protect the connections and data flow during the use of this service.

## General Information

The main goal of the project is providing secure communication between the client-webserver and bank-webserver.

### Built With

* [Java](https://openjdk.java.net/) - Programming Language and Platform
* [Maven](https://maven.apache.org/) - Build Tool and Dependency Management
* [Spring Boot](https://spring.io/projects/spring-boot) - Tool to build the API
* [JavaScript](https://www.javascript.com/) - Programming Language and Platform
* [React](https://reactjs.org/) - JavaScript library for building user interfaces
* [PostgreSQL](https://www.postgresql.org/) - Open source database
* [Visual Studio Code](https://code.visualstudio.com/) - Code editing tool

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

You must have 5 ubuntu Virtual Machines.
On all the Vms you should clone the submitted code (SIRS_PROJECT)

DataBase VM must have PostegreSQL installed.

Webserver VM must have spring-boot, npm, maven, nodejs and java installed.

Bank VM must have java and maven installed.

### Deploying

Turn on the FireWall VM to open communication between the end points. 

On one VM (Database) move to the folder SIRS_PROJECT/ and run:
```
psql -h localhost -U t48 -d thecork -f <Path to the populate.sql file inside SIRS_PROJECT>
```

On other VM (WebServer) move to the folder SIRS_PROJECT/TheCorkApi and run:

```
mvm clean install
```

```
mvn spring-boot:run
```

then open another terminal, move to the folder SIRS_PROJECT/forntend_sirs and run:

```
npm install --force
```

```
npm run start
```

On other VM (Bank) move to the folder SIRS_PROJECT/Secure-Messages and run:

```
mvn compile exec:java -Dmainclass=pt.tecnico.SecureClient -Dexec.args="192.168.1.1 8000 keys/bobpub.der"
Cliente
```

Finely on the Client VM search on the browser for the url https://192.168.1.1:3000 

## Additional Information

### Authors

* **Bernardo Castiço** - *Initial work* - ist196845
* **Hugo Rita** - *Initial work* - ist196870
* **Pedro Pereira** - *Initial work* - ist196905
