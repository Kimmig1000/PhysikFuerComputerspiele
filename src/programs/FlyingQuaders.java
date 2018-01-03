package programs;

//  -------------   JOGL 3D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

import com.jogamp.opengl.*;
import ch.fhnw.util.math.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;

public class FlyingQuaders implements WindowListener, GLEventListener, KeyListener {

	// --------- globale Daten ---------------------------

	String windowTitle = "JOGL-Application";
	int windowWidth = 800;
	int windowHeight = 600;
	String vShader = MyShaders.vShader1; // Vertex-Shader mit
											// Transformations-Matrizen
	String fShader = MyShaders.fShader0; // Fragment-Shader
	int maxVerts = 2048; // max. Anzahl Vertices im Vertex-Array
	GLCanvas canvas; // OpenGL Window
	MyGLBase1 mygl; // OpenGL Basis-Funktionen

	float elevation = 10;
	float azimut = 40;

	Quader quad;
	RotKoerper rotk;
	Quader quad2;
	
	Stack<Mat4> matrixStack = new Stack<>();

	Mat4 M; // ModelView-Matrix
	Mat4 P; // Projektions-Matrix

	// -------- Viewing-Volume ---------------
	float left = -4f, right = 4f;
	float bottom, top;
	float near = -10, far = 1000;

	double x = left, dx = 0.05; // Quaderposition

	double quaderLength = 2, quaderWidth = 2, quaderHeight = 2;
	GyroDynamics gyro;

	double t = 0, dt = 0.1; // SLERP Parameter

	// LookAt-Parameter fuer Kamera-System
	Vec3 A = new Vec3(0, 0, 4); // Kamera-Pos. (Auge)
	Vec3 B = new Vec3(0, 0, 0); // Zielpunkt
	Vec3 up = new Vec3(0, 1, 0); // up-Richtung

	// --------- Methoden ----------------------------------

	public FlyingQuaders() // Konstruktor
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

	// ---------- OpenGL-Events ---------------------------

	@Override
	public void init(GLAutoDrawable drawable) // Initialisierung
	{
		GL3 gl = drawable.getGL().getGL3();
		System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
		System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
		System.out.println();
		gl.glEnable(GL3.GL_DEPTH_TEST);
		gl.glClearColor(0, 0, 1, 1);
		int programId = MyShaders.initShaders(gl, vShader, fShader);
		mygl = new MyGLBase1(gl, programId, maxVerts);
		quad = new Quader(mygl);
		quad2 = new Quader(mygl);
		
		rotk = new RotKoerper(mygl);
		
		double paramA = (quaderLength * quaderLength + quaderWidth * quaderWidth) / 12;
		double paramB = (quaderLength * quaderLength + quaderHeight * quaderHeight) / 12;
		double paramC = (quaderWidth * quaderWidth + quaderHeight * quaderHeight) / 12;
		gyro = new GyroDynamics(paramA, paramB, paramC);
		gyro.setState(1, 2, 4, 30, 3, 1, 2);
		
		FPSAnimator anim = new FPSAnimator(canvas, 200, true);
		anim.start();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
		M = Mat4.ID;
		Mat4 R1 = Mat4.rotate(-elevation, 1, 0, 0);
		Mat4 R2 = Mat4.rotate(azimut, 0, 1, 0);
		Mat4 R = R1.postMultiply(R2);

		mygl.setM(gl, Mat4.lookAt(R.transform(A), B, R.transform(up))); // Blickrichtung
		matrixStack.push(M);
		mygl.setColor(1, 0, 0);
		mygl.setShadingLevel(gl, 0);
		mygl.drawAxis(gl, 2, 2, 2); // Koordinatenachsen
		mygl.setShadingParam(gl, 0.2f, 0.8f);
		mygl.setShadingLevel(gl, 1);
		
	
		M = M.postMultiply(Mat4.translate((float) x, 0, 0));
		matrixStack.push(M);
		
		
		gyro.move(0.1);
		double[] states = gyro.getState();
		M = matrixStack.pop();
		M = M.postMultiply(Mat4.rotate((float) states[3], new Vec3(states[4], states[5], states[6])));
		mygl.setM(gl, M);
		quad.zeichne(gl, (float) quaderLength, (float) quaderWidth, (float) quaderHeight, true);
		M = matrixStack.pop();
		mygl.setM(gl, M);
		
		matrixStack.push(M);
		
		gyro.move(0.4);
		M = M.postMultiply(Mat4.rotate((float) states[3], new Vec3(states[4], states[5], states[6])));
		mygl.setM(gl, M);
		quad2.zeichne(gl, (float) quaderLength+1, (float) quaderWidth+1, (float) quaderHeight+1, true);
		mygl.setM(gl, M);
		M = matrixStack.pop();
		mygl.setM(gl, M);
		mygl.setColor(1, 0, 0);

		//zeichneLinie(gl, new Vec3(states[0], states[1], states[2]));
		
		x += dx; // B

	}

	public void zeichneLinie(GL3 gl, Vec3 vec) {
		// System.out.println(vec.x + ";" + vec.y + ";" + vec.z);
		mygl.rewindBuffer(gl);
		mygl.putVertex(0, 0, 0); // Startpunkt -> muss mit Matrizen verschoben
									// werden
		mygl.putVertex(vec.x, vec.y, vec.z);
		mygl.copyBuffer(gl);
		mygl.drawArrays(gl, GL3.GL_LINE_STRIP);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL3 gl = drawable.getGL().getGL3();
		// Set the viewport to be the entire window
		gl.glViewport(0, 0, width, height);
		float aspect = (float) height / width;
		bottom = aspect * left;
		top = aspect * right;
		mygl.setP(gl, Mat4.ortho(left, right, bottom, top, near, far));
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	} // not needed

	// ----------- main-Methode ---------------------------

	public static void main(String[] args) {
		new FlyingQuaders();
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
		if ((e.getKeyChar() == 'H') || (e.getKeyChar() == 'h')) {
			gyro.move(0.1);
		}
	}

}