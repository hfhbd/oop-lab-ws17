plugins {
    kotlin("jvm") version "1.5.30"
    id("org.jetbrains.compose") version "1.0.0-alpha4-build328"
}

repositories {
    mavenCentral()
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
