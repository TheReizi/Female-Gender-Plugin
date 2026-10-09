plugins {
    kotlin("jvm") version "2.2.21"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

group = "ru.mth"
version = providers.environmentVariable("RELEASE_VERSION").orElse("0.0.0-dev").get()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        name = "placeholderapi-repo"
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.21")
    compileOnly(files("libs/packetevents.jar"))
    compileOnly("me.clip:placeholderapi:2.11.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.shadowJar {
    archiveFileName.set("FemaleGenderPlugin.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    dependencies {
        exclude(dependency("org.jetbrains:annotations"))
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
