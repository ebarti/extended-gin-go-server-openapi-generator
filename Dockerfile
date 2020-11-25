FROM maven:3.6.3-jdk-11-openj9 AS builder

WORKDIR /work
COPY . .
RUN mvn package


FROM openapitools/openapi-generator-cli:latest

COPY --from=builder /work/target/extended-gin-go-server-openapi-generator*.jar /opt/extended-gin-go-server-openapi-generator.jar

ENTRYPOINT ["java", "-classpath", "/opt/openapi-generator/modules/openapi-generator-cli/target/openapi-generator-cli.jar:/opt/extended-gin-go-server-openapi-generator.jar", "org.openapitools.codegen.OpenAPIGenerator"]
CMD ["help"]