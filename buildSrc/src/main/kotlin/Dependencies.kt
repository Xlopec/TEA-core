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

import Libraries.Versions.accompanies
import Libraries.Versions.compose
import Libraries.Versions.coroutines
import Libraries.Versions.ktor

const val kotlinVersion = "1.4.32"

object Libraries {
    object Versions {
        const val coroutines = "1.4.2"
        const val ktor = "1.5.1"
        const val compose = "1.0.0-beta05"
        const val accompanies = "0.8.1"
    }

    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines"
    const val coroutinesSwing = "org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutines"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"

    const val ktorServerCore = "io.ktor:ktor-server-core:$ktor"
    const val ktorServerNetty = "io.ktor:ktor-server-netty:$ktor"
    const val ktorServerWebsockets = "io.ktor:ktor-websockets:$ktor"

    const val ktorClientWebsockets = "io.ktor:ktor-client-websockets:$ktor"
    const val ktorClientOkHttp = "io.ktor:ktor-client-okhttp:$ktor"
    const val gson = "com.google.code.gson:gson:2.8.6"

    const val stitch = "4.1.0"
    const val appcompat = "1.2.0"

    const val logback = "ch.qos.logback:logback-classic:1.2.3"
    const val atomicfu = "org.jetbrains.kotlinx:atomicfu:0.15.1"
    const val immutableCollections = "org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.3"

    const val composeUi = "androidx.compose.ui:ui:$compose"
    const val composeFoundation = "androidx.compose.foundation:foundation:$compose"
    const val composeFoundationLayout = "androidx.compose.foundation:foundation-layout:$compose"
    const val composeMaterial = "androidx.compose.material:material:$compose"
    const val composeMaterialIconsExtended =
        "androidx.compose.material:material-icons-extended:$compose"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling:$compose"
    const val composeRuntime = "androidx.compose.runtime:runtime:$compose"
    const val composeAnimation = "androidx.compose.animation:animation:$compose"
    const val composeCompiler = "androidx.compose.compiler:compiler:$compose"
    const val composeActivity = "androidx.activity:activity-compose:1.3.0-alpha07"

    const val accompaniestInsets = "com.google.accompanist:accompanist-insets:$accompanies"
    const val accompaniestCoil = "com.google.accompanist:accompanist-coil:$accompanies"
    const val accompaniestSwipeRefresh =
        "com.google.accompanist:accompanist-swiperefresh:$accompanies"

    const val mongoDb = "org.mongodb:stitch-android-sdk:$stitch"

    const val appCompat = "androidx.appcompat:appcompat:$appcompat"

    const val okHttp = "com.squareup.okhttp3:okhttp:4.8.1"
    const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
    const val converterGson = "com.squareup.retrofit2:converter-gson:2.9.0"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.8.1"
}

object TestLibraries {
    private object Versions {
        const val ktor = "1.5.1"
    }

    const val junit = "junit:junit:4.13.1"
    const val junitRunner = "io.kotlintest:kotlintest-runner-junit4:3.4.2"
    const val espressoRunner = "androidx.test:runner:1.3.0"
    const val espressoCore = "androidx.test.espresso:espresso-core:3.3.0"
    const val ktorMockJvm = "io.ktor:ktor-client-mock-jvm:${Versions.ktor}"
    const val ktorServerTests = "io.ktor:ktor-server-tests:${Versions.ktor}"
}
