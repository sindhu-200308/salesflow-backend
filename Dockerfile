FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY . .

WORKDIR /app/sales-crm-backend

RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

EXPOSE 8080

CMD java -jar target/*.jar
