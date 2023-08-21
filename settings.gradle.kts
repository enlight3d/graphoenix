pluginManagement {
  val quarkusVersion: String by settings
  val kotlinVersion: String by settings
  val jnxplusGradlePluginVersion: String by settings
  plugins {
    id("io.quarkus") version quarkusVersion
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    id("io.github.khalilou88.jnxplus") version jnxplusGradlePluginVersion
  }
}

rootProject.name = "nx-cloud-ce"
