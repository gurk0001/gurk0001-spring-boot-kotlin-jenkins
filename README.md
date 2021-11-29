#### REFERENCE
* Repository to go along with the *How To Secure Your Gradle Credentials In Jenkins* article
at [tomgregory.com](https://www.tomgregory.com).
  
* https://docs.gradle.org/current/userguide/publishing_maven.html (Kotlin for gradle 7.0)

## Pre-requisites

* Docker Installations
* If you are running it for the first time then use the following command to push Jenkin to popye's docker hub
  ```
  docker build -t popyeindia/popye-jenkins:0.0.1 .
  docker push  popyeindia/popye-jenkins:0.0.1
  ```

## Setting `gradlew docker` plugin in spring boot using `build.gradle.kts`
```
plugins {
    id("com.palantir.docker") version "0.31.0"
    id("com.palantir.docker-compose") version "0.31.0"
}

docker {
    name = "${project.name}:${project.version}"
    files("plugins.txt", "seedJob.xml")
    setDockerfile(file("Dockerfile"))
}
```
**Note:** 
Make sure you have `plugins.txt` and `seedJob.xml` file under your root folder to make this work exactly how it is specified here.

## Running using `gradlew docker` plugin command

`./gradlew docker dockerComposeUp`

This will run:
* Jenkins
* Archiva

## Stopping `gradlew docker` plugin command

`./gradlew docker dockerComposeUp` or `docker-compose down`

(We are using `Palantir Gradle plugin` to support *dockerComposeUp* / *dockerComposeDown* tasks)


## Publishing

`./gradlew publish`

This will publish a simple jar file with a single Java class to a locally configured Archiva Maven repository.

## Building

`./gradlew build`

This will try to build the `project-to-build` project. It has a dependency on an artifact that it expects to be
in a locally configured Archiva Maven repository.

# FAQ:
#### 1. How are the CREDENTIALS configured so that Jenkins communicates to Archiva ?
  
  When you start Jenkins make sure you have added proper credentials:

  `Dashboard >>  Credentials >>  System >>  Global credentials (unrestricted)`

  ***NOTE:***
      - In this project we have created **Global Credentials** in Jenkis using **ID** as `archiva` which will be the same ID used in Jenkin pipleline script to provide credentials!!
  

#### 2. How does Jenkins knows the exact service location of Archiva to upload artifacts ?
  
  When you create Jenkins file make sure you add `ARCHIVA_URL` that corresponds to the `docker service name` you had provided in `docker-compose.yaml`.

NOTE: Since, Jenkin and Archiva communicates within docker containers, we need to provide service port that containers can communicate. But, for local build you can provide the forwarded port information.

i.e., In this project we have provided `localhost:4333` to run locally and `archiva:8080` for the containers.
   
```
    pipeline {
    agent any
    
        environment {
            ARCHIVA = credentials('archiva')
            ARCHIVA_URL = "archiva:8080"
        }
        stages {
            stage('Build') {
                steps {
                    git 'https://github.com/gurk0001/gradle-credentials-jenkins.git'
    
                    sh './gradlew build --info --stacktrace'
                }
            }
        }
    }
```
#### 3. How to make this project work in local environment?
Make sure you have added following information into ENVIRONMENT.

Edit your profile file  `~/.bash-profile` or `~/.zshrc` or `~/.bashrc`

  ```
  export ARCHIVA_URL=localhost:4333
  export ARCHIVA_USR= << Whatever the name you have given in Jenkin System Credentials (with ID: 'archiva') >>
  export ARCHIVA_PSW= << Whatever the password you have given in Jenkin System Credentials (with ID: 'archiva') >>
  ```

After you are done with edit make sure you call `source` to apply changes:

`source ~/.bash-profile` (or) `source ~/.zshrc` (or) `source ~/.bashrc`

#### 4. What are the build.gradle config we must need to push an `artifact` to `archiva` ?
Typically, for a java library we would need minimum of following configurations.

```
plugins {
    id 'java'
    id 'java-library'
    id 'maven-publish'
}

publishing {
    publications {
        maven(MavenPublication) {
            groupId = project.group
            artifactId = project.name
            version = project.version
            pom {
                name = project.name
                description = "This is to showcase that we can publish java project into archiva repo"
                url = "www.popye.in"
            }

            from components.java
        }
    }

    repositories {
       mavenLocal()
        maven {
            def ARCHIVA_URL = System.getenv('ARCHIVA_URL')
            url "http://${ARCHIVA_URL}/repository/snapshots"

            allowInsecureProtocol true
            credentials {
                username System.getenv('ARCHIVA_USR')
                password System.getenv('ARCHIVA_PSW')
            }
        }
    }
}
```
