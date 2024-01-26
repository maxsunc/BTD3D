package me.ChristopherW.core.entity;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;

import me.ChristopherW.core.utils.Utils;
import me.ChristopherW.process.Game;

import org.joml.Vector3f;

public class Entity {
    private String name;
    private PhysicsRigidBody rigidBody;
    private Model model;
    private Vector3f position, rotation, scale;
    private boolean isVisible;
    private boolean enabled;

    public Entity(Model model, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.name = "New Object";
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.isVisible = true;
        this.enabled = true;
        this.rigidBody = null;
    }

    public Entity(String name, Model model, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.name = name;
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.isVisible = true;
        this.enabled = true;
        this.rigidBody = null;
    }

    public boolean isEnabled() {
        return enabled;
    }


    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) {
            if(rigidBody != null)
                Game.physicsSpace.addCollisionObject(this.rigidBody);
            this.setVisible(true);
        } else {
            if(rigidBody != null)
                Game.physicsSpace.removeCollisionObject(this.rigidBody);
            this.setVisible(false);
        }
    }

    public void localTranslate(float x, float y, float z) {
        if(z != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * z;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * z;
        }
        if(x != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * x;
        }
        position.y += y;
    }
    public Vector3f toLocale(float x, float y, float z, Vector3f rot) {
        Vector3f vec = new Vector3f();
        if(z != 0) {
            vec.x = (float) Math.sin(Math.toRadians(rot.y)) * -1.0f * z;
            vec.z = (float) Math.cos(Math.toRadians(rot.y)) * z;
        }
        if(x != 0) {
            vec.x = (float) Math.sin(Math.toRadians(rot.y - 90)) * -1.0f * x;
            vec.z = (float) Math.cos(Math.toRadians(rot.y - 90)) * x;
        }
        vec.y = y;
        return vec;
    }
    public Vector3f toLocale(float x, float y, float z) {
        Vector3f vec = new Vector3f();
        if(z != 0) {
            vec.x = (float) Math.sin(Math.toRadians(this.rotation.y)) * -1.0f * z;
            vec.z = (float) Math.cos(Math.toRadians(this.rotation.y)) * z;
        }
        if(x != 0) {
            vec.x = (float) Math.sin(Math.toRadians(this.rotation.y - 90)) * -1.0f * x;
            vec.z = (float) Math.cos(Math.toRadians(this.rotation.y - 90)) * x;
        }
        vec.y = y;
        return vec;
    }
    public void localTranslate(float x, float y, float z, Vector3f rot) {
        if(z != 0) {
            position.x += (float) Math.sin(Math.toRadians(rot.y)) * -1.0f * z;
            position.z += (float) Math.cos(Math.toRadians(rot.y)) * z;
        }
        if(x != 0) {
            position.x += (float) Math.sin(Math.toRadians(rot.y - 90)) * -1.0f * x;
            position.z += (float) Math.cos(Math.toRadians(rot.y - 90)) * x;
        }
        position.y += y;
    }
    public void translate(float x, float y, float z) {
        this.position.x += x;
        this.position.y += y;
        this.position.z += z;
        if(this.rigidBody != null)
            this.rigidBody.setPhysicsLocation(Utils.convert(position));
    }
    public void translate(Vector3f translation) {
        translate(translation.x, translation.y, translation.z);
    }
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
        if(this.rigidBody != null)
            this.rigidBody.setPhysicsLocation(Utils.convert(position));
    }
    public void setPosition(Vector3f position) {
        setPosition(position.x, position.y, position.z);
    }

    public void rotate(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
    }
    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
        if(this.rigidBody != null) {
            Quaternion quat = new Quaternion();
            quat.fromAngles((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
            this.rigidBody.setPhysicsRotation(quat);
        }


    }
    public void setRotation(Vector3f rotation) {
            setRotation(rotation.x, rotation.y, rotation.z);
}
    public void setScale(float scale) {
        setScale(scale, scale, scale);
    }
    public void setScale(Vector3f scale) {
        setScale(scale.x, scale.y, scale.z);
    }
    public void setScale(float x, float y, float z) {
        this.scale.x = x;
        this.scale.y = y;
        this.scale.z = z;
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public PhysicsRigidBody getRigidBody() {
        return rigidBody;
    }

    public void setRigidBody(PhysicsRigidBody rigidBody) {
        this.rigidBody = rigidBody;
        rigidBody.setPhysicsLocation(Utils.convert(this.getPosition()));
        rigidBody.setPhysicsRotation(rigidBody.getPhysicsRotation(null).fromAngles(this.rotation.x, this.rotation.y, this.rotation.z));
        Game.physicsSpace.addCollisionObject(rigidBody);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void lookAtY(Vector3f in) {
        float Dx = in.x - position.x;
        float Dz = in.z - position.z;
        float y = (float)(-Math.atan2(Dz,Dx));
        this.setRotation(0, (float)Math.toDegrees(y) + 90, 0);
    }

    public Vector3f getFoward() {
        Vector3f forward = new Vector3f();
        forward.x = (float) (Math.cos(Math.toRadians(this.rotation.x)) * Math.sin(Math.toRadians(this.rotation.y)));
        forward.y = (float) (-Math.sin(Math.toRadians(this.rotation.x)));
        forward.z = (float) (Math.cos(Math.toRadians(this.rotation.x)) * Math.cos(Math.toRadians(this.rotation.y)));
        return forward;
    }

    public Vector3f getRight() {
        Vector3f right = new Vector3f();
        right.x = (float) (Math.cos(Math.toRadians(this.rotation.y)));
        right.y = 0;
        right.z = (float) (-Math.sin(Math.toRadians(this.rotation.y)));
        return right;
    }
}