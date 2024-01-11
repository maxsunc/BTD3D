package me.ChristopherW.core.custom;

import java.awt.Color;

public enum BloonType {
	RED(1,1, Color.RED), 
	BLUE(2, 1.4f, Color.BLUE), 
	GREEN(3,1.8f, Color.GREEN),
	YELLOW(4,2.4f, Color.YELLOW),
	PINK(5,2.8f, Color.PINK), 
	BLACK(6, 1.5f, Color.BLACK), 
	ZEBRA(7, 1.8f, Color.WHITE),
	PURPLE(15,3.5f, Color.MAGENTA);
	
	public int health;
	public float speed;
	public Color color;
	
	// constructor
	BloonType(int health, float speed, Color color){
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
