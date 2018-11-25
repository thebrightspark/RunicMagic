package brightspark.runicmagic.enums;

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

    SpellType(MagicType magicType)
    {
        this.magicType = magicType;
    }

    public MagicType getMagicType()
    {
        return magicType;
    }

    public enum MagicType
    {
        COMBAT,
        TELEPORT,
        SKILLING
    }
}
