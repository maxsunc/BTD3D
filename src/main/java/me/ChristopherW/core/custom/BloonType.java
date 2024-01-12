package me.ChristopherW.core.custom;

import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.process.Game;

public enum BloonType {
	RED(1,1, Game.RED), 
	BLUE(2, 1.4f, Game.BLUE), 
	GREEN(3,1.8f, Game.GREEN),
	YELLOW(4,2.4f, Game.YELLOW),
	PINK(5,2.8f, Game.PINK), 
	BLACK(6, 1.5f, Game.BLACK), 
	ZEBRA(7, 1.8f, Game.WHITE),
	PURPLE(15,3.5f, Game.MAGENTA),
	MOAB(100,0.8f, Game.MOAB);
	
	public int health;
	public float speed;
	public Texture color;
	
	// constructor
	BloonType(int health, float speed, Texture color){
		this.health = health;
		// speed relative to red bloon (1)
		this.speed = speed;
		this.color = color;
	}

	public static BloonType getTypeFromHealth(int health) {
		for (int i = 0; i < values().length; i++) {
			BloonType t = values()[i];
			if(t.health == health)
			return t;
		}
		return null;
	}
}
