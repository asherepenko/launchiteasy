import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

enum class Increment {
    MAJOR,
    MINOR,
    PATCH
}

abstract class IncrementVersion : DefaultTask() {
    @get:Input
    var prodRelease: Boolean = false

    @get:Input
    abstract var increment: Increment

    @get:Input
    abstract var version: BuildVersion

    @TaskAction
    fun increment() {
        println("Incrementing ${increment.name} version...")

        val prevVersionCode = version.versionCode
        val prevVersionName = version.versionName

        if (prodRelease) {
            version.prepareProdRelease()
        }

        when (increment) {
            Increment.MAJOR -> {
                version.incrementMajor()
            }
            Increment.MINOR -> {
                version.incrementMinor()
            }
            Increment.PATCH -> {
                version.incrementPatch()
            }
        }

        println("$prevVersionName ($prevVersionCode) -> ${version.versionName} (${version.versionCode})")
    }
}
