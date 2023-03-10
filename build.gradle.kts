plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.compose") version "1.3.1"
    id("app.cash.licensee") version "1.6.0"
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    testImplementation(kotlin("test-junit"))
}

kotlin.jvmToolchain(11)

compose.desktop {
    application {
        mainClass = "fh.ws17.conterm.gui.MainKt"
    }
}

licensee {
    allow("Apache-2.0")
}
