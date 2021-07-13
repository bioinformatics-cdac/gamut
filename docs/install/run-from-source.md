## How to Use the Gamut from source

#### Step 1: Make clone image of gamut
> `git clone https://github.com/bioinformatics-cdac/gamut `
##### Step 2:  Go into folder
> `cd gamut`
##### Step 3:  Make maven clean install
> `mvn clean install`
##### Step 4:  Build docker
> `docker build -t bioinfocdac/gamut`
##### Step 5:  Run gamut docker image
> `docker-compose up -d` 