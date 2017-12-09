package programs;

//-------------   JOGL 3D-Programm  -------------------

//*********** Klotoide Kurve ***************/

import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import ch.fhnw.util.math.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;
import java.util.*;

public class Vectorfield implements WindowListener, GLEventListener, KeyListener {

	
	String windowTitle = "JOGL-Application";
	int windowWidth = 800;
	int windowHeight = 600;
	String vShader = MyShaders.vShader1; 											
	String fShader = MyShaders.fShader0; 
	int maxVerts = 2 * 10048; 
	GLCanvas canvas; 
	MyGLBase1 mygl; 

	float elevation = 0;
	float azimut = 0;

	Mat4 M; 
	Mat4 P; 

	
	float radius = 15.0f;

	LorenzDynamics lorenzdynamics;

	// Viewing Volume
	float left = -60, right = 60;
	float bottom, top;
	float near = -10, far = 1000;

	// Kamera-System
	Vec3 A = new Vec3(0, 0, 100); 
	Vec3 B = new Vec3(0, 0, 0); 
	Vec3 up = new Vec3(0, 1, 0); 
	// --------------
	
	class LorenzDynamics extends Dynamics {

		float radius;

		public LorenzDynamics(float radius) {
			this.radius = radius;
		}

		public double[] f(double[] x) {
			double x1 = x[0], x2 = x[1];
			double[] y = { f1(x1, x2, radius), f2(x1, x2, radius), 0 };

			return y;
		}

		private double f1(double x1, double x2, double radius) {
			return 1 + (radius * radius) / (x1 * x1 + x2 * x2)
					- (2 * radius * radius * x1 * x1) / Math.pow((x1 * x1 + x2 * x2), 2);
		}

		private double f2(double x1, double x2, double radius) {
			return (-1) * (2 * radius * radius * x1 * x2) / Math.pow((x1 * x1 + x2 * x2), 2);
		}

		
		public void zeichneBahn(MyGLBase1 mygl, GL3 gl, double xStart, double yStart, double zStart, double dt,
				double nSchritte) {

			double[] x = { xStart, yStart, zStart };
			mygl.rewindBuffer(gl);

			for (int i = 0; i < 50; i++) {
				x = runge(x, dt);
			}
			for (int i = 0; i < nSchritte; i++) {
				x = runge(x, dt);

				mygl.putVertex((float) x[0], (float) x[1], (float) x[2]);

			}

			mygl.copyBuffer(gl);
			mygl.drawArrays(gl, GL3.GL_LINE_STRIP);
		}
	}


	// --------- Methoden ----------------------------------

	public Vectorfield() // Konstruktor
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
		mygl.rewindBuffer(gl); // Vertex-Buffer zuruecksetzen
		mygl.putVertex(xm, ym, 0); // Kreismittelpunkt in VertexArray speichern
		for (int i = 0; i < nPkte + 1; i++) {
			x = xm + r * Math.cos(i * phi);
			y = ym + r * Math.sin(i * phi);
			mygl.putVertex((float) x, (float) y, 0);
		}

		mygl.copyBuffer(gl);
		mygl.drawArrays(gl, GL3.GL_TRIANGLE_FAN);
	}

	// ------- Klothoide (Cornu'sche Spirale) ------------
	public void zeichneBahn(GL3 gl, double xStart, double yStart, double zStart, double phi, double ds,
			double kruemmung, double dKruemmung, int nPunkte) {
		double x = xStart, y = yStart, z = zStart;
		mygl.rewindBuffer(gl);
		mygl.putVertex((float) x, (float) y, (float) z);
		for (int i = 1; i < nPunkte; i++) {
			x += Math.cos(phi) * ds;
			y += Math.sin(phi) * ds;
			mygl.putVertex((float) x, (float) y, (float) z);
			phi += kruemmung * ds;
			kruemmung += dKruemmung * ds;
		}
		mygl.copyBuffer(gl);
		mygl.drawArrays(gl, GL3.GL_LINE_STRIP);
	}

	// ---------- OpenGL-Events ---------------------------

	@Override
	public void init(GLAutoDrawable drawable) // Initialisierung
	{
		GL3 gl = drawable.getGL().getGL3();
		System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
		System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
		System.out.println();
		gl.glEnable(GL3.GL_DEPTH_TEST);
		gl.glClearColor(0.2f, 0.2f, 1.0f, 1);
		int programId = MyShaders.initShaders(gl, vShader, fShader);
		mygl = new MyGLBase1(gl, programId, maxVerts);
		FPSAnimator anim = new FPSAnimator(canvas, 200, true); // Animations-Thread,
																// 200
																// Frames/sek
		anim.start();
		lorenzdynamics = new LorenzDynamics(radius);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
		M = Mat4.ID;
		mygl.setM(gl, M);
		Mat4 R1 = Mat4.rotate(-elevation, 1, 0, 0);
		Mat4 R2 = Mat4.rotate(azimut, 0, 1, 0);
		Mat4 R = R1.preMultiply(R2);
		M = Mat4.lookAt(R.transform(A), B, R.transform(up));
		mygl.setM(gl, M);
		mygl.setColor(1, 1, 1);
		
		mygl.setColor(0, 1, 1);

		for (int i = -50; i < 50; i += 3) {
			lorenzdynamics.zeichneBahn(mygl, gl, -80, i + 2.5f, 10, 0.01f, 18000);
		}

		mygl.setColor(0.2f, 0.2f, 0.2f);
		zeichneKreis(gl, radius, 0.0f, 0.0f, 50);

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL3 gl = drawable.getGL().getGL3();
		gl.glViewport(0, 0, width, height);
		float aspect = (float) height / width;
		bottom = aspect * left;
		top = aspect * right;
		mygl.setP(gl, Mat4.ortho(left, right, bottom, top, near, far));
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	} 

	public static void main(String[] args) {
		new Vectorfield();
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

	public void keyPressed(KeyEvent e) {
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
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

}