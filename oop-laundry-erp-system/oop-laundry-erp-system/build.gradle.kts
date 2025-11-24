plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.12" apply false
}

group = "com.erp.laundry"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Kita definisikan versi Ktor yang akan dipakai
val ktor_version = "2.3.12"

dependencies {
    // --- INI YANG HILANG DARI FILE ANDA SEBELUMNYA ---

    // 1. Core Server (Wajib)
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")

    // 2. Engine Netty (Wajib buat jalanin server)
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

    // 3. Content Negotiation (Buat handle JSON)
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")

    // 4. Jackson Serialization (Buat ubah Objek <-> JSON)
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktor_version")

    // 5. Logging (Supaya error SLF4J hilang & bisa lihat log server)
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Testing
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}