plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
}

intellij {
    pluginName.set("nyantray")
    version.set("212.5712.43")
    type.set("IC")
}

group = "org.jraf"
version = "1.3.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:_")
    implementation(Kotlin.stdlib.jdk8)
    implementation(KotlinX.coroutines.core)
}

tasks {
    patchPluginXml {
        sinceBuild.set("212")
        untilBuild.set("241.*")
    }

    compileKotlin {
        dependsOn("generateVersionKt")
    }

    compileTestKotlin {
        dependsOn("generateVersionKt")
    }
}

kotlin {
    jvmToolchain(11)
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

// `./gradlew refreshVersions` to update dependencies
// `./gradlew signPlugin` to build new version
