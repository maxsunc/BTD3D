package me.ChristopherW.core.custom.Towers;

import java.util.ArrayList;

import org.joml.Random;
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

public class SniperMonkey extends Tower implements ITower {

    public SniperMonkey(String name, RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale,
            TowerType type) {
        super(name, model, position, rotation, scale, type);
        setPath1(Upgrade.SM_BASE_DAMAGE);
        setPath2(Upgrade.SM_BASE_RATE);
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
            case SM_FULL_METAL_JACKET:
                this.setDamage(this.getDamage() + 2);
                break;
            case SM_LARGE_CALIBRE:
                this.setDamage(this.getDamage() + 4);
                break;
            case SM_FASTER_FIRING:
                this.setRate(getRate() * 0.85f);
                break;
            case SM_EVEN_FASTER_FIRING:
                this.setRate(this.getRate() * 0.70f);
                break;
            case SM_SEMIAUTOMATIC:
                this.setRate(this.getRate() * 1/3);
                break;
            case SM_FULL_AUTOMATIC:
                this.setRate(this.getRate() * 1/9);
                break;
            case SM_DEADLY_PRECISION:
                this.setDamage(this.getDamage() + 13);
                break;
        }
    }

    
    Random random = new Random();
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


        for(int i = 0; i < this.getDamage(); i++) {
            int result = target.damage(this.getDamage());
            if(result >= 0) {
                Launcher.getGame().playRandom(new String[]{"pop_1", "pop_2", "pop_3", "pop_4"});
                Launcher.getGame().player.addMoney(1);
                
                if(result > 0) {
                    Launcher.getGame().entities.remove(target.getName());
                    bloons.remove(target);
                    target.setPopped(true);
                }
            }
        }

        this.setTick(0);
    }
    
}
