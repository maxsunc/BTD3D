package me.ChristopherW.core.custom;

public enum MonkeyType {
	DART_MONKEY(5,0.5f,50),
	SNIPER_MONKEY(30,1.34f,100);
	
	public int range;
    public float rate;
	public float speed;
	
	MonkeyType(int range, float rate, float speed){
		this.range = range;
        this.rate = rate;
		this.speed = speed;
	}
}
