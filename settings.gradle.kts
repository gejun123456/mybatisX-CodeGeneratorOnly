pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "MybatisX-CodeGeneratorOnly"
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
