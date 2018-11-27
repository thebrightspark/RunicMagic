package brightspark.runicmagic.gui;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.capability.CapSpell;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.message.MessageSetSelectedSpell;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;
import java.util.Map;

public class GuiSpellSelect extends RMGuiScreen
{
    private static final int buttonSize = 18;
    private static final int buttonSpacing = buttonSize + 4;
    private static final int buttonStartPos = 12;
    private static final int buttonGridSize = 8;
    private EntityPlayer player;
    private CapSpell capSpell;

    public GuiSpellSelect(EntityPlayer player)
    {
        super("spell_select", 196, 196);
        this.player = player;
        capSpell = RMCapabilities.getSpells(player);
    }

    public void onSpellCapChange()
    {
        RunicMagic.LOG.info("Spell cap changed!");
        Spell selectedSpell = capSpell.getSelectedSpell();
        Map<Spell, Long> cooldowns = capSpell.getCooldowns();
        buttonList.forEach(button -> {
            if(button instanceof ButtonSpell)
            {
                ButtonSpell buttonSpell = (ButtonSpell) button;
                buttonSpell.selected = buttonSpell.spell == selectedSpell;
                buttonSpell.cooldown = cooldowns.getOrDefault(buttonSpell.spell, 0L);
            }
        });
    }

    @Override
    public void initGui()
    {
        super.initGui();
        //Add buttons
        List<Spell> spells = RMSpells.getSortedSpells();
        Map<Spell, Long> cooldowns = capSpell.getCooldowns();
        Spell selectedSpell = capSpell.getSelectedSpell();
        for(int i = 0; i < spells.size(); i++)
        {
            int buttonX = buttonStartPos + (i * buttonSpacing - (i / buttonGridSize * (buttonGridSize * buttonSpacing)));
            int buttonY = buttonStartPos + (i / buttonGridSize * buttonSpacing);
            Spell spell = spells.get(i);
            ButtonSpell button = new ButtonSpell(i, buttonX, buttonY, spell, cooldowns.getOrDefault(spell, 0L), spell == selectedSpell);
            addButton(button);
        }
    }

    @Override
    protected void drawTooltips(List<String> tooltip, int mouseX, int mouseY)
    {
        //TODO: Spell tooltips
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if(button instanceof ButtonSpell)
            NetworkHandler.network.sendToServer(new MessageSetSelectedSpell(((ButtonSpell) button).spell));
    }

    private class ButtonSpell extends GuiButton
    {
        public final Spell spell;
        public long cooldown;
        public boolean selected;

        public ButtonSpell(int id, int x, int y, Spell spell, long cooldown, boolean selected)
        {
            super(id, guiLeft + x, guiTop + y, buttonSize, buttonSize, "");
            this.spell = spell;
            this.cooldown = cooldown;
            this.selected = selected;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
        {
            GlStateManager.color(1F, 1F, 1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            //Draw button background
            boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + buttonSize && mouseY < y + buttonSize;
            int textureNum = selected ? 3 : !enabled ? 0 : hovered ? 2 : 1;
            mc.getTextureManager().bindTexture(guiImage);
            drawTexturedModalRect(x, y, textureNum * 18, 238, width, height);
            //Draw spell icon
            //TODO: Fix icon rendering!
            mc.getTextureManager().bindTexture(spell.getIconRL());
            drawTexturedModalRect(x + 1, y + 1, 0, 0, 16, 16);
        }
    }
}
