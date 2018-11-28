package brightspark.runicmagic.gui;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.capability.CapSpell;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.handler.NetworkHandler;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.item.ItemRune;
import brightspark.runicmagic.message.MessageSetSelectedSpell;
import brightspark.runicmagic.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    private Map<RuneType, Integer> getNumInInventory(Set<RuneType> runeTypes)
    {
        Map<RuneType, Integer> counts = new HashMap<>(runeTypes.size());
        for(int i = 0; i < player.inventory.getSizeInventory(); i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if(stack.getItem() instanceof ItemRune)
            {
                RuneType type = ItemRune.getRuneType(stack.getMetadata());
                if(type == null || runeTypes.contains(type))
                    continue;
                int count = stack.getCount();
                counts.compute(type, (runeType, num) -> num == null ? count : num + count);
            }
        }
        return counts;
    }

    @Override
    protected void drawTooltips(List<String> tooltip, int mouseX, int mouseY)
    {
        ButtonSpell buttonSpell = (ButtonSpell) buttonList.stream().filter(button -> button instanceof ButtonSpell && button.isMouseOver()).findFirst().orElse(null);
        if(buttonSpell != null)
        {
            Spell spell = buttonSpell.spell;
            tooltip.add(spell.getSpellType().getMagicType().getTextColour() + I18n.format(spell.getUnlocName()));
            tooltip.add(spell.getSpellType().getTranslation());
            if(!spell.isSelectable())
                tooltip.add("Will cast on click");
            if(spell.getCooldown() > 0)
                tooltip.add("Cooldown: " + TimeUnit.MILLISECONDS.toSeconds(spell.getCooldown()) + "s");
            tooltip.add("Cost:");
            Map<RuneType, Short> cost = spell.getCost();
            if(cost.isEmpty())
                tooltip.add("\tNone");
            else
            {
                Map<RuneType, Integer> numInInv = getNumInInventory(cost.keySet());
                cost.forEach(((runeType, num) -> {
                    int typeInInv = numInInv.getOrDefault(runeType, 0);
                    TextFormatting colour = typeInInv >= num ? TextFormatting.GREEN : TextFormatting.RED;
                    tooltip.add(colour + String.format("%s/%s %s", typeInInv, num, runeType.getTranslation()));
                }));
            }
        }
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
            hovered = mouseX >= x && mouseY >= y && mouseX < x + buttonSize && mouseY < y + buttonSize;
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
