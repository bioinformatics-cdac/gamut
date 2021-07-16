FROM jboss/wildfly
LABEL org.opencontainers.image.authors="rkrishnan@cdac.in"
LABEL org.opencontainers.image.authors="sandeepm@cdac.in"
LABEL org.opencontainers.image.authors="renug@cdac.in"
LABEL org.opencontainers.image.version="1.0.0"

COPY --chown=jboss support_files/standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml
COPY --chown=jboss gamut-web-ui/snp/ /opt/jboss/.snp/
COPY --chown=jboss support_files/gamut gamut
COPY --chown=jboss gamut-web-ui/target/GAMUT.war /opt/jboss/wildfly/standalone/deployments/

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
