package me.ChristopherW.core.custom;

import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.process.Game;

public enum Upgrade {
	BT_EVEN_MORE_RANGE("Even More Range", "Increases to even more range!", null, 300),
	BT_EXTRA_RANGE("Extra Range", "Increases attack range.", BT_EVEN_MORE_RANGE, 200),
	BT_EVEN_FASTER_RELOAD("Even Faster Reload", "Reloads EVEN Faster!", null, 400),
	BT_FASTER_RELOAD("Faster Reload", "Reloads Faster.", BT_EVEN_FASTER_RELOAD, 250),
	BT_BASE_RELOAD("","",BT_FASTER_RELOAD,0),
	BT_BASE_RANGE("","",BT_EXTRA_RANGE,0),
	SM_FULL_AUTOMATIC("Full Automatic", "Attacks 9x as faster.", null, 6000),
	SM_SEMIAUTOMATIC("Semi Automatic", "Attacks 3x as faster.", SM_FULL_AUTOMATIC, 3000),
	SM_EVEN_FASTER_FIRING("Even Faster Firing", "Shoots even faster!.", SM_SEMIAUTOMATIC, 400),
	SM_FASTER_FIRING("Faster Firing", "Shoots faster than normal.", SM_EVEN_FASTER_FIRING, 400),
	SM_DEADLY_PRECISION("Deadly Precision", "20 Damage per shot.", null, 3000),
	SM_LARGE_CALIBRE("Large Calibre", "7 Damage per shot.", SM_DEADLY_PRECISION, 1500),
	SM_FULL_METAL_JACKET("Full Metal Jacket", "3 Damage per shot.", SM_LARGE_CALIBRE, 350),
	SM_BASE_DAMAGE("","",SM_FULL_METAL_JACKET,0),
	SM_BASE_RATE("","",SM_FASTER_FIRING,0),
	DM_TRIPLE_SHOT("Triple Shot", "Throws 3 darts at a time instead of 1", null, 400),
	DM_VERY_QUICK_SHOTS("Very Quick Shots", "Shoots 33% faster!", DM_TRIPLE_SHOT, 190),
	DM_QUICK_SHOTS("Quick Shots", "Shoots 15% faster", DM_VERY_QUICK_SHOTS, 100),
	DM_ENCHANCED_EYESIGHT("Enchanced Eyesight", "Shoots even furthur", null, 200),
	DM_LONG_RANGE("Long Range Darts", "Makes the Dart Monkey shoot furthur than normal.", DM_ENCHANCED_EYESIGHT, 90),
	DM_BASE_RANGE("","", DM_LONG_RANGE, 0),
	DM_BASE_RATE("","", DM_QUICK_SHOTS, 0);

	public String name;
	public String description;
	public Upgrade nextUpgrade;
	public int cost;

	// RBE is Red Bloon Equivalent
	// constructor
	Upgrade(String name, String description, Upgrade nextUpgrade, int cost) {
		this.name = name;
		this.description = description;
		this.nextUpgrade = nextUpgrade;
		this.cost = cost;
	}
}
