# COPY CODE AND BUILD ONTOBROWSER EXECUTABLE
FROM maven:3-adoptopenjdk-8 AS BUILDER_1

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

# ENSURE LATEST CODE ON EACH BUILD
ADD "https://www.random.org/cgi-bin/randbyte?nbytes=10&format=h" skipcache
COPY ./src ./src
RUN mvn clean package


# DOWNLOADS DEPENDENCIES AND COPIES WILDFLY CONFIG FILES
FROM alpine:3 AS BUILDER_2
WORKDIR /root/ontobrowser

RUN apk add --no-cache curl
RUN apk add --no-cache unzip

WORKDIR /root/ontobrowser

# INSTALLING WILDFLY, DB CONNECTOR, CONFIGURATIONS AND THE ONTOBROWSER
RUN curl -Lo "mysql.tar.xz" "https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.27.zip" \
    && unzip "mysql.tar.xz" -d "mysqlj" \
    && curl -Lo "wildfly.tar.gz" "https://download.jboss.org/wildfly/14.0.1.Final/wildfly-14.0.1.Final.tar.gz" \
    && tar -xvzf "wildfly.tar.gz" \
    && mkdir -p "wildfly-14.0.1.Final/modules/system/layers/base/com/mysql/main" \
    && cp "mysqlj/mysql-connector-java-8.0.27/mysql-connector-java-8.0.27.jar" "wildfly-14.0.1.Final/modules/system/layers/base/com/mysql/main"

COPY module.xml ./wildfly-14.0.1.Final/modules/system/layers/base/com/mysql/main/module.xml
COPY standalone.xml ./wildfly-14.0.1.Final/standalone/configuration/standalone.xml
COPY --from=BUILDER_1 /app/target/ontobrowser.war ./wildfly-14.0.1.Final/standalone/deployments/ROOT.war


# WHAT WE RUN
FROM adoptopenjdk/openjdk8:alpine-jre

WORKDIR /root/ontobrowser
RUN apk add --no-cache graphviz
COPY --from=BUILDER_2 /root/ontobrowser/wildfly-14.0.1.Final ./wildfly-14.0.1.Final

EXPOSE 8080
CMD /root/ontobrowser/wildfly-14.0.1.Final/bin/standalone.sh -b 0.0.0.0