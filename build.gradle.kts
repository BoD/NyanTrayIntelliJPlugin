buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.intellij") version "0.4.21"
    id("com.github.ben-manes.versions") version "0.28.0"
}

apply(plugin = "idea")
apply(plugin = "org.jetbrains.intellij")

intellij {
    version = "IC-2019.1"
    pluginName = "nyantray"
}

group = "org.jraf"
version = "1.2.0"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "6.5"
    }

    patchPluginXml {
        untilBuild(null)
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        dependsOn("generateVersionKt")
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
        dependsOn("generateVersionKt")
    }
}

// Generate a Version.kt file with a constant for the version name
tasks.register("generateVersionKt") {
    val outputDir = layout.buildDirectory.dir("generated/source/kotlin").get().asFile
    outputs.dir(outputDir)
    doFirst {
        val outputWithPackageDir = File(outputDir, "org/jraf/intellijplugin/nyantray").apply { mkdirs() }
        File(outputWithPackageDir, "Version.kt").writeText(
            """
                package org.jraf.intellijplugin.nyantray

                const val VERSION = "v${project.version}"
            """.trimIndent()
        )
    }
}

kotlin {
    sourceSets["main"].kotlin.srcDir(tasks.getByName("generateVersionKt").outputs.files)
}
