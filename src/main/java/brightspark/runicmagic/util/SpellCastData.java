package brightspark.runicmagic.util;

public class SpellCastData
{
	private final int magicLevel;
	private final float attackBonus;

	public SpellCastData(int magicLevel, float attackBonus)
	{
		this.magicLevel = magicLevel;
		this.attackBonus = attackBonus;
	}

	public int getMagicLevel()
	{
		return magicLevel;
	}

	public float getAttackBonus()
	{
		return attackBonus;
	}
}
