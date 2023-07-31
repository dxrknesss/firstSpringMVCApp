# Spring Boot "Social Network app"

* Create people, edit or delete them
* Manage friends of created people
* Implemented authentication and authorization systems, as well as security tweaks!

## Table of Contents
* [Installation](#installation)
* [Adminer](#adminer)

## Installation
Install docker-compose or docker-desktop from [Docker's official site](https://docs.docker.com/get-docker/) and type next command 
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

## Adminer
To easily access and change database records, you can use Adminer, simply by going to ``localhost:8888``
