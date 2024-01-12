package me.ChristopherW.core.custom;

import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.process.Game;

public enum BloonType {
	RED(1,1,1, Game.RED), 
	BLUE(1,2,1.4f, Game.BLUE), 
	GREEN(1,3,1.8f, Game.GREEN),
	YELLOW(1,4,3.2f, Game.YELLOW),
	PINK(1,5,3.5f, Game.PINK), 
	BLACK(1,11,1.8f, Game.BLACK), 
	WHITE(1,11,2f, Game.WHITE),
	CERAMIC(10, 104,2.5f, Game.CERAMIC),
	MOAB(200,616,1f, Game.MOAB);
	
	public int health;
	public int RBE;
	public float speed;
	public Texture color;
	
	//RBE is Red Bloon Equivalent 
	// constructor
	BloonType(int health, int RBE, float speed, Texture color){
		this.health = health;
		this.RBE = RBE;
		this.speed = speed;
		this.color = color;
	}
}
