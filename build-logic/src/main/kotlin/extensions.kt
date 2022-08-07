import net.kyori.indra.git.IndraGitExtension
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.named

fun Project.applyJarMetadata(moduleName: String) {
  if("jar" in tasks.names) {
    tasks.named<Jar>("jar") {
      val fullVersion = rootProject.version.toString().replace("-SNAPSHOT", "").split('.')
      var specificationVersion = fullVersion[0];
      if(fullVersion.size > 1) {
        specificationVersion += ("." + fullVersion[1]);
      } else {
        specificationVersion += ".0"
      }

      manifest.attributes(
        "Automatic-Module-Name" to moduleName,
        "Specification-Title" to moduleName,
        "Specification-Version" to specificationVersion,
        "Specification-Vendor" to "vectrix-space",
        "Implementation-Title" to moduleName,
        "Implementation-Version" to rootProject.version,
        "Implementation-Vendor" to "vectrix-space"
      )

      val indraGit = rootProject.extensions.findByType<IndraGitExtension>()
      indraGit?.applyVcsInformationToManifest(manifest)
    }
  }
}

fun Project.applyJarMetadata(modulePath: String, moduleName: String) {
  if("jar" in tasks.names) {
    tasks.named<Jar>("jar") {
      val fullVersion = rootProject.version.toString().replace("-SNAPSHOT", "").split('.')
      var specificationVersion = fullVersion[0];
      if(fullVersion.size > 1) {
        specificationVersion += ("." + fullVersion[1]);
      } else {
        specificationVersion += ".0"
      }

      manifest.attributes(modulePath,
        "Automatic-Module-Name" to moduleName,
        "Specification-Title" to moduleName,
        "Specification-Version" to specificationVersion,
        "Specification-Vendor" to "vectrix-space",
        "Implementation-Title" to moduleName,
        "Implementation-Version" to rootProject.version,
        "Implementation-Vendor" to "vectrix-space"
      )
    }
  }
}
