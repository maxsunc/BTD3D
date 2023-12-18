package me.ChristopherW.core;

import me.ChristopherW.core.entity.Material;
import me.ChristopherW.core.utils.Utils;
import me.ChristopherW.lighting.DirectionalLight;
import me.ChristopherW.lighting.PointLight;
import me.ChristopherW.lighting.SpotLight;
import org.joml.*;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private final int programID;
    private int vertexShaderID, fragmentShaderID;
    private String vertexPath, fragmentPath;

    private final Map<String, Integer> uniforms;

    public ShaderManager(String vertexPath, String fragmentPath) throws Exception {
        // create a new shader program using the vertex and fragment shaders given
        this.vertexPath = vertexPath;
        this.fragmentPath = fragmentPath;
        programID = GL20.glCreateProgram();
        if(programID == 0)
            throw new Exception("Unable to create shader program");

        uniforms = new HashMap<>();
    }

    // ***
    // multiple different forms of Uniform methods as an object uniform has multiple properties that need to be created
    // ***
    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = GL20.glGetUniformLocation(programID, uniformName);
        if(uniformLocation < 0)
            throw new Exception("Couldn't find uniform " + uniformName);
        uniforms.put(uniformName, uniformLocation);
    }
    public void createPointLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".constant");
        createUniform(uniformName + ".linear");
        createUniform(uniformName + ".exponent");
    }
    public void createPointLightListUniform(String uniformName, int size) throws Exception {
        for(int i = 0; i < size; i++) {
            createPointLightUniform(uniformName + "[" + i + "]");
        }
    }
    public void createSpotLightUniform(String uniformName) throws Exception {
        createPointLightUniform(uniformName + ".pl");
        createUniform(uniformName + ".conedir");
        createUniform(uniformName + ".cutoff");
    }
    public void createSpotLightListUniform(String uniformName, int size) throws Exception {
        for(int i = 0; i < size; i++) {
            createSpotLightUniform(uniformName + "[" + i + "]");
        }
    }
    public void createDirectionalLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    // ***
    // multiple different forms of Uniform methods as each type identifier requires a different method
    // ***
    public void setUniform(String uniformName, Matrix3f value) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix3fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(9)));
        }
    }
    public void setUniform(String uniformName, Matrix4f value) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }
    public void setUniform(String uniformName, Vector2f value) {
        GL20.glUniform2f(uniforms.get(uniformName), value.x, value.y);
    }
    public void setUniform(String uniformName, Vector3f value) {
        GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }
    public void setUniform(String uniformName, Vector4f value) {
        GL20.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }
    public void setUniform(String uniformName, boolean value) {
        float res = 0;
        if(value)
            res = 1;
        GL20.glUniform1f(uniforms.get(uniformName), res);
    }
    public void setUniform(String uniformName, int value) {
        GL20.glUniform1i(uniforms.get(uniformName), value);
    }
    public void setUniform(String uniformName, float value) {
        GL20.glUniform1f(uniforms.get(uniformName), value);
    }
    public void setUniform(String uniformName, DirectionalLight light) {
        setUniform(uniformName + ".color", light.getColor());
        setUniform(uniformName + ".direction", light.getDirection());
        setUniform(uniformName + ".intensity", light.getIntensity());
    }
    public void setUniform(String uniformName, PointLight light) {
        setUniform(uniformName + ".color", light.getColor());
        setUniform(uniformName + ".position", light.getPosition());
        setUniform(uniformName + ".intensity", light.getIntensity());
        setUniform(uniformName + ".constant", light.getConstant());
        setUniform(uniformName + ".linear", light.getLinear());
        setUniform(uniformName + ".exponent", light.getExponent());
    }
    public void setUniform(String uniformName, SpotLight light) {
        setUniform(uniformName + ".pl", light.getPointLight());
        setUniform(uniformName + ".conedir", light.getConeDirection());
        setUniform(uniformName + ".cutoff", light.getCutoff());
    }
    public void setUniform(String uniformName, PointLight[] lights) {
        int numLights = lights != null ? lights.length : 0;
        for(int i = 0; i < numLights; i++) {
            setUniform(uniformName, lights[i], i);
        }
    }
    public void setUniform(String uniformName, PointLight light, int pos) {
        setUniform(uniformName + "[" + pos + "]", light);
    }
    public void setUniform(String uniformName, SpotLight[] lights) {
        int numLights = lights != null ? lights.length : 0;
        for(int i = 0; i < numLights; i++) {
            setUniform(uniformName, lights[i], i);
        }
    }
    public void setUniform(String uniformName, SpotLight light, int pos) {
        setUniform(uniformName + "[" + pos + "]", light);
    }

    // create vertex and fragment shaders
    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }
    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    // take the shader code as a string, compile, and attach it to the shader program
    public int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderID = GL20.glCreateShader(shaderType);
        if(shaderID == 0)
            throw new Exception("Unable to create shader. Type: " + shaderType);

        GL20.glShaderSource(shaderID, shaderCode);
        GL20.glCompileShader(shaderID);

        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0)
            throw new Exception("Unable to compile shader. Info: " + GL20.glGetShaderInfoLog(shaderID, 1024));

        GL20.glAttachShader(programID, shaderID);

        return shaderID;
    }

    public void link() throws Exception {
        // link the shader program to connect the inputs and outputs of the sub-shaders (Vertex and Fragment) 
        GL20.glLinkProgram(programID);
        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0)
            throw new Exception("Unable to link shader. Info: " + GL20.glGetProgramInfoLog(programID, 1024));

        if(vertexShaderID != 0)
            GL20.glDetachShader(programID, vertexShaderID);
        if(fragmentShaderID != 0)
            GL20.glDetachShader(programID, fragmentShaderID);

        GL20.glValidateProgram(programID);
        if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0)
            throw new Exception("Unable to validate shader. Info: " + GL20.glGetProgramInfoLog(programID, 1024));
    }

    public void bind() {
        GL20.glUseProgram(programID);
    }
    public void unbind() {
        GL20.glUseProgram(0);
    }
    public void cleanup() {
        unbind();
        if(programID != 0)
            GL20.glDeleteProgram(programID);
    }

    public void init() throws Exception {
        // load sub-shaders from resource
        createVertexShader(Utils.loadResource(vertexPath));
        createFragmentShader(Utils.loadResource(fragmentPath));
    }
}
