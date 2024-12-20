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
    id("checkstyle")
    id("io.spring.javaformat") version "0.0.43"
    id("org.openrewrite.rewrite") version "6.28.3"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

rewrite {

    setCheckstyleConfigFile(file("${rootDir}/config/checkstyle/google_checks.xml"))
    activeRecipe("org.openrewrite.staticanalysis.CodeCleanup", "org.openrewrite.java.OrderImports")
    isExportDatatables = true

}

allprojects {
    group = "com.simra.konsumgandalf"
    version = "0.0.2-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    apply {
        plugin("checkstyle")
        plugin("io.spring.javaformat")
    }

    checkstyle {
        toolVersion = "10.20.2"
        configFile = file("${rootDir}/config/checkstyle/google_checks.xml")
    }
    dependencies {
        checkstyle("io.spring.javaformat:spring-javaformat-checkstyle:0.0.43")
    }

}

println(rewrite.checkstyleConfigFile?.path)

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

    rewrite("org.openrewrite.recipe:rewrite-static-analysis:latest.release")
    implementation("com.puppycrawl.tools:checkstyle:10.21.0")
}

