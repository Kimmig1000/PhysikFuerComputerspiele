package programs;

import ch.fhnw.util.math.Vec3;

public class GyroDynamics extends Dynamics {
	double I1, I2, I3; // Traegheitsmomente
	double[] x;

	public GyroDynamics(double I1, double I2, double I3) {
		this.I1 = I1;
		this.I2 = I2;
		this.I3 = I3;
	}

	public GyroDynamics(ModifyableQuader quader) {
		this(quader.getM(), quader.getA(), quader.getB(), quader.getC());
	}

	public GyroDynamics(float m, float a, float b, float c) {
		I1 = 1.0f / 12.0f * m * (b * b + c * c);
		I2 = 1.0f / 12.0f * m * (a * a + c * c);
		I3 = 1.0f / 12.0f * m * (a * a + b * b);
		// see script p.73
	}

	public void initState(double w1, double w2, double w3, double phi, double x, double y, double z) {
		double q0 = Math.cos(0.5 * phi * Math.PI / 180);
		Vec3 n = new Vec3(x, y, z);
		n = n.normalize();
		double s = Math.sin(0.5 * phi * Math.PI / 180);
		this.x = new double[] { w1, w2, w3, q0, s * n.x, s * n.y, s * n.z };
	}

	public double[] f(double[] x) {
		double w1 = x[0], w2 = x[1], w3 = x[2];
		double q0 = x[3], q1 = x[4], q2 = x[5], q3 = x[6];
		double[] y = { (I2 - I3) / I1 * w2 * w3, (I3 - I1) / I2 * w3 * w1, (I1 - I2) / I3 * w1 * w2,
				-0.5 * (q1 * w1 + q2 * w2 + q3 * w3), 0.5 * (q0 * w1 + q2 * w3 - q3 * w2),
				0.5 * (q0 * w2 + q3 * w1 - q1 * w3), 0.5 * (q0 * w3 + q1 * w2 - q2 * w1) };
		return y;
	}

	public void setState(double w1, double w2, double w3, double phi, double x, double y, double z) {
		double q0 = Math.cos(0.5 * phi * Math.PI / 180);
		Vec3 n = new Vec3(x, y, z);
		n = n.normalize();
		double s = Math.sin(0.5 * phi * Math.PI / 180);
		this.x = new double[] { w1, w2, w3, q0, s * n.x, s * n.y, s * n.z };
	}

	public double[] getState() {
		double w1 = x[0], w2 = x[1], w3 = x[2];
		double q0 = x[3], q1 = x[4], q2 = x[5], q3 = x[6];
		double phi = 2 * Math.acos(q0) * 180 / Math.PI;
		double[] xx = { w1, w2, w3, phi, q1, q2, q3 };
		return xx;
	}

	public void move(double dt) {
		x = runge(x, dt);
	}

	public void setGyroDynamics(float m, float a, float b, float c) {
		I1 = 1.0f / 12.0f * m * (b * b + c * c);
		I2 = 1.0f / 12.0f * m * (a * a + c * c);
		I3 = 1.0f / 12.0f * m * (a * a + b * b);
		// see script p.73
	}
}
