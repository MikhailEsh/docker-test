FROM frolvlad/alpine-oraclejdk8:slim
ADD server-web.jar app.jar
RUN sh -c 'touch /app.jar'
WORKDIR /home
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]