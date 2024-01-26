package me.ChristopherW.core.custom;

import java.util.Arrays;

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
    private boolean popped;


    public boolean isPopped() {
        return popped;
    }




    public void setPopped(boolean popped) {
        this.popped = popped;
    }




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
        Game game = Launcher.getGame();

        if(this.health <= 0 && type == BloonType.RED)
            return -1;

        this.health -= amount;

        if(this.health <= 0) {
            if(type != BloonType.RED) {
                BloonType newBloonType = BloonType.values()[Arrays.binarySearch(BloonType.values(), type) - 1];
                if(newBloonType != null) {
                    if(type == BloonType.MOAB) {
                        this.setModel(Model.copy(Assets.bloonModel));
                        this.setScale(0.5f);
                        Game.audioSources.get("moab_destroyed").play();
        
                        Vector3f diff = new Vector3f();
                        game.getMapNodes()[Math.max(this.nodeIndex - 1,0)].sub(this.getPosition(), diff);
                        for(int i = 0; i < 9; i++) {
                            // move it towards the node pos
                            Vector3f newPos = new Vector3f();
                            Vector3f prev = game.getMapNodes()[Math.max(this.nodeIndex - 1,0)];
                            prev.lerp(new Vector3f(getPosition().x, prev.y, getPosition().z), i/9f, newPos);
                            Vector3f difference = new Vector3f();
                            game.getMapNodes()[this.nodeIndex].sub(newPos, difference);
                            difference.normalize();
                            Bloon b = new Bloon("bloon", BloonType.CERAMIC,
                                Model.copy(Assets.bloonModel), 
                                new Vector3f(newPos), 
                                new Vector3f(),
                                new Vector3f(0.5f)
                            );
                            game.getBloons().add(b);
                            game.addBloonCount(1);
                            game.entities.put("bloon" + game.getBloonCount(), b);
                            b.setName("bloon" + game.getBloonCount());
                            b.setCurrentHeading(difference);
                            b.setNodeIndex(this.nodeIndex);
                        }
                    }
                    else if(type == BloonType.CERAMIC) {
                        newBloonType = BloonType.RAINBOW;

                        game.playRandom(new String[]{"ceramic_destroy_1", "ceramic_destroy_2", "ceramic_destroy_3"});

                        Vector3f diff = new Vector3f();
                        game.getMapNodes()[Math.max(this.nodeIndex - 1,0)].sub(this.getPosition(), diff);
                        // move it towards the node pos
                        Vector3f newPos = new Vector3f();
                        Vector3f prev = game.getMapNodes()[Math.max(this.nodeIndex - 1,0)];
                        prev.lerp(new Vector3f(getPosition().x, prev.y, getPosition().z), 1/9f, newPos);
                        Vector3f difference = new Vector3f();
                        game.getMapNodes()[this.nodeIndex].sub(newPos, difference);
                        difference.normalize();
                        BloonType bloonType = BloonType.RAINBOW;
                        Bloon b = new Bloon("bloon", bloonType,
                            Model.copy(Assets.bloonModel), 
                            new Vector3f(newPos), 
                            new Vector3f(),
                            new Vector3f(bloonType.size)
                        );
                        game.getBloons().add(b);
                        game.addBloonCount(1);
                        game.entities.put("bloon" + game.getBloonCount(), b);
                        b.setName("bloon" + game.getBloonCount());
                        b.setCurrentHeading(difference);
                        b.setNodeIndex(this.nodeIndex);
                        
                    }
                    else if(type == BloonType.RAINBOW) {
                        newBloonType = BloonType.ZEBRA;

                        Vector3f diff = new Vector3f();
                        game.getMapNodes()[Math.max(this.nodeIndex - 1,0)].sub(this.getPosition(), diff);
                        // move it towards the node pos
                        Vector3f newPos = new Vector3f();
                        Vector3f prev = game.getMapNodes()[Math.max(this.nodeIndex - 1,0)];
                        prev.lerp(new Vector3f(getPosition().x, prev.y, getPosition().z), 1/9f, newPos);
                        Vector3f difference = new Vector3f();
                        game.getMapNodes()[this.nodeIndex].sub(newPos, difference);
                        difference.normalize();
                        BloonType bloonType = BloonType.ZEBRA;
                        Bloon b = new Bloon("bloon", bloonType,
                            Model.copy(Assets.bloonModel), 
                            new Vector3f(newPos), 
                            new Vector3f(),
                            new Vector3f(bloonType.size)
                        );
                        game.getBloons().add(b);
                        game.addBloonCount(1);
                        game.entities.put("bloon" + game.getBloonCount(), b);
                        b.setName("bloon" + game.getBloonCount());
                        b.setCurrentHeading(difference);
                        b.setNodeIndex(this.nodeIndex);
                    }
                    else if(type == BloonType.ZEBRA) {
                        newBloonType = BloonType.BLACK;
                        Vector3f diff = new Vector3f();
                        game.getMapNodes()[Math.max(this.nodeIndex - 1,0)].sub(this.getPosition(), diff);
                        // move it towards the node pos
                        Vector3f newPos = new Vector3f();
                        Vector3f prev = game.getMapNodes()[Math.max(this.nodeIndex - 1,0)];
                        prev.lerp(new Vector3f(getPosition().x, prev.y, getPosition().z), 1/9f, newPos);
                        Vector3f difference = new Vector3f();
                        game.getMapNodes()[this.nodeIndex].sub(newPos, difference);
                        difference.normalize();
                        BloonType bloonType = BloonType.WHITE;
                        Bloon b = new Bloon("bloon", bloonType,
                            Model.copy(Assets.bloonModel), 
                            new Vector3f(newPos), 
                            new Vector3f(),
                            new Vector3f(bloonType.size)
                        );
                        game.getBloons().add(b);
                        game.addBloonCount(1);
                        game.entities.put("bloon" + game.getBloonCount(), b);
                        b.setName("bloon" + game.getBloonCount());
                        b.setCurrentHeading(difference);
                        b.setNodeIndex(this.nodeIndex);
                    }
                    else if(type == BloonType.LEAD) {
                        newBloonType = BloonType.BLACK;
                        Vector3f diff = new Vector3f();
                        game.getMapNodes()[Math.max(this.nodeIndex - 1,0)].sub(this.getPosition(), diff);
                        // move it towards the node pos
                        Vector3f newPos = new Vector3f();
                        Vector3f prev = game.getMapNodes()[Math.max(this.nodeIndex - 1,0)];
                        prev.lerp(new Vector3f(getPosition().x, prev.y, getPosition().z), 1/9f, newPos);
                        Vector3f difference = new Vector3f();
                        game.getMapNodes()[this.nodeIndex].sub(newPos, difference);
                        difference.normalize();
                        BloonType bloonType = BloonType.BLACK;
                        Bloon b = new Bloon("bloon", bloonType,
                            Model.copy(Assets.bloonModel), 
                            new Vector3f(newPos), 
                            new Vector3f(),
                            new Vector3f(bloonType.size)
                        );
                        game.getBloons().add(b);
                        game.addBloonCount(1);
                        game.entities.put("bloon" + game.getBloonCount(), b);
                        b.setName("bloon" + game.getBloonCount());
                        b.setCurrentHeading(difference);
                        b.setNodeIndex(this.nodeIndex);
                    }
                    else if(type == BloonType.BLACK || type == BloonType.WHITE) {
                        newBloonType = BloonType.PINK;
                        Vector3f diff = new Vector3f();
                        game.getMapNodes()[Math.max(this.nodeIndex - 1,0)].sub(this.getPosition(), diff);
                        // move it towards the node pos
                        Vector3f newPos = new Vector3f();
                        Vector3f prev = game.getMapNodes()[Math.max(this.nodeIndex - 1,0)];
                        prev.lerp(new Vector3f(getPosition().x, prev.y, getPosition().z), 1/9f, newPos);
                        Vector3f difference = new Vector3f();
                        game.getMapNodes()[this.nodeIndex].sub(newPos, difference);
                        difference.normalize();
                        BloonType bloonType = BloonType.PINK;
                        Bloon b = new Bloon("bloon", bloonType,
                            Model.copy(Assets.bloonModel), 
                            new Vector3f(newPos), 
                            new Vector3f(),
                            new Vector3f(bloonType.size)
                        );
                        game.getBloons().add(b);
                        game.addBloonCount(1);
                        game.entities.put("bloon" + game.getBloonCount(), b);
                        b.setName("bloon" + game.getBloonCount());
                        b.setCurrentHeading(difference);
                        b.setNodeIndex(this.nodeIndex);
                    }

                    this.speed = newBloonType.speed;
                    this.health = newBloonType.health;
                    this.getModel().setAllMaterials(new Material(1f, 5f, newBloonType.color));
                    this.setScale(newBloonType.size);
                    this.type = newBloonType;
                    return 0;
                }
            }
            return 1;
        }

        if(type == BloonType.MOAB) {
            game.playRandom(new String[]{"moab_damage_1", "moab_damage_2", "moab_damage_3", "moab_damage_4"});
            if(this.health < 40) {
                this.getModel().setAllMaterials(new Material(1f, 5f, Assets.MOAB_4));
            }
            else if(this.health < 80) {
                this.getModel().setAllMaterials(new Material(1f, 5f, Assets.MOAB_3));
            }
            else if(this.health < 120) {
                this.getModel().setAllMaterials(new Material(1f, 5f, Assets.MOAB_2));
            }
            else if(this.health < 160) {
                this.getModel().setAllMaterials(new Material(1f, 5f, Assets.MOAB_1));
            }
        }
        else if(type == BloonType.CERAMIC) {
            game.playRandom(new String[]{"ceramic_hit_1", "ceramic_hit_2", "ceramic_hit_3", "ceramic_hit_4"});
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
