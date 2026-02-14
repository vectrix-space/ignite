import com.diffplug.gradle.spotless.FormatExtension

fun FormatExtension.applyCommon() {
  trimTrailingWhitespace()
  endWithNewline()
  leadingTabsToSpaces(2)
}
