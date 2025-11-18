# ProgettoAST

Automated Software Testing Project

[![Coverage Status](https://coveralls.io/repos/github/Virgula0/ProgettoAST/badge.svg)](https://coveralls.io/github/Virgula0/ProgettoAST)
[![Maven build Java CI](https://github.com/Virgula0/ProgettoAST/actions/workflows/maven.yaml/badge.svg)](https://github.com/Virgula0/ProgettoAST/actions/workflows/maven.yaml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Virgula0_ProgettoAST&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Virgula0_ProgettoAST)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Virgula0_ProgettoAST&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Virgula0_ProgettoAST)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Virgula0_ProgettoAST&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Virgula0_ProgettoAST)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Virgula0_ProgettoAST&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Virgula0_ProgettoAST)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=Virgula0_ProgettoAST&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=Virgula0_ProgettoAST)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Virgula0_ProgettoAST&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Virgula0_ProgettoAST)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Virgula0_ProgettoAST&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=Virgula0_ProgettoAST)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Virgula0_ProgettoAST&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Virgula0_ProgettoAST)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Virgula0_ProgettoAST&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=Virgula0_ProgettoAST)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Virgula0_ProgettoAST&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Virgula0_ProgettoAST)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Virgula0_ProgettoAST&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Virgula0_ProgettoAST)

# Summary

- [Report ITA](Report.md)
- [Compile and run](#requirements-for-docker-setup)


I provided a Docker container in the repository to share an X11 server along with `mongodb` and `mariadb`.

### Requirements for docker setup

- xhost utility
    - Usually already installed but available in the package `x11-xserver-utils`
- Docker and Docker-compose
    - If you want to run tests locally, remember that the version of test container used (v 1.X) is compatible with docker daemon < 28

```
make build-and-run
```

This will compile the application within a Docker container and run it in a safe environment.

When you finished

```
make docker-stop
```

However, due to a different graphical setup, it can lead to problems; in fact, the spawned panel can result in being `blank`, showing no graphics when running the application within the container.

Having database containers up, you can start the application manually with:

```
make package && java -jar com.rosa.angelo.progetto.ast/target/ast-1.0.0-jar-with-dependencies.jar --db=mariadb
```

Or with mongodb:

```
make package && java -jar com.rosa.angelo.progetto.ast/target/ast-1.0.0-jar-with-dependencies.jar --db=mongodb
```

> [!WARNING]
> You need the token phrase `validToken` to be able to perform registrations.

You need `>= Java 17` to run it.

In alternative, you can download and run the latest release already pre-compiled at [Github Releases](https://github.com/Virgula0/ProgettoAST/releases/)