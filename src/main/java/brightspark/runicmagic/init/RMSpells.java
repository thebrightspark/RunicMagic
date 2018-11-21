package brightspark.runicmagic.init;

import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.spell.projectile.SpellProjectileBase;
import brightspark.runicmagic.spell.self.SpellBonesToApples;
import brightspark.runicmagic.spell.self.SpellEnchant;
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
			new SpellTeleportSpawn(),
			new SpellTeleportHome(),

			new SpellProjectileBase("air_strike", RuneType.AIR).addRuneCost(RuneType.AIR, 1),
			new SpellProjectileBase("earth_strike", RuneType.EARTH).addRuneCost(RuneType.AIR, 1).addRuneCost(RuneType.EARTH, 1),
			new SpellProjectileBase("fire_strike", RuneType.FIRE).addRuneCost(RuneType.AIR, 1).addRuneCost(RuneType.FIRE, 1),
			new SpellProjectileBase("water_strike", RuneType.WATER).addRuneCost(RuneType.AIR, 1).addRuneCost(RuneType.WATER, 1),

			new SpellEnchant(1),
			new SpellBonesToApples()
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
