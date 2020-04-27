import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
    implementation("org.openmhealth.schema:omh-schema-sdk:1.2.1")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("org.postgresql:postgresql")
    implementation("com.vladmihalcea:hibernate-types-52:2.9.5")
    implementation("com.h2database:h2:1.3.148")
    implementation("com.google.guava:guava:23.0")
    implementation("com.github.scribejava:scribejava-core:6.9.0")
    implementation("io.vertx:vertx-core:3.8.5")
    implementation("io.vertx:vertx-lang-kotlin:3.8.5")
    implementation("io.vertx:vertx-web:3.8.5")
    implementation("io.vertx:vertx-web-client:3.8.5")
    implementation("io.vertx:vertx-rx-java2:3.8.5")
    implementation("io.vertx:vertx-auth-oauth2:3.8.5")
    implementation("io.vertx:vertx-pg-client:3.9.0")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:3.9.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.github.maricn:logback-slack-appender:1.4.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("io.vertx:vertx-junit5:3.8.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.5")
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
