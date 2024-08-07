import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
}

group = "xyz.acrylicstyle"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://repo.azisaba.net/repository/maven-public/")
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("net.azisaba:LifeCore:1.15.2+6.16.8")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        doNotTrackState("plugin.yml should be updated every time")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from(sourceSets.main.get().resources.srcDirs) {
            filter(ReplaceTokens::class, mapOf("tokens" to mapOf("version" to project.version.toString())))
            filteringCharset = "UTF-8"
        }
    }
}
