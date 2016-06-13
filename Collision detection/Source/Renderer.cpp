#include <windows.h>        
#include <gl/glut.h>    
#include <math.h>	   
#include <stdlib.h>
#include <ctime>
#include <vector>
#include <iostream>
#include "Material.h"
#include "Renderer.h"  

using namespace std;

float cam_pos_x = 220.0f;
float cam_pos_y = 50.0f;
float cam_pos_z = 160.0f;
float cam_look_x = 1.5f;
float cam_look_y = 0.3f;
float cam_look_z = -1.0f;

float world_translate_x = 0.0f;
float world_translate_y = 0.0f;
float world_translate_z = 0.0f;

float world_rotate_z = 0.0f;

int rendering_mode = GL_FILL;

int mouse_button = -1;
int prev_x = 0;
int prev_y = 0;

GLfloat blackColor[] = { 0.0f, 0.0f, 0.0f, 1.0f }; 
const float cubedimension=100.0f;                  //cube side dimension
const float halfdimension=cubedimension/2;         //used later in calcualtions
int maxradious=5;							//max ball radious
int minradious=5;							//min ball radious
const int numberofballstobespawned=5;			//number of balls spawned on every R
const float maxspeedofballs=0.5f;               //maximum ball velocity
const float backstepanalogy=0.01f;
float distancefromcentertocenter;
int sumofradiouses;
float penetrationdepth;

int ballsexist=0, flag2;
int NumberOfBalls=0;							//maximum balls
int stopcreatinglast5balls=0;				
unsigned int i=0;
unsigned int previousi;

int frameCount=0;						//variables that have to do with CalculateFPS()
float currentTime;
float previousTime=0;
float fps;
float timertoprint=0;

int flagtoshowonlyonce=0;				//flag for the initial message about the quadtree, changes in initialmessage
int state=-1;							//state -1 means the quadtree is used, state 1 it is not

class MyBalls;
inline void initialmessage(void);
inline void calculateFPS(void);
inline void moveballs(void);
inline void CreateBalls(void);
inline void DestroyBalls(void);
inline int samequadrant(int k, int n);
void wallcollisions(void);
void balltoballcollisions(void);
void balldeflections(int n, int k);
inline void chooseballmaterial(int j);
void quadtree(void);
void testforcollisions(void);
void Balls(void);

vector<MyBalls> newBalls;
Material materialGlass, materialRed, materialGold, materialGreen, materialOrange, materialGrey;					

class MyBalls{
	int radious;
	float x,y,z;
	float speedx, speedy, speedz;
	int space[4];
public:
	void setspace(int i, int yesorno){space[i]=yesorno;}   
	int getspace(int i){return space[i];}				   
	void setradious(int iradious){radious=iradious;}
	int getradious(void){return radious;}
	void setballattributes(void);			
	void setxpos(float ix){x=ix;}    
	void setypos(float iy){y=iy;}
	void setzpos(float iz){z=iz;}
	float getxpos(){return x;}		 
	float getypos(){return y;}
	float getzpos(){return z;}
	void setspeedx(float ix){speedx=ix;}	
	void setspeedy(float iy){speedy=iy;}
	void setspeedz(float iz){speedz=iz;}
	float getspeedx(){return speedx;}		
	float getspeedy(){return speedy;}
	float getspeedz(){return speedz;}
};

