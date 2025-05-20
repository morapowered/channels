plugins {
    `maven-publish`
}

publishing  {
    publications {
        create<MavenPublication>(project.name) {
            groupId = rootProject.group.toString()
            artifactId = "channels-${project.name}"
            version = rootProject.version.toString()

            from(components["java"])

            // much more
        }
    }

}