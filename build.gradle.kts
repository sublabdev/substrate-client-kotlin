import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `maven-publish`
}

group = "dev.sublab"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://repo.repsy.io/mvn/chrynan/public") } // Kotlin SecureRandom
}

val kotlinVersion: String by project
val kotlinxSerializationJsonVersion: String by project
val kotlinxCoroutinesVersion: String by project
val ktorVersion: String by project
val kotlinExtlibVersion: String by project
val kotlinxDateTimeVersion: String by project
val okioVersion: String by project
val sublabCommonVersion: String by project
val sublabScaleVersion: String by project
val sublabHashingVersion: String by project
val sublabEncryptingVersion: String by project

dependencies {
    testImplementation(kotlin("test"))

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    // Kotlin X
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJsonVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesVersion")

    // Ktor
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")

    // Kotlin platform
    implementation("org.kotlinextra:kotlin-extlib-jvm:$kotlinExtlibVersion") // TODO: resolve to all platforms
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDateTimeVersion")
    implementation("com.squareup.okio:okio:$okioVersion")

    // Sublab
    implementation("dev.sublab:common-kotlin:$sublabCommonVersion")
    implementation("dev.sublab:scale-codec-kotlin:$sublabScaleVersion")
    implementation("dev.sublab:hashing-kotlin:$sublabHashingVersion")
    implementation("dev.sublab:encrypting-kotlin:$sublabEncryptingVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}