void MyBalls::setballattributes(void){

	int collisiondetected=0;
	register int loopcounter=0; 
	do{
			collisiondetected=0;
			if(minradious==maxradious)
			{
				setradious(minradious);
			}
			else
			{
				setradious(minradious + rand()/(RAND_MAX/(maxradious-minradious)));
			}
			setxpos((float)rand()/((float)RAND_MAX/cubedimension)-halfdimension);			//produces from -halfdimension to halfdimension, ie the boundraries of the cube centered on (0, 0, 0)
			setypos((float)rand()/((float)RAND_MAX/cubedimension)-halfdimension);
			setzpos((float)rand()/((float)RAND_MAX/cubedimension)-halfdimension);
			if(abs(getxpos())+getradious()>=halfdimension)
			{
				collisiondetected=1;    //check for left-right walls
			}
			if(abs(getypos())+getradious()>=halfdimension)
			{
				collisiondetected=1;   //check for up-down walls
			}
			if(abs(getzpos())+getradious()>=halfdimension)
			{
				collisiondetected=1;    //check for front-back walls
			}
			if(ballsexist!=0 && collisiondetected!=1)
			{
				for(int k=0; k<newBalls.size()-flag2-1; k++)   //with flag2 here it is checked that the new balls will collide with the old ones AND with the ones spawned before the one with the recent R press. -1 id because we count from 0
				{				
					if(sqrt(pow(getxpos()-newBalls[k].getxpos(),2)+pow(getypos()-newBalls[k].getypos(),2)+pow(getzpos()-newBalls[k].getzpos(),2))<getradious()+newBalls[k].getradious())
					{
						collisiondetected=1;    //check for other balls
						break;
					}
					
				}
			}
			loopcounter++;
			if(loopcounter>700)		//if in the last 700 loops it can't find a place to put the ball, it doesn't put any of the last 5
			{
				cout<<"LAST 5 BALLS WON'T SPAWN DUE TO LACK OF SPACE\n";
				stopcreatinglast5balls=1;    //used in createballs in order to stop producing the rest of the 5 balls and to take the previous i
				break;
			}
	}while(collisiondetected!=0);

	do{
		setspeedx((float)rand()/((float)RAND_MAX/(2*maxspeedofballs))-maxspeedofballs);      
		setspeedy((float)rand()/((float)RAND_MAX/(2*maxspeedofballs))-maxspeedofballs);
		setspeedz((float)rand()/((float)RAND_MAX/(2*maxspeedofballs))-maxspeedofballs);
	}while(getspeedx()==0 || getspeedy()==0 || getspeedz()==0);
}

inline void CreateBalls(void){
	
	NumberOfBalls=NumberOfBalls+numberofballstobespawned; 
	newBalls.resize(NumberOfBalls);
	flag2=numberofballstobespawned-1;
	previousi=i;							//we need to know which is the previous in order to recover it in case the loop in setballattributes reaches 700
	for(i; i<newBalls.size(); i++)			//for example the loop may be stack in the second ball we put and we must know which was the previous i
	{		
		if(stopcreatinglast5balls==1)
		{
			DestroyBalls();				   
			stopcreatinglast5balls=0;
			break;
		}
		newBalls[i].setballattributes();
		flag2--;
	}
	ballsexist=1;
}

inline void DestroyBalls(void)
{
	if(NumberOfBalls>0)
	{
		newBalls.resize(NumberOfBalls-numberofballstobespawned);
		NumberOfBalls=NumberOfBalls-numberofballstobespawned;
		if(stopcreatinglast5balls==1)					//if it tries to put balls and it can't find a position in setballattributes stop trying to put the next of the 5 balls
		{
			i=previousi;				
		}
		else
		{
			i=i-numberofballstobespawned;
		}
		if(NumberOfBalls==0) ballsexist=0;
	}
}

inline void initialmessage(void)
{
	if(flagtoshowonlyonce==0)
	{
		if(state==-1)
		{
			cout<<"With quadtree"<<"\n";
			flagtoshowonlyonce=1;
		}
		else
		{
			cout<<"Without quadtree"<<"\n";
			flagtoshowonlyonce=1;
		}
	}
}

