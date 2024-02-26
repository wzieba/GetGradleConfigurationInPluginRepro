@file:Suppress("UnstableApiUsage")

package io.wzieba

import javax.inject.Inject
import kotlin.time.ExperimentalTime
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.flow.FlowAction
import org.gradle.api.flow.FlowParameters
import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.build.event.BuildEventsListenerRegistry

@ExperimentalTime
class Plugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry,
    private val flowScope: FlowScope,
    private val flowProviders: FlowProviders,
) : Plugin<Project> {
    override fun apply(project: Project) {
        project.afterEvaluate {
            project.logger.printData(
                project.gradle.startParameter.maxWorkerCount,
                project.gradle.startParameter.isConfigureOnDemand,
                project.gradle.startParameter.taskNames[0]
            )
            project.logger.error(
                "Max workers: ${project.gradle.startParameter.maxWorkerCount}"
            )
            project.logger.error(
                "Configure on demand: ${project.gradle.startParameter.isConfigureOnDemand}"
            )
        }

        flowScope.always(BuildFinishedFlowAction::class.java) { spec ->
            spec.parameters.maxWorkers.set(project.gradle.startParameter.maxWorkerCount)
            spec.parameters.configureOnDemand.set(project.gradle.startParameter.isConfigureOnDemand)
            spec.parameters.task.set(project.gradle.startParameter.taskNames[0])
            spec.parameters.logger.set(project.logger)
        }
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