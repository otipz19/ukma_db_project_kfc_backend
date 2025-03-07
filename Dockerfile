FROM quay.io/wildfly/wildfly:35.0.1.Final-jdk21
COPY ./target/KFC.war /opt/jboss/wildfly/standalone/deployments/
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]