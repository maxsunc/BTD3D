package me.ChristopherW.core.custom;

public enum TowerType {
	DART_MONKEY(170,5,0.5f,50, TowerClass.PRIMARY),
	SNIPER_MONKEY(300,100,1.34f,100, TowerClass.MILITARY),
	BOMB_TOWER(445 ,6,0.8f,25, TowerClass.PRIMARY);
	
	public int cost;
	public int range;
    public float rate;
	public float speed;
	public TowerClass towerClass;
	
	TowerType(int cost, int range, float rate, float speed, TowerClass towerClass){
		this.cost = cost;
		this.range = range;
        this.rate = rate;
		this.speed = speed;
		this.towerClass = towerClass;
	}
}
