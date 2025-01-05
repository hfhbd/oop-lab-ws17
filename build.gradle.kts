plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.licensee)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.components.resources)

    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.coroutines.test)
}

kotlin {
    jvmToolchain(21)
}

compose.desktop {
    application {
        mainClass = "fh.ws17.conterm.gui.MainKt"
    }
}

licensee {
    allow("Apache-2.0")
}
