plugins {
  id("net.kyori.indra.publishing")
}

indra {
  signWithKeyFromPrefixedProperties("vectrix")
  configurePublications {
    pom {
      developers {
        developer {
          id.set("vectrix")
          name.set("Vectrix")
        }
      }
    }
  }
}
