package me.ChristopherW.core;

import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.custom.Assets;
import me.ChristopherW.core.custom.Animations.RiggedMesh;
import me.ChristopherW.core.custom.Shaders.DebugShader;
import me.ChristopherW.core.custom.Shaders.DepthShader;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Mesh;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.process.Game;
import me.ChristopherW.process.Launcher;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderManager {
    private final WindowManager window;
    public Matrix4f viewMatrix;
    private Map<Mesh, List<Entity>> entities = new HashMap<>();
    private int depthFBO;
    private int depthMap;
    private DepthShader depthShader;
    private DebugShader debugShader;

    public RenderManager() {
        window = Launcher.getWindow();
    }

    public void init() {
        depthFBO = GL30.glGenFramebuffers();
        depthMap = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, depthMap);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT, GlobalVariables.SHADOW_RES, GlobalVariables.SHADOW_RES, 0, GL30.GL_DEPTH_COMPONENT, GL30.GL_FLOAT, MemoryUtil.NULL);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_BORDER); 
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_BORDER); 
        //float[] borderColor = { 0.0f, 0.0f, 0.0f, 0.0f };
        float[] borderColor = { 1.0f, 1.0f, 1.0f, 1.0f };
        GL30.glTexParameterfv(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_BORDER_COLOR, borderColor);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthFBO);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_TEXTURE_2D, depthMap, 0);
        GL30.glDrawBuffer(GL30.GL_NONE);
        GL30.glReadBuffer(GL30.GL_NONE);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);  

        
        try {
            depthShader = new DepthShader("/shaders/depthv.glsl", "/shaders/depthf.glsl");
            depthShader.start();

            debugShader = new DebugShader("/shaders/debugv.glsl", "/shaders/debugf.glsl");
            debugShader.start();
        } catch (Exception e) {
            
        }
    }

    public void bind(Mesh mesh) {
        GL30.glBindVertexArray(mesh.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if(mesh instanceof RiggedMesh) {
            GL20.glEnableVertexAttribArray(3);
            GL20.glEnableVertexAttribArray(4);
            GL20.glEnableVertexAttribArray(5);
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0);

        // bind the texture that the model has
        if(mesh.getMaterial().hasTexture()) {
            GL11.glBindTexture(GL30.GL_TEXTURE_2D, mesh.getMaterial().getTexture().getId());
        } 
        // or use the default texture if none specified
        else {
            GL11.glBindTexture(GL30.GL_TEXTURE_2D, Assets.defaultTexture.getId());
        }
        GL13.glActiveTexture(GL30.GL_TEXTURE0 + 1);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, depthMap);
    }

    public void unbind() {
        // unbind the model's VAOs 
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL20.glDisableVertexAttribArray(5);
        GL30.glBindVertexArray(0);
    }

    public void prepare(IShader shader, Entity entity, Mesh mesh, Camera camera) {
        // set all the uniform variables to pass to the shader
        shader.prepare(entity, mesh, camera);
    }

    public void renderSceneOrtho(Camera camera) {
        for(Mesh mesh : entities.keySet()) {
            // bind the model's shader and set the projectionMatrix uniform data
            depthShader.bind();
            
            // bind the model itself
            bind(mesh);

            // for each entity that uses that mesh
            List<Entity> entityList = entities.get(mesh);
            for(Entity entity : entityList) {
                prepare((IShader)depthShader, entity, mesh, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            
            // unbind the model
            unbind();

            // unbind the shader
            depthShader.unbind();
        }
    }

    int quadVAO = 0;
    int quadVBO;
    void renderQuad() {
        debugShader.bind();
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, depthMap);
        if (quadVAO == 0)
        {
            float quadVertices[] = {
                // positions        // texture Coords
                -1.0f,  1.0f, 0.0f, 0.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
                1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            };
            // setup plane VAO
            quadVAO = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(quadVAO);
            quadVBO = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, quadVBO);
            FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(quadVertices.length);
            vertexBuffer.put(quadVertices).flip();
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexBuffer, GL30.GL_STATIC_DRAW);

            // Specify the vertex attribute pointers for position and texture coordinates
            int positionAttrib = 0; // Position attribute location in the shader
            int texCoordAttrib = 1; // Texture coordinate attribute location in the shader
            int stride = (3 + 2) * 4; // 3 float values for position + 2 float values for texture coordinates
            GL30.glVertexAttribPointer(positionAttrib, 3, GL11.GL_FLOAT, false, stride, 0);
            GL30.glEnableVertexAttribArray(positionAttrib);
            GL30.glVertexAttribPointer(texCoordAttrib, 2, GL11.GL_FLOAT, false, stride, 3 * 4);
            GL30.glEnableVertexAttribArray(texCoordAttrib);
        }
        
        GL30.glBindVertexArray(quadVAO);
        GL30.glDrawArrays(GL30.GL_TRIANGLE_STRIP, 0, 4);
        GL30.glBindVertexArray(0);

        debugShader.unbind();
    }

    public void forceRender(Entity entity, Camera camera) {
        if(!entity.isEnabled() || !entity.isVisible())
            return;
        for(Mesh mesh : entity.getModel().getMeshes().values()) {
            // bind the model's shader and set the projectionMatrix uniform data
            mesh.getShader().bind();
            mesh.getShader().setUniform("projectionMatrix", window.updateProjectionMatrix());
            
            // bind the mesh itself
            bind(mesh);

                // prepare it to be rendered then draw the triangles to the viewBuffer
            prepare((IShader) mesh.getShader(), entity, mesh, camera);
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
 
            // unbind the model
            unbind();

            // unbind the shader
            mesh.getShader().unbind();
        }
    }

    public void renderScene(Camera camera) {

        // for each model in the entities 
        for(Mesh mesh : entities.keySet()) {
            // bind the model's shader and set the projectionMatrix uniform data
            mesh.getShader().bind();
            mesh.getShader().setUniform("projectionMatrix", window.updateProjectionMatrix());
            // bind the mesh itself
            bind(mesh);
            // for each entity that uses that model
            List<Entity> entityList = entities.get(mesh);
            for(Entity entity : entityList) {
                // prepare it to be rendered then draw the triangles to the viewBuffer
                prepare((IShader) mesh.getShader(), entity, mesh, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            
            // unbind the model
            unbind();

            // unbind the shader
            mesh.getShader().unbind();
        }
    }

    public void render(Camera camera) {
        GL30.glViewport(0, 0, GlobalVariables.SHADOW_RES, GlobalVariables.SHADOW_RES);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthFBO);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        renderSceneOrtho(camera);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        GL11.glViewport(0,0, window.getWidth(), window.getHeight());
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        if(GlobalVariables.DEBUG_SHADOWS)
            renderQuad();
        else
            renderScene(camera);
        // clear the entities array for that model for the next frame
        entities.clear();
    }
    public void processEntity(Entity entity) {
        for(Mesh mesh : entity.getModel().getMeshes().values()) {
            List<Entity> entityList = entities.get(mesh);
            if(entityList != null)
                entityList.add(entity);
            else {
                List<Entity> newEntityList = new ArrayList<>();
                newEntityList.add(entity);
                entities.put(mesh, newEntityList);
            }
        }
        // TLDR; bind together the entities that share the same model
        
    }

    public void cleanup() {
        // clean up the memory of all the models
        for(Mesh mesh : entities.keySet()) {
            mesh.getShader().cleanup();
        }
    }
}
