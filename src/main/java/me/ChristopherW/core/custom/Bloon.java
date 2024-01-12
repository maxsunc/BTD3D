package me.ChristopherW.core.custom;

import org.joml.Vector3f;

import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Material;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.process.Game;
import me.ChristopherW.process.Launcher;

public class Bloon extends Entity{
    private float speed;
    private BloonType type;
    private int health;
    private int nodeIndex;
    private Vector3f currentHeading;
    


    public Bloon(String name, BloonType type, Model model, Vector3f position, Vector3f rotation, Vector3f scale){
        super(name, model, position, rotation, scale);
        nodeIndex = 0;
        this.type = type;
        this.speed = type.speed;
        this.health = type.health;
        this.getModel().setAllMaterials(new Material(1f, 5f, type.color));
    }

    


    public float getSpeed() {
        return speed;
    }


    public void setSpeed(float speed) {
        this.speed = speed;
    }


    public BloonType getType() {
        return type;
    }


    public void setType(BloonType type) {
        this.type = type;
    }


    public int getHealth() {
        return health;
    }


    public void setHealth(int health) {
        this.health = health;
    }
    
    public int damage(int amount){
        this.health -= amount;

        if(this.health <= 0) {
            return 1;
        }

        if(type == BloonType.MOAB) {
            Game.audioSources.get("moab_damage").play();
        }

        BloonType newBloonType = BloonType.getTypeFromHealth(health);
        if(newBloonType != null) {
            if(type == BloonType.MOAB) {
                this.setModel(Model.copy(Game.bloonModel));
                this.setScale(0.5f);
                Game.audioSources.get("moab_destroyed").play();

                Game game = Launcher.getGame();
                Vector3f diff = new Vector3f();
                game.bloonNodes[Math.max(this.nodeIndex - 1,0)].sub(this.getPosition(), diff);
                for(int i = 0; i < 9; i++) {
                    Bloon b = new Bloon("bloon", BloonType.PURPLE,
                        Model.copy(Game.bloonModel), 
                        new Vector3f(game.bloonNodes[Math.max(this.nodeIndex - 1,0)]), 
                        new Vector3f(),
                        new Vector3f(0.5f)
                    );
                    game.bloons.add(b);
                    game.bloonCounter++;
                    game.entities.put("bloon" + game.bloonCounter, b);
                    b.setName("bloon" + game.bloonCounter);
                    b.setCurrentHeading(this.currentHeading);
                    b.setNodeIndex(this.nodeIndex);
                }
            }
            this.speed = newBloonType.speed;
            this.health = newBloonType.health;
            this.getModel().setAllMaterials(new Material(1f, 5f, newBloonType.color));
            this.type = newBloonType;
            return 0;
        }

        return -1;
    }


    public int getNodeIndex() {
        return nodeIndex;
    }


    public void incremenNodeIndex(){
        if(nodeIndex >= 45){
            this.setEnabled(false);
            return;
        }
        nodeIndex++;
    }




    public void setNodeIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }




    public Vector3f getCurrentHeading() {
        return currentHeading;
    }




    public void setCurrentHeading(Vector3f currentHeading) {
        this.currentHeading = currentHeading;
    }

}
