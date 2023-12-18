package me.ChristopherW.core;

import org.joml.Vector3f;

import org.joml.Matrix4f;

public class Camera {
    private Vector3f position, rotation;
    private Matrix4f lightSpaceMatrix;

    public Camera() {
        this(new Vector3f(0,0,0), new Vector3f(0,0,0));
    }
    public Camera(Vector3f pos, Vector3f rot) {
        this.position = pos;
        this.rotation = rot;

        Matrix4f lightProjection = new Matrix4f();
        lightProjection.ortho(-20, 20, -20, 20, 15f, 45f);

        Matrix4f lightView = new Matrix4f();
        lightView.lookAt(
            new Vector3f(20.0f, 20f, 10.0f),
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(0.0f, 1.0f, 0.0f)
        );

        lightSpaceMatrix = lightProjection.mul(lightView); 
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }
    public void setPosition(Vector3f vector3f) {
        setPosition(vector3f.x, vector3f.y, vector3f.z);
    }


    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    public void rotate(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setLightSpaceMatrix(Matrix4f mat) {
        lightSpaceMatrix = mat;
    }

    public Matrix4f getLightSpaceMatrix() {
        return lightSpaceMatrix;
    }
}
