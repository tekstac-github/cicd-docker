FROM tomcat:9.0

# Copy the WAR file built by Maven to the Tomcat webapps directory
COPY target/ABSampleJava.war /usr/local/tomcat/webapps/

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
