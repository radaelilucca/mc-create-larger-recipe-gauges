# Development

## Requirements

- Java 21
- Minecraft 1.21.1 and NeoForge 21.1.236
- Create 6.0.10-280 for Minecraft 1.21.1

## Build and run

- Build: `gradlew.bat build` on Windows, or `./gradlew build` on Linux/macOS.
- Run the development client: `gradlew.bat runClient` on Windows, or `./gradlew runClient` on Linux/macOS.

Pull requests run the Gradle build through GitHub Actions. Keep changes scoped to the mod and avoid committing Gradle caches, build output, or local run data.
