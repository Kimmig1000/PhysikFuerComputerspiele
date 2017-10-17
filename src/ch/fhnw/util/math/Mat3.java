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

import ch.fhnw.util.IFloatArrayCopyProvider;

/**
 * 3x3 matrix for dealing with OpenGL 3x3 matrices (column major). Mat3 is immutable.
 *
 * @author radar
 */
public final class Mat3 implements IFloatArrayCopyProvider {
	public static final Mat3 ZERO = new Mat3();
	public static final Mat3 ID = new Mat3(1, 0, 0, 0, 1, 0, 0, 0, 1);

	public final float m00;
	public final float m10;
	public final float m20;
	public final float m01;
	public final float m11;
	public final float m21;
	public final float m02;
	public final float m12;
	public final float m22;

	/**
	 * Create empty 3x3 matrix.
	 */
	public Mat3() {
		this(0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	/**
	 * Create 3x3 matrix from 9 float values.
	 */
	public Mat3(float m00, float m10, float m20, float m01, float m11, float m21, float m02, float m12, float m22) {
		this.m00 = m00;
		this.m10 = m10;
		this.m20 = m20;
		this.m01 = m01;
		this.m11 = m11;
		this.m21 = m21;
		this.m02 = m02;
		this.m12 = m12;
		this.m22 = m22;
	}

	/**
	 * Create 3x3 matrix from array of 9 float values.
	 */
	public Mat3(float[] m) {
		this(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8]);
	}

	/**
	 * Create 3x3 matrix from top-left 3x3 matrix of a 4x4 matrix
	 */
	public Mat3(Mat4 m) {
		this(m.m00, m.m10, m.m20, m.m01, m.m11, m.m21, m.m02, m.m12, m.m22);
	}

	/**
	 * Post-multiply this matrix with mat (result = this * mat).
	 *
	 * @param mat
	 *            the second factor of the matrix product
	 */
	public Mat3 postMultiply(Mat3 mat) {
		return multiply(this, mat);
	}

	/**
	 * Pre-multiply this matrix with mat (result = mat * this).
	 *
	 * @param mat
	 *            the first factor of the matrix product
	 */
	public Mat3 preMultiply(Mat3 mat) {
		return multiply(mat, this);
	}

	/**
	 * Transform vector (result = m * vec).
	 *
	 * @param vec
	 *            the vector to be transformed
	 * @return the transformed vector
	 */
	public Vec3 transform(Vec3 vec) {
		float x = m00 * vec.x + m01 * vec.y + m02 * vec.z;
		float y = m10 * vec.x + m11 * vec.y + m12 * vec.z;
		float z = m20 * vec.x + m21 * vec.y + m22 * vec.z;
		return new Vec3(x, y, z);
	}

	/**
	 * Transform a float array of xyz vectors.
	 *
	 * @param xyz
	 *            the input array of vectors to be transformed
	 * @param result
	 *            the array where to store the transformed vectors or NULL to create a new array
	 * @return the transformed result
	 */
	public float[] transform(float[] xyz, float[] result) {
		if (xyz == null)
			return null;
		if (result == null)
			result = new float[xyz.length];
		for (int i = 0; i < xyz.length; i += 3) {
			float x = m00 * xyz[i] + m01 * xyz[i + 1] + m02 * xyz[i + 2];
			float y = m10 * xyz[i] + m11 * xyz[i + 1] + m12 * xyz[i + 2];
			float z = m20 * xyz[i] + m21 * xyz[i + 1] + m22 * xyz[i + 2];
			result[i] = x;
			result[i + 1] = y;
			result[i + 2] = z;
		}
		return result;
	}

	/**
	 * Transform a float array of xyz vectors.
	 *
	 * @param xyz
	 *            the input array of vectors to be transformed
	 * @return new array containing the transformed result
	 */
	public float[] transform(float[] xyz) {
		return transform(xyz, null);
	}

	/**
	 * Get the transpose matrix.
	 *
	 * @return the transpose matrix
	 */
	public Mat3 transpose() {
		return new Mat3(m00, m01, m02, m10, m11, m12, m20, m21, m22);
	}

	/**
	 * Get the determinant.
	 * 
	 * @return the determinant
	 */
	public float determinant() {
		return m00 * m11 * m22 + m01 * m12 * m20 + m02 * m10 * m21 - m00 * m12 * m21 - m01 * m10 * m22 - m02 * m11 * m20;
	}

	/**
	 * Get the inverse matrix.
	 *
	 * @return the inverse or null if a is singular
	 */
	public Mat3 inverse() {
		float d = determinant();
		if (d == 0)
			return null;

		float v00 = (m11 * m22 - m21 * m12) / d;
		float v10 = (m20 * m12 - m10 * m22) / d;
		float v20 = (m10 * m21 - m20 * m11) / d;
		float v01 = (m21 * m02 - m01 * m22) / d;
		float v11 = (m00 * m22 - m20 * m02) / d;
		float v21 = (m20 * m01 - m00 * m21) / d;
		float v02 = (m01 * m12 - m11 * m02) / d;
		float v12 = (m10 * m02 - m00 * m12) / d;
		float v22 = (m00 * m11 - m10 * m01) / d;

		return new Mat3(v00, v10, v20, v01, v11, v21, v02, v12, v22);
	}

	/**
	 * Multiplies two matrices (result = a * b).
	 *
	 * @param a
	 *            3x3 matrix
	 * @param b
	 *            3x3 matrix
	 * @return a * b
	 */
	public static Mat3 multiply(Mat3 a, Mat3 b) {
		float v00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20;
		float v01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21;
		float v02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22;

		float v10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20;
		float v11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21;
		float v12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22;

		float v20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20;
		float v21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21;
		float v22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22;

		return new Mat3(v00, v10, v20, v01, v11, v21, v02, v12, v22);
	}

	/**
	 * Multiplies three matrices (result = a * b * c).
	 *
	 * @param a
	 *            3x3 matrix in column-major order
	 * @param b
	 *            3x3 matrix in column-major order
	 * @param c
	 *            3x3 matrix in column-major order
	 * @return a * b * c
	 */
	public static Mat3 multiply(Mat3 a, Mat3 b, Mat3 c) {
		return multiply(a, multiply(b, c));
	}

	/**
	 * Multiplies four matrices (result = a * b * c * d).
	 *
	 * @param a
	 *            3x3 matrix in column-major order
	 * @param b
	 *            3x3 matrix in column-major order
	 * @param c
	 *            3x3 matrix in column-major order
	 * @param d
	 *            3x3 matrix in column-major order
	 * @return a * b * c * d
	 */
	public static Mat3 multiply(Mat3 a, Mat3 b, Mat3 c, Mat3 d) {
		return multiply(a, multiply(b, c, d));
	}

	/**
	 * Multiplies an arbitrary sequence matrices (result = a * b * c * d * ...).
	 *
	 * @param a
	 *            Sequence of 3x3 matrices in column-major order
	 * @return a0 * a1 * a2 * ...
	 */
	public static Mat3 multiply(Mat3... a) {
		return multiply(0, a);
	}
	
	// TODO: optimize for memory allocation
	private static Mat3 multiply(int i, Mat3[] a) {
		if (i == a.length - 1)
			return a[i];
		return multiply(a[i], multiply(i + 1, a));
	}

	/**
	 * Create rotation matrix.
	 *
	 * @param angle
	 *            rotation angle in degrees
	 * @param x
	 *            rotation axis x
	 * @param y
	 *            rotation axis y
	 * @param z
	 *            rotation axis z
	 * @return the rotation matrix
	 */
	public static Mat3 rotate(float angle, float x, float y, float z) {
		float l = (float) Math.sqrt(x * x + y * y + z * z);
		if (l != 0 && l != 1) {
			l = 1.0f / l;
			x *= l;
			y *= l;
			z *= l;
		}

		float radians = angle * MathUtil.DEGREES_TO_RADIANS;
		float c = (float) Math.cos(radians);
		float ic = 1.0f - c;
		float s = (float) Math.sin(radians);

		float xy = x * y;
		float xz = x * z;
		float xs = x * s;
		float ys = y * s;
		float yz = y * z;
		float zs = z * s;

		float m00 = x * x * ic + c;
		float m10 = xy * ic + zs;
		float m20 = xz * ic - ys;
		float m01 = xy * ic - zs;
		float m11 = y * y * ic + c;
		float m21 = yz * ic + xs;
		float m02 = xz * ic + ys;
		float m12 = yz * ic - xs;
		float m22 = z * z * ic + c;

		return new Mat3(m00, m10, m20, m01, m11, m21, m02, m12, m22);
	}

	/**
	 * Create rotation matrix.
	 *
	 * @param angle
	 *            rotation angle in degrees
	 * @param axis
	 *            rotation axis
	 * @return the rotation matrix
	 */
	public static Mat3 rotate(float angle, Vec3 axis) {
		return rotate(angle, axis.x, axis.y, axis.z);
	}

	/**
	 * Create scale matrix.
	 *
	 * @param sx
	 *            scale x factor
	 * @param sy
	 *            scale y factor
	 * @param sz
	 *            scale z factor
	 * @return the scale matrix
	 */
	public static Mat3 scale(float sx, float sy, float sz) {
		//@formatter:off
		return new Mat3(sx, 0,  0, 
						0,  sy, 0, 
						0,  0,  sz);
		//@formatter:on
	}

	/**
	 * Create scale matrix.
	 *
	 * @param s
	 *            scale xyz vector
	 * @return the scale matrix
	 */
	public static Mat3 scale(Vec3 s) {
		return scale(s.x, s.y, s.z);
	}

	@Override
	public float[] toArray() {
		return new float[] { m00, m10, m20, m01, m11, m21, m02, m12, m22 };
	}

	@Override
	public String toString() {
		//@formatter:off
		return String.format("[% .2f,% .2f,% .2f\n % .2f,% .2f,% .2f\n % .2f,% .2f,% .2f\n\n",
							 m00, m01, m02,
							 m10, m11, m12,
							 m20, m21, m22);
		//@formatter:on
	}
}
