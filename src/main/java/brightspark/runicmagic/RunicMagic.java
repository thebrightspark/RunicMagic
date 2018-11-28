package brightspark.runicmagic;

import brightspark.runicmagic.command.CommandExecuteSpell;
import brightspark.runicmagic.handler.GuiHandler;
import brightspark.runicmagic.handler.NetworkHandler;
import brightspark.runicmagic.init.*;
import brightspark.runicmagic.item.RMItemSubBase;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.ClientUtils;
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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.Logger;

@Mod(modid = RunicMagic.MOD_ID, name = RunicMagic.MOD_NAME, version = RunicMagic.VERSION)
@Mod.EventBusSubscriber
public class RunicMagic
{
	public static final String MOD_ID = "runicmagic";
	public static final String MOD_NAME = "Runic Magic";
	public static final String VERSION = "@VERSION@";

	public static final CreativeTabs TAB = new CreativeTabs(MOD_ID) {
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(Items.GLOWSTONE_DUST);
		}
	};

	@Mod.Instance(MOD_ID)
	public static RunicMagic instance;

	public static Logger LOG;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		LOG = event.getModLog();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		NetworkHandler.init();
		RMCapabilities.init();

		if(event.getSide() == Side.CLIENT)
			ClientUtils.initTextures();
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
