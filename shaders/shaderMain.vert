#version 330

layout(location = 0) in vec4 position;

smooth out vec4 theColor;

uniform mat4 camera;
uniform mat4 perspectiveMatrix;
uniform mat4 objectMatrix;

uniform vec3 color;

void main()
{
    vec4 cameraPos = camera * objectMatrix * position;
    gl_Position = perspectiveMatrix * cameraPos;

    theColor = vec4(color.x, color.y, color.z, 1.0);
}