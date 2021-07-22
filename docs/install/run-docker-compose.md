## How to Use the Gamut from DockerHub

## Pre-requisites

Docker Engine and Docker Compose installed either locally or remote, depending on available setup.

**Docker Installation**

* Install Docker [https://docs.docker.com/get-docker](https://docs.docker.com/get-docker)

* Install Docker Compose [https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/)

* Check docker run
```
docker ps
```



### Step 1: Make clone image of Gamut
```
git clone https://github.com/bioinformatics-cdac/gamut
```
    
### Step 2:  Change the directory
```
cd gamut
```
### Step 3:  Run Gamut docker image
```
docker-compose up -d
```
![Docker compose Command](https://raw.githubusercontent.com/bioinformatics-cdac/gamut/main/docs/img/gamut_start.png)

wait for 2 minutes for loading of sample data

### Step 4:  Open url in browser

>   [http://localhost:8080/](http://localhost:8080/)


### _Screenshot_

 [![Gamut Home Page](https://raw.githubusercontent.com/bioinformatics-cdac/gamut/main/docs/img/gamut.png)](https://raw.githubusercontent.com/bioinformatics-cdac/gamut/main/docs/img/gamut.png)