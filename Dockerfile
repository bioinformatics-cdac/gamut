FROM jboss/wildfly
ADD gamut-web-ui/target/GAMUT.war /opt/jboss/wildfly/standalone/deployments/
ADD gamut-web-ui/snp/ /opt/jboss/.snp/

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
