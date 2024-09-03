# Murder Run

[![CodeFactor](https://www.codefactor.io/repository/github/pulsebeat02/murderrun/badge)](https://www.codefactor.io/repository/github/pulsebeat02/murderrun)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=PulseBeat02_MurderRun&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=PulseBeat02_MurderRun)

## Inspiration
Based on the popular game Dead by Daylight, Murder Run is an advanced mini-game revolving around killers
and survivors. In a desolate map, survivors must find all the vehicle parts and throw them back onto the
truck before the killers murder everyone. Both killers and survivors have access to a 100+ different gadgets,
which can be used in combination to try and win. This game was also inspired by SSundee's "Murder Run" series
on YouTube.

## Commands
`/murder gui`: Allows the player to manage lobbies, arenas, and games. Users are able to create and configure
existing lobbies and arenas through this menu. They are also able to start games and invite players through
this user-interface.

## Configuration
Every-single message of this plugin is configurable inside the `/locale/murderrun_en.properties` file. This
includes user-interface message components as well! You are also able to tweak any specific game settings by
editing properties in `/settings/game.properties`, such as the sounds made by each gadget, their durations,
and so much more. You can also change the sounds and textures of the resource-pack provided by changing their
respective files in the `/sounds` and `/textures` folders. Resource-packs are built at runtime and served
using MC Pack Hosting by default. Check the `config.yml` file for more specific details.

## Building
1) Install [Jetbrains Runtime](https://github.com/JetBrains/JetBrainsRuntime) (Java 21)
   - Used for hot swapping purposes and faster development. Set the project JDK to be this for Gradle to work.
2) Run `gradlew build`