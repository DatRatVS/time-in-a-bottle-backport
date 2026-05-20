<pre align="center">
   ___       __  ___       __    __      _                       ___
  / _ \___ _/ /_/ _ \___ _/ /_  / /_____(_)__ ___   _______  ___/ (_)__  ___ _
 / // / _ `/ __/ , _/ _ `/ __/ / __/ __/ / -_|_-<  / __/ _ \/ _  / / _ \/ _ `/
/____/\_,_/\__/_/|_|\_,_/\__/  \__/_/ /_/\__/___/  \__/\___/\_,_/_/_//_/\_, /
                                                                       /___/
</pre>

<p align="center">
  <img alt="Forge" src="https://img.shields.io/badge/Forge-555?style=for-the-badge">
  <img alt="1.7.10" src="https://img.shields.io/badge/1.7.10-555?style=for-the-badge">
</p>

<p align="center">
  <a href="#features">Features</a> ·
  <a href="#backport-goals">Backport Goals</a> ·
  <a href="#time-acceleration">Time Acceleration</a> ·
  <a href="#commands-and-api">Commands and API</a> ·
  <a href="#limits">Limits</a> ·
  <a href="#build-instructions">Build Instructions</a>
</p>

# Time in a Bottle: Rewinded

A Minecraft `1.7.10` Forge backport of Time in a Bottle behavior from the modern versions, rebuilt for legacy packs.

## Features

- **Faithful 1.7.10 Backport**: Ports the modern Time in a Bottle gameplay loop to Minecraft `1.7.10` and Forge `10.13.4.1614`.
- **Single Bottle Item**: Adds `tiab:time_in_a_bottle`, a max-stack-size `1` item that stores time while carried.
- **Stored Time NBT Compatibility**: Uses the modern branch NBT keys: `storedTime`, `totalAccumulatedTime`, `timeRate`, `remainingTime`, and `position`.
- **Passive Time Storage**: Adds `20` ticks of stored time every second while the bottle is in a player inventory, capped by config.
- **Duplicate Bottle Protection**: Clears lower-time duplicate bottles every 10 seconds to match the modern anti-duplication behavior.
- **Block Acceleration**: Accelerates tile entities through extra `TileEntity.updateEntity()` calls and random-tick blocks through extra random tick attempts.
- **Face Overlay Renderer**: Renders remaining duration and acceleration multiplier on the target block faces, similar to the original visual style.
- **Commands and Addon API**: Includes `/tiab addTime`, `/tiab removeTime`, and a 1.7.10-compatible `com.magorage.tiab.api` package.

## Backport Goals

This project prioritizes matching the behavior of the modern Forge branch while using APIs that actually exist in Minecraft `1.7.10`.

The port keeps the same mod id and item id:

```text
mod id: tiab
item id: tiab:time_in_a_bottle
```

It is intentionally a normal Forge mod, not a coremod. Registration uses 1.7.10-era `GameRegistry.registerItem(...)`, `EntityRegistry.registerModEntity(...)`, client/server proxies, `.lang` localization, and `assets/tiab/textures/items/`.

## Time Acceleration

Using the bottle on an acceleratable block creates or upgrades a `TimeAcceleratorEntity`.

- First use creates an accelerator with `timeRate=1`, displayed as `x2`.
- Reusing the same block doubles `timeRate`, so the overlay becomes `x4`, `x8`, `x16`, and so on.
- The maximum rate is controlled by `maxTimeRatePower`.
- Each upgrade refreshes duration by refunding half of the already-used duration.
- The accelerator removes itself when time expires or when the target stops being acceleratable.

The default cost progression follows the modern branch:

```text
x2  create: 30 seconds
x4 upgrade: 30 seconds
x8 upgrade: 60 seconds
x16 upgrade: 120 seconds
```

Creative players can create or upgrade accelerators without consuming stored time. Survival players must have enough time in the used bottle for the requested create or upgrade; insufficient requests are rejected.

## Commands and API

The mod registers one root command:

```text
/tiab addTime <seconds>
/tiab removeTime <seconds>
```

Both subcommands require permission level `2` and operate on the first Time in a Bottle item in the executing player's inventory. Values are clamped to the configured maximum and invalid or negative input is rejected.

The public addon API is available under:

```text
com.magorage.tiab.api
```

The API keeps the modern structure but replaces modern Minecraft types with 1.7.10 equivalents, such as `EntityPlayerMP`, `EntityPlayer`, `World`, `ItemStack`, and integer block coordinates. Runtime access is provided through Forge `FMLInterModComms`, with config-based API mutation blacklisting preserved.

## Config

The generated Forge config includes:

```properties
maxTimeRatePower=8
eachUseDuration=30
averageUpdateRandomTick=1365
maxStoredTime=622080000
apiAccessBlacklist=[]
```

## Limits

- The renderer is implemented against the 1.7.10 fixed-function render pipeline, so visual behavior can still vary with aggressive renderer replacement mods.
- Random-tick acceleration follows the configured average random tick interval rather than rewriting block growth logic.
- Tile entity acceleration calls `TileEntity.updateEntity()` extra times; tile entities with unusual side effects may behave according to their own tick implementation.
- The addon API is source-compatible in shape, not binary-compatible with modern Minecraft imports.

## Build Instructions

```bash
JAVA_HOME=/usr/lib/jvm/java-8-openjdk ./gradlew --no-daemon clean build
```

The built JAR will be located at:

```text
build/libs/time-in-a-bottle-1.7.10-1.0.2.jar
```
