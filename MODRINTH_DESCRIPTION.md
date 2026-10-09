<p align="center">
  <img src="https://cdn.modrinth.com/data/cached_images/6699d5c39e0a2ee0148f991e113f87f5d7c68a89_0.webp" alt="FGP_Banner"><br><br>
  
  <a href="https://www.spigotmc.org/resources/add">
    <img src="https://cdn.modrinth.com/data/cached_images/ce4da87664b4c0d43e307a618ab610289ff52191.png" alt="Spigot-Button-64">
  </a>
  <a href="https://black-minecraft.com/members/aleksandr-rejzov.109485">
    <img src="https://github.com/user-attachments/assets/a61ad836-1826-4983-913e-400c68fe6fad" alt="BlackMinecraft-Button-64">
  </a>
  <a href="https://github.com/TheReizi/Female-Gender-Plugin">
    <img src="https://cdn.modrinth.com/data/cached_images/42bad645daf4235b5c050ed51afb7c44671f02b2.png" alt="GitHub-Button-64">
  </a>
  <a href="https://discord.gg/MMwDXeT64j">
    <img src="https://cdn.modrinth.com/data/cached_images/b5c1c8717c38148ee9ecf2ae700554e8f011ac0b.png" alt="Discord-Button-64">
  </a>
</p>

**What this plugin does**

FemaleGenderPlugin is a lightweight **server-side plugin** for [Female Gender Mod](https://modrinth.com/mod/wildfire-gender) (Fabric / Forge). The mod itself lets each player customize how their body is rendered — but by default that data has to travel client-to-client. On offline-mode servers, or whenever direct client sync isn't reliable, players end up seeing outdated or default bodies for everyone else.

This plugin fixes that by becoming the relay: every client sends its mod data to the server once, and the server re-broadcasts it to everyone else — instantly, consistently, and regardless of the server's online-mode setting. No player-to-player negotiation, no missed updates.

**Key features**

- **Works on offline servers** — sync happens entirely server-side, so it doesn't depend on Mojang session/skin infrastructure the way peer-to-peer syncing can.
- **Automatic, real-time resync** — triggered on join, on quit, and whenever a player updates their mod settings in-game. Everyone online sees changes immediately.
- **Broad mod-version support** — speaks protocol versions 1 through 5, covering Wildfire's Gender Mod from `0.0.1` all the way through current releases, for both the Fabric and Forge packet formats.
- **No player-facing configuration** — nothing to set up for your players. If they have the mod installed, it just works.

**How syncing works, in short**

1. A player's client (running Female Gender Mod) sends its configuration to the server over the mod's plugin channel.
2. The plugin stores it and immediately re-serializes it for every online player, in the correct packet format for each recipient (Fabric or Forge).
3. When a new player joins, everyone (including the newcomer) gets re-synced, so nobody is stuck looking at stale data.

**Configuration**

```yaml
mod:
  # The packet format used for mod data reads/writes.
  # -1 (default) always uses the newest known protocol, so you won't need
  # to touch this on routine plugin/mod updates.
  # An undocumented/unsupported value disables the plugin on launch.
  protocol: -1
```

If you need to pin a specific protocol (e.g. because your server intentionally runs an older mod build), set it explicitly:

| Protocol | Supported mod versions |
|---|---|
| 1 | 0.0.1 – 2.8.0 |
| 2 | 2.8.1 – 3.0.1 |
| 3 | 3.1.0 – 4.0.0 |
| 4 | 4.0.1 – 4.x (pre-5.0.0) |
| 5 | 5.0.0+ |

<details>
<summary>Developer API</summary>

For plugin developers who want to hook into gender data programmatically, FemaleGenderPlugin exposes a public API object (`ru.mth.femalegender.api.FemaleGenderAPI`) — usable from Kotlin or Java, no extra library needed. Add this plugin as a (soft-)dependency in your `plugin.yml` / `paper-plugin.yml` and call it directly.

**Per-viewer overrides** — make one player look different to a specific viewer only, without touching that player's own stored data:

```kotlin
// Make `target` appear with a custom configuration, but only to `viewer`.
FemaleGenderAPI.setOverride(target = targetUuid, viewer = viewerUuid, configuration = customConfig)

// Remove it again
FemaleGenderAPI.clearOverride(target = targetUuid, viewer = viewerUuid)
FemaleGenderAPI.clearOverridesFor(target = targetUuid)
```

Useful for disguise plugins, roleplay illusions, admin tooling, or any system where "what player A sees of player B" needs to diverge from B's real settings.

**Forced gender (for everyone)** — authoritatively lock a player's gender identity across *all* viewers, e.g. driven by an RP application form, without overwriting their other personal settings (bust size, physics, etc.):

```kotlin
FemaleGenderAPI.setForcedGender(
    target = playerUuid,
    identity = GenderIdentities.FEMALE,
    fallback = fallbackConfiguration // used only if the player has never sent mod data at all
)

FemaleGenderAPI.clearForcedGender(playerUuid)
```

- If the player already has real mod data on record, only `generalOptions.genderIdentity` is overridden — everything else stays theirs.
- If the player has never sent any mod data (mod not installed, or hasn't connected yet), the provided `fallback` configuration is used in full, so even mod-less players can be given a consistent, visible identity to others.
- Calling either method triggers an immediate resync — no need to wait for the next join/update event.

```kotlin
FemaleGenderAPI.getOverride(target, viewer)   // ModConfiguration? currently in effect for a specific pair
FemaleGenderAPI.getForcedGender(target)       // ForcedGender? currently active
FemaleGenderAPI.forcedTargets()               // Set<UUID> of everyone with a forced gender
```
</details>

**Compatibility**

- **Server:** Paper 1.21+ (Java 21)
- **Client:** [Female Gender Mod](https://modrinth.com/mod/wildfire-gender), Fabric or Forge, versions 0.0.1 through current
- Players without the client mod installed are unaffected unless targeted via the developer API's forced-gender fallback