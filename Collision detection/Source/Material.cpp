//----------------------------------------------------//
//                                                    //
// File: Material.cpp                                 //
// Material class. Provides easier handling of        //
// OpenGL materials.								  //
//                                                    //
//----------------------------------------------------//

#include "Material.h"

// Windows API and system calls
#include <windows.h>   
// The OpenGL API 
#include <gl/gl.h>

// Constructor
Material::Material(void)
{
	reset();
}

// Destructor
Material::~Material(void)
{

}

// reset to OpenGL default values
void Material::reset(void)
{
	// GL_AMBIENT The initial ambient reflectance is (0.2, 0.2, 0.2, 1.0).
	// GL_DIFFUSE The initial diffuse reflectance is (0.8, 0.8, 0.8, 1.0).
	// GL_SPECULAR The initial specular reflectance is (0.0, 0.0, 0.0, 1.0).
	// GL_EMISSION The initial emission intensity is (0.0, 0.0, 0.0, 1.0).
	// GL_SHININESS Only values in the range [0, 128] are accepted. The initial specular exponent is 0.

	m_ambient[0] = 0.2f;
	m_ambient[1] = 0.2f;
	m_ambient[2] = 0.2f;
	m_ambient[3] = 1.0f;

	m_diffuse[0] = 0.8f;
	m_diffuse[1] = 0.8f;
	m_diffuse[2] = 0.8f;
	m_diffuse[3] = 1.0f;

	m_specular[0] = 0.0f;
	m_specular[1] = 0.0f;
	m_specular[2] = 0.0f;
	m_specular[3] = 1.0f;

	m_shininess[0] = 0.0f;

	m_emission[0] = 0.0f;
	m_emission[1] = 0.0f;
	m_emission[2] = 0.0f;
	m_emission[3] = 1.0f;
}

void Material::setAmbient(float r, float g, float b, float a)
{
	m_ambient[0] = r;
	m_ambient[1] = g;
	m_ambient[2] = b;
	m_ambient[3] = a;
}

void Material::setDiffuse(float r, float g, float b, float a)
{
	m_diffuse[0] = r;
	m_diffuse[1] = g;
	m_diffuse[2] = b;
	m_diffuse[3] = a;
}

void Material::setSpecular(float r, float g, float b, float a)
{
	m_specular[0] = r;
	m_specular[1] = g;
	m_specular[2] = b;
	m_specular[3] = a;
}

void Material::setShininess(float s)
{
	m_shininess[0] = s;
}

void Material::setEmission(float r, float g, float b, float a)
{
	m_emission[0] = r;
	m_emission[1] = g;
	m_emission[2] = b;
	m_emission[3] = a;
}

void Material::apply(int face)
{
	glMaterialfv(face, GL_AMBIENT, m_ambient);
	glMaterialfv(face, GL_DIFFUSE, m_diffuse);
	glMaterialfv(face, GL_SPECULAR, m_specular);
	glMaterialfv(face, GL_SHININESS, m_shininess);
	glMaterialfv(face, GL_EMISSION, m_emission);
}