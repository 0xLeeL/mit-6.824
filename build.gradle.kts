plugins {
    java
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

////
////// 编译java文件时采用的UTF-8， 注意这是指定源码编码的字符集【源文件】
////    tasks.withType(JavaCompile) {
////        options.encoding = 'UTF-8'
////    }
////// 文档【源文件】
////    tasks.withType(Javadoc) {
////        options.encoding = 'UTF-8'
////    }
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        mavenCentral()
    }
}


