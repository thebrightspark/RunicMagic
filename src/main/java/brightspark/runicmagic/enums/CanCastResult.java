package brightspark.runicmagic.enums;

import brightspark.runicmagic.RunicMagic;

public enum CanCastResult
{
    SUCCESS(""),
    NO_SPELL("nospell"),
    COOLDOWN("cooldown"),
    LEVEL("level"),
    RUNES("runes"),
    SPELL_REQ("spellreq");

    private final String failLang;

    CanCastResult(String failLang)
    {
        this.failLang = RunicMagic.MOD_ID + ".message.castfail." + failLang;
    }

    public String getFailLang()
    {
        return failLang;
    }
}
