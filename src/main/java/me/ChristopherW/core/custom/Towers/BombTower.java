package me.ChristopherW.core.custom.Towers;

import org.joml.Vector3f;

import me.ChristopherW.core.custom.Bomb;
import me.ChristopherW.core.custom.Projectile;
import me.ChristopherW.core.custom.Tower;
import me.ChristopherW.core.custom.TowerType;
import me.ChristopherW.core.custom.Animations.RiggedModel;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.process.Game;

public class BombTower extends Tower implements ITower {

    public BombTower(String name, RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale,
            TowerType type) {
        super(name, model, position, rotation, scale, type);
        this.setPostAttackAnimationId(1);
        this.setAttackAnimationId(0);
        this.setIdleAnimationId(1);
        this.setIdle1AnimationId(1);
        this.setIdle2AnimationId(1);
        this.setSpawnAnimationId(1);
    }

    @Override
    public Projectile spawnProjectile() {
        return new Bomb("dart", Model.copy(Game.bombModel), 
            new Vector3f(this.getPosition().x, this.getPosition().y + 0.6f, this.getPosition().z), 
            new Vector3f(), 
            new Vector3f(0.5f)
        );
    }
    
}
