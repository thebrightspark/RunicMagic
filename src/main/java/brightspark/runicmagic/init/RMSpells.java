package brightspark.runicmagic.init;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.spell.projectile.SpellBolt;
import brightspark.runicmagic.spell.projectile.SpellConfuse;
import brightspark.runicmagic.spell.projectile.SpellStrike;
import brightspark.runicmagic.spell.projectile.SpellWeaken;
import brightspark.runicmagic.spell.self.SpellBonesToApples;
import brightspark.runicmagic.spell.self.SpellChargeOrb;
import brightspark.runicmagic.spell.self.SpellEnchant;
import brightspark.runicmagic.spell.self.SpellHumidify;
import brightspark.runicmagic.spell.teleport.SpellTeleportHome;
import brightspark.runicmagic.spell.teleport.SpellTeleportSpawn;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

// https://runescape.fandom.com/wiki/List_of_spells
public class RMSpells
{
	public static IForgeRegistry<Spell> REGISTRY;

	public static void register(IForgeRegistry<Spell> registry)
	{
		registry.registerAll(
			//Teleport Spells
			new SpellTeleportSpawn(),
			new SpellTeleportHome(),

			//Projectile Attack Spells
			new SpellStrike(RuneType.AIR),
			new SpellStrike(RuneType.EARTH),
			new SpellStrike(RuneType.FIRE),
			new SpellStrike(RuneType.WATER),
			new SpellBolt(RuneType.AIR),
			new SpellBolt(RuneType.EARTH),
			new SpellBolt(RuneType.FIRE),
			new SpellBolt(RuneType.WATER),

			//Projectile Effect Spells
			new SpellWeaken(),
			new SpellConfuse(),

			//Self Spells
			new SpellEnchant(1).addRuneCost(RuneType.WATER, 1),
			new SpellEnchant(2).addRuneCost(RuneType.AIR, 3),
			new SpellEnchant(3).addRuneCost(RuneType.FIRE, 5),
			new SpellEnchant(4).addRuneCost(RuneType.EARTH, 10),
			new SpellEnchant(5).addRuneCost(RuneType.EARTH, 15).addRuneCost(RuneType.WATER, 15),
			new SpellEnchant(6).addRuneCost(RuneType.FIRE, 20).addRuneCost(RuneType.EARTH, 20),
			new SpellBonesToApples(),
			new SpellHumidify(),

			//Charge Orb
			new SpellChargeOrb(RuneType.AIR),
			new SpellChargeOrb(RuneType.EARTH),
			new SpellChargeOrb(RuneType.FIRE),
			new SpellChargeOrb(RuneType.WATER)
		);
	}

	public static List<String> getRegNames()
	{
		return REGISTRY.getKeys().stream()
			.map(ResourceLocation::toString)
			.sorted(String::compareToIgnoreCase)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Spell getSpell(String name)
	{
		return REGISTRY.getValue(new ResourceLocation(name));
	}
}
