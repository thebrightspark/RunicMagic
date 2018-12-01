package brightspark.runicmagic.gui;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.capability.CapSpell;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.handler.NetworkHandler;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.init.RMItems;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.item.ItemRuneTypeBase;
import brightspark.runicmagic.message.MessageGuiSpellClick;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.CommonUtils;
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

public class GuiSpellSelect extends RMGuiScreen
{
    private static final int buttonSize = 18;
    private static final int buttonSpacing = buttonSize + 4;
    private static final int buttonStartPos = 12;
    private static final int buttonGridSize = 8;
    private static final String TAB = "  ";
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
        Map<Spell, Long> cooldowns = capSpell.getCooldowns(mc.world);
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
        Map<Spell, Long> cooldowns = capSpell.getCooldowns(mc.world);
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
            if(stack.getItem() == RMItems.rune)
            {
                RuneType type = ((ItemRuneTypeBase) stack.getItem()).getRuneType(stack.getMetadata());
                if(type == null || !runeTypes.contains(type))
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
            tooltip.add(TextFormatting.GRAY + spell.getSpellType().getTranslation());
            if(!spell.isSelectable())
                tooltip.add("Will cast on click");
            tooltip.add("");
            if(spell.getCooldown() > 0)
                tooltip.add("Cooldown: " + CommonUtils.ticksToSecsString(spell.getCooldown()));
            tooltip.add("Cost:");
            Map<RuneType, Short> cost = spell.getCost();
            if(cost.isEmpty())
                tooltip.add(TAB + "None");
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
        {
            Spell spell = ((ButtonSpell) button).spell;
            NetworkHandler.network.sendToServer(new MessageGuiSpellClick(spell));
            if(!spell.isSelectable())
                mc.player.closeScreen();
        }
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
            //Draw button background
            hovered = mouseX >= x && mouseY >= y && mouseX < x + buttonSize && mouseY < y + buttonSize;
            int textureNum = selected ? 3 : !enabled ? 0 : hovered ? 2 : 1;
            mc.getTextureManager().bindTexture(guiImage);
            drawTexturedModalRect(x, y, textureNum * 18, 238, width, height);
            //Draw spell icon
            mc.getTextureManager().bindTexture(spell.getIconRL());
            drawModalRectWithCustomSizedTexture(x + 1, y + 1, 0, 0, 16, 16, 16, 16);
        }
    }
}
