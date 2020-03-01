import java.io.File
import java.lang.IllegalArgumentException

class BuildVersion(
    private val major: Int = 1,
    private val minor: Int = 0,
    private val patch: Int = 0,
    private val preRelease: String? = null,
    private val buildMetadata: String? = null
) {
    companion object {

        private val VERSION_PATTERN = Regex(
            """(0|[1-9]\d*)?(?:\.)?(0|[1-9]\d*)?(?:\.)?(0|[1-9]\d*)?(?:-([\dA-z\-]+(?:\.[\dA-z\-]+)*))?(?:\+([\dA-z\-]+(?:\.[\dA-z\-]+)*))?"""
        )

        private val PRE_RELEASE_PATTERN = Regex(
            """[\dA-z\-]+(?:\.[\dA-z\-]+)*"""
        )

        private val BUILD_METADATA_PATTERN = Regex(
            """[\dA-z\-]+(?:\.[\dA-z\-]+)*"""
        )

        @JvmStatic
        fun parse(versionText: String): BuildVersion {
            val result = VERSION_PATTERN.matchEntire(versionText)
                ?: throw IllegalArgumentException("Unable to parse build version: $versionText")
            return BuildVersion(
                major = result.groupValues[1].toInt(),
                minor = result.groupValues[2].toInt(),
                patch = result.groupValues[3].toInt(),
                preRelease = if (result.groupValues[4].isEmpty()) null else result.groupValues[4],
                buildMetadata = if (result.groupValues[5].isEmpty()) null else result.groupValues[5]
            )
        }

        @JvmStatic
        fun parse(versionFile: File): BuildVersion =
            if (versionFile.exists() && versionFile.canRead()) {
                parse(versionFile.readText())
            } else {
                throw IllegalArgumentException("Unable to read version file: ${versionFile.path}")
            }
    }

    init {
        require(major >= 0) { "Major version must be a positive number" }
        require(minor >= 0) { "Minor version must be a positive number" }
        require(patch >= 0) { "Patch version must be a positive number" }
        require(major > 0 || minor > 0) { "Major or Minor version must be greater than 0" }

        preRelease?.let {
            require(it.matches(PRE_RELEASE_PATTERN)) { "Pre-release version is not valid" }
        }

        buildMetadata?.let {
            require(buildMetadata.matches(BUILD_METADATA_PATTERN)) { "Build metadata is not valid" }
        }
    }

    val versionCode: Int = major * 10000 + minor * 1000 + patch

    val versionName: String = buildString {
        append("$major.$minor.$patch")

        preRelease?.let {
            append("-$it")
        }

        buildMetadata?.let {
            append("+$it")
        }
    }
}
