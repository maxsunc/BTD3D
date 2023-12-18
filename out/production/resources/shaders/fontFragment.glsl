#version 400 core

in vec2 fragTextureCoord;

out vec4 fragColor;

uniform vec3 color;
uniform sampler2D fontAtlas;

void main(void){
    fragColor = vec4(color,texture(fontAtlas, fragTextureCoord).a);
}