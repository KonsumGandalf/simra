plugins {
    id("java")
}

group = "com.simra.konsumgandalf"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("com.opencsv:opencsv:5.9")
    implementation("org.hibernate.orm:hibernate-spatial:6.6.2.Final")
    implementation("org.n52.jackson:jackson-datatype-jts:1.2.10")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    mainClass = ""
}
