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

public class DartMonkey extends Tower implements ITower{

    public DartMonkey(String name, RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale,
            TowerType type) {
        super(name, model, position, rotation, scale, type);
        setPath1(Upgrade.DM_BASE_RATE);
        setPath2(Upgrade.DM_BASE_RANGE);
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
            case DM_LONG_RANGE:
                this.setRange(this.getRange() * 1.3f);
                break;
            case DM_ENCHANCED_EYESIGHT:
                this.setRange(getRange() * 1.5f);
                break;
            case DM_QUICK_SHOTS:
                this.setRate(getRate() * 0.8f);
                break;
            case DM_VERY_QUICK_SHOTS:
                this.setRate(this.getRate() * 0.75f);
                break;
            case DM_TRIPLE_SHOT:
                // die
                break;
        }
        System.out.println(this.getRange());
        System.out.println("RATE: " + this.getRate());
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

        if(getPath1() == Upgrade.DM_TRIPLE_SHOT) {
            for(int i = -1; i < 2; i += 2) {
                Vector3f offset = new Vector3f();
                getRight().mul(i,1,1, offset);
                Vector3f pos = new Vector3f(d.getTarget().getPosition()).add(offset);
                Projectile dart = this.spawnProjectile();
                dart.setDestinationDirection(pos);
                dart.lookAtY(pos);
                dart.setSource(this);
                dart.setSpeed(this.getSpeed());
                projectiles.add(dart);
                dart.setName("dart" + projectiles.size());
                Launcher.getGame().entities.put("dart" + projectiles.size(), dart);
            }
        }

        projectiles.add(d);
        d.setName("dart" + projectiles.size());
        Launcher.getGame().entities.put("dart" + projectiles.size(), d);
        this.setTick(0);
    }
}
