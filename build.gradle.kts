import org.jetbrains.kotlin.gradle.plugin.mpp.*
import org.jetbrains.kotlin.gradle.targets.js.dsl.*
import org.jetbrains.kotlin.gradle.targets.js.nodejs.*
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.*

plugins {
    kotlin("multiplatform") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
}

repositories {
    mavenCentral()
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    jvmToolchain(8)

    jvm()
    js {
        nodejs()
        browser()
    }
    wasmJs {
        nodejs()
        browser()
    }
    wasmWasi {
        nodejs()
    }

    macosArm64()
    macosX64()
    linuxX64()
    mingwX64()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.3")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.6.3")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }

    // release mode
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.test(listOf(NativeBuildType.RELEASE))
    }
    targets.withType<KotlinNativeTargetWithTests<*>>().configureEach {
        testRuns.create("releaseTest") {
            setExecutionSourceFrom(binaries.getTest(NativeBuildType.RELEASE))
        }
    }
}

plugins.withType<NodeJsRootPlugin> {
    // node version with wasm support
    extensions.configure<NodeJsRootExtension> {
        nodeVersion = "21.0.0-v8-canary202310177990572111"
        nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
    }

    // because of custom nodejs version
    tasks.withType<KotlinNpmInstallTask>().configureEach {
        args.add("--ignore-engines")
    }
}
