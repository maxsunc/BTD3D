#version 400 core

in vec2 position;
in vec2 textureCoord;

out vec2 fragTextureCoord;

uniform vec2 translation;

void main(void){
    gl_Position = vec4(position + translation * vec2(2.0, -2.0), 0.0, 1.0);
    fragTextureCoord = textureCoord;
}