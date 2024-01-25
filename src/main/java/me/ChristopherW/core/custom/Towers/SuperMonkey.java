package me.ChristopherW.core.custom.Towers;

import java.util.ArrayList;

import org.joml.Vector3f;

import me.ChristopherW.core.custom.Bloon;
import me.ChristopherW.core.custom.Projectile;
import me.ChristopherW.core.custom.Tower;
import me.ChristopherW.core.custom.TowerType;
import me.ChristopherW.core.custom.Upgrade;
import me.ChristopherW.core.custom.Animations.RiggedModel;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.process.Game;
import me.ChristopherW.process.Launcher;

public class SuperMonkey extends Tower implements ITower{

    public SuperMonkey(String name, RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale,
            TowerType type) {
        super(name, model, position, rotation, scale, type);
        setPath1(Upgrade.SUPM_BASE_RANGE);
        setPath2(Upgrade.SUPM_BASE_RATE);
        this.setPostAttackAnimationId(1);
        this.setAttackAnimationId(0);
        this.setIdleAnimationId(1);
        this.setIdle1AnimationId(2);
        this.setIdle2AnimationId(2);
        this.setSpawnAnimationId(2);
    }

    @Override
    public Projectile spawnProjectile() {
        return new Projectile("dart", Model.copy(Game.dartModel), 
            new Vector3f(this.getPosition().x, this.getPosition().y + 0.6f, this.getPosition().z).add(this.getRight().div(-3)), 
            new Vector3f(), 
            new Vector3f(0.1f), getDamage()
        );
    }

    @Override
    public void upgrade(Upgrade currentUpgrade){
        switch(currentUpgrade.nextUpgrade){
            
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
        
        Projectile d = this.spawnProjectile();
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
