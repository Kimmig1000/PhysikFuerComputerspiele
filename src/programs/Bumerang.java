package programs;

//  -------------   JOGL EllipseMitGedrehtenHalbachsen-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

public class Bumerang implements WindowListener, GLEventListener, KeyListener {

	// --------- globale Daten ---------------------------

	String windowTitle = "JOGL-Application";
	int windowWidth = 800;
	int windowHeight = 600;
	String vShader = MyShaders.vShader1; // Vertex-Shader
	String fShader = MyShaders.fShader0; // Fragment-Shader
	int maxVerts = 2048; // max. Anzahl Vertices im Vertex-Array
	GLCanvas canvas; // OpenGL Window
	MyGLBase1 mygl; // eigene OpenGL-Basisfunktionen
	
	Mat4 M = Mat4.ID; // Transformationsmatrix
	Mat4 P = Mat4.ID; // Proj. Matrix
	// float drehwinkel = 30;
	// Koordinaten für P
	double ybottom, ytop;
	float xleft = -60, xright = 60; // ViewingVol
	float znear = -100, zfar = 100;

	// für Erde
	static double g = 9.81e-6; // Erdbeschl. [E/s^2]
	static double rE = 6.378; // Erdradius [e]
	static double GM = g * rE * rE;// G*M
	double h = 20; // Höhe Satellit
	float phi = 1;

	double r0 = 7;
	
	boolean rotate = false;
	int radiusBumerang = 1;

	// LookAt-Parameter fuer Kamera-System
	Vec3 A = new Vec3(0, 0, 50); // Kamera-Pos. (Auge)
	Vec3 B = new Vec3(0, 0, 0); // Zielpunkt
	Vec3 up = new Vec3(0, 1, 0); // up-Richtung

	float elevation = 10;
	float azimut = 40;

	// --------- Methoden ----------------------------------

	public Bumerang() // Konstruktor
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
		// rotk = new RotKoerper(mygl);
		gl.glEnable(GL3.GL_DEPTH_TEST);
	}

	@Override
	public void display(GLAutoDrawable drawable) {

		GL3 gl = drawable.getGL().getGL3();
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT); // Bildschirm
																		// loeschen

		Mat4 mSave = mygl.getM();

		Mat4 R1 = Mat4.rotate(-elevation, 1, 0, 0);
		Mat4 R2 = Mat4.rotate(azimut, 0, 1, 0);
		Mat4 R = R1.postMultiply(R2);

		M = Mat4.lookAt(R.transform(A), B, R.transform(up));

		mygl.setM(gl, M); // Blickrichtung

		mygl.drawAxis(gl, 50, 50, 50);

		Mat4 M = Mat4.ID;

		// rotation für kreisbewegung
		M = M.postMultiply(Mat4.rotate((float) phi, 0, 1, 0));
		//M = M.postMultiply(Mat4.rotate((float) phi, 0, 1, 0));
		//M = M.postMultiply(Mat4.rotate((float) phi, 0, 1, 0));
		
		// versetzen um Radius der Kreisbahn
		
		for (int i = 0; i < radiusBumerang; i++){
		M = M.postMultiply(Mat4.translate((float) r0, 0, 0));
		}
		
		// rotation, damit der Bumerang nicht genau zum Bahnmittelpunkt schaut
		M = M.postMultiply(Mat4.rotate((float) 90, 0, 1, 0));
				
		// Senkrecht stellen des Bumerang
		M = M.postMultiply(Mat4.rotate((float) 90, 0, 1, 0));
		
		
		// Drehung der waagrechten Umlaufbahn
		M = M.preMultiply(Mat4.rotate((float) -20, 0, 0, 1));

		mygl.setM(gl, M);

		mygl.setColor(1, 0, 0);

		zeichneKreis(gl, (float) (0.2 * rE), 0, 0, 60);

		mygl.setM(gl, mSave);

		phi += 3.1;
		phi = phi % 360;

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
		new Bumerang();
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
			elevation++;
			break;
		case KeyEvent.VK_DOWN:
			elevation--;
			break;
		case KeyEvent.VK_LEFT:
			azimut--;
			break;
		case KeyEvent.VK_RIGHT:
			azimut++;
			;
			break;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		char code = e.getKeyChar();
		if (code == 'r') {
			rotate = true;
		}
		if (code == 'a') {
			radiusBumerang++;
		}
		if (code == 'd') {
			radiusBumerang--;
		}
		

	}

}
