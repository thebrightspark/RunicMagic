package brightspark.runicmagic.enums;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.Locale;

import static brightspark.runicmagic.enums.SpellType.MagicType.*;

public enum SpellType
{
    ELEMENTAl(COMBAT),
    CURSES(COMBAT),
    SUPPORT(COMBAT),

    TELESELF(TELEPORT),
    TELEOTHER(TELEPORT),
    TELEGROUP(TELEPORT),

    ENCHANTMENT(SKILLING),
    ALCHEMY(SKILLING),
    OTHER(SKILLING);

    private final MagicType magicType;
    private final String unlocName;

    SpellType(MagicType magicType)
    {
        this.magicType = magicType;
        unlocName = "spelltype." + name().toLowerCase(Locale.ROOT) + ".name";
    }

    public MagicType getMagicType()
    {
        return magicType;
    }

    public String getTranslation()
    {
        return I18n.format(unlocName);
    }

    public enum MagicType
    {
        COMBAT(TextFormatting.RED),
        TELEPORT(TextFormatting.BLUE),
        SKILLING(TextFormatting.GOLD);

        private final TextFormatting textColour;

        MagicType(TextFormatting textColour)
        {
            this.textColour = textColour;
        }

        public TextFormatting getTextColour()
        {
            return textColour;
        }
    }
}
