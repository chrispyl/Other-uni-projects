//----------------------------------------------------//
//                                                    //
// File: Material.h                                   //
// Material class. Provides easier handling of        //
// OpenGL materials.								  //
//                                                    //
//                                                    //
//----------------------------------------------------//

#pragma once

class Material
{
private:
	// private variable declarations
	float								m_ambient[4];
	float								m_diffuse[4];
	float								m_specular[4];
	float								m_shininess[1];
	float								m_emission[4];

public:
	// Constructor
	Material(void);

	// Destructor
	~Material(void);

	// public function declarations
	void								setAmbient(float r, float g, float b, float a = 1.0f);
	void								setDiffuse(float r, float g, float b, float a = 1.0f);
	void								setSpecular(float r, float g, float b, float a = 1.0f);
	void								setShininess(float s);
	void								setEmission(float r, float g, float b, float a = 1.0f);
	void								reset(); // reset to OpenGL default values
	void								apply(int face); // apply the material
};