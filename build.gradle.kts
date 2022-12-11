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
    dependencies{
        implementation("org.slf4j:slf4j-api:2.0.5")
        implementation("ch.qos.logback:logback-core:1.4.5")
        implementation("ch.qos.logback:logback-classic:1.4.5")
    }
}
