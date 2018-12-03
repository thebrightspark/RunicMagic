package brightspark.runicmagic.init;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.spell.projectile.SpellBolt;
import brightspark.runicmagic.spell.projectile.SpellConfuse;
import brightspark.runicmagic.spell.projectile.SpellStrike;
import brightspark.runicmagic.spell.projectile.SpellWeaken;
import brightspark.runicmagic.spell.self.*;
import brightspark.runicmagic.spell.teleport.SpellTeleportGatestone;
import brightspark.runicmagic.spell.teleport.SpellTeleportHome;
import brightspark.runicmagic.spell.teleport.SpellTeleportSpawn;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Comparator;
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
				new SpellGatestoneCreate(),
				new SpellTeleportGatestone(),

				//Projectile Attack Spells
				new SpellStrike(RuneType.AIR, 1),
				new SpellStrike(RuneType.WATER, 5),
				new SpellStrike(RuneType.EARTH, 9),
				new SpellStrike(RuneType.FIRE, 13),
				new SpellBolt(RuneType.AIR, 17),
				new SpellBolt(RuneType.WATER, 23),
				new SpellBolt(RuneType.EARTH, 29),
				new SpellBolt(RuneType.FIRE, 35),

				//Projectile Effect Spells
				new SpellWeaken(),
				new SpellConfuse(),

				//Self Spells
				new SpellEnchant(1, 7).addRuneCost(RuneType.WATER, 1),
				new SpellEnchant(2, 27).addRuneCost(RuneType.AIR, 3),
				new SpellEnchant(3, 49).addRuneCost(RuneType.FIRE, 5),
				new SpellEnchant(4, 57).addRuneCost(RuneType.EARTH, 10),
				new SpellEnchant(5, 68).addRuneCost(RuneType.EARTH, 15).addRuneCost(RuneType.WATER, 15),
				new SpellEnchant(6, 87).addRuneCost(RuneType.FIRE, 20).addRuneCost(RuneType.EARTH, 20),
				new SpellBonesToApples(),
				new SpellHumidify(),

				//Charge Orb
				new SpellChargeOrb(RuneType.WATER, 56),
				new SpellChargeOrb(RuneType.EARTH, 60),
				new SpellChargeOrb(RuneType.FIRE, 63),
				new SpellChargeOrb(RuneType.AIR, 66)
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

	public static List<Spell> getSortedSpells()
	{
		List<Spell> spells = new LinkedList<>(REGISTRY.getValuesCollection());
		spells.sort(Comparator.comparingInt(Spell::getLevel));
		return spells;
	}
}
