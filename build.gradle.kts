import org.gradle.jvm.tasks.Jar
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.2.60"
val vertxVersion = "3.5.3"
val mainVerticalName = "com.fueledbysoda.todo.MainVerticle"

group = "com.fueledbysoda.todo"
version = "0.0.1"

repositories {
    jcenter()
    mavenCentral()
}

plugins {
    java
    application
    kotlin("jvm") version "1.2.60"
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

dependencies {
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("io.vertx:vertx-core:$vertxVersion")
    implementation("io.vertx:vertx-web:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
    implementation("io.vertx:vertx-mysql-postgresql-client:$vertxVersion")
}

application {
    //mainClassName = "com.fueledbysoda.todo.MainVerticle"
    mainClassName = "io.vertx.core.Launcher"
}

tasks{
    "compileKotlin"(KotlinCompile::class) {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    withType<ShadowJar> {
        classifier = "fat"
        mergeServiceFiles {
            include("META-INF/services/io.vertx.core.spi.VerticleFactory")
        }
        manifest {
            attributes(
                    mapOf(
                            "Main-Class" to application.mainClassName,
                            "Main-Verticle" to mainVerticalName
                    )
            )
        }
    }

    withType<JavaExec> {
        args("run", mainVerticalName, "--launcher-class=${application.mainClassName}")
    }

    "stage" {
        dependsOn("shadowJar")
    }
}

