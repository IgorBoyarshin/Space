#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec4 normal;

out vec4 theColor;

uniform mat4 perspectiveMatrix;

uniform mat4 cameraMatrixNoPos;

void main()
{
    gl_Position = perspectiveMatrix * (cameraMatrixNoPos * position);

    theColor = vec4(1.0, 1.0, 1.0, 1.0);
}