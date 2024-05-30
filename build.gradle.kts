import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("fabric-loom") version "1.6-SNAPSHOT"
    kotlin("jvm") version "1.2.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("com.modrinth.minotaur") version "2.+"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("java")
}

group = "me.obsilabor"
version = "1.0.4+mc1.20.6"

repositories {
    mavenCentral()
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com")
}

dependencies {
    // kotlin
    implementation(kotlin("stdlib"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    // fabric
    minecraft("com.mojang:minecraft:1.20.6")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.15.11")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.99.4+1.20.6")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.11.0+kotlin.2.0.0")
    // modmenu & clothconfig
    modApi("com.terraformersmc:modmenu:10.0.0-beta.1") {
        exclude("net.fabricmc.fabric-api")
    }
    modApi("me.shedaniel.cloth:cloth-config-fabric:14.0.126") {
        exclude("net.fabricmc.fabric-api")
    }
}


kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version
        )

        inputs.properties(props)

        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }
    compileJava {
        options.release.set(21)
        options.encoding = "UTF-8"
    }
    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
    named("curseforge") {
        onlyIf {
            System.getenv("CURSEFORGE_TOKEN") != null
        }
        dependsOn(remapJar)
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("VP0RHYaP")
    versionNumber.set(project.version.toString())
    versionType.set("release")
    gameVersions.addAll(listOf("1.20.6"))
    loaders.add("fabric")
    loaders.add("quilt")
    dependencies {
        required.project("Ha28R6CL") // fabric language kotlin
        required.project("9s6osm5g") // cloth config
        optional.project("mOgUt4GM") // mod menu
    }

    uploadFile.set(tasks.remapJar.get())
}

curseforge {
    project(closureOf<CurseProject> {
        apiKey = System.getenv("CURSEFORGE_TOKEN")
        mainArtifact(tasks.remapJar.get())

        id = "632589"
        releaseType = "release"
        addGameVersion("1.20.6")
        addGameVersion("Fabric")
        addGameVersion("Quilt")

        relations(closureOf<CurseRelation> {
            requiredDependency("cloth-config")
            requiredDependency("fabric-language-kotlin")
            optionalDependency("modmenu")
        })
    })
    options(closureOf<Options> {
        forgeGradleIntegration = false
    })
}