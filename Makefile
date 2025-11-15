POM_PATH = com.rosa.angelo.progetto.ast/pom.xml

junit:
	mvn clean verify -f $(POM_PATH)
.PHONY: junit

junit-skipcoverage:
	mvn clean verify -DskipCoverage=true -f $(POM_PATH)
.PHONY: junit-skipcoverage

integration-test:
	mvn clean verify -Pintegration-tests -f $(POM_PATH)
.PHONY: integration-test

run-pit:
	mvn clean verify org.pitest:pitest-maven:mutationCoverage -f $(POM_PATH)
.PHONY: run-pit

run-all:
	mvn clean verify org.pitest:pitest-maven:mutationCoverage -Pintegration-tests -f $(POM_PATH)
.PHONY: run-all

package:
	mvn clean package dependency:go-offline -Pskip-tests -DskipCoverage=true -f $(POM_PATH)
.PHONY: package

docker-build:
	@export DISPLAY=${DISPLAY}
	@echo "Using display ${DISPLAY}"
	@xhost +local:docker
	docker compose up --build
.PHONY: docker-build

docker-stop:
	xhost -local:docker
	docker compose down -v
.PHONY: docker-stop

sonarcube-up:
	docker compose -f sonarcube/docker-compose.yaml up --build
.PHONY: sonarcube-up

sonarcube-down:
	docker compose -f sonarcube/docker-compose.yaml down -v
.PHONY: sonarcube-down

sonarcube:
# generate SONAR_TOKEN => http://localhost:9000/account/security
	mvn clean verify \
		-Pjacoco sonar:sonar -Pintegration-tests -f $(POM_PATH) \
		-Dsonar.host.url=http://localhost:9000 \
		-Dsonar.token=$$SONAR_TOKEN -X
.PHONY: sonarcube

build-and-run: docker-build
test: run-all
run-sonarcube: sonarcube-up sonarcube
