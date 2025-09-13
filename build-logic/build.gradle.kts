import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(libs.build.nexus)
  implementation(libs.build.shadow)
  implementation(libs.build.spotless)
}

dependencies {
  compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
  target {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_11
    }
  }
}
