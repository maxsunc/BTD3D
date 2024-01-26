package me.ChristopherW.core.custom.Towers;

import java.util.ArrayList;

import org.joml.Vector3f;

import me.ChristopherW.core.custom.Assets;
import me.ChristopherW.core.custom.Bloon;
import me.ChristopherW.core.custom.Bomb;
import me.ChristopherW.core.custom.Projectile;
import me.ChristopherW.core.custom.Tower;
import me.ChristopherW.core.custom.TowerType;
import me.ChristopherW.core.custom.Upgrade;
import me.ChristopherW.core.custom.Animations.RiggedModel;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.process.Launcher;

public class BombTower extends Tower implements ITower {

    public BombTower(String name, RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale,
            TowerType type) {
        super(name, model, position, rotation, scale, type);
        setPath1(Upgrade.BT_BASE_RELOAD);
        setPath2(Upgrade.BT_BASE_RANGE);
        this.setPostAttackAnimationId(1);
        this.setAttackAnimationId(0);
        this.setIdleAnimationId(1);
        this.setIdle1AnimationId(1);
        this.setIdle2AnimationId(1);
        this.setSpawnAnimationId(1);
    }

    @Override
    public Projectile spawnProjectile() {
        return new Bomb("dart", Model.copy(Assets.bombModel), 
            new Vector3f(this.getPosition().x, this.getPosition().y + 0.6f, this.getPosition().z), 
            new Vector3f(), 
            new Vector3f(0.5f), getDamage()
        );
    }

    @Override
    public void upgrade(Upgrade currentUpgrade){
        switch(currentUpgrade.nextUpgrade){
            case BT_EXTRA_RANGE:
                this.setRange(this.getRange() * 1.25f);
                break;
            case BT_EVEN_MORE_RANGE:
                this.setRange(getRange() * 1.2f);
                break;
            case BT_FASTER_RELOAD:
                this.setRate(getRate() *0.85f);
                break;
            case BT_EVEN_FASTER_RELOAD:
                this.setRate(this.getRate() * 0.80f);
                break;
        }
    }

    @Override
    public void shoot(ArrayList<Bloon> bloons, ArrayList<Projectile> projectiles, ArrayList<Bloon> targeted){
        if(bloons.size() < 1)
            return;
        Bloon target = null;
        for(int b = 0; b < bloons.size(); b++) {
            Bloon bloon = bloons.get(b);
            if(bloon.isPopped()) {
                bloons.remove(bloon);
                continue;
            }
            if(bloon.getPosition().distance(this.getPosition()) <= this.getRange()) {
                target = bloon;
                break;
            }
        }
        if(target == null)
            return;
        this.setAnimationId(this.getAttackAnimationId());
        this.lookAtY(new Vector3f(target.getPosition()));
        
        Projectile d = ((ITower)this).spawnProjectile();
        d.setDestinationDirection(new Vector3f(target.getPosition()));
        d.lookAtY(new Vector3f(target.getPosition()));
        d.setSource(this);
        d.setSpeed(this.getSpeed());
        d.setTarget(target);
        targeted.add(target);
        bloons.remove(target);

        projectiles.add(d);
        d.setName("dart" + projectiles.size());
        Launcher.getGame().entities.put("dart" + projectiles.size(), d);
        this.setTick(0);
    }
    
}
