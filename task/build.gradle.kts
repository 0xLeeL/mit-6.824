plugins {
    id("java")
}

group = "org.lee.study"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.apache.curator:curator-recipes:5.8.0")
}

tasks.test {
    useJUnitPlatform()
}