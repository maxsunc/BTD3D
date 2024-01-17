package me.ChristopherW.core.custom.Towers;

import java.util.ArrayList;

import me.ChristopherW.core.custom.Bloon;
import me.ChristopherW.core.custom.Projectile;
import me.ChristopherW.core.custom.Upgrade;

public interface ITower {
    public Projectile spawnProjectile();

    public void upgrade(Upgrade currentUpgrade);
    public void shoot(ArrayList<Bloon> bloons, ArrayList<Projectile> projectiles, ArrayList<Bloon> targeted);

}
