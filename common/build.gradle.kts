plugins {
    id("java")
}

group = "com.simra.konsumgandalf"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")

    implementation("com.google.guava:guava:33.3.1-jre")
    implementation("io.hypersistence:hypersistence-utils-hibernate-60:3.9.2")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.4.0")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.4.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("com.opencsv:opencsv:5.9")
    implementation("org.hibernate.orm:hibernate-spatial:6.6.2.Final")
    implementation("org.n52.jackson:jackson-datatype-jts:1.2.10")
    implementation("org.locationtech.jts.io:jts-io-common:1.20.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    mainClass.set("com.simra.konsumgandalf.backend.BackendApplication")
}
