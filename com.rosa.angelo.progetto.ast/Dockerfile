FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app

# install make
RUN apt update && apt install -y make

COPY . .

RUN make package

FROM eclipse-temurin:17-jdk-jammy 
# using this version instad of openjdk because some libraries are needed to be installed for running the GUI
# the openjdk spec is deprecated and not supported

RUN apt update \
    && apt install -y --no-install-recommends \
        # Essential X11 libraries for Java GUI
        libxext6 \
        libxrender1 \
        libxtst6 \
        x11-utils \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/com.rosa.angelo.progetto.ast/target/*-jar-with-dependencies.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar", "--mongo-host", "mongodb"]