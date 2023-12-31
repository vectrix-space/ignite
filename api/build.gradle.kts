plugins {
  id("ignite.publish-conventions")
}

dependencies {
  api(libs.tinylog.api)

  api(libs.mixin) {
    exclude(group = "com.google.guava")
    exclude(group = "com.google.code.gson")
    exclude(group = "org.ow2.asm")
  }

  api(libs.mixinExtras) {
    exclude(group = "org.apache.commons")
  }
}
