plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.bybresh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.dv8tion:JDA:5.2.1")
    implementation("com.mysql:mysql-connector-j:9.1.0")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.12")
}
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}