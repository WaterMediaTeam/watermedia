package me.srrapero720.watermedia.gradle.minecraft

import groovy.json.JsonOutput

class FavricModJson {
    int schemaVersion
    String id
    String version
    String name
    String modDescription
    List<String> authors
    Contact contact
    String license
    String icon
    String environment
    Entrypoints entrypoints
    Breaks breaks
    Depends depends

    FavricModJson(int schemaVersion, String id, String version, String name, String modDescription,
                  List<String> authors, Contact contact, String license, String icon, String environment,
                  Entrypoints entrypoints, Breaks breaks, Depends depends) {
        this.schemaVersion = schemaVersion
        this.id = id
        this.version = version
        this.name = name
        this.modDescription = modDescription
        this.authors = authors
        this.contact = contact
        this.license = license
        this.icon = icon
        this.environment = environment
        this.entrypoints = entrypoints
        this.breaks = breaks
        this.depends = depends
    }

    String toJsonString() {
        JsonOutput.toJson(this)
    }

    class Contact {
        String homepage
        String sources
        String issues
    }

    class Entrypoints {
        List<String> preLaunch
    }

    class Breaks {
        Map<String, String> fancyvideoApi
    }

    class Depends {
        Map<String, String> java
    }

// Ejemplo de uso
    def miModInstance = new FavricModJson(
            1,
            "miMod",
            "1.0",
            "Mi Mod",
            "Descripción de mi mod",
            ["Autor1", "Autor2"],
            new Contact(
                    "https://www.curseforge.com/minecraft/mc-mods/miMod",
                    "https://github.com/miUsuario/miProyecto",
                    "https://github.com/miUsuario/miProyecto/issues"
            ),
            "MIT",
            "pack.png",
            "client",
            new Entrypoints(preLaunch: ["me.srrapero720.watermedia.loaders.FavricLoader"]),
            new Breaks(fancyvideoApi: ["*"]),
            new Depends(java: [">=8"])
    )
}