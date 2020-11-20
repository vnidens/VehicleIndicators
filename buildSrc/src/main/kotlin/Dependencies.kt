import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 15.11.2020
 */

object Versions {
    const val `kotlin-coroutines` = "1.4.0"

    const val slf4j = "1.7.30"
}

fun DependencyHandler.kotlinx(module: String, version: String): String =
    "org.jetbrains.kotlinx:$module:$version"

fun DependencyHandler.`kotlinx-coroutines-android`(version: String? = null): String =
        kotlinx("kotlinx-coroutines-android", version ?: Versions.`kotlin-coroutines`)

fun DependencyHandler.slf4j(version: String? = null): String =
    "org.slf4j:slf4j-api:${version ?: Versions.slf4j}"