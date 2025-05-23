plugins {
    java
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:${rootProject.property("jUnitVersion")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    test {
        useJUnitPlatform()
    }
}