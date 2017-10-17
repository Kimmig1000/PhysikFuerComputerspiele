/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.util.math;

public final class MathUtil {
	static public final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
	static public final float PI = (float) Math.PI;
	static public final float RADIANS_TO_DEGREES = 180f / PI;
	static public final float DEGREES_TO_RADIANS = PI / 180;

	public static float map(float value, float sourceMin, float sourceMax, float targetMin, float targetMax) {
		if (sourceMax - sourceMin == 0)
			return 0;
		return (value - sourceMin) * (targetMax - targetMin) / (sourceMax - sourceMin) + targetMin;
	}

	public static double map(double value, double sourceMin, double sourceMax, double targetMin, double targetMax) {
		if (sourceMax - sourceMin == 0)
			return 0;
		return (value - sourceMin) * (targetMax - targetMin) / (sourceMax - sourceMin) + targetMin;
	}

	public static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	public static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	/** Returns true if the value is zero (using the default tolerance as upper bound) */
	static public boolean isZero(float value) {
		return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
	}

	/**
	 * Returns true if the value is zero.
	 * 
	 * @param tolerance
	 *            represent an upper bound below which the value is considered zero.
	 */
	static public boolean isZero(float value, float tolerance) {
		return Math.abs(value) <= tolerance;
	}

	/**
	 * Returns true if a is nearly equal to b. The function uses the default floating error tolerance.
	 * 
	 * @param a
	 *            the first value.
	 * @param b
	 *            the second value.
	 */
	static public boolean isEqual(float a, float b) {
		return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
	}

	/**
	 * Returns true if a is nearly equal to b.
	 * 
	 * @param a
	 *            the first value.
	 * @param b
	 *            the second value.
	 * @param tolerance
	 *            represent an upper bound below which the two values are considered equal.
	 */
	static public boolean isEqual(float a, float b, float tolerance) {
		return Math.abs(a - b) <= tolerance;
	}

	/**
	 * Calculates the length of a vector [x, y].
	 * 
	 * @return the euclidean length of [x, y]
	 */
	public static float length(float x, float y) {
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * Calculates the length of a vector [x, y, z].
	 * 
	 * @return the euclidean length of [x, y, z]
	 */
	public static float length(float x, float y, float z) {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Calculates the length of a vector [x, y, z, w].
	 * 
	 * @return the euclidean length of [x, y, z, w]
	 */
	public static float length(float x, float y, float z, float w) {
		return (float) Math.sqrt(x * x + y * y + z * z + w * w);
	}

	/**
	 * Calculates the dot product between two vectors [x, y].
	 * 
	 * @return The dot product between the two vectors
	 */
	public static float dot(float ax, float ay, float bx, float by) {
		return ax * bx + ay * by;
	}

	/**
	 * Calculates the dot product between two vectors [x, y, z].
	 * 
	 * @return The dot product between the two vectors
	 */
	public static float dot(float ax, float ay, float az, float bx, float by, float bz) {
		return ax * bx + ay * by + az * bz;
	}

	/**
	 * Calculates the dot product between two vectors [x, y, z, w].
	 * 
	 * @return The dot product between the two vectors
	 */
	public static float dot(float ax, float ay, float az, float aw, float bx, float by, float bz, float bw) {
		return ax * bx + ay * by + az * bz + aw * bw;
	}

	/**
	 * Checks if a number is a power of two.
	 * @param n the number to be evaluated
	 * @return true if n is a power of two, false otherwise
	 */
	public static boolean isPowerOfTwo(int n) {
		return (n > 0) && ((n & (n - 1)) == 0);
	}
}
