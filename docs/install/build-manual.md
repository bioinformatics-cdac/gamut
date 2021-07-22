
Step 1: git clone  https://github.com/bioinformatics-cdac/gamut

Step 2: cd gamut && mvn clean install

Step 3: Install Mongo DB

Step 4: copy .snp folder at /home/

Step 5: Make dir /home/gamut/user/upload

Step 6: Download WildFly web server https://www.wildfly.org/downloads/

Step 7 : Copy Gamut war file in to Wildfly/standalone/deployments/

Step 8: Modify standalone.xml file with following configuration
`Undertow configuration`