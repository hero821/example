# docker build -t k8sproxy:1.0 --build-arg JAR_FILE=AppK8sProxy-jar-with-dependencies.jar .
FROM fabric8/java-alpine-openjdk8-jre:1.8
ARG JAR_FILE
ADD ${JAR_FILE} ${JAVA_APP_DIR}
