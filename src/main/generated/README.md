# Generated resources
**This directory contains generated resources. Do not edit them directly as they can be overwritten (or deleted).**


### Adding more resources
Resources that cannot be generated (e.g. music, textures, languages, etc.) should be put into [src/main/resources](../resources).

To generate additional resources, see the `dev.galacticraft.mod.data` package.

### Updating generated resources
To regenerate resources, run the following command:
```bash
./gradlew runDatagen
```
or the "Data Generation" run configuration in your IDE.
