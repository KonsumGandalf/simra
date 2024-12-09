plugins {
    id("java")
}

group = "com.simra.konsumgandalf"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":osmrBackend"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("com.opencsv:opencsv:5.9")
    implementation("org.hibernate.orm:hibernate-spatial:6.6.2.Final")
    implementation("org.springframework.boot:spring-boot-configuration-processor:3.4.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.4.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.test {
    useJUnitPlatform()
}
