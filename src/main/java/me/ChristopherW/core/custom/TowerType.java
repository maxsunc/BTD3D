package me.ChristopherW.core.custom;

public enum TowerType {
	DART_MONKEY("Dart Monkey", 170,5,0.95f,50, TowerClass.PRIMARY),
	SNIPER_MONKEY("Sniper Monkey", 300,100,1.59f,100, TowerClass.MILITARY),
	BOMB_TOWER("Bomb Shooter", 445 ,6,1.5f,25, TowerClass.PRIMARY);
	
	public String name;
	public int cost;
	public int range;
    public float rate;
	public float speed;
	public TowerClass towerClass;
	
	TowerType(String name, int cost, int range, float rate, float speed, TowerClass towerClass) {
		this.name = name;
		this.cost = cost;
		this.range = range;
        this.rate = rate;
		this.speed = speed;
		this.towerClass = towerClass;
	}
}
