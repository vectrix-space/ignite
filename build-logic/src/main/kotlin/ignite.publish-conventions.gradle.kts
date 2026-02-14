plugins {
  id("net.kyori.indra.publishing")
}

indra {
  signWithKeyFromPrefixedProperties("vectrix")
  configurePublications {
    pom {
      developers {
        developer {
          id = "VectrixDevelops"
          name = "Vectrix"
        }
      }
    }
  }
}
