package brightspark.runicmagic.gui;

import brightspark.runicmagic.capability.CapSpell;
import brightspark.runicmagic.init.RMCapabilities;
import brightspark.runicmagic.init.RMSpells;
import brightspark.runicmagic.spell.Spell;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.List;

public class GuiSpellSelect extends RMGuiScreen
{
    private static final int buttonSize = 18;
    private EntityPlayer player;
    private CapSpell capSpell;
    private List<Spell> spells;
    private Spell selected;

    public GuiSpellSelect(EntityPlayer player)
    {
        super("spell_select");
        this.player = player;
        capSpell = RMCapabilities.getSpells(player);
        spells = RMSpells.getSortedSpells();
        selected = capSpell.getSelectedSpell();
    }

    private class ButtonSpell extends GuiButton
    {
        private Spell spell;

        public ButtonSpell(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
        {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
        }
    }

    private class ScrollingList extends GuiScrollingList
    {
        private List<ButtonSpell> spells;

        public ScrollingList(int width, int height, int top, int bottom, int left, int entryHeight)
        {
            super(mc, width, height, top, bottom, left, buttonSize, GuiSpellSelect.this.width, GuiSpellSelect.this.height);
        }

        @Override
        protected int getSize()
        {
            return spells.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick)
        {

        }

        @Override
        protected boolean isSelected(int index)
        {
            return false;
        }

        @Override
        protected void drawBackground() {}

        @Override
        protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess)
        {

        }
    }
}
