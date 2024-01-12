package me.ChristopherW.core.custom;

public enum MonkeyType {
	DART_MONKEY(170,5,0.5f,50),
	SNIPER_MONKEY(300,100,1.34f,100);
	
	public int cost;
	public int range;
    public float rate;
	public float speed;
	
	MonkeyType(int cost, int range, float rate, float speed){
		this.cost = cost;
		this.range = range;
        this.rate = rate;
		this.speed = speed;
	}
}
