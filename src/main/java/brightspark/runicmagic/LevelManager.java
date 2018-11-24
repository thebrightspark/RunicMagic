package brightspark.runicmagic;

import java.util.*;

public class LevelManager
{
	private static Map<Short, Integer> experience;

	static
	{
		experience = new HashMap<>();
		put(1, 0);
		for(int i = 2; i < 100; i++)
			put(i, experienceForLevel(i));
	}

	private static void put(int level, int xp)
	{
		experience.put((short) level, xp);
	}

	private static int experienceForLevel(int level)
	{
		double L1_8 = 0.125D * (double) level;
		double d1 = Math.pow(2D, (double) (level - 1) / 7D) - 1D;
		double d2 = 1D - Math.pow(2D, -(1D / 7D));
		double xp = (((double) level * L1_8) - L1_8) + (75D * (d1 / d2));
		double truncation = 0.109D * (double) level;
		return (int) Math.round(xp - truncation);
	}

	public static int get(int level)
	{
		Integer xp = experience.get((short) level);
		if(xp == null)
		{
			xp = experienceForLevel(level);
			put(level, xp);
		}
		return xp;
	}

	public static int getXpToNextLevel(int level)
	{
		return get(level + 1) - get(level);
	}

	public static int getLevelForXp(int xp)
	{
		List<Map.Entry<Short, Integer>> list = new ArrayList<>(experience.entrySet());
		list.sort(Comparator.comparing(Map.Entry::getKey));
		for(int i = 0; i < list.size(); i++)
		{
			Map.Entry<Short, Integer> entry = list.get(i);
			if(xp >= entry.getValue() && xp < list.get(i + 1).getValue())
				return entry.getKey();
		}
		return list.get(list.size() - 1).getKey();
	}
}
