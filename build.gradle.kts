import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("maven-publish")
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"

    id("com.palantir.docker") version "0.31.0"
    id("com.palantir.docker-compose") version "0.31.0"
}


group = "com.gurk0001"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks.create("fetch") {
    doFirst {
        getArchivaUrlStr()
    }
}

fun getArchivaUrlStr(): java.net.URI {
    val archivaUrl = System.getenv("ARCHIVA_URL")
    println("ARCHIVA URL: $archivaUrl")
    val releasesRepoUrl = uri("http://$archivaUrl/repository/releases")
    val snapshotsRepoUrl = uri("http://$archivaUrl/repository/snapshots")
    return if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = getArchivaUrlStr()
        isAllowInsecureProtocol = true
        credentials {
            username = System.getenv("ARCHIVA_USR")
            password = System.getenv("ARCHIVA_PSW")
        }
    }
}

dependencies {
    implementation("com.gurk0001:kotlin-library:0.0.5")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            url = uri(getArchivaUrlStr())
            isAllowInsecureProtocol = true
            credentials {
                username = System.getenv("ARCHIVA_USR")
                password = System.getenv("ARCHIVA_PSW")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])

            pom {
                name.set(project.name)
                description.set("This is to showcase that we can publish java project into archiva repo")
                url.set("www.popye.in")
                developers {
                    developer {
                        id.set("gurk0001")
                        name.set("Guru")
                        email.set("gurk001@test.com")
                    }
                }
            }
        }
    }
}

docker {
    name = "${project.name}:${project.version}"
    files("plugins.txt", "seedJob.xml")
    setDockerfile(file("Dockerfile"))
}

