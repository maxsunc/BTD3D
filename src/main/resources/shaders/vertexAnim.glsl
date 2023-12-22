#version 440 core

const int MAX_WEIGHTS = 3;
const int MAX_JOINTS = 150;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoord;
layout (location = 2) in vec3 normal;
layout (location = 3) in ivec3 jointIndices;
layout (location = 4) in vec3 jointWeights;

out vec3 lightVec;
out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec4 fragPosLightSpace;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 lightProjection;
uniform mat3 m3x3InvTrans;
uniform mat4 lightSpaceMatrix;
uniform mat4 jointsMatrix[MAX_JOINTS];

const float density = 0.01;
const float gradient = 10;

void main() {

    vec4 worldPos = transformationMatrix * vec4(position, 1.0);
    vec4 modelViewPosition = viewMatrix * worldPos;

    vec4 normalPos =  vec4(normal, 1.0);
    lightVec = vec3(400, 400, 200);
    fragNormal = normalize(m3x3InvTrans * normalPos.xyz);

    // Apply bone transformations
    vec4 finalPosition = vec4(0.0);
    for (int i = 0; i < MAX_WEIGHTS; i++) {
        int jointIndex = jointIndices[i];
        float weight = jointWeights[i];
        mat4 jointMatrix = jointsMatrix[jointIndex];
        finalPosition += jointMatrix * vec4(position, 1.0) * weight;
    }
    fragPos = finalPosition.xyz;

    fragPosLightSpace = lightSpaceMatrix * vec4(fragPos, 1.0);
    fragTextureCoord = textureCoord;
    gl_Position = projectionMatrix * modelViewPosition;

    float distance = length(modelViewPosition.xyz);
    visibility = exp(-pow((distance*density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}