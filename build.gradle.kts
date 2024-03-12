plugins {
    java
}



tasks.withType<JavaCompile>{
    options.encoding = "UTF-8"
}
tasks.withType<Javadoc>{
    options.encoding = "UTF-8"
}


allprojects {

    apply(plugin = "java")
    apply(plugin = "java-library")
    group = "org.lee.study"
    version = "1.0-SNAPSHOT"

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        mavenCentral()
    }

    tasks.withType<JavaCompile>{
        options.encoding = "UTF-8"
    }
    tasks.test{
        useJUnitPlatform()
        jvmArgs(
            "-Djunit.jupiter.execution.parallel.config.dynamic.factor=8",
            "-Djunit.jupiter.execution.parallel.config.fixed.parallelism=8",
        )
    }
    dependencies{

        implementation(rootProject.libs.jctools)
        compileOnly("org.projectlombok", "lombok", "1.18.20")
        annotationProcessor("org.projectlombok", "lombok", "1.18.20")
        implementation("org.slf4j:slf4j-api:2.0.5")
        implementation("ch.qos.logback:logback-core:1.4.5")
        implementation("ch.qos.logback:logback-classic:1.4.5")

        testImplementation("org.mockito:mockito-all:1.10.19")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    }

}
