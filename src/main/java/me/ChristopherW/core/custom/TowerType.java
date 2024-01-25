package me.ChristopherW.core.custom;

public enum TowerType {
	DART_MONKEY("Dart Monkey", 170,32,0.95f,30, TowerClass.PRIMARY),
	SNIPER_MONKEY("Sniper Monkey", 300,1600,1.59f,50, TowerClass.MILITARY),
	BOMB_TOWER("Bomb Shooter", 445 ,40,1.5f,25, TowerClass.PRIMARY),
	SUPER_MONKEY("Super Monkey", 2125,75,0.145f,30, TowerClass.PRIMARY);
	
	public String name;
	public int cost;
	public int range;
    public float rate;
	public float speed;
	public TowerClass towerClass;
	
	TowerType(String name, int cost, int range, float rate, float speed, TowerClass towerClass) {
		this.name = name;
		this.cost = cost;
		this.range = range/10;
        this.rate = rate;
		this.speed = speed;
		this.towerClass = towerClass;
	}
}
