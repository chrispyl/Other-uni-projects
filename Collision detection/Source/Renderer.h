//----------------------------------------------------//
//                                                    //
// File: Renderer.h                                   //
// Handles the entry point of the application,  	  //
// creates the window and initializes the GLContext   //
//                                                    //
// Lab 2: Transformations (world, eye, clip)		  //
// Projections (parallel, perspective), Primitives,   // 
// Alpha testing, Depth testing, Face Culling		  //
//                                                    //
//----------------------------------------------------//


// This function is called before GLUT goes into its main loop.
void InitializeRenderer(void);

// This function draws everything in the OpenGL window
void Render(void);

// When a resize event occurs, this function is called
void Resize(int width, int height);

// This function handles the presses of "character keys"
void Keyboard(unsigned char key, int x, int y);

// This function handles special keys, like arrow or function keys
void KeyboardSpecial(int key, int x, int y);

// This function handles mouse events
void Mouse(int button, int state, int x, int y);

// This function handles mouse motion
void MouseMotion(int x, int y);

// This function is called when no events are received
void Idle();