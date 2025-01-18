plugins {
    id("java")
}

group = "com.simra.konsumgandalf"
version = "0.0.2-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.google.guava:guava:33.3.1-jre")
}

tasks.test {
    useJUnitPlatform()
}
