package me.ChristopherW.core.custom;

import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.process.Game;

public enum BloonType {
	RED(1,1,1, Game.RED, 0.5f),
	BLUE(1,2,1.4f, Game.BLUE, 0.5f),
	GREEN(1,3,1.8f, Game.GREEN, 0.5f),
	YELLOW(1,4,3.2f, Game.YELLOW, 0.5f),
	PINK(1,5,3.5f, Game.PINK, 0.5f),
	BLACK(1,11,1.8f, Game.BLACK, 0.4f),
	WHITE(1,11,2f, Game.WHITE, 0.4f),
	LEAD(1,23, 1.0f, Game.LEAD, 0.5f),
	ZEBRA(1,23,1.8f, Game.ZEBRA, 0.5f),
	RAINBOW(1,47,2.2f, Game.RAINBOW, 0.5f),
	CERAMIC(10, 104,2.5f, Game.CERAMIC, 0.5f),
	MOAB(200,616,1f, Game.MOAB, 0.075f);
	
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
		this.speed = speed;
		this.color = color;
		this.size = size;
	}
}
