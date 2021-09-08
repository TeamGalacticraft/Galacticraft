name: Bug Report
description: File a bug report
labels:
  - 'status: triage'
  - 'type: bug'
body:
  - type: markdown
    attributes:
      value: Thanks for taking the time to submit a bug report!
  - type: markdown
    attributes:
      value: >-
        Please note that support for compiling the mod is not currently provided.

        For help with Galacticraft 4 and below, please use [TeamGalacticraft/Galacticraft-Legacy](https://github.com/TeamGalacticraft/Galacticraft-Legacy).

        For general Galacticraft help and support visit the [Galacticraft Central Discord](http://discord.galacticraftcentral.com/)
  - type: dropdown
    id: modloader
    attributes:
      label: Mod Loader
      description: Which mod loader are you using?
      options:
        - Fabric
        - Forge (NYI)
    validations:
      required: true
  - type: input
    id: version
    attributes:
      label: Version Information
      description: What version (commit hash or branch) of Galacticraft are you using?
      placeholder: 4cb9a5d
    validations:
      required: true
  - type: input
    id: logs
    attributes:
      label: Log or Crash Report
      description: >-
        Please upload your log (latest.log or crash report) to [Github
        Gist](https://gist.github.com/)
      placeholder: 'https://gist.github.com/ghost/1a79a4d60de6718e8e5b326e338ae533'
    validations:
      required: true
  - type: textarea
    id: repro
    attributes:
      label: Reproduction steps
      description: How do you trigger this bug? Please walk us through it step by step.
      value: |
        1. Do this
        2. Do that
        3. Crash!
        ...
    validations:
      required: true
