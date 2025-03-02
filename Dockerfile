FROM tomcat:11-jre21
COPY ./target/KFC.war $CATALINA_HOME/webapps/KFC.war
CMD ["catalina.sh", "run"]