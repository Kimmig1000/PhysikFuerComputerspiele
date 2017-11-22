package programs;

public abstract class Dynamics {

	public double[] euler(double[] x, double dt) {
		double[] y = f(x);

		double[] xx = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			xx[i] = x[i] + y[i] * dt;
			
		}
		return xx;
		
	}
	
	public double [] runge(double [] x, double dt){
		double[] y1 = f(x);
		// y1 = f(x)
		double[] xx = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			xx[i] = x[i] + 0.5 * dt * y1[i];
			
		} 
		// y2 = f(x + dt 2 y1)
		double [] y2 = f(xx);
		for (int i = 0; i < x.length; i++) {
			xx[i] = x[i] + 0.5 * dt * y2[i];
			
		} 
		
		//y3 = f(x + dt 2 y2)
		double [] y3 = f(xx);
		for (int i = 0; i < x.length; i++) {
			xx[i] = x[i] + dt * y3[i];
			
		} 
		
		// y4 = f(x + dt · y3)
		// y = 1/6 (y1 + 2y2 + 2y3 + y4)
		double [] y4 = f(xx);
		for (int i = 0; i < x.length; i++) {
			xx[i] = x[i] + dt * (y1[i] + 2 * y2[i] + 2 * y3 [i] + y4[i]) / 6;
			
		} 
		
		return xx;
	
	}
	
	
	public abstract double [] f (double [] x);
	
	
}
