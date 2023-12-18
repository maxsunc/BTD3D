#version 410 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoord;
layout (location = 2) in vec3 normal;

out vec3 lightVec;
out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec4 fragPosLightSpace;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 lightProjection;
uniform mat3 m3x3InvTrans;
uniform mat4 lightSpaceMatrix;

void main() {
    vec4 worldPos = transformationMatrix * vec4(position, 1.0);
    vec4 normalPos =  vec4(normal, 1.0);
    lightVec = vec3(200, 200, 100);
    fragNormal = normalize(m3x3InvTrans * normalPos.xyz);
    fragPos = worldPos.xyz;
    fragPosLightSpace = lightSpaceMatrix * vec4(fragPos, 1.0);
    fragTextureCoord = textureCoord;
    gl_Position = projectionMatrix * viewMatrix * worldPos;
}