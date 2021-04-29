/*
 * Copyright (C) 2021. Maksym Oliinyk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.

installGitHooks()

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
}

allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
    }

    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "1.8"
            // disables warning about usage of experimental Kotlin features
            @Suppress("SuspiciousCollectionReassignment")
            freeCompilerArgs += listOf(
                // todo: cleanup after migration to kotlin 1.5
                "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
                "-Xuse-experimental=kotlin.Experimental",
                "-Xuse-experimental=kotlin.contracts.ExperimentalContracts",
                "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xuse-experimental=io.ktor.util.KtorExperimentalAPI",
                "-Xuse-experimental=kotlin.ExperimentalStdlibApi",
                "-XXLanguage:+NewInference",
                "-XXLanguage:+InlineClasses"
            )
        }
    }

    if (isCiEnv) {

        logger.info("Modifying tests output")

        tasks.withType<Test>().all {
            reports {
                html.destination =
                    file("${rootProject.buildDir}/junit-reports/${project.name}/html")
                junitXml.destination =
                    file("${rootProject.buildDir}/junit-reports/${project.name}/xml")
            }
        }
    } else {
        logger.info("Default tests output")
    }
}

val detektAll by tasks.registering(Detekt::class) {
    description = "Runs analysis task over whole codebase"
    debug = false
    parallel = true
    ignoreFailures = false
    disableDefaultRuleSets = false
    buildUponDefaultConfig = true
    setSource(files(projectDir))
    config.setFrom(detektConfig)
    baseline.set(detektBaseline)

    include("**/*.kt", "**/*.kts")
    exclude("resources/", "**/build/**", "**/test/java/**")

    reports {
        xml.enabled = false
        txt.enabled = false
        html.enabled = true
    }
}

val detektProjectBaseline by tasks.registering(DetektCreateBaselineTask::class) {
    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(files(rootDir))
    config.setFrom(detektConfig)
    baseline.set(detektBaseline)
    include("**/*.kt", "**/*.kts")
    exclude("**/resources/**", "**/build/**")
}

val detektFormat by tasks.registering(Detekt::class) {
    parallel = true
    autoCorrect = true
    buildUponDefaultConfig = true
    failFast = false
    ignoreFailures = false
    setSource(files(projectDir))

    include("**/*.kt", "**/*.kts")
    exclude("**/resources/**", "**/build/**")

    config.setFrom(detektConfig)
}

val releaseAll by tasks.registering(DefaultTask::class) {
    group = "release"
    description = "Runs release tasks for each project"

    setDependsOn(libraryProjects.releaseTasks + pluginProject.releaseTask)
}

val ciTests by tasks.registering(Test::class) {
    group = "verification"
    description = "Prepares and runs tests relevant for CI build"

    setDependsOn(libraryProjects.tests + pluginProject.test)
}

val Project.releaseTask: Task
    get() = tasks.findByName("releaseLibrary")
        ?: tasks.findByName("releasePlugin")
        ?: error("Couldn't find release task for project $name")

val libraryProjects: Collection<Project>
    get() = subprojects.filter { project -> project.plugins.hasPlugin("published-library") }

val pluginProject: Project
    get() = subprojects.find { project -> project.plugins.hasPlugin("org.jetbrains.intellij") }
        ?: error("No plugin project found")

val Iterable<Project>.releaseTasks: Collection<Task>
    get() = mapNotNull { project -> project.releaseTask }

val Iterable<Project>.tests: Collection<Test>
    get() = mapNotNull { project -> project.test }

val Project.test: Test
    get() = tasks.test.get() ?: error("No test task found in project $name")
