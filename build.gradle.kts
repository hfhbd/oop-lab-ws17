plugins {
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev729"
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

compose.desktop {
    application {
        mainClass = "fh.ws17.conterm.gui.MainKt"
    }
}