void InitializeRenderer(void)   
{ 

	srand((unsigned)time(0));
	
	glShadeModel(GL_SMOOTH);	

	glEnable(GL_DEPTH_TEST);

	glDepthFunc(GL_LEQUAL);

	glClearDepth(1.0f);

	glClearColor(0.2f, 0.2f, 0.25f, 1.0f);

	GLfloat ambientLight[] = {0.2f, 0.2f, 0.2f, 1.0f};

	GLfloat diffuseLightColor[] = {1.0f, 1.0f, 1.0f, 1.0f};

	GLfloat lightPos[] = {0.0f, 0.0f, 150.0f, 1.0};

	glEnable(GL_LIGHTING);

	glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLightColor);
	glLightfv(GL_LIGHT0, GL_POSITION, lightPos);
	
	glEnable(GL_LIGHT0);  
	
	glEnable (GL_BLEND);                                   
	glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	materialGlass.setAmbient(0.6f, 0.8f, 1.0f, 0.4f);  //more turquoise 0.1f, 0.1872f, 0.1745f, 0.4f
	materialGlass.setDiffuse(0.396f, 0.74151f, 0.69102f, 0.4f);
	materialGlass.setSpecular(0.297254f, 0.30829f, 0.306678f, 0.4f);
	materialGlass.setShininess(0.1f);
	materialGlass.setEmission(0.396f, 0.74151f, 0.69102f);

	materialRed.setAmbient(0.2f, 0.2f, 0.2f);
	materialRed.setDiffuse(0.8f, 0.0f, 0.0f);
	materialRed.setSpecular(blackColor[0], blackColor[1], blackColor[2]);
	materialRed.setShininess(0.0f);
	materialRed.setEmission(blackColor[0], blackColor[1], blackColor[2]);

	materialGreen.setAmbient(0.0f, 0.0f, 0.0f);
	materialGreen.setDiffuse(0.1f, 0.35f, 0.1f);
	materialGreen.setSpecular(0.45f, 0.55f, 0.45f);
	materialGreen.setShininess(0.25f);
	materialGreen.setEmission(blackColor[0], blackColor[1], blackColor[2]);

	materialOrange.setAmbient(0.19125f, 0.0735f, 0.0225f);
	materialOrange.setDiffuse(0.7038f, 0.27048f, 0.0828f);
	materialOrange.setSpecular(0.256777f, 0.137622f, 0.086014f);
	materialOrange.setShininess(0.1f);
	materialOrange.setEmission(blackColor[0], blackColor[1], blackColor[2]);

	materialGrey.setAmbient(0.0f, 0.0f, 0.0f);
	materialGrey.setDiffuse(0.01f, 0.01f, 0.01f);
	materialGrey.setSpecular(0.5, 0.5, 0.5);
	materialGrey.setShininess(0.25f);
	materialGrey.setEmission(blackColor[0], blackColor[1], blackColor[2]);

	materialGold.setAmbient(0.24725f, 0.1995f, 0.0745f);
	materialGold.setDiffuse(0.75164f, 0.60648f, 0.22648f);
	materialGold.setSpecular(0.628281f, 0.555802f, 0.366065f);
	materialGold.setShininess(51.2f);
	materialGold.setEmission(blackColor[0], blackColor[1], blackColor[2]);
	
	initialmessage();
}

inline void calculateFPS(void)
{
    frameCount++;
    currentTime = glutGet(GLUT_ELAPSED_TIME);
    int timeInterval = currentTime - previousTime;

    if(timeInterval > 1000)
    {
        fps = frameCount / (timeInterval / 1000.0f);
        previousTime = currentTime;
        frameCount = 0;
    }
}

void Render(void)
{
	
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);  

	glPolygonMode(GL_FRONT_AND_BACK, rendering_mode);

	glMatrixMode(GL_MODELVIEW);

	glLoadIdentity();

	gluLookAt(cam_pos_x, cam_pos_y, cam_pos_z, cam_look_x, cam_look_y, cam_look_z, 0.0f, 1.0f, 0.0f);

	glTranslatef(world_translate_x, world_translate_y, world_translate_z);
	glRotatef(world_rotate_z, 0, 0, 1);

	Balls();
	
	calculateFPS();
	if(glutGet(GLUT_ELAPSED_TIME)-timertoprint>1200)
	{
		cout<<"fps: "<<fps<<"   Number of balls "<<NumberOfBalls<<"\n";            
		timertoprint=glutGet(GLUT_ELAPSED_TIME);
	}
	
	glutSwapBuffers();             
}

inline void moveballs(void)
{

	if(ballsexist==1){
		for(int j=0; j<newBalls.size(); j++){
			newBalls[j].setxpos(newBalls[j].getxpos()+newBalls[j].getspeedx());
			newBalls[j].setypos(newBalls[j].getypos()+newBalls[j].getspeedy());
			newBalls[j].setzpos(newBalls[j].getzpos()+newBalls[j].getspeedz());
		}
	}

}

