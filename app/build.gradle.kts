import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.paper.PaperPluginDescription
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.tasks.Jar
import org.gradle.api.tasks.compile.JavaCompile

plugins {
    java

    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("de.eldoria.plugin-yml.paper") version "0.9.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.2.2"
}

group = "org.amethystdev"
version = "2.0.0"

repositories {

    mavenLocal()
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
    maven("https://repo.essentialsx.net/releases/")
}

dependencies {

    // Paper
    paperweight.paperDevBundle("26.1.2.build.+")
    paperLibrary("com.google.code.gson:gson:2.13.1")

    // Lamp command framework
    val lampVersion = "4.0.0-rc.12"
    val HikariCPVersion = "7.0.2"
    val gsonVersion = "2.14.0"
    val mariadbVersion = "3.5.3"
    val sqliteVersion = "3.53.1.0"
    val EssentialsXVersion = "2.21.2"
    val lettuceVersion = "7.6.0.RELEASE"

    implementation("io.github.revxrsal:lamp.common:$lampVersion")
    implementation("io.github.revxrsal:lamp.bukkit:$lampVersion")
    implementation("com.zaxxer:HikariCP:$HikariCPVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("org.mariadb.jdbc:mariadb-java-client:$mariadbVersion")
    implementation("org.xerial:sqlite-jdbc:$sqliteVersion")

    compileOnly("net.essentialsx:EssentialsX:$EssentialsXVersion") {
        isTransitive = false
    }
}

java {

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }

    withSourcesJar()
    withJavadocJar()
}

tasks {

    withType<JavaCompile>().configureEach {

        options.release.set(25)
        options.encoding = "UTF-8"

        // Preserve parameter names
        options.compilerArgs.add("-parameters")
    }

    withType<Jar>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    processResources {
        filteringCharset = "UTF-8"
    }

    build {
        dependsOn(shadowJar)
    }

    named<ShadowJar>("shadowJar") {

        archiveBaseName.set("SleepPolls")
        archiveClassifier.set("")

        relocate(
            "io.github.revxrsal",
            "${project.group}.libs.lamp"
        )

        mergeServiceFiles()
    }

    generatePaperPluginDescription {
        useDefaultCentralProxy()
    }

    runServer {

        minecraftVersion("26.1.2")

        jvmArgs(
            "-Xms2G",
            "-Xmx2G"
        )

        downloadPlugins {

            // ServiceIO
            hangar("ServiceIO", "2.6.0")

            // WorldEdit
            modrinth("worldedit", "k9KdTr1M")

            // LuckPerms
            url("https://download.luckperms.net/1641/bukkit/loader/LuckPerms-Bukkit-5.5.53.jar")

            // MiniPlaceholders
            modrinth("miniplaceholders", "N2WfJ0ll")

            // ViaVersion
            modrinth("viaversion", "BqLcExWb")

            // ViaBackwards
            modrinth("viabackwards", "whyGF8dZ")
        }
    }
}

paper {

    name = "SleepPolls"

    version = project.version.toString()

    description = "A modern sleep voting system for Paper servers that transforms night-time gameplay with democratic polling."

    main = "org.amethystdev.Main"

    apiVersion = "26.1"

    prefix = "SleepPolls"

    authors = listOf(
        "Monk",
        "NichuNaizam",
        "The Amethyst Team"
    )

    bootstrapper = "org.amethystdev.Bootstrapper"
    loader = "org.amethystdev.PluginLibrariesLoader"

    hasOpenClassloader = false
    generateLibrariesJson = true

    foliaSupported = true

    serverDependencies {

        register("LuckPerms") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("ViaVersion") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("WorldEdit") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("MiniPlaceholders") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}