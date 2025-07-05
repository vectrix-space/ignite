plugins {
  id("io.github.gradle-nexus.publish-plugin")
}

nexusPublishing {
  repositories {
    sonatype()
  }

  repositories {
    sonatype {
      nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
      snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
    }
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.layout.buildDirectory)
}