void quadtree(void)
{
	int flagforspace1, flagforspace2, flagforspace3, flagforspace4;

	for(int h=0; h<newBalls.size(); h++)
	{
		flagforspace1=0;
		flagforspace2=0;
		flagforspace3=0;
		flagforspace4=0;
		if(newBalls[h].getxpos()>0 && newBalls[h].getzpos()<0)
		{
			flagforspace1=1;
				if(abs(newBalls[h].getxpos())<=newBalls[h].getradious())
				{
					flagforspace2=1;
				}
				if(abs(newBalls[h].getzpos())<=newBalls[h].getradious())
				{
					flagforspace4=1;
				}
				if(abs(newBalls[h].getxpos())<=newBalls[h].getradious() && abs(newBalls[h].getzpos())<=newBalls[h].getradious())
				{
					flagforspace3=1;
				}				
		}
		else if(newBalls[h].getxpos()<0 && newBalls[h].getzpos()<0)
		{
			flagforspace2=1;
				if(abs(newBalls[h].getxpos())<=newBalls[h].getradious())
				{
					flagforspace1=1;
				}
				if(abs(newBalls[h].getzpos())<=newBalls[h].getradious())
				{
					flagforspace3=1;
				}
				if(abs(newBalls[h].getxpos())<=newBalls[h].getradious() && abs(newBalls[h].getzpos())<=newBalls[h].getradious())
				{
					flagforspace4=1;
				}
		}
		else if(newBalls[h].getxpos()<0 && newBalls[h].getzpos()>0)
		{
			flagforspace3=1;
				if(abs(newBalls[h].getxpos())<=newBalls[h].getradious())
				{
					flagforspace4=1;
				}
				if(abs(newBalls[h].getzpos())<=newBalls[h].getradious())
				{
					flagforspace2=1;
				}
				if(abs(newBalls[h].getxpos())<=newBalls[h].getradious() && abs(newBalls[h].getzpos())<=newBalls[h].getradious())
				{
					flagforspace1=1;
				}
		}
		else if(newBalls[h].getxpos()>0 && newBalls[h].getzpos()>0)
		{
			flagforspace4=1;
				if(abs(newBalls[h].getxpos())<=newBalls[h].getradious())
				{
					flagforspace3=1;
				}
				if(abs(newBalls[h].getzpos())<=newBalls[h].getradious())
				{
					flagforspace1=1;
				}
				if(abs(newBalls[h].getxpos())<=newBalls[h].getradious() && abs(newBalls[h].getzpos())<=newBalls[h].getradious())
				{
					flagforspace2=1;
				}
		}
		else if(newBalls[h].getxpos()==0 && newBalls[h].getzpos()==0)
		{
			flagforspace1=1;
			flagforspace2=1;
			flagforspace3=1;
			flagforspace4=1;
		}

		if(flagforspace1==1) 
		{
			newBalls[h].setspace(0,1);
		}
		else
		{
			newBalls[h].setspace(0,0);
		}
		if(flagforspace2==1) 
		{
			newBalls[h].setspace(1,1);
		}
		else
		{
			newBalls[h].setspace(1,0);
		}
		if(flagforspace3==1) 
		{
			newBalls[h].setspace(2,1);
		}
		else
		{
			newBalls[h].setspace(2,0);
		}	
		if(flagforspace4==1) 
		{
			newBalls[h].setspace(3,1);
		}
		else
		{
			newBalls[h].setspace(3,0);
		}

	}

}

inline int samequadrant(int k, int n)
{
	for(int c=0;c<4;c++)
	{
		if(newBalls[k].getspace(c)==1 && newBalls[n].getspace(c)==1) return 1;
	}
	return 0;
}

