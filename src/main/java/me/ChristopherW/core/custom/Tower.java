package me.ChristopherW.core.custom;

import org.joml.Vector3f;

import me.ChristopherW.core.custom.Towers.ITower;

import me.ChristopherW.core.custom.Animations.AnimatedEntity;
import me.ChristopherW.core.custom.Animations.RiggedModel;

public class Tower extends AnimatedEntity{
    private int value;
    private float range;
    private float rate;
    private float speed;
    private int damage = 1;
    private float tick;
    private Upgrade path1;
    private Upgrade path2;
    
    private int spawnAnimationId;
    private int postAttackAnimationId;
    private int attackAnimationId;
    private int idleAnimationId;
    private int idle1AnimationId;
    private int idle2AnimationId;

    public Tower(String name, RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale, TowerType type){
        super(name, model, position, rotation, scale);
        this.range = type.range;
        this.rate = type.rate;
        this.speed = type.speed;
        this.value = type.cost;
        this.tick = 0f;

        this.spawnAnimationId = 2;
        this.postAttackAnimationId = 0;
        this.attackAnimationId = 1;
        this.idleAnimationId = 2;
        this.idle1AnimationId = 3;
        this.idle2AnimationId = 4;
    }

    public int getValue() {
        return value;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getTick() {
        return tick;
    }

    public void addTick(float tick) {
        this.tick += tick;
    }
    public void setTick(float tick) {
        this.tick = tick;
    }


    public void setSpawnAnimationId(int spawnAnimationId) {
        this.spawnAnimationId = spawnAnimationId;
    }
    public int getSpawnAnimationId() {
        return spawnAnimationId;
    }
    public int getAttackAnimationId() {
        return this.attackAnimationId;
    }
    public void setAttackAnimationId(int id) {
        this.attackAnimationId = id;
    }

    public int getIdleAnimationId() {
        return idleAnimationId;
    }

    public void setIdleAnimationId(int idleAnimationId) {
        this.idleAnimationId = idleAnimationId;
    }

    public int getIdle1AnimationId() {
        return idle1AnimationId;
    }

    public void setIdle1AnimationId(int idle1AnimationId) {
        this.idle1AnimationId = idle1AnimationId;
    }

    public int getIdle2AnimationId() {
        return idle2AnimationId;
    }

    public void setIdle2AnimationId(int idle2AnimationId) {
        this.idle2AnimationId = idle2AnimationId;
    }

    public int getPostAttackAnimationId() {
        return postAttackAnimationId;
    }
    public void setPostAttackAnimationId(int postAttackAnimationId) {
        this.postAttackAnimationId = postAttackAnimationId;
    }

    public void setPath1(Upgrade path1) {
        this.path1 = path1;
    }

    public void setPath2(Upgrade path2) {
        this.path2 = path2;
    }

    public void nextPath1() {
        this.path1 = this.path1.nextUpgrade;
    }

    public void nextPath2() {
        this.path2 = this.path2.nextUpgrade;
    }

    public int getDamage(){
        return this.damage;
    }

    public void setDamage(int damage){
        this.damage = damage;
    }

    public Upgrade getPath1() {
        return path1;
    }

    public Upgrade getPath2() {
        return path2;
    }

    public void upgradePath(int index){
        if(index == 1){
            ((ITower)this).upgrade(getPath1());
            setPath1(getPath1().nextUpgrade);
            this.value += getPath1().cost;
        }
        else if(index == 2){
            ((ITower)this).upgrade(getPath2());
            setPath2(getPath2().nextUpgrade);
            this.value += getPath2().cost;
        }
    }

}
