package programs;

//  -------------   JOGL EllipseMitGedrehtenHalbachsen-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

import ch.fhnw.util.math.Mat4;

public class Kepler implements WindowListener, GLEventListener, KeyListener {

	// --------- globale Daten ---------------------------

	String windowTitle = "JOGL-Application";
	int windowWidth = 800;
	int windowHeight = 600;
	String vShader = MyShaders.vShader1; // Vertex-Shader
	String fShader = MyShaders.fShader0; // Fragment-Shader
	int maxVerts = 2048; // max. Anzahl Vertices im Vertex-Array
	GLCanvas canvas; // OpenGL Window
	MyGLBase1 mygl; // eigene OpenGL-Basisfunktionen
	MyGLBase1 vb; // für viereck
	Mat4 M = Mat4.ID; // Transformationsmatrix
	Mat4 P = Mat4.ID; // Proj. Matrix
	// float drehwinkel = 30;
	// Koordinaten für P
	double ybottom, ytop;
	float xleft = -60, xright = 60; // ViewingVol
	float znear = -100, zfar = 100;

	//final double g = 9.81; // Erdbeschleunigung
	final double m = 1; // Masse
	double v0x = 8; // Anfangsgeschwindigkeit
	double v0y = 10; // Anfangsgeschwindigkeit
	// Zum zurücksetzen
	double x0 = -8;
	double y0 = 0;
	// ---------------
	double x = x0;
	double y = y0;
	double vx = v0x;
	double vy = v0y;
	double ax = 0;
	double ay = -g;
	double dt = 0.01; // Zeitschritt
	boolean stopped = false;
	//für 
	static double g = 9.81e-6; // Erdbeschl. [E/s^2]
	static double rE = 6.378;  // Erdradius [e]
	static double GM = g*rE*rE;// G*M
	double h = 20; 			   // Höhe Satellit	
	
	// für viereck
	float a = (float) 0.4;
	float b = (float) 0.8;
	float c = (float) 0.0;

	// --------- Methoden ----------------------------------

	public Kepler() // Konstruktor
	{
		createFrame();
	}

	void createFrame() // Fenster erzeugen
	{
		Frame f = new Frame(windowTitle);
		f.setSize(windowWidth, windowHeight);
		f.addWindowListener(this);
		GLProfile glp = GLProfile.get(GLProfile.GL3);
		GLCapabilities glCaps = new GLCapabilities(glp);
		canvas = new GLCanvas(glCaps);
		canvas.addGLEventListener(this);
		f.add(canvas);
		f.setVisible(true);
		f.addKeyListener(this);
		canvas.addKeyListener(this);
	};


	public void zeichneKreis(GL3 gl, float r, float xm, float ym, int nPkte) {
		double phi = 2 * Math.PI / nPkte;
		double x, y;

		mygl.rewindBuffer(gl);
		mygl.putVertex(xm, ym, 0);

		for (int i = 0; i <= nPkte; i++) {
			x = xm + r * Math.cos(i * phi);
			y = ym + r * Math.sin(i * phi);
			mygl.putVertex((float) x, (float) y, 0);
		}

		mygl.copyBuffer(gl);
		mygl.drawArrays(gl, GL3.GL_TRIANGLE_FAN);
	}
	/*
	 * public void zeichneViereck(GL3 gl, float a, float b, float c, float d){
	 * vb.rewindBuffer(gl); vb.putVertex(a, b,0);
	 * 
	 * 
	 * 
	 * vb.copyBuffer(gl); vb.drawArrays(gl, GL3.GL_TRIANGLES);
	 * 
	 * }
	 */

	// ---------- OpenGL-Events ---------------------------

	@Override
	public void init(GLAutoDrawable drawable) // Initialisierung
	{
		GL3 gl = drawable.getGL().getGL3();
		System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
		System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
		System.out.println();
		gl.glClearColor(0, 0, 1, 1); // Hintergrundfarbe
		int programId = MyShaders.initShaders(gl, vShader, fShader); // Compile/Link
																		// Shader-Programme
		mygl = new MyGLBase1(gl, programId, maxVerts); // OpenGL
														// Basis-Funktionen
		FPSAnimator anim = new FPSAnimator(canvas, 200, true);
		anim.start();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT); // Bildschirm loeschen
		mygl.setColor(0, 1, 0); // Farbe der Vertices
		
		double alpha = 0;
		//Quader viereck = new Quader(mygl);
		//viereck.zeichne(gl, a, b, c, true);

		M=Mat4.ID;
		mygl.setM(gl, M);
		// soll Erde sein
		zeichneKreis(gl, 0.2f, 0, 0, 20);
		
		M= Mat4.translate((float)x,(float)y,0);
		alpha = (Math.atan(vy/vx)) * (180/Math.PI);
		M = M.postMultiply(Mat4.rotate((float)alpha, 0,0,1));
		mygl.setM(gl,M);
		zeichneSpeer(gl, 1.2f, 0.04f, 0.2f);
		
		// eulerischer Algorythmus 2D
		if (stopped)
			return;
		
		x = x + vx * dt;
		y = y + vy * dt;
		vx = vx + ax * dt;
		vy = vy + ay * dt;
		if (y < ybottom) {
			x = x0;
			y = y0;
			vx = v0x;
			vy = v0y;
		}
		
		
		
		// y = y + v*dt;
		// v = v + -g*dt;
	}

	public void zeichneDreieck(GL3 gl, float x1, float y1, float x2, float y2, float x3, float y3) {
		mygl.rewindBuffer(gl); // Vertex-Buffer zuruecksetzen
		mygl.putVertex(x1, y1, 0); // Eckpunkte in VertexArray speichern
		mygl.putVertex(x2, y2, 0);
		mygl.putVertex(x3, y3, 0);
		mygl.copyBuffer(gl);
		mygl.drawArrays(gl, GL3.GL_TRIANGLES);
	}

	public void zeichneSpeer(GL3 gl, float a, float b, float c) {
		mygl.rewindBuffer(gl);
		// Viereck zeichnen
		mygl.putVertex(-a, -b, 0);
		mygl.putVertex(a, -b, 0);
		mygl.putVertex(a, b, 0);
		mygl.putVertex(-a, b, 0);

		mygl.copyBuffer(gl);
		mygl.drawArrays(gl, GL3.GL_TRIANGLE_FAN);
		zeichneDreieck(gl, a, -b, a + c, 0, a, b);
		zeichneDreieck(gl, -a, b,-(a + c), 0, -a, -b);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL3 gl = drawable.getGL().getGL3();
		// Set the viewport to be the entire window
		gl.glViewport(0, 0, width, height);
		double aspect = (double) height / width;
		ybottom = aspect * xleft;
		ytop = aspect * xright;
		P = Mat4.ortho(xleft, xright, (float) ybottom, (float) ytop, znear, zfar);
		mygl.setP(gl, P);

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	} // not needed

	// ----------- main-Methode ---------------------------

	public static void main(String[] args) {
		new Kepler();
	}

	// --------- Window-Events --------------------

	public void windowClosing(WindowEvent e) {
		System.out.println("closing window");
		System.exit(0);
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_UP:
			v0y += 0.5;
			break;
		case KeyEvent.VK_DOWN:
			v0y -= 0.5;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	// es braucht in diesem Fall nur diese Methode
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		char code = e.getKeyChar();
		if (code == 's') {
			stopped = true;
		}

	}

}
