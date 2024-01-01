plugins {
  id("ignite.launcher-conventions")
}

dependencies {
  implementation(project(":ignite-api"))

  implementation(libs.tinylog.impl)

  implementation(libs.mixin) {
    exclude(group = "com.google.guava")
    exclude(group = "com.google.code.gson")
    exclude(group = "org.ow2.asm")
  }

  implementation(libs.mixinExtras) {
    exclude(group = "org.apache.commons")
  }

  implementation(libs.accessWidener)
  implementation(libs.asm)
  implementation(libs.asm.analysis)
  implementation(libs.asm.commons)
  implementation(libs.asm.tree)
  implementation(libs.asm.util)

  implementation(libs.gson)
}
