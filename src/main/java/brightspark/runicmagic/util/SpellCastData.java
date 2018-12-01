package brightspark.runicmagic.util;

import brightspark.runicmagic.enums.RuneType;

public class SpellCastData
{
	private final int magicLevel;
	private final float attackBonus;
	private final RuneType runeCostReduction;

	public SpellCastData(int magicLevel, float attackBonus, RuneType runeCostReduction)
	{
		this.magicLevel = magicLevel;
		this.attackBonus = attackBonus;
		this.runeCostReduction = runeCostReduction;
	}

	public int getMagicLevel()
	{
		return magicLevel;
	}

	public float getAttackBonus()
	{
		return attackBonus;
	}

	public RuneType getRuneCostReduction()
	{
		return runeCostReduction;
	}
}