void wallcollisions(void)
{
	int flag;                            //shows similtaneous collision of a sphere with the wall and another sphere, in order not to change again the direction of the velocity and the loop becomes infinite
	for(int k=0;k<newBalls.size();k++){
		if(abs(newBalls[k].getxpos())+newBalls[k].getradious()>halfdimension)
		{
			flag=0;
			if(newBalls[k].getxpos()-newBalls[k].getradious() < -halfdimension)
			{
				if(newBalls[k].getspeedx()>0)
				{
					newBalls[k].setspeedx(-newBalls[k].getspeedx());
					flag=1;
				}	
			}
			else if(newBalls[k].getxpos()+newBalls[k].getradious() > halfdimension)
			{
				if(newBalls[k].getspeedx()<0)
				{
					newBalls[k].setspeedx(-newBalls[k].getspeedx());
					flag=1;
				}	
			}
			while(newBalls[k].getxpos()-newBalls[k].getradious() < -halfdimension || newBalls[k].getxpos()+newBalls[k].getradious() > halfdimension)
			{
				newBalls[k].setxpos(newBalls[k].getxpos()-backstepanalogy*newBalls[k].getspeedx());
				newBalls[k].setypos(newBalls[k].getypos()-backstepanalogy*newBalls[k].getspeedy());
				newBalls[k].setzpos(newBalls[k].getzpos()-backstepanalogy*newBalls[k].getspeedz());
			}
			if(flag==0) newBalls[k].setspeedx(-newBalls[k].getspeedx());
		}
		if(abs(newBalls[k].getypos())+newBalls[k].getradious()>halfdimension)
		{
			flag=0;
			if(newBalls[k].getypos()-newBalls[k].getradious() < -halfdimension)
			{
				if(newBalls[k].getspeedy()>0)
				{
					newBalls[k].setspeedy(-newBalls[k].getspeedy());
					flag=1;
				}	
			}
			else if(newBalls[k].getypos()+newBalls[k].getradious() > halfdimension)
			{
				if(newBalls[k].getspeedy()<0)
				{
					newBalls[k].setspeedy(-newBalls[k].getspeedy());
					flag=1;
				}	
			}
			while(newBalls[k].getypos()-newBalls[k].getradious() < -halfdimension || newBalls[k].getypos()+newBalls[k].getradious() > halfdimension)
			{
				newBalls[k].setxpos(newBalls[k].getxpos()-backstepanalogy*newBalls[k].getspeedx());
				newBalls[k].setypos(newBalls[k].getypos()-backstepanalogy*newBalls[k].getspeedy());
				newBalls[k].setzpos(newBalls[k].getzpos()-backstepanalogy*newBalls[k].getspeedz());
			}
			if(flag==0) newBalls[k].setspeedy(-newBalls[k].getspeedy());
		}
		if(abs(newBalls[k].getzpos())+newBalls[k].getradious()>halfdimension)
		{
			flag=0;
			if(newBalls[k].getzpos()-newBalls[k].getradious() < -halfdimension)
			{
				if(newBalls[k].getspeedz()>0)
				{
					newBalls[k].setspeedz(-newBalls[k].getspeedz());
					flag=1;
				}	
			}
			else if(newBalls[k].getzpos()+newBalls[k].getradious() > halfdimension)
			{
				if(newBalls[k].getspeedz()<0)
				{
					newBalls[k].setspeedz(-newBalls[k].getspeedz());
					flag=1;
				}	
			}
			while(newBalls[k].getzpos()-newBalls[k].getradious() < -halfdimension || newBalls[k].getzpos()+newBalls[k].getradious() > halfdimension)
			{
				newBalls[k].setxpos(newBalls[k].getxpos()-backstepanalogy*newBalls[k].getspeedx());
				newBalls[k].setypos(newBalls[k].getypos()-backstepanalogy*newBalls[k].getspeedy());
				newBalls[k].setzpos(newBalls[k].getzpos()-backstepanalogy*newBalls[k].getspeedz());
			}
			if(flag==0) newBalls[k].setspeedz(-newBalls[k].getspeedz());
		}
	}				
}

