package programs;

//  -------------   JOGL EllipseMitGedrehtenHalbachsen-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

import ch.fhnw.util.math.Mat4;

public class LissajousUebung1 implements WindowListener, GLEventListener, KeyListener {

	// --------- globale Daten ---------------------------

	String windowTitle = "Lissjous-Figur-Uebung1";
	int windowWidth = 800;
	int windowHeight = 800; // changed to square to not deshape the circle
	String vShader = MyShaders.vShader1; // Vertex-Shader
	String fShader = MyShaders.fShader0; // Fragment-Shader
	int maxVerts = 2048; // max. Anzahl Vertices im Vertex-Array
	GLCanvas canvas; // OpenGL Window
	MyGLBase1 mygl; // eigene OpenGL-Basisfunktionen
	Mat4 M = Mat4.ID; // Transformationsmatrix
	// Mat4 P = Mat4.ID; // Proj. Matrix
	
	// variables for Lissajous
	// Amplituden
	double a1 = 5;
	double a2 = 4;

	// Kreisfrequenzen
	double w1 = 1;
	double w2 = w1;

	// Phasenverschiebung
	double phi = 0;
	
	 // Keys Activation
	static boolean start = false;

	// --------- Methoden ----------------------------------

	public LissajousUebung1() // Konstruktor
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
	};


	public void zeichneLissajous(GL3 gl, double a1, double a2, double w1, double w2){
	
		
		mygl.rewindBuffer(gl);
		
		int points = 100;
		double dt = (2 * Math.PI) / (w1 * points);
		for (int i = 0; i < points + 1; i++){
			float x = (float) calculateX(i, a1, w1, dt);
			float y = (float) calculateY(i, a2, w2, dt, phi);
			mygl.putVertex(x, y, 0f);
		}	
		phi += 0.1;
		
		
		mygl.copyBuffer(gl);
		mygl.drawArrays(gl, GL3.GL_LINE_LOOP);
	}

	private double calculateX(int k, double a, double w, double t) {
		return a * Math.cos(w * k * t);
	}

	private double calculateY(int k, double a, double w, double t, double phi) {
		return a * Math.sin(w * k * t - phi);
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
		FPSAnimator anim = new FPSAnimator(canvas, 12, true);
		anim.start();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		
		GL3 gl = drawable.getGL().getGL3();
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT); // Bildschirm loeschen
		mygl.setColor(0, 1, 0); // Farbe der Vertices
		float a1 = 1f;
		
		zeichneLissajous(gl, w1, a1, w2, a2);
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL3 gl = drawable.getGL().getGL3();
		// Set the viewport to be the entire window
		gl.glViewport(0, 0, width, height);

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	} // not needed

	// ----------- main-Methode ---------------------------

	public static void main(String[] args) {
		
		new LissajousUebung1();
		
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
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_UP:
			
			
			break;
		case KeyEvent.VK_DOWN:
			
			break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		char code = e.getKeyChar();
		if (code == 's') {
			start = true;
		}

	}

}
