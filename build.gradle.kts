import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("org.jetbrains.intellij.platform")
  id("org.jetbrains.changelog")
  id("com.apollographql.apollo").version("5.2.0")
}

dependencies {
  implementation("com.apollographql.apollo:apollo-runtime:5.2.0")

  testImplementation("junit:junit:4.13.2")

  // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
  intellijPlatform {
    intellijIdea("2026.2.5")
    testFramework(TestFrameworkType.Platform)
  }
}

val generateVersionKtTask: TaskProvider<Task> = tasks.register("generateVersionKt") {
  description = "Generates Version.kt"
  val outputDir = layout.buildDirectory.dir("generated/source/kotlin").get().asFile
  outputs.dir(outputDir)
  val versionProvider = providers.provider { rootProject.version.toString() }
  inputs.property("version", versionProvider)
  doFirst {
    val outputWithPackageDir = File(outputDir, "org/jraf/intellijplugin/nyantray").apply { mkdirs() }
    File(outputWithPackageDir, "Version.kt").writeText(
      """
        package org.jraf.intellijplugin.nyantray
        const val VERSION = "v${versionProvider.get()}"
      """.trimIndent()
    )
  }
}

kotlin {
  sourceSets {
    main {
      kotlin {
        srcDir(generateVersionKtTask)
      }
    }
  }
}

apollo {
  service("nyan") {
    packageName.set("org.jraf.intellijplugin.nyantray.graphql")
  }
}

// `./gradlew signPlugin` to build new version
