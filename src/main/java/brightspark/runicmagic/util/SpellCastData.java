package brightspark.runicmagic.util;

public class SpellCastData
{
	private final int magicLevel, weaponLevel;

	public SpellCastData(int magicLevel, int weaponLevel)
	{
		this.magicLevel = magicLevel;
		this.weaponLevel = weaponLevel;
	}

	public int getMagicLevel()
	{
		return magicLevel;
	}

	public int getWeaponLevel()
	{
		return weaponLevel;
	}
}
