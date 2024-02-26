plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(gradleTestKit())
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

group = "io.wzieba"

gradlePlugin {
    plugins.register("reproduction") {
        id = "io.wzieba.reproduction"
        implementationClass = "io.wzieba.Plugin"
    }
}

