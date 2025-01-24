plugins {
    java
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.jankinwu"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.8")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.8:models")
    implementation("org.apache.poi:poi:5.4.0")
    implementation("org.apache.poi:poi-scratchpad:5.4.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
