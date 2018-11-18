package brightspark.runemagic.init;

import brightspark.runemagic.spell.Spell;
import brightspark.runemagic.spell.projectile.elemental.SpellAirStrike;
import brightspark.runemagic.spell.self.SpellBonesToApples;
import brightspark.runemagic.spell.teleport.SpellTeleportSpawn;
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
			new SpellAirStrike(),
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