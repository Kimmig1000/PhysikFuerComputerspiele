package programs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

import ch.fhnw.util.math.Mat4;
import programs.MyGLBase1;
import programs.MyShaders;

public class SchussAufBlech implements WindowListener, GLEventListener, KeyListener {

	// --------- globale Daten ---------------------------

	String windowTitle = "JOGL-Application";
	int windowWidth = 800;
	int windowHeight = 600;
	String vShader = MyShaders.vShader1; // Vertex-Shader
	String fShader = MyShaders.fShader0; // Fragment-Shader
	int maxVerts = 2048; // max. Anzahl Vertices im Vertex-Array
	GLCanvas canvas; // OpenGL Window
	MyGLBase1 mygl; // eigene OpenGL-Basisfunktionen
	MyGLBase1 vb; // f�r viereck
	Mat4 M = Mat4.ID; // Transformationsmatrix
	Mat4 P = Mat4.ID; // Proj. Matrix
	// float drehwinkel = 30;
	// Koordinaten f�r P
	double ybottom, ytop;
	float xleft = -10, xright = 10; // ViewingVol
	float znear = -100, zfar = 100;

	JFrame parent = new JFrame();
	// Circle values

	final double g = 9.81; // Erdbeschleunigung
	final double m = 1; // Masse
	double v0x = 8; // Anfangsgeschwindigkeit
	double v0y = 10; // Anfangsgeschwindigkeit
	// Zum zur�cksetzen
	double x0 = -8;
	double y0 = -4;
	// ---------------
	double x = x0;
	double y = y0;
	double vx = v0x;
	double vy = v0y;
	double ax = 0;
	double ay = -g;
	double dt = 0.01; // Zeitschritt
	boolean stopped = false;
	

	// Rectangle

	// for the case when y reaches ybotton, we need to reset these values
	final double startPositionXForReset = 8;
	final double startPositionYForReset = 5;

	// we will use these values for calculating
	double startPositionX = startPositionXForReset;
	double startPositionY = startPositionYForReset;

	double sizeRectangleLength = 1;
	double sizeRectangleHeight = 2;

	// Velocity, with which the rectangle moves to the x Koordinate (decreases y
	// Koordinate)
	final double velocity = -9.3;

	// only 20 chances
	int counter = 0;

	// Luftwiderstand
	boolean luftwiderstand;
	double c = 0.025;
	
	
	// --------- Methoden ----------------------------------

	public SchussAufBlech() // Konstruktor
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
	}

	// Objekte

	public void zeichneRechteck(GL3 gl, float x1, float y1, float width, float height) {

		mygl.rewindBuffer(gl); // Vertex-Buffer zuruecksetzen
		// 1. Paramter x-Koordinate; 2. Paramter y-Koordinate; 3. Paramter
		// z-Koordinate ->
		// hier gleich 0, da nur 2Dimensional
		mygl.putVertex(x1, y1, 0);
		mygl.putVertex(x1 + width, y1, 0);
		mygl.putVertex(x1, y1 + height, 0);
		mygl.putVertex(x1 + width, y1 + height, 0);

		mygl.copyBuffer(gl);
		mygl.drawArrays(gl, GL3.GL_TRIANGLE_STRIP);
	}

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

	}

	@Override
	public void display(GLAutoDrawable drawable) {

		GL3 gl = drawable.getGL().getGL3();
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT); // Bildschirm loeschen
		mygl.setColor(0, 1, 0); // Farbe der Vertices		

		M = Mat4.ID;
		mygl.setM(gl, M);
		
		
		zeichneKreis(gl, 0.2f, (float) x, (float) y, 20);

		zeichneRechteck(gl, (float) startPositionX, (float) startPositionY, (float) sizeRectangleLength,
				(float) sizeRectangleHeight);

		M = Mat4.translate((float) x, (float) y, 0);
		
		// M = M.postMultiply(Mat4.rotate((float) alpha, 0, 0, 1));
		// mygl.setM(gl, M);
		// zeichneSpeer(gl, 1.2f, 0.04f, 0.2f);

		if (stopped) {

			// eulerischer Algorythmus 2D

			x = x + vx * dt;
			y = y + vy * dt;
			vx = vx + ax * dt;
			vy = vy + ay * dt;
			
			
			if (y < ybottom || x > xright) {
				counter++;
				stopped = false;
				x = x0;
				y = y0;
				vx = v0x;
				vy = v0y;
				startPositionY = startPositionYForReset;
			}

			// y = y + v*dt;
			// v = v + -g*dt;
			startPositionY = startPositionY + velocity * dt;

			// falls startPosition Y in negativen Bereich kommt:
			if (startPositionY < ybottom) {
				startPositionY = startPositionYForReset;

			}

			if (counter > 3) {
				JDialog.setDefaultLookAndFeelDecorated(true);
				int response = JOptionPane.showConfirmDialog(null, "3 Versuche gehabt. Neu starten?", "Confirm",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.NO_OPTION) {
					System.out.println("No button clicked");
					System.exit(1);
				} else if (response == JOptionPane.YES_OPTION) {
					System.out.println("Yes button clicked");
					counter = 0;
				} else if (response == JOptionPane.CLOSED_OPTION) {
					System.exit(1);
				}
			}

			// falls Luftwiderstand eingestellt

			if (luftwiderstand == true) {

				// eulerischer Algorythmus 2D
				
				
				x = x +  -c * Math.sqrt(Math.pow(vx, 2)+Math.pow(vy, 2))  * dt;
				y = y + -c * Math.sqrt(Math.pow(vx, 2)+Math.pow(vy, 2))  * dt;
				
				vx = vx + ax * dt;
				vy = vy + ay * dt;
				
				
				
				if (y < ybottom || x > xright) {
					counter++;
					stopped = false;
					x = x0;
					y = y0;
					vx = v0x;
					vy = v0y;
					startPositionY = startPositionYForReset;
				}

				// y = y + v*dt;
				// v = v + -g*dt;
				startPositionY = startPositionY + velocity * dt;

				// falls startPosition Y in negativen Bereich kommt:
				if (startPositionY < ybottom) {
					startPositionY = startPositionYForReset;

				}

				if (counter > 3) {
					JDialog.setDefaultLookAndFeelDecorated(true);
					int response = JOptionPane.showConfirmDialog(null, "3 Versuche gehabt. Neu starten?", "Confirm",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (response == JOptionPane.NO_OPTION) {
						System.out.println("No button clicked");
						System.exit(1);
					} else if (response == JOptionPane.YES_OPTION) {
						System.out.println("Yes button clicked");
						counter = 0;
					} else if (response == JOptionPane.CLOSED_OPTION) {
						System.exit(1);
					}
				}

			}
		}

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
		new SchussAufBlech();
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
			v0x += 0.5;
			
			vx += 0.5;
			vy += 0.5;
			break;
		case KeyEvent.VK_DOWN:
			v0y -= 0.5;
			v0x -= 0.5;
			
			vy -= 0.5;
			vx -= 0.5;			
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
		if (code == 'l') {
			luftwiderstand = true;
		}

		

	}

}
