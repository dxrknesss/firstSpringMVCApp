# Spring Boot "Social Network app"

* Create people, edit or delete them
* Manage friends of created people
* Implemented authentication and authorization systems, as well as security tweaks!
* You can use this app as **REST API** by accessing ``/api`` pages!

## Table of Contents
* [Installation](#Installation)
* [API](#API)
* [Future plans](#Future plans)

## Installation
First, you'll need to build the project.
JDK20+ Has to be installed on your PC. [(JDK download link)](https://www.oracle.com/java/technologies/downloads/). 
After installing JDK, you should set JAVA_HOME environment variable.  
Then, you can build project, using mvnw, the tool in the project's root folder:

On linux:
```shell
./mvnw clean install -Dmaven.test.skip
```

On windows:
```shell
mvnw.cmd clean install -Dmaven.test.skip
```

Then, install docker-compose or docker-desktop from [Docker's official site](https://docs.docker.com/get-docker/) and type next command 
into your terminal while you are in the project's folder 
```shell
docker-compose up
```

After that, you should go to ``localhost:9001`` address and login into MINIo system  
* login: minio-root-user    
* password: minio-root-password    
(both can be configured in docker-compose.yml file)  
After you have successfully signed in, create a bucket with default parameters  
named **"picture-bucket"**

After that, you're good to go! Go to ``localhost:8080`` and have fun!
## API
You can also use this app as a REST API.  
In order to do this, you will need to query:
1. GET query to ``localhost:8080/api/people`` will display all the people
2. GET query to ``localhost:8080/api/people/{id}`` - you should substitute id with id of the person you want to display
3. POST query to ``localhost:8080/api/people`` will create a new person!  
**Required fields:**
   * Name (2-30 characters long)
   * Email 
   * Age (from 0 to 110 not inclusive)
   * Password

## Future plans
* Add missing methods to /api section of an app