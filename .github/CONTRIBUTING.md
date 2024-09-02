Contributing to Galacticraft
=

Thanks for your interest in contributing to Galacticraft!

## Testing and Bug Reports

### Builds
> [!IMPORTANT]
> Galacticraft 5 is still under development!\
> It is possible that saves will become corrupted and/or unloadable after updates.\
> It is strongly discouraged to use these builds for anything but testing.\
> No support will be provided regarding the use of these builds.

Pre-alpha builds are created after every commit to the `main` branch of this repository.\
They can be found on the [GitHub Actions][actions] tab (select a commit and look for "Artifacts" at the bottom).

### Bug Reports
* Before opening a new bug report, please check to see if your issue has already been reported.
* Please create a separate bug report for each bug that you find.
  * This makes it easier to keep track of what is and is not fixed.
* Please include either the commit hash (e.g., `a17fe4784ed658a9c70ecebd2173af755299d0cb`)
or the GitHub actions build number (e.g., #253) in your report.
  * Commit hashes are preferred, but the build number is acceptable too.
  * This helps us figure out when a bug may have been introduced,
  or if there are any commit(s) that may have already fixed this bug. 

## Translations
Translations are submitted through [Crowdin][crowdin].
If your language is missing, or you're having trouble accessing the site,
please get in touch on our [Discord server][discord].

## Contributing Code

If you're planning on fixing an open bug,
please comment on the issue tracker to let people know that you're working on it.

If you're planning on contributing a larger feature,
please get in contact with us [on Discord][discord], so that we can avoid duplicating work.

Please try to keep pull requests focused on one feature or subsystem
to keep them small and ensure a quick review process.

### Code style
Mainly:
* Indent using four spaces
* Braces `{}` go on the same line

Overall, follow the style of the file you're in.
Feel free to use `var` for variables with obvious types.

### Managing resources
> [!IMPORTANT]
> Do not manually edit or add any files to [`src/main/generated`][generated] as they will be deleted.

#### Textures, Music and Complex Models
These (and other non-generatable) resources belong in the [`src/main/resources`] directory.

#### Simple Models, Loot Tables, and Tags
These (and other generatable) resources are programmatically created using Minecraft's datagen system.
See the [`dev.galacticraft.mod.data`](/src/main/java/dev/galacticraft/mod/data) package to see how these are generated.

### Before Committing Changes

#### Data Generation
If you've added, removed, or otherwise modified resource generation code,
you'll need to re-run the data generator to see your changes.

This can be found as the `Data Generation` configuration in your IDE, or run from Gradle:
```shell
./gradlew runDatagen
```

#### License Headers
If you've added any new classes, be sure to check that they have the standard [license header][license header] applied.

If any files are missing the header, you can add it by executing the following Gradle task:
```shell
./gradlew updateLicenses
```

#### Commit Message
We use [conventional commits][conventional commits] specification to write commit messages.
However, we tend to squash-merge pull requests, so you are not required to do this.


[actions]: https://github.com/TeamGalacticraft/Galacticraft/actions/workflows/build.yml?query=branch%3Amain+is%3Asuccess
[conventional commits]: https://www.conventionalcommits.org
[crowdin]: https://teamgalacticraft.crowdin.com/galacticraft
[discord]: https://discord.gg/n3QqhMYyFK
[generated]: /src/main/generated
[license header]: /LICENSE_HEADER.txt
