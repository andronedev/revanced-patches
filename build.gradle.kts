import org.gradle.kotlin.dsl.support.listFilesOrdered

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.binary.compatibility.validator)
    `maven-publish`
    signing
}

group = "app.revancedcontributor"

repositories {
    mavenCentral()
    mavenLocal()
    google()
    maven {
        // A repository must be speficied for some reason. "registry" is a dummy.
        url = uri("https://maven.pkg.github.com/revanced/registry")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation(libs.revanced.patcher)
    implementation(libs.smali)
}

kotlin {
    jvmToolchain(11)
}

tasks {
    withType(Jar::class) {
        manifest {
            attributes["Name"] = "ReVanced Patches"
            attributes["Description"] = "Patches for ReVanced."
            attributes["Version"] = version
            attributes["Timestamp"] = System.currentTimeMillis().toString()
            attributes["Source"] = "git@github.com:andronedev/revanced-patches.git"
            attributes["Author"] = "andronedev"
            attributes["Contact"] = "contact@nomail.com"
            attributes["Origin"] = "https://revanced.app"
            attributes["License"] = "GNU General Public License v3.0"
        }
    }

    register("buildDexJar") {
        description = "Build and add a DEX to the JAR file"
        group = "build"

        dependsOn(build)

        doLast {
            val d8 = File(System.getenv("ANDROID_HOME")).resolve("build-tools")
                .listFilesOrdered().last().resolve("d8").absolutePath

            val patchesJar = configurations.archives.get().allArtifacts.files.files.first().absolutePath
            val workingDirectory = layout.buildDirectory.dir("libs").get().asFile

            exec {
                workingDir = workingDirectory
                commandLine = listOf(d8, "--release", patchesJar)
            }

            exec {
                workingDir = workingDirectory
                commandLine = listOf("zip", "-u", patchesJar, "classes.dex")
            }
        }
    }

    // Needed by gradle-semantic-release-plugin.
    // Tracking: https://github.com/KengoTODA/gradle-semantic-release-plugin/issues/435
    publish {
        dependsOn("buildDexJar")
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/revanced/revanced-patches-template")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("revanced-patches-publication") {
            from(components["java"])

            pom {
                name = "ReVanced Patches template"
                description = "Patches template for ReVanced."
                url = "https://revanced.app"

                licenses {
                    license {
                        name = "GNU General Public License v3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                    }
                }
                developers {
                    developer {
                        id = "ReVanced"
                        name = "ReVanced"
                        email = "contact@revanced.app"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/andronedev/revanced-patches.git"
                    developerConnection = "scm:git:git@github.com:andronedev/revanced-patches.git"
                    url = "https://github.com/andronedev/revanced-patches"
                }
            }
        }
    }
}

signing {
    useGpgCmd()

    sign(publishing.publications["revanced-patches-publication"])
}