void balltoballcollisions(void)
{
		for(int k=0; k<newBalls.size(); k++)
		{
			for(int n=k+1; n<newBalls.size(); n++)
				{	
					if(state==-1)
					{
						if(samequadrant(k, n))
						{
							distancefromcentertocenter = sqrt(pow(newBalls[k].getxpos() - newBalls[n].getxpos(),2) + pow(newBalls[k].getypos() - newBalls[n].getypos(),2) +  pow(newBalls[k].getzpos() - newBalls[n].getzpos(),2));
							sumofradiouses = newBalls[k].getradious()+newBalls[n].getradious();

							if(distancefromcentertocenter < sumofradiouses) 
							{
								penetrationdepth = sumofradiouses - distancefromcentertocenter;
								while(penetrationdepth>0)
								{
									newBalls[n].setxpos(newBalls[n].getxpos()-backstepanalogy*newBalls[n].getspeedx());
									newBalls[n].setypos(newBalls[n].getypos()-backstepanalogy*newBalls[n].getspeedy());
									newBalls[n].setzpos(newBalls[n].getzpos()-backstepanalogy*newBalls[n].getspeedz());
															
									newBalls[k].setxpos(newBalls[k].getxpos()-backstepanalogy*newBalls[k].getspeedx());
									newBalls[k].setypos(newBalls[k].getypos()-backstepanalogy*newBalls[k].getspeedy());
									newBalls[k].setzpos(newBalls[k].getzpos()-backstepanalogy*newBalls[k].getspeedz());	
									distancefromcentertocenter = sqrt(pow(newBalls[k].getxpos() - newBalls[n].getxpos(),2) + pow(newBalls[k].getypos() - newBalls[n].getypos(),2) +  pow(newBalls[k].getzpos() - newBalls[n].getzpos(),2));
									penetrationdepth=sumofradiouses-distancefromcentertocenter;
								}
								balldeflections(n,k);
							}
						}
					}
					else
					{
						distancefromcentertocenter = sqrt(pow(newBalls[k].getxpos() - newBalls[n].getxpos(),2) + pow(newBalls[k].getypos() - newBalls[n].getypos(),2) +  pow(newBalls[k].getzpos() - newBalls[n].getzpos(),2));
							sumofradiouses = newBalls[k].getradious()+newBalls[n].getradious();

							if(distancefromcentertocenter < sumofradiouses) 
							{
								penetrationdepth = sumofradiouses - distancefromcentertocenter;
								while(penetrationdepth>0)
								{
									newBalls[n].setxpos(newBalls[n].getxpos()-backstepanalogy*newBalls[n].getspeedx());
									newBalls[n].setypos(newBalls[n].getypos()-backstepanalogy*newBalls[n].getspeedy());
									newBalls[n].setzpos(newBalls[n].getzpos()-backstepanalogy*newBalls[n].getspeedz());
															
									newBalls[k].setxpos(newBalls[k].getxpos()-backstepanalogy*newBalls[k].getspeedx());
									newBalls[k].setypos(newBalls[k].getypos()-backstepanalogy*newBalls[k].getspeedy());
									newBalls[k].setzpos(newBalls[k].getzpos()-backstepanalogy*newBalls[k].getspeedz());	
									distancefromcentertocenter = sqrt(pow(newBalls[k].getxpos() - newBalls[n].getxpos(),2) + pow(newBalls[k].getypos() - newBalls[n].getypos(),2) +  pow(newBalls[k].getzpos() - newBalls[n].getzpos(),2));
									penetrationdepth=sumofradiouses-distancefromcentertocenter;
								}
								balldeflections(n,k);
							}
					}							
			}
		}
}

void balldeflections(int n, int k)
{
	if(newBalls[n].getspeedx()*newBalls[k].getspeedx()<=0)
	{
		newBalls[n].setspeedx(-newBalls[n].getspeedx());
		newBalls[k].setspeedx(-newBalls[k].getspeedx());
	}
	else
	{
		if(newBalls[n].getspeedx()>newBalls[k].getspeedx())
		{
			newBalls[n].setspeedx(-newBalls[n].getspeedx());
		}
		else
		{
			newBalls[k].setspeedx(-newBalls[k].getspeedx());
		}
	}

	if(newBalls[n].getspeedy()*newBalls[k].getspeedy()<=0)
	{
		newBalls[n].setspeedy(-newBalls[n].getspeedy());
		newBalls[k].setspeedy(-newBalls[k].getspeedy());
	}
	else
	{
		if(newBalls[n].getspeedy()>newBalls[k].getspeedy())
		{
			newBalls[n].setspeedy(-newBalls[n].getspeedy());
		}
		else
		{
			newBalls[k].setspeedy(-newBalls[k].getspeedy());
		}
	}

	if(newBalls[n].getspeedz()*newBalls[k].getspeedz()<=0)
	{
		newBalls[n].setspeedz(-newBalls[n].getspeedz());
		newBalls[k].setspeedz(-newBalls[k].getspeedz());
	}
	else
	{
		if(newBalls[n].getspeedz()>newBalls[k].getspeedz())
		{
			newBalls[n].setspeedz(-newBalls[n].getspeedz());
		}
		else
		{
			newBalls[k].setspeedz(-newBalls[k].getspeedz());
		}
	}
}

