package brightspark.runicmagic.handler;

import brightspark.runicmagic.RunicMagic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid = RunicMagic.MOD_ID)
public class KeyHandler
{
	public static KeyBinding keySpellSelect = new KeyBinding("key.runicmagic.spellselect", Keyboard.KEY_L, "key.runicmagic.category");

	@SubscribeEvent
	public static void onKey(KeyInputEvent event)
	{
		if(keySpellSelect.isPressed())
		{
			EntityPlayer player = Minecraft.getMinecraft().player;
			BlockPos pos = player.getPosition();
			player.openGui(RunicMagic.instance, 0, player.world, pos.getX(), pos.getY(), pos.getZ());
		}
	}
}
