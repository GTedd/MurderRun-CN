plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.4"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    compileOnly(project(":nms-api"))
}