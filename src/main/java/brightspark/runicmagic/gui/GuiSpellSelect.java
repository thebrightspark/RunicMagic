package brightspark.runicmagic.gui;

import brightspark.runicmagic.RunicMagic;
import brightspark.runicmagic.capability.CapSpell;
import brightspark.runicmagic.enums.RuneType;
import brightspark.runicmagic.handler.NetworkHandler;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.init.RMItems;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.item.ItemRuneTypeBase;
import brightspark.runicmagic.item.ItemStaff;
import brightspark.runicmagic.message.MessageGuiSpellClick;
import brightspark.runicmagic.spell.Spell;
import brightspark.runicmagic.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiSpellSelect extends RMGuiScreen
{
    private static final int buttonSize = 18;
    private static final int buttonSpacing = buttonSize + 4;
    private static final int buttonStartPos = 12;
    private static final int buttonGridSize = 8;
    private static final String TAB = "  ";
    private final Map<RuneType, Integer> runesInInv = new HashMap<>();
    private final int playerLevel;
    private EntityPlayer player;
    private CapSpell capSpell;

    public GuiSpellSelect(EntityPlayer player)
    {
        super("spell_select", 196, 196);
        this.player = player;
        capSpell = RMCapabilities.getSpells(player);
        playerLevel = RMCapabilities.getLevel(player).getLevel();
    }

    public void onSpellCapChange()
    {
        RunicMagic.LOG.info("Spell cap changed!");
        updateCache();
        updateButtons();
    }

    private void updateCache()
    {
        //Update cached runes in inv
        runesInInv.clear();
        player.inventory.mainInventory.forEach(stack -> {
            if(stack.getItem() == RMItems.rune)
            {
                RuneType type = ((ItemRuneTypeBase) stack.getItem()).getRuneType(stack.getMetadata());
                if(type != null)
                {
                    int count = stack.getCount();
                    runesInInv.compute(type, (runeType, num) -> num == null ? count : num + count);
                }
            }
        });
        Pair<ItemStack, EnumHand> held = CommonUtils.findHeldStaff(player);
        RuneType runeType = held != null ? ItemStaff.getRuneType(held.getKey()) : null;
        if(runeType != null)
            runesInInv.put(runeType, -1);
    }

    private void updateButtons()
    {
        //Update buttons
        Spell selectedSpell = capSpell.getSelectedSpell();
        Map<Spell, Long> cooldowns = capSpell.getCooldowns(mc.world);
        buttonList.forEach(button -> {
            if(button instanceof ButtonSpell)
            {
                ButtonSpell buttonSpell = (ButtonSpell) button;
                buttonSpell.enabled = playerLevel >= buttonSpell.spell.getLevel() && hasRunesInInv(buttonSpell.spell.getCost());
                buttonSpell.selected = buttonSpell.spell == selectedSpell;
                buttonSpell.cooldown = cooldowns.getOrDefault(buttonSpell.spell, 0L);
            }
        });
    }

    private boolean hasRunesInInv(Map<RuneType, Short> cost)
    {
        Map<RuneType, Short> costCopy = new HashMap<>(cost);
        //Remove from the costCopy all runes in the runesInInv
        runesInInv.forEach((runeType, num) ->
                costCopy.computeIfPresent(runeType, (costType, costNum) ->
                        num < 0 ? null : num >= costNum ? null : (short) (costNum - num)));
        //If costCopy is empty, then we had enough runes
        return costCopy.isEmpty();
    }

    @Override
    public void initGui()
    {
        super.initGui();
        updateCache();
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
            button.enabled = playerLevel >= spell.getLevel() && hasRunesInInv(spell.getCost());
            addButton(button);
        }
    }

    @Override
    protected void drawTooltips(List<String> tooltip, int mouseX, int mouseY)
    {
        ButtonSpell buttonSpell = (ButtonSpell) buttonList.stream().filter(button -> button instanceof ButtonSpell && button.isMouseOver()).findFirst().orElse(null);
        if(buttonSpell != null)
        {
            Spell spell = buttonSpell.spell;
            int spellLevel = spell.getLevel();
            TextFormatting levelColour = playerLevel >= spellLevel ? TextFormatting.LIGHT_PURPLE : TextFormatting.DARK_PURPLE;
            tooltip.add(levelColour + "[" + spell.getLevel() + "] " + spell.getSpellType().getMagicType().getTextColour() + I18n.format(spell.getUnlocName()));
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
                cost.forEach(((runeType, num) -> {
                    int typeInInv = runesInInv.getOrDefault(runeType, 0);
                    String typeInInvText = typeInInv < 0 ? "∞" : Integer.toString(typeInInv);
                    TextFormatting colour = typeInInv < 0 || typeInInv >= num ? TextFormatting.GREEN : TextFormatting.RED;
                    tooltip.add(colour + String.format("%s/%s %s", typeInInvText, num, runeType.getTranslation()));
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
