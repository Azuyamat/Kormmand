# Kormmand
A simple command manager for [Kord](https://github.com/kordlib/kord), a Kotlin Discord API wrapper.
    
![GitHub release (latest by date)](https://img.shields.io/github/v/release/Azuyamat/Kormmand?style=for-the-badge)

# Table of Contents
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)

## Installation
Kormmand is available on [Jitpack](https://jitpack.io). Here is how to add it to your project.

> Note: Replace `Tag` with the latest version of Kormmand.

<details>
  <summary>Maven</summary>

#### Repository 
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

#### Dependency
```xml
<dependency>
    <groupId>com.github.Azuyamat</groupId>
    <artifactId>Kormmand</artifactId>
    <version>Tag</version>
</dependency>
```
</details>

<details>
  <summary>Gradle</summary>

#### Repository
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

#### Dependency
```groovy
dependencies {
        implementation 'com.github.Azuyamat:Kormmand:Tag'
}
```
</details>

<details>
  <summary>Kotlin Gradle</summary>

#### Repository
```groovy
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
```

#### Dependency
```groovy
dependencies {
    implementation("com.github.Azuyamat:Kormmand:Tag")
}
```
</details>

## Usage

### Creating a command

To create a command, start by initializing a class that extends the `Command` interface.
```kotlin
class HelpCommand : Command {
    override val name: String = "help"
    override val description: String = "Show the help menu."

    override suspend fun execute(event: GuildChatInputCommandInteractionCreateEvent) {
        event.interaction.respondPublic {
            content = "This is a test" }
    }
}
```

### Registering commands

For a command to function, it must be registered by the bot at runtime. To do this, you must create a `CommandManager` and register the command.

<details>
  <summary>Multiple commands</summary>

```kotlin
val commandManager = CommandManager()

commandManager.registerCommands(
    listOf(
        HelpCommand()
    )
)
```
</details>

<details>
  <summary>Single command</summary>

```kotlin
import jdk.internal.joptsimple.HelpFormatter

val commandManager = CommandManager()

commandManager.registerCommand(HelpCommand())
```
</details>

### Permission requirements

To add permission requirements to a command, you must add the `permission` property to the command.

```kotlin
class HelpCommand : Command {
    override val name: String = "help"
    override val description: String = "Show the help menu."
    override val permission: Permission
        get() = Permission.Administrator

    override suspend fun execute(event: GuildChatInputCommandInteractionCreateEvent) {
        event.interaction.respondPublic {
            content = "You are an admin!" }
    }
}
```

### Adding subcommands

To add subcommands to a command, you must add the `builder` property to the command.

```kotlin
class HelpCommand : Command {
    override val name: String = "help"
    override val description: String = "Show the help menu."
    override val builder: GlobalChatInputCreateBuilder.() -> Unit
        get() = {
            subCommand("info", "Get info about a user."){
                user("user", "The user to get info about.")
            }
        }

    override suspend fun execute(event: GuildChatInputCommandInteractionCreateEvent) {
        val interaction = event.interaction
        val c = interaction.command
        val subCommandName = when (c) {
            is RootCommand -> null
            is GroupCommand -> c.name
            is SubCommand -> c.name
        }

        if (subCommandName == "info") {
            interaction.respondPublic {
                content = "This is a test" 
            }
        }
    }
}
```

## Contributing

Contributions are welcome! If you have any issues or feature requests, please open an issue or a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.