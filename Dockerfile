FROM jboss/wildfly
LABEL org.opencontainers.image.authors="rkrishnan@cdac.in"
LABEL org.opencontainers.image.authors="sandeepm@cdac.in"
LABEL org.opencontainers.image.authors="renug@cdac.in"
LABEL org.opencontainers.image.version="1.0.0"

ADD gamut-web-ui/target/GAMUT.war /opt/jboss/wildfly/standalone/deployments/
ADD gamut-web-ui/snp/ /opt/jboss/.snp/

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
