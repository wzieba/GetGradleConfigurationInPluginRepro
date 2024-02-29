@file:Suppress("UnstableApiUsage")

package io.wzieba

import javax.inject.Inject
import kotlin.time.ExperimentalTime
import org.gradle.StartParameter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.flow.FlowAction
import org.gradle.api.flow.FlowParameters
import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.Input

@ExperimentalTime
class Plugin @Inject constructor(
    private val flowScope: FlowScope,
    private val flowProviders: FlowProviders,
) : Plugin<Project> {
    override fun apply(project: Project) {
        val maxWorkersProvider = project.providers.of(
            MaxWorkersProvider::class.java
        ) {
            it.parameters.startParameter.set(project.gradle.startParameter)
        }

        maxWorkersProvider.get()

//        project.logger.printData(
//            provider.get(),
//            project.gradle.startParameter.isConfigureOnDemand,
//            project.gradle.startParameter.taskNames[0]
//        )
    }
}

abstract class MaxWorkersProvider : ValueSource<Int, MaxWorkersProvider.Parameters> {
    interface Parameters : ValueSourceParameters {
        @get:Input
        val startParameter: Property<StartParameter>
    }

    override fun obtain(): Int {
        return parameters.startParameter.get().maxWorkerCount
    }
}

class BuildFinishedFlowAction : FlowAction<BuildFinishedFlowAction.Parameters> {
    interface Parameters : FlowParameters {
        @get:Input
        val maxWorkers: Property<Int>

        @get:Input
        val configureOnDemand: Property<Boolean>

        @get:Input
        val task: Property<String>

        @get:Input
        val logger: Property<Logger>
    }

    override fun execute(parameters: Parameters) {
        println(
            parameters.logger.get().printData(
                parameters.maxWorkers.get(),
                parameters.configureOnDemand.get(),
                parameters.task.get()
            )
        )
    }
}

fun Logger.printData(workers: Int, configureOnDemand: Boolean, task: String) {
    error("Max workers: $workers")
    error("Configure on demand: $configureOnDemand")
    error("Task: $task")
}