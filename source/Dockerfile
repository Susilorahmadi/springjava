FROM jtl-tkgiharbor.hq.bni.co.id/psd/maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
ADD ./pom.xml /app
ADD ./settings.xml /app
ADD ./src /app/src/
RUN mvn clean package -s settings.xml -DskipTests

FROM jtl-tkgiharbor.hq.bni.co.id/psd/openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/target/card-binding-1.0.0.jar /app/card-binding-1.0.0.jar
COPY src/main/resources /app/resources
ENV TZ=Asia/Jakarta
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/card-binding-1.0.0.jar"]