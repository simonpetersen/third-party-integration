import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"
    kotlin("plugin.serialization") version "1.3.61"
}

group = "dtu.openhealth"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
    implementation("com.google.guava:guava:23.0")
    implementation("org.openmhealth.schema:omh-schema-sdk:1.2.1")
    implementation("com.github.scribejava:scribejava-core:6.9.0")
    implementation("com.github.scribejava:scribejava-httpclient-ahc:6.9.0")
    implementation("io.vertx:vertx-core:3.8.5")
    implementation("io.vertx:vertx-lang-kotlin:3.9.0")
    implementation("io.vertx:vertx-web:3.8.5")
    implementation("io.vertx:vertx-web-client:3.8.5")
    implementation("io.vertx:vertx-rx-java2:3.8.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.4")
    implementation("io.vertx:vertx-kafka-client:3.8.5") {
        exclude("org.slf4j")
        exclude("log4j")
    }
    implementation("io.vertx:vertx-auth-oauth2:3.8.5")
    implementation("io.vertx:vertx-pg-client:3.9.0")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:3.9.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.github.maricn:logback-slack-appender:1.4.0")
    implementation("io.vertx:vertx-config:3.9.0")
    implementation("io.vertx:vertx-config-vault:3.9.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("io.vertx:vertx-junit5:3.8.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes("Main-Class" to "dtu.openhealth.integration.IntegrationApplicationKt")
        }
        from(configurations.runtimeClasspath.get()
                .onEach { println("add from dependencies: ${it.name}") }
                .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
    }
}


tasks.withType<AbstractArchiveTask> {setProperty("archiveFileName", "integration.jar")}