void testforcollisions(void)
{
		balltoballcollisions();
		wallcollisions();
}

inline void chooseballmaterial(int j)
{
	int sumtochoosequadrant=0;
	for(int t=0;t<4;t++)
			{
				if(newBalls[j].getspace(t)==1) sumtochoosequadrant++;
			}
			if(sumtochoosequadrant>1)
			{
				materialRed.apply(GL_FRONT_AND_BACK);
			}
			else
			{
				if(newBalls[j].getspace(0)==1)
				{
					materialGold.apply(GL_FRONT_AND_BACK);
				}
				if(newBalls[j].getspace(1)==1)
				{
					materialGreen.apply(GL_FRONT_AND_BACK);
				}
				if(newBalls[j].getspace(2)==1)
				{
					materialOrange.apply(GL_FRONT_AND_BACK);
				}
				if(newBalls[j].getspace(3)==1)
				{
					materialGrey.apply(GL_FRONT_AND_BACK);
				}
			}
}

void Balls(void)
{	
	moveballs();
	if(state==-1) quadtree();				//because we want to know where collitions take place
	testforcollisions();
	if(state==-1) quadtree();				//because some balls changed corrdinates and we can see it on the screen 

	if(ballsexist==1){                                      //do if balls exist, changes in createballs
		for(int j=0; j<newBalls.size(); j++)		   
		{
			glPushMatrix();
			if(state==-1)
			{
				chooseballmaterial(j);
			}
			else
			{
				materialRed.apply(GL_FRONT_AND_BACK);
			}
			glTranslatef(newBalls[j].getxpos(), newBalls[j].getypos(), newBalls[j].getzpos());
			glutSolidSphere(newBalls[j].getradious(), 20, 20);
			glPopMatrix();								
		}
	}


	glPushMatrix();
	materialGlass.apply(GL_FRONT_AND_BACK);
	glBegin(GL_QUADS); 	
	glVertex3f(-halfdimension, -halfdimension, halfdimension); 
	glVertex3f(-halfdimension, halfdimension, halfdimension); 
	glVertex3f(halfdimension, halfdimension, halfdimension); 
	glVertex3f(halfdimension, -halfdimension, halfdimension); 
	
	glVertex3f(-halfdimension, -halfdimension, -halfdimension); 
	glVertex3f(-halfdimension, halfdimension, -halfdimension); 
	glVertex3f(halfdimension, halfdimension, -halfdimension);
	glVertex3f(halfdimension, -halfdimension, -halfdimension); 
	
	glVertex3f(-halfdimension, halfdimension, halfdimension); 
	glVertex3f(-halfdimension, halfdimension, -halfdimension); 
	glVertex3f(-halfdimension, -halfdimension, -halfdimension); 
	glVertex3f(-halfdimension, -halfdimension, halfdimension); 

	glVertex3f(halfdimension, halfdimension, halfdimension); 
	glVertex3f(halfdimension, halfdimension, -halfdimension); 
	glVertex3f(halfdimension, -halfdimension, -halfdimension); 
	glVertex3f(halfdimension, -halfdimension, halfdimension); 
	 
	glVertex3f(-halfdimension, halfdimension, halfdimension); 
	glVertex3f(halfdimension, halfdimension, halfdimension); 
	glVertex3f(halfdimension, halfdimension, -halfdimension);
	glVertex3f(-halfdimension, halfdimension, -halfdimension);

	glVertex3f(-halfdimension, -halfdimension, halfdimension); 
	glVertex3f(halfdimension, -halfdimension, halfdimension); 
	glVertex3f(halfdimension, -halfdimension, -halfdimension);
	glVertex3f(-halfdimension, -halfdimension, -halfdimension); 
	glEnd();
	glPopMatrix();
	
}

