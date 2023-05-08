plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose)
    alias(libs.plugins.licensee)
}

dependencies {
    implementation(compose.desktop.currentOs)
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.coroutines.test)
}

kotlin {
    jvmToolchain(11)

    sourceSets {
        configureEach {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
    }
}

compose.desktop {
    application {
        mainClass = "fh.ws17.conterm.gui.MainKt"
    }
}

licensee {
    allow("Apache-2.0")
}
