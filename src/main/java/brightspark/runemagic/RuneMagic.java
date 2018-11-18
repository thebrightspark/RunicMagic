package brightspark.runemagic;

import brightspark.runemagic.command.CommandExecuteSpell;
import brightspark.runemagic.init.*;
import brightspark.runemagic.item.RMItemSubBase;
import brightspark.runemagic.spell.Spell;
import brightspark.runemagic.util.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(modid = RuneMagic.MOD_ID, name = RuneMagic.MOD_NAME, version = RuneMagic.VERSION)
@Mod.EventBusSubscriber
public class RuneMagic
{
	public static final String MOD_ID = "runemagic";
	public static final String MOD_NAME = "Rune Magic";
	public static final String VERSION = "@VERSION@";

	public static final CreativeTabs TAB = new CreativeTabs(MOD_ID) {
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(Items.GLOWSTONE_DUST);
		}
	};

	@Mod.Instance(MOD_ID)
	public static RuneMagic instance;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkHandler.init();
		RMCapabilities.init();
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandExecuteSpell());
	}

	private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> createRegistry(Class<T> type, String name)
	{
		return new RegistryBuilder<T>().setName(new ResourceLocation(MOD_ID, name)).setType(type).disableSaving().allowModification().create();
	}

	@SubscribeEvent
	public static void regRegistries(RegistryEvent.NewRegistry event)
	{
		RMSpells.REGISTRY = createRegistry(Spell.class, "spells");
	}

	@SubscribeEvent
	public static void regItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> registry = event.getRegistry();
		registry.registerAll(RMItems.getItems());
		registry.registerAll(RMBlocks.getItemBlocks());
	}

	@SubscribeEvent
	public static void regBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(RMBlocks.getBlocks());
	}

	@SubscribeEvent
	public static void regEntities(RegistryEvent.Register<EntityEntry> event)
	{
		RMEntities.register(event.getRegistry());
	}

	@SubscribeEvent
	public static void regSpells(RegistryEvent.Register<Spell> event)
	{
		RMSpells.register(event.getRegistry());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void regModels(ModelRegistryEvent event)
	{
		regModels(RMItems.getItems());
		regModels(RMBlocks.getItemBlocks());
		RMEntities.registerRenders();
	}

	@SideOnly(Side.CLIENT)
	private static void regModels(Item[] items)
	{
		for(Item item : items)
		{
			if(item instanceof RMItemSubBase && item.getHasSubtypes())
			{
				String[] subNames = ((RMItemSubBase) item).getSubNames();
				for(int meta = 0; meta < subNames.length; meta++)
					ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName().toString() + "/" + subNames[meta], "inventory"));
			}
			else
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
	}
}
