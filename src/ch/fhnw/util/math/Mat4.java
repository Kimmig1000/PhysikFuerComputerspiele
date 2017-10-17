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
 * 4x4 matrix for dealing with OpenGL 4x4 matrices (column major). Mat 4 is immutable.
 *
 * @author radar
 */
public final class Mat4 implements IFloatArrayCopyProvider {
	public static final Mat4 ZERO = new Mat4();
	public static final Mat4 ID = new Mat4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);

	public final float m00;
	public final float m10;
	public final float m20;
	public final float m30;
	public final float m01;
	public final float m11;
	public final float m21;
	public final float m31;
	public final float m02;
	public final float m12;
	public final float m22;
	public final float m32;
	public final float m03;
	public final float m13;
	public final float m23;
	public final float m33;

	/**
	 * Create empty 4x4 matrix.
	 */
	public Mat4() {
		this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	/**
	 * Create 4x4 matrix from 16 float values.
	 */
	public Mat4(float m00, float m10, float m20, float m30, float m01, float m11, float m21, float m31, float m02, float m12, float m22, float m32, float m03,
			float m13, float m23, float m33) {
		this.m00 = m00;
		this.m10 = m10;
		this.m20 = m20;
		this.m30 = m30;
		this.m01 = m01;
		this.m11 = m11;
		this.m21 = m21;
		this.m31 = m31;
		this.m02 = m02;
		this.m12 = m12;
		this.m22 = m22;
		this.m32 = m32;
		this.m03 = m03;
		this.m13 = m13;
		this.m23 = m23;
		this.m33 = m33;
	}

	/**
	 * Create 4x4 matrix from array of 16 float values.
	 */
	public Mat4(float[] m) {
		this(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9], m[10], m[11], m[12], m[13], m[14], m[15]);
	}

	/**
	 * Create 4x4 matrix from 3x3 matrix which 4th row/column vector as [0, 0, 0, 1].
	 */
	public Mat4(Mat3 m) {
		this(m.m00, m.m10, m.m20, 0, m.m01, m.m11, m.m21, 0, m.m02, m.m12, m.m22, 0, 0, 0, 0, 1);
	}

	/**
	 * Create 4x4 matrix from quaternion.
	 */
	public Mat4(Quaternion q) {
		float xx = q.x * q.x;
		float xy = q.x * q.y;
		float xz = q.x * q.z;
		float xw = q.x * q.w;
		float yy = q.y * q.y;
		float yz = q.y * q.z;
		float yw = q.y * q.w;
		float zz = q.z * q.z;
		float zw = q.z * q.w;

		m00 = 1 - 2 * (yy + zz);
		m01 = 2 * (xy - zw);
		m02 = 2 * (xz + yw);
		m03 = 0;
		m10 = 2 * (xy + zw);
		m11 = 1 - 2 * (xx + zz);
		m12 = 2 * (yz - xw);
		m13 = 0;
		m20 = 2 * (xz - yw);
		m21 = 2 * (yz + xw);
		m22 = 1 - 2 * (xx + yy);
		m23 = 0;
		m30 = 0;
		m31 = 0;
		m32 = 0;
		m33 = 1;
	}

	/**
	 * Post-multiply this matrix with mat (result = this * mat).
	 *
	 * @param mat
	 *            the second factor of the matrix product
	 */
	public Mat4 postMultiply(Mat4 mat) {
		return multiply(this, mat);
	}

	/**
	 * Pre-multiply this matrix with mat (result = mat * this).
	 *
	 * @param mat
	 *            the first factor of the matrix product
	 */
	public Mat4 preMultiply(Mat4 mat) {
		return multiply(mat, this);
	}
	
	/**
	 * Transform vector (result = m * vec).
	 *
	 * @param vec
	 *            the vector to be transformed
	 * @return the transformed vector
	 */
	public Vec4 transform(Vec4 vec) {
		float x = m00 * vec.x + m01 * vec.y + m02 * vec.z + m03 * vec.w;
		float y = m10 * vec.x + m11 * vec.y + m12 * vec.z + m13 * vec.w;
		float z = m20 * vec.x + m21 * vec.y + m22 * vec.z + m23 * vec.w;
		float w = m30 * vec.x + m31 * vec.y + m32 * vec.z + m33 * vec.w;
		return new Vec4(x, y, z, w);
	}

	/**
	 * Transform vector and divide by w (result = m * vec).
	 *
	 * @param vec
	 *            the vector to be transformed
	 * @return the transformed vector
	 */
	public Vec3 transform(Vec3 vec) {
		float x = m00 * vec.x + m01 * vec.y + m02 * vec.z + m03;
		float y = m10 * vec.x + m11 * vec.y + m12 * vec.z + m13;
		float z = m20 * vec.x + m21 * vec.y + m22 * vec.z + m23;
		float w = m30 * vec.x + m31 * vec.y + m32 * vec.z + m33;
		return new Vec3(x / w, y / w, z / w);
	}

	/**
	 * Transform a float array of xyz vectors and divide by w.
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
			float x = m00 * xyz[i] + m01 * xyz[i + 1] + m02 * xyz[i + 2] + m03;
			float y = m10 * xyz[i] + m11 * xyz[i + 1] + m12 * xyz[i + 2] + m13;
			float z = m20 * xyz[i] + m21 * xyz[i + 1] + m22 * xyz[i + 2] + m23;
			float w = m30 * xyz[i] + m31 * xyz[i + 1] + m32 * xyz[i + 2] + m33;
			result[i] = x / w;
			result[i + 1] = y / w;
			result[i + 2] = z / w;
		}
		return result;
	}

	/**
	 * Transform a float array of xyz vectors and divide by w.
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
	public Mat4 transpose() {
		return new Mat4(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}

	/**
	 * Get the determinant.
	 * 
	 * @return the determinant
	 */
	public float determinant() {
		//@formatter:off
		return m30 * m21 * m12 * m03 - m20 * m31 * m12 * m03 - m30 * m11 * m22 * m03 
			 + m10 * m31 * m22 * m03 + m20 * m11 * m32 * m03 - m10 * m21 * m32 * m03
			 - m30 * m21 * m02 * m13 + m20 * m31 * m02 * m13 + m30 * m01 * m22 * m13
			 - m00 * m31 * m22 * m13 - m20 * m01 * m32 * m13 + m00 * m21 * m32 * m13
			 + m30 * m11 * m02 * m23 - m10 * m31 * m02 * m23 - m30 * m01 * m12 * m23 
			 + m00 * m31 * m12 * m23 + m10 * m01 * m32 * m23 - m00 * m11 * m32 * m23
			 - m20 * m11 * m02 * m33 + m10 * m21 * m02 * m33 + m20 * m01 * m12 * m33 
			 - m00 * m21 * m12 * m33 - m10 * m01 * m22 * m33 + m00 * m11 * m22 * m33;
		//@formatter:on
	}

	/**
	 * Get the inverse matrix.
	 *
	 * @return the inverse or null if a is singular
	 */
	public Mat4 inverse() {
		float d = determinant();
		if (d == 0)
			return null;

		float v00 = (m12 * m23 * m31 - m13 * m22 * m31 + m13 * m21 * m32 - m11 * m23 * m32 - m12 * m21 * m33 + m11 * m22 * m33) / d;
		float v01 = (m03 * m22 * m31 - m02 * m23 * m31 - m03 * m21 * m32 + m01 * m23 * m32 + m02 * m21 * m33 - m01 * m22 * m33) / d;
		float v02 = (m02 * m13 * m31 - m03 * m12 * m31 + m03 * m11 * m32 - m01 * m13 * m32 - m02 * m11 * m33 + m01 * m12 * m33) / d;
		float v03 = (m03 * m12 * m21 - m02 * m13 * m21 - m03 * m11 * m22 + m01 * m13 * m22 + m02 * m11 * m23 - m01 * m12 * m23) / d;
		float v10 = (m13 * m22 * m30 - m12 * m23 * m30 - m13 * m20 * m32 + m10 * m23 * m32 + m12 * m20 * m33 - m10 * m22 * m33) / d;
		float v11 = (m02 * m23 * m30 - m03 * m22 * m30 + m03 * m20 * m32 - m00 * m23 * m32 - m02 * m20 * m33 + m00 * m22 * m33) / d;
		float v12 = (m03 * m12 * m30 - m02 * m13 * m30 - m03 * m10 * m32 + m00 * m13 * m32 + m02 * m10 * m33 - m00 * m12 * m33) / d;
		float v13 = (m02 * m13 * m20 - m03 * m12 * m20 + m03 * m10 * m22 - m00 * m13 * m22 - m02 * m10 * m23 + m00 * m12 * m23) / d;
		float v20 = (m11 * m23 * m30 - m13 * m21 * m30 + m13 * m20 * m31 - m10 * m23 * m31 - m11 * m20 * m33 + m10 * m21 * m33) / d;
		float v21 = (m03 * m21 * m30 - m01 * m23 * m30 - m03 * m20 * m31 + m00 * m23 * m31 + m01 * m20 * m33 - m00 * m21 * m33) / d;
		float v22 = (m01 * m13 * m30 - m03 * m11 * m30 + m03 * m10 * m31 - m00 * m13 * m31 - m01 * m10 * m33 + m00 * m11 * m33) / d;
		float v23 = (m03 * m11 * m20 - m01 * m13 * m20 - m03 * m10 * m21 + m00 * m13 * m21 + m01 * m10 * m23 - m00 * m11 * m23) / d;
		float v30 = (m12 * m21 * m30 - m11 * m22 * m30 - m12 * m20 * m31 + m10 * m22 * m31 + m11 * m20 * m32 - m10 * m21 * m32) / d;
		float v31 = (m01 * m22 * m30 - m02 * m21 * m30 + m02 * m20 * m31 - m00 * m22 * m31 - m01 * m20 * m32 + m00 * m21 * m32) / d;
		float v32 = (m02 * m11 * m30 - m01 * m12 * m30 - m02 * m10 * m31 + m00 * m12 * m31 + m01 * m10 * m32 - m00 * m11 * m32) / d;
		float v33 = (m01 * m12 * m20 - m02 * m11 * m20 + m02 * m10 * m21 - m00 * m12 * m21 - m01 * m10 * m22 + m00 * m11 * m22) / d;

		return new Mat4(v00, v10, v20, v30, v01, v11, v21, v31, v02, v12, v22, v32, v03, v13, v23, v33);
	}

	/**
	 * Multiplies two matrices (result = a * b).
	 *
	 * @param a
	 *            4x4 matrix in column-major order
	 * @param b
	 *            4x4 matrix in column-major order
	 * @return a * b
	 */
	public static Mat4 multiply(Mat4 a, Mat4 b) {
		float m00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20 + a.m03 * b.m30;
		float m10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20 + a.m13 * b.m30;
		float m20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20 + a.m23 * b.m30;
		float m30 = a.m30 * b.m00 + a.m31 * b.m10 + a.m32 * b.m20 + a.m33 * b.m30;

		float m01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21 + a.m03 * b.m31;
		float m11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21 + a.m13 * b.m31;
		float m21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21 + a.m23 * b.m31;
		float m31 = a.m30 * b.m01 + a.m31 * b.m11 + a.m32 * b.m21 + a.m33 * b.m31;

		float m02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22 + a.m03 * b.m32;
		float m12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22 + a.m13 * b.m32;
		float m22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22 + a.m23 * b.m32;
		float m32 = a.m30 * b.m02 + a.m31 * b.m12 + a.m32 * b.m22 + a.m33 * b.m32;

		float m03 = a.m00 * b.m03 + a.m01 * b.m13 + a.m02 * b.m23 + a.m03 * b.m33;
		float m13 = a.m10 * b.m03 + a.m11 * b.m13 + a.m12 * b.m23 + a.m13 * b.m33;
		float m23 = a.m20 * b.m03 + a.m21 * b.m13 + a.m22 * b.m23 + a.m23 * b.m33;
		float m33 = a.m30 * b.m03 + a.m31 * b.m13 + a.m32 * b.m23 + a.m33 * b.m33;

		return new Mat4(m00, m10, m20, m30, m01, m11, m21, m31, m02, m12, m22, m32, m03, m13, m23, m33);
	}

	/**
	 * Multiplies three matrices (result = a * b * c).
	 *
	 * @param a
	 *            4x4 matrix in column-major order
	 * @param b
	 *            4x4 matrix in column-major order
	 * @param c
	 *            4x4 matrix in column-major order
	 * @return a * b * c
	 */
	public static Mat4 multiply(Mat4 a, Mat4 b, Mat4 c) {
		return multiply(a, multiply(b, c));
	}

	/**
	 * Multiplies four matrices (result = a * b * c * d).
	 *
	 * @param a
	 *            4x4 matrix in column-major order
	 * @param b
	 *            4x4 matrix in column-major order
	 * @param c
	 *            4x4 matrix in column-major order
	 * @param d
	 *            4x4 matrix in column-major order
	 * @return a * b * c * d
	 */
	public static Mat4 multiply(Mat4 a, Mat4 b, Mat4 c, Mat4 d) {
		return multiply(a, multiply(b, c, d));
	}

	/**
	 * Multiplies an arbitrary sequence matrices (result = a * b * c * d * ...).
	 *
	 * @param a
	 *            Sequence of 4x4 matrices in column-major order
	 * @return a0 * a1 * a2 * ...
	 */
	public static Mat4 multiply(Mat4... a) {
		return multiply(0, a);
	}

	// TODO: optimize for memory allocation
	private static Mat4 multiply(int i, Mat4[] a) {
		if (i == a.length - 1)
			return a[i];
		return multiply(a[i], multiply(i + 1, a));
	}
	
	/**
	 * Create translation matrix
	 *
	 * @param tx
	 *            x translation
	 * @param ty
	 *            y translation
	 * @param tz
	 *            z translation
	 * @return the translation matrix
	 */
	public static Mat4 translate(float tx, float ty, float tz) {
		return new Mat4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, tx, ty, tz, 1);
	}

	/**
	 * Create translation matrix
	 *
	 * @param t
	 *            translation vector
	 * @return the translation matrix
	 */
	public static Mat4 translate(Vec3 t) {
		return translate(t.x, t.y, t.z);
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
	public static Mat4 rotate(float angle, float x, float y, float z) {
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

		return new Mat4(m00, m10, m20, 0, m01, m11, m21, 0, m02, m12, m22, 0, 0, 0, 0, 1);
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
	public static Mat4 rotate(float angle, Vec3 axis) {
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
	public static Mat4 scale(float sx, float sy, float sz) {
		//@formatter:off
		return new Mat4(sx, 0,  0,  0, 
						0,  sy, 0,  0,
						0,  0,  sz, 0, 
						0,  0,  0,  1);
		//@formatter:on
	}

	/**
	 * Create scale matrix.
	 *
	 * @param s
	 *            scale xyz vector
	 * @return the scale matrix
	 */
	public static Mat4 scale(Vec3 s) {
		return scale(s.x, s.y, s.z);
	}

	/**
	 * Create view matrix from position (eye point), target (center/reference point) and up vector.
	 * 
	 * @param position
	 *            camera position in world coordinates
	 * @param target
	 *            camera target in world coordinates
	 * @param up
	 *            camera up vector in world coordinates
	 * @return view matrix
	 */
	public static Mat4 lookAt(Vec3 position, Vec3 target, Vec3 up) {
		up = up.normalize();
		Vec3 f = target.subtract(position).normalize();
		Vec3 s = f.cross(up).normalize();
		Vec3 u = s.cross(f);
		Vec3 t = position.negate();

		float m00 = s.x;
		float m10 = u.x;
		float m20 = -f.x;
		float m01 = s.y;
		float m11 = u.y;
		float m21 = -f.y;
		float m02 = s.z;
		float m12 = u.z;
		float m22 = -f.z;
		float m03 = s.x * t.x + s.y * t.y + s.z * t.z;
		float m13 = u.x * t.x + u.y * t.y + u.z * t.z;
		float m23 = -f.x * t.x - f.y * t.y - f.z * t.z;
		float m33 = 1;
		return new Mat4(m00, m10, m20, 0, m01, m11, m21, 0, m02, m12, m22, 0, m03, m13, m23, m33);
	}

	/**
	 * Create perspective projection matrix from left/right/bottom/top. Supports far plane at infinity.
	 *
	 * @param left
	 *            left clipping plane
	 * @param right
	 *            right clipping plane
	 * @param bottom
	 *            bottom clipping plane
	 * @param top
	 *            top clipping plane
	 * @param near
	 *            near clipping plane
	 * @param far
	 *            far clipping plane (set to Float.POSITIVE_INFINITY for far plane at infinity)
	 * @return perspective projection matrix
	 */
	public static Mat4 perspective(float left, float right, float bottom, float top, float near, float far) {
		float m00 = 2 * near / (right - left);
		float m11 = 2 * near / (top - bottom);
		float m02 = (right + left) / (right - left);
		float m12 = (top + bottom) / (top - bottom);
		float m22 = far >= Double.POSITIVE_INFINITY ? -1 : -(far + near) / (far - near);
		float m32 = -1;
		float m23 = far >= Double.POSITIVE_INFINITY ? -2 * near : -2 * far * near / (far - near);
		return new Mat4(m00, 0, 0, 0, 0, m11, 0, 0, m02, m12, m22, m32, 0, 0, m23, 0);
	}

	/**
	 * Create perspective projection matrix from fov and aspect. Supports far plane at infinity.
	 *
	 * @param fov
	 *            field of view (degrees)
	 * @param aspect
	 *            aspect ratio
	 * @param near
	 *            near clipping plane
	 * @param far
	 *            far clipping plane (set to Float.POSITIVE_INFINITY for far plane at infinity)
	 * @return perspective projection matrix
	 */
	public static Mat4 perspective(float fov, float aspect, float near, float far) {
		double radians = fov / 2 * MathUtil.DEGREES_TO_RADIANS;
		double sine = Math.sin(radians);
		float deltaZ = far - near;

		if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
			throw new IllegalArgumentException("illegal arguments (fovy=" + fov + " aspect=" + aspect + " near=" + near + " far=" + far);
		}

		double cotangent = (float) (Math.cos(radians) / sine);

		float m00 = (float) (cotangent / aspect);
		float m11 = (float) cotangent;
		float m22 = far >= Double.POSITIVE_INFINITY ? -1 : -(far + near) / deltaZ;
		float m32 = -1;
		float m23 = far >= Double.POSITIVE_INFINITY ? -2 * near : -2 * near * far / deltaZ;
		return new Mat4(m00, 0, 0, 0, 0, m11, 0, 0, 0, 0, m22, m32, 0, 0, m23, 0);
	}

	/**
	 * Create an orthographic projection matrix.
	 *
	 * @param left
	 *            left clipping plane
	 * @param right
	 *            right clipping plane
	 * @param bottom
	 *            bottom clipping plane
	 * @param top
	 *            top clipping plane
	 * @param near
	 *            near clipping plane
	 * @param far
	 *            far clipping plane
	 * @return orthographic projection matrix
	 */
	public static Mat4 ortho(float left, float right, float bottom, float top, float near, float far) {
		float dx = right - left;
		float dy = top - bottom;
		float dz = far - near;
		float tx = -1.0f * (right + left) / dx;
		float ty = -1.0f * (top + bottom) / dy;
		float tz = -1.0f * (far + near) / dz;

		float m00 = 2.0f / dx;
		float m11 = 2.0f / dy;
		float m22 = -2.0f / dz;
		float m03 = tx;
		float m13 = ty;
		float m23 = tz;
		float m33 = 1;
		return new Mat4(m00, 0, 0, 0, 0, m11, 0, 0, 0, 0, m22, 0, m03, m13, m23, m33);
	}

	@Override
	public float[] toArray() {
		return new float[] { m00, m10, m20, m30, m01, m11, m21, m31, m02, m12, m22, m32, m03, m13, m23, m33 };
	}

	@Override
	public String toString() {
		//@formatter:off
		return String.format("[% .2f,% .2f,% .2f,% .2f\n % .2f,% .2f,% .2f,% .2f\n % .2f,% .2f,% .2f,% .2f\n % .2f,% .2f,% .2f,% .2f \n\n",
							 m00, m01, m02, m03,
							 m10, m11, m12, m13,
							 m20, m21, m22, m23,
							 m30, m31, m32, m33);
		//@formatter:on
	}
}
