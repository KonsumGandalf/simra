buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.spring.javaformat:spring-javaformat-gradle-plugin:0.0.43")
    }
}

plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("io.spring.javaformat") version "0.0.43"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

allprojects {
    group = "com.simra.konsumgandalf"
    version = "0.0.2-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    apply {
        plugin("io.spring.javaformat")
    }

}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        implementation("org.postgresql:postgresql")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(project(":rides"))
    implementation(project(":common"))
    implementation(project(":osmPlanet"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("org.hibernate.orm:hibernate-spatial:6.6.2.Final")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
