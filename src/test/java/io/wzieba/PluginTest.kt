package io.wzieba

import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PluginTest {

    @BeforeEach
    fun clear() {
        File("build/functionalTest").deleteRecursively()
    }

    @Test
    fun maxWorkersAreNotUpdated() {
        val firstRun = runner("help", "-Dorg.gradle.workers.max=5").build()
        assertTrue(firstRun.output.contains("Max workers: 5"))

        val secondRun = runner("help", "-Dorg.gradle.workers.max=3").build()
        assertTrue(secondRun.output.contains("Max workers: 3"))
    }

    @Test
    fun configureOnDemandAreNotUpdated() {
        val firstRun = runner("help", "-Dorg.gradle.configureondemand=true").build()
        assertTrue(firstRun.output.contains("Configure on demand: true"))

        val secondRun = runner("help", "-Dorg.gradle.configureondemand=false").build()
        assertTrue(secondRun.output.contains("Configure on demand: false"))
    }

    private fun runner(vararg arguments: String): GradleRunner {
        val projectDir = File("build/functionalTest")
            .apply {
                mkdirs()
                resolve("settings.gradle.kts").writeText("")
                resolve("build.gradle.kts").writeText(
                    """
                    plugins {
                        id("io.wzieba.reproduction")
                    }
                    """.trimIndent()
                )
            }

        val runner = GradleRunner.create()
            .apply {
                withPluginClasspath()
                withArguments(*arguments, "--configuration-cache", "--stacktrace")
                withProjectDir(projectDir)
            }
        return runner
    }
}