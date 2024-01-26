package me.ChristopherW.core.custom;

import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.process.Game;

public enum BloonType {
	RED(1,1,1, Assets.RED, 0.5f),
	BLUE(1,2,1.4f, Assets.BLUE, 0.5f),
	GREEN(1,3,1.8f, Assets.GREEN, 0.5f),
	YELLOW(1,4,3.2f, Assets.YELLOW, 0.5f),
	PINK(1,5,3.5f, Assets.PINK, 0.5f),
	BLACK(1,11,1.8f, Assets.BLACK, 0.4f),
	WHITE(1,11,2f, Assets.WHITE, 0.4f),
	LEAD(1,23, 1.0f, Assets.LEAD, 0.5f),
	ZEBRA(1,23,1.8f, Assets.ZEBRA, 0.5f),
	RAINBOW(1,47,2.2f, Assets.RAINBOW, 0.5f),
	CERAMIC(10, 104,2.5f, Assets.CERAMIC, 0.5f),
	MOAB(132,616,1f, Assets.MOAB, 0.075f);
	
	public int health;
	public int RBE;
	public float speed;
	public Texture color;
	public float size;
	
	//RBE is Red Bloon Equivalent 
	// constructor
	BloonType(int health, int RBE, float speed, Texture color, float size) {
		this.health = health;
		this.RBE = RBE;
		this.speed = speed * 0.91f;
		this.color = color;
		this.size = size;
	}
}
