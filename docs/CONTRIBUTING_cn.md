为 Galacticraft 做出贡献
=

**中文** | [English](CONTRIBUTING.md)

非常感谢您对 Galacticraft 做出的贡献！

## 测试和 Bug 报告

### 测试
> [!重要]
> Galacticraft 5 仍在开发中！\
> 更新后可能会导致存档损坏或无法加载。\
> 强烈建议不要将这些构建用于除测试之外的任何用途。\
> 对于使用这些版本所遇到的问题，我们不提供支持。

每次提交到 `main` 分支后，都会创建新的 Pre-Alpha 版本。\
你可以在 [GitHub Actions][actions] 选项卡上找到它们 (选择一个提交并在底部查找 "Artifacts")。

### Bug报告
* 在打开新的 bug 报告之前，请检查您的问题是否已被报告。
* 请为您找到的每个 bug 创建一个单独的 bug 报告。
  * 这使得跟踪哪些是固定的，哪些是未固定的。
* 请包含提交哈希值 (例如 `a17fe4784ed658a9c70ecebd2173af755299d0cb`)
或者报告中的 GitHub Actions 内部版本号 (例如 #253) in your report.
  * 我们更倾向于使用提交哈希值，但构建编号也可以接受。
  * 这有助于我们确定错误可能是在何时引入的，或者是否有提交已修复了该错误。

## 翻译
翻译通过 [Crowdin][crowdin] 平台进行。\
如果您的语言缺失，或您在访问该网站时遇到问题，请在我们的 [Discord服务器][discord] 上与我们联系。

## 贡献代码

如果您计划修复一个已知的错误，请在问题追踪器(Issue Tracker)上留言，告知他人您正在处理该问题。

如果您计划贡献一个较大的功能，请通过 [Discord][discord] 与我们联系，以避免重复劳动。

请尽量使拉取请求专注于一个功能或子系统，以保持其简洁，并确保快速的审查流程。

### 代码风格
主要规范：
* 使用四个空格进行缩进。
* 大括号 `{}` 放在同一行。

总体而言，请遵循您所在文件的代码风格。
对于类型明显的变量，您可以自由使用 `var`

### 管理资源
> [!重要]\
> 请勿手动编辑或添加任何文件到 [`src/main/generated`][generated] 目录中，因为它们将被删除。

#### 纹理、音乐和复杂模型
这些 (以及其他无法生成的资源) 应放置在 [`src/main/resources`] 目录中。

#### 简单模型、战利品表和标签
这些 (以及其他可生成的资源) 是使用 Minecraft 的数据生成系统程序化创建的。
请参阅 [`dev.galacticraft.mod.data`](/src/main/java/dev/galacticraft/mod/data) 包，了解这些资源是如何生成的。

### 提交更改之前

#### 数据生成
如果您添加、删除或以其他方式修改了资源生成代码，您需要重新运行数据生成器以查看您的更改。

这可以在您的 IDE 中作为 `Data Generation` 配置找到，或通过 Gradle 运行：
```shell
./gradlew runDatagen
```

#### 许可证标头
如果您添加了任何新类，请务必检查它们是否应用了标准 [许可证标头][license header]。

如果有任何文件缺少标头，您可以通过执行以下 Gradle 任务来添加它：
```shell
./gradlew updateLicenses
```

#### 提交消息
我们使用 [conventional commits][conventional commits] 规范来编写提交消息。 但是，我们倾向于压缩合并拉取请求，因此您不需要这样做。


[actions]: https://github.com/TeamGalacticraft/Galacticraft/actions/workflows/build.yml?query=branch%3Amain+is%3Asuccess
[conventional commits]: https://www.conventionalcommits.org
[crowdin]: https://teamgalacticraft.crowdin.com/galacticraft
[discord]: https://discord.gg/n3QqhMYyFK
[generated]: /src/main/generated
[license header]: /LICENSE_HEADER.txt
