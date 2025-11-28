plugins {
    java
    `java-library`
    id("io.spring.dependency-management") version "1.1.7"
    `maven-publish`
}

group = "com.github.edikdavydiants"
version = "0.0.1"
description = "client-chromadb"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.0")
    }
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter:3.4.0")

    api("com.fasterxml.jackson.core:jackson-core:2.17.2")
    api("com.fasterxml.jackson.core:jackson-annotations:2.17.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.0")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    implementation("org.yaml:snakeyaml:2.2")

    compileOnly("org.springframework.boot:spring-boot-configuration-processor:3.4.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "client-chromadb"
            version = project.version.toString()
        }
    }
}
