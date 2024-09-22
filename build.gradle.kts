import org.checkerframework.gradle.plugin.CheckerFrameworkExtension

plugins {
  id("java")
  id("application")
  id("com.github.ben-manes.versions") version "0.51.0"
  id("io.freefair.lombok") version "6.3.0"
  id("org.checkerframework") version "0.6.44"
  id("com.diffplug.spotless") version "7.0.0.BETA2"
}

group = "mx.oscarvarto"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
  implementation("org.functionaljava:functionaljava:5.0")
  implementation("com.google.guava:guava:33.3.0-jre")
  implementation("ch.qos.logback:logback-classic:1.5.8")
  implementation("org.slf4j:slf4j-api:2.1.0-alpha1")

  compileOnly("org.checkerframework:checker-qual:3.47.0")
  testCompileOnly("org.checkerframework:checker-qual:3.47.0")
  checkerFramework("org.checkerframework:checker:3.47.0")
  implementation("org.checkerframework:checker-util:3.47.0")

  // testImplementation(platform("org.junit:junit-bom:5.11.0"))
  // testImplementation("org.junit.jupiter:junit-jupiter")
  // testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  testImplementation("org.testng:testng:7.7.0") // Compatible with gradle 7.3
  testImplementation("org.assertj:assertj-core:3.26.3")

  compileOnly("org.projectlombok:lombok:1.18.34")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

application {
  // applicationDefaultJvmArgs = listOf("-Astubs=collection-object-parameters-may-be-null.astub")
}

val patchModuleArg = "--patch-module=jdk.compiler=" +
    files(sourceSets["main"].java.srcDirs, sourceSets["test"].java.srcDirs).asPath

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf(
        "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
        patchModuleArg,
        "-Xmaxerrs", "10000",
        "-Xmaxwarns", "10000",
        "-Awarns"
    ))
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED")
}

spotless {
    java {
        eclipse().configFile("${rootProject.projectDir}/eclipse-java-google-style.xml")
        importOrder("", "\\#")
            .wildcardsLast(false)
            .semanticSort()
        removeUnusedImports()
        formatAnnotations()
    }
}

configure<CheckerFrameworkExtension> {
  checkers = listOf("org.checkerframework.checker.nullness.NullnessChecker")
}

tasks.test {
    // useJUnitPlatform()
    useTestNG()
    maxHeapSize = "8G"
}

tasks.wrapper {
    gradleVersion = "7.3"
}
