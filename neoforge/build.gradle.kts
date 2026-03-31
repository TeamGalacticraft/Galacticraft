plugins {
    java
}

val modName = rootProject.property("mod.name").toString()

base {
    archivesName.set("${modName}-neoforge")
}

dependencies {
    implementation(project(path = ":common", configuration = "namedElements"))
}