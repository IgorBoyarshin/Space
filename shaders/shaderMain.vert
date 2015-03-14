#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec3 normal;

smooth out vec4 theColor;

uniform mat4 camera;
uniform mat4 perspectiveMatrix;
uniform mat4 objectMatrix;

uniform vec3 color;

void main()
{
    vec4 obj = objectMatrix * position;
    vec4 cameraPos = camera * obj;
    gl_Position = perspectiveMatrix * cameraPos;

    float cosNormal = clamp( dot(normal, vec3(-obj.x, -obj.y, -obj.z)), 0, 1);

    theColor = vec4(color * cosNormal, 1.0);
}