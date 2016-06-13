#include <windows.h>   
#include <stdio.h>     
#include <string>     
#include <gl/glut.h>   
#include "Renderer.h"   


void main(int argc, char* argv[])
{
	
	int width = 500;
	int height = 500;

	int xPos = 50;
	int yPos = 50;

	
	glutInit(&argc, argv);

	glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH);

	glutInitWindowSize(width, height);

	glutInitWindowPosition(xPos, yPos);

	glutCreateWindow("Collision Detection");

	glutDisplayFunc(Render);
	
	glutReshapeFunc(Resize);

	glutKeyboardFunc(Keyboard);

	glutSpecialFunc(KeyboardSpecial);

	glutMouseFunc(Mouse);

	glutMotionFunc(MouseMotion);

	glutIdleFunc(Idle);

	InitializeRenderer();

	glutMainLoop();
}  