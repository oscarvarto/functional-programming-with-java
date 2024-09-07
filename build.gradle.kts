import org.checkerframework.gradle.plugin.CheckerFrameworkExtension

plugins {
    id("java")
    id("application")
    id("com.github.ben-manes.versions") version "0.51.0"
    id("io.freefair.lombok") version "8.10"
    id("org.checkerframework") version "0.6.44"
}

group = "mx.oscarvarto"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    //maven(url = "https://projectlombok.org/edge-releases")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

lombok {
    version = "1.18.34"
}

dependencies {
    implementation("org.functionaljava:functionaljava:5.0")
    implementation("com.google.guava:guava:33.3.0-jre")
    implementation("ch.qos.logback:logback-classic:1.5.8")
    implementation("org.slf4j:slf4j-api:2.1.0-alpha1")

    implementation("org.graalvm.polyglot:polyglot:24.0.2")
    implementation("org.graalvm.polyglot:python:24.0.2")
    implementation("org.graalvm.polyglot:tools:24.0.2")
    implementation("org.graalvm.polyglot:dap:24.0.2")

    compileOnly("org.checkerframework:checker-qual:3.47.0")
    testCompileOnly("org.checkerframework:checker-qual:3.47.0")
    checkerFramework("org.checkerframework:checker:3.47.0")
    implementation("org.checkerframework:checker-util:3.47.0")

    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.assertj:assertj-core:3.26.3")

    compileOnly("org.projectlombok:lombok:1.18.34")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    applicationDefaultJvmArgs = listOf("-Astubs=collection-object-parameters-may-be-null.astub")
}

configure<CheckerFrameworkExtension> {
    checkers = listOf(
        "org.checkerframework.checker.nullness.NullnessChecker"
    )
}

tasks.test {
    useJUnitPlatform()
}
