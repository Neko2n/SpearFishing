# Minecraft Mod - Spear Fishing

Adds spear fishing to Minecraft!

## Description

Have you ever tried to catch fish with your bare hands? It's annoyingly difficult to hit them. So, I've created Spear Fishing to solve that issue!

### The Pike

Craft a **Pike** with 3 sticks in a crafting grid. Pikes are simple throwing weapons, slotting into progression as an incredibly weak form of the trident. They're purposefully easy to craft so that you can go spear fishing in the early game, when it's most useful.

Pikes do very little damage to normal mobs, and are very slow to swing. Don't expect to throw one at a Zombie and take it down in one hit. However, pikes do **twice as much damage** to fish and squids! This instantly kills most fish, leading into the next feature of the pike:

### Kebobs

Thrown pikes have a special function when they kill a mob. **The mob's body and its loot will stick to the pike!** Throw a pike at a salmon and watch it carry the salmon gracefully to the ocean floor, where you can easily go pick up your spoils.

You can also throw pikes at any dropped items! Items hit by a pike will stick to it and be carried, just like the loot from mobs you kill. Have your friend throw an apple up into the air and try to spear it! Deliver items across long distances! Get items from hard-to-reach places! It's pretty fun.

The "kebob" mechanic pairs perfectly with the **loyalty** enchantment, which can now also be applied to pikes. Grab loot with your pike and watch it return to you with the loot in tow. Loot goblins have never had it this easy.

### Customization

The mod features extensive customization options, both through configuration files and through data packs.

The following data is tag-driven:
- Entities which take extra damage from pikes (spearfishing/tags/entity/spear_prey.json)
  - By default, it's a lot of fish.
- Items which are classified as "spears", granting bonus damage against fish and the "kebob" mechanic (spearfishing/tags/item/spears.json)
  - By default, this just contains the pike. If you want tridents to have spearfishing capabilities, add them to this tag.
 
The mod features two configuration files:

<details>
<summary><code>spearfishing-common.toml</code></summary>
  
  ```toml
  #Multiplier applied to damage dealt to fish and squid.
  # Default: 3.0
  # Range: 1.0 ~ 1.7976931348623157E308
  spear_prey_damage_multiplier = 3.0
  #Relative speed of thrown spears underwater.
  #Lower values make spears slow down faster in water.
  #A value of 1 makes spears travel through water as if it were air; a value of 0 makes spears instantly stop in water.
  # Default: 0.9
  # Range: 0.0 ~ 1.0
  spear_water_inertia = 0.9
  #Whether items dropped by mobs should be instantly added to the player's inventory when using a spear.
  spear_quick_pickup = true
  #Whether fisherman villagers offer spears as a trade.
  fisherman_sells_spears = true
  ```
</details>

<details>
<summary><code>spearfishing-startup.toml</code></summary>
  
  ```toml
  #Durability of spears.
  # Default: 64
  # Range: > 1
  spear_durability = 64
  ```
</details>

### Compatibility

The mod comes with special compatibility for **No Man's Land**, featuring an **incendiary pike**.

The mod is also compatible with Better Combat, providing the necessary data and assets to give pikes proper swinging animations.

Tag support is provided for a number of popular mods by default, allowing pikes to deal bonus damage to modded fish and cephalopods. If a mod is missing, feel free to submit your tag files to the issue tracker.

Known issues:
- Hold My Items (HMI) breaks the position of the pike in your hand. Replacing the pike's item model files with default positions/rotations _should_ fix this.
- Better Combat's swinging animations are offset awkwardly. Custom swinging animations are ready, but they derive from existing animation files, and Better Combat is All Rights Reserved, so I need permission to distribute them. That, or I need someone to make custom animations.

## Links

You can download the mod on Modrinth or CurseForge.

Modrinth: https://modrinth.com/mod/spear-fishing

CurseForge: https://curseforge.com/minecraft/mod/spear-fishing

## AI Disclosure

I used generative AI to assist with speeding up the coding process.

I could easily explain any part of the code--all architectural decisions were made deliberately. I don't cut corners.