/////////////////////////////////////////////////////////

void Keyboard(unsigned char key, int x, int y)
{
	OutputDebugStringA("Entered Keyboard\n");
	switch(key)
	{
	case 'R':
	case 'r':
		CreateBalls();
		break;
	case 'E':
	case 'e':
		DestroyBalls();
		break;
	case 'F':
	case 'f':
		if(ballsexist){
			if(state==-1)
			{
				state=1;
				cout<<"Without quadtree"<<"\n";
			}
			else
			{
				state=-1;
				cout<<"With quadtree"<<"\n";
			}
		}
		break;
	case 'K':
	case 'k':
		cam_pos_x = 0.0f;
		cam_pos_y = cubedimension+100;
		cam_pos_z = -1.0f; //an einai 0 den tha xei klish kai den tha fainetai
		cam_look_x = 0.0f;
		cam_look_y = -1.0f;
		cam_look_z = 0.0f;
		break;
	case 'L':
	case 'l':
		cam_pos_x = 0.0f;
		cam_pos_y = 0.0f;
		cam_pos_z = cubedimension+120;
		cam_look_x = 0.0f;
		cam_look_y = 0.0f;	
		cam_look_z = -1.0f;
		break;
	case '1':
		if (rendering_mode == GL_POINT) rendering_mode = GL_LINE;
		else if (rendering_mode == GL_LINE) rendering_mode = GL_FILL;
		else if (rendering_mode == GL_FILL) rendering_mode = GL_POINT;
		break;
	case 'W':
	case 'w':
		cam_pos_z -= 1.0f;
		break;
	case 'S':
	case 's':
		cam_pos_z += 1.0f;
		break;
	case 'A':
	case 'a':
		cam_pos_x -= 1.0f;
		break;
	case 'D':
	case 'd':
		cam_pos_x += 1.0f;
		break;
	case 'Z':
	case 'z':
		world_rotate_z += 1.0f;
		break;
	case 'X':
	case 'x':
		world_rotate_z -= 1.0f;
		break;
	case 27: // escape
		exit(0);
		break;
	default:
		return;
	}
	// since we have changed some of our parameters, redraw!
	glutPostRedisplay();
}

void Idle(void)
{
	glutPostRedisplay();
}

void KeyboardSpecial(int key, int x, int y)
{
	switch(key)
	{
	case GLUT_KEY_PAGE_UP:
		world_translate_y++;
		break;
	case GLUT_KEY_PAGE_DOWN:
		world_translate_y--;
		break;
	case GLUT_KEY_UP:
		world_translate_z--;
		break;
	case GLUT_KEY_DOWN:
		world_translate_z++;
		break;
	case GLUT_KEY_LEFT:
		world_translate_x--;
		break;
	case GLUT_KEY_RIGHT:
		world_translate_x++;
		break;
	default:
		return;
	}
	// since we have changed some of our parameters, redraw!
	glutPostRedisplay();
}

void Mouse(int button, int state, int x, int y)
{
	OutputDebugStringA("Entered Mouse\n");
	if (state == GLUT_DOWN && button == GLUT_LEFT_BUTTON)
	{
		prev_x = x;
		prev_y = y;
	}
	mouse_button = button;
}

void MouseMotion(int x, int y)
{
	OutputDebugStringA("Entered Mouse Motion\n");
	switch (mouse_button)
	{
	case GLUT_LEFT_BUTTON:
		cam_look_x -= (x - prev_x) * 0.05f;
		cam_look_y += (y - prev_y) * 0.05f;

		prev_x = x;
		prev_y = y;
		glutPostRedisplay();
		break;
	default:
		break;
	}
}

void Resize(int width, int height)
{ 
	// Hack to void zero height
	if (height == 0) height = 1;

	// Set up the viewport
	glViewport(0, 0, width, height); 

	// Set up a projection transformation
	glMatrixMode(GL_PROJECTION); 
	glLoadIdentity();
	// Set up the projection matrix
	gluPerspective(45, width/(float)height, 1.0, 300);
}
