import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(libs.build.indra)
  implementation(libs.build.indra.sonatype)
  implementation(libs.build.indra.spotless)
  implementation(libs.build.shadow)
}

dependencies {
  compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
  target {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_17
    }
  }
}
