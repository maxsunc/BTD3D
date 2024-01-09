package me.ChristopherW.core.custom;

public enum BloonType {
  RED(1,1), BLUE(2, 1.4f), GREEN(3,1.8f),YELLOW(4,2.4f),PINK(5,2.8f), BLACK(6, 1.5f), ZEBRA(7, 1.8f);
public int health;
  public float speed;
  // constructor
  BloonType(int health, float speed){
    this.health = health;
    // speed relative to red bloon (1)
    this.speed = speed;
  }
}
