FROM java:8
VOLUME /tmp
ADD ./target/alpha-gateway-1.0.0.jar alpha-gateway.jar
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
ENTRYPOINT ["java","-Xms512m","-Xmx512m","-Djava.security.egd=file:/dev/./urandom","-jar","/alpha-gateway.jar"]