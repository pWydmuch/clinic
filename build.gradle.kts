plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.flywaydb:flyway-core")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.mapstruct:mapstruct:1.6.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.0")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.17.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.3")
    implementation("io.jsonwebtoken:jjwt:0.12.6")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
