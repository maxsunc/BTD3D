package me.ChristopherW.core.custom.Towers;

import org.joml.Vector3f;

import me.ChristopherW.core.custom.Projectile;
import me.ChristopherW.core.custom.Tower;
import me.ChristopherW.core.custom.TowerType;
import me.ChristopherW.core.custom.Animations.RiggedModel;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.process.Game;

public class DartMonkey extends Tower implements ITower{

    public DartMonkey(String name, RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale,
            TowerType type) {
        super(name, model, position, rotation, scale, type);
    }

    @Override
    public Projectile spawnProjectile() {
        return new Projectile("dart", Model.copy(Game.dartModel), 
            new Vector3f(this.getPosition().x, this.getPosition().y + 0.6f, this.getPosition().z).add(this.getRight().div(-3)), 
            new Vector3f(), 
            new Vector3f(0.1f)
        );
    }
    

}
