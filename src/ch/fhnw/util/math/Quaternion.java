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

/*
 * Largely taken from libgdx with some adaptations.
 * http://libgdx.badlogicgames.com
 */
package ch.fhnw.util.math;

import ch.fhnw.util.Pair;

/**
 * Quaternion. Instances are immutable.
 *
 * @author radar
 */
public final class Quaternion {
  public static final Quaternion ID = new Quaternion(0, 0, 0, 1);

  public final float x;
  public final float y;
  public final float z;
  public final float w;

  public Quaternion(float x, float y, float z, float w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public Quaternion(double x, double y, double z, double w) {
    this((float) x, (float) y, (float) z, (float) w);
  }

  /**
   * Creates quaternion from given euler angles in degrees.
   *
   * @param xRotation
   *            the rotation around the x axis in degrees ("pitch")
   * @param yRotation
   *            the rotation around the y axis in degrees ("yaw")
   * @param zRotation
   *            the rotation around the z axis in degrees ("roll")
   */
  public static Quaternion fromEulerAngles(float xRotation, float yRotation, float zRotation) {
    float hx = xRotation * 0.5f * MathUtil.DEGREES_TO_RADIANS;
    float shx = (float) Math.sin(hx);
    float chx = (float) Math.cos(hx);

    float hy = yRotation * 0.5f * MathUtil.DEGREES_TO_RADIANS;
    float shy = (float) Math.sin(hy);
    float chy = (float) Math.cos(hy);

    float hz = zRotation * 0.5f * MathUtil.DEGREES_TO_RADIANS;
    float shz = (float) Math.sin(hz);
    float chz = (float) Math.cos(hz);

    return new Quaternion(chy * shx * chz + shy * chx * shz,
                shy * chx * chz - chy * shx * shz,
                chy * chx * shz - shy * shx * chz,
                chy * chx * chz + shy * shx * shz);
  }

  /**
   * Creates quaternion components from the given axis and angle around that axis.
   *
   * @param axis
   *            the axis
   * @param angle
   *            the angle in degrees
   */
  public static Quaternion fromAxis(Vec3 axis, float angle) {
    float rad = angle * MathUtil.DEGREES_TO_RADIANS;
    float d = axis.length();
    if (d == 0f)
      return ID;
    d = 1f / d;
    float alpha = rad;
    float sin = (float) Math.sin(alpha / 2);
    float cos = (float) Math.cos(alpha / 2);
    return new Quaternion(d * axis.x * sin, d * axis.y * sin, d * axis.z * sin, cos).normalize();
  }

  /**
   * Creates quaternion from the given x-, y- and z-axis which have to be orthonormal.
   */
  public static Quaternion fromAxes(Vec3 vx, Vec3 vy, Vec3 vz) {
    return fromAxes(vx, vy, vz, false);
  }

  /**
   * Creates quaternion from the given x-, y- and z-axis.
   */
  public static Quaternion fromAxes(Vec3 vx, Vec3 vy, Vec3 vz, boolean normalize) {
    if (normalize) {
      vx = vx.normalize();
      vy = vy.normalize();
      vz = vz.normalize();
    }
    // the trace is the sum of the diagonal elements; see
    // http://mathworld.wolfram.com/MatrixTrace.html
    float t = vx.x + vy.y + vz.z;

    float x;
    float y;
    float z;
    float w;

    // we protect the division by s by ensuring that s>=1
    if (t >= 0) { // |w| >= .5
      float s = (float) Math.sqrt(t + 1); // |s|>=1 ...
      w = 0.5f * s;
      s = 0.5f / s; // so this division isn't bad
      x = (vz.y - vy.z) * s;
      y = (vx.z - vz.x) * s;
      z = (vy.x - vx.y) * s;
    } else if ((vx.x > vy.y) && (vx.x > vz.z)) {
      float s = (float) Math.sqrt(1.0 + vx.x - vy.y - vz.z); // |s|>=1
      x = s * 0.5f; // |x| >= .5
      s = 0.5f / s;
      y = (vy.x + vx.y) * s;
      z = (vx.z + vz.x) * s;
      w = (vz.y - vy.z) * s;
    } else if (vy.y > vz.z) {
      float s = (float) Math.sqrt(1.0 + vy.y - vx.x - vz.z); // |s|>=1
      y = s * 0.5f; // |y| >= .5
      s = 0.5f / s;
      x = (vy.x + vx.y) * s;
      z = (vz.y + vy.z) * s;
      w = (vx.z - vz.x) * s;
    } else {
      float s = (float) Math.sqrt(1.0 + vz.z - vx.x - vy.y); // |s|>=1
      z = s * 0.5f; // |z| >= .5
      s = 0.5f / s;
      x = (vx.z + vz.x) * s;
      y = (vz.y + vy.z) * s;
      w = (vy.x - vx.y) * s;
    }

    return new Quaternion(x, y, z, w);
  }

  /**
   * Creates quaternion from the rotation between two vectors.
   *
   * @param v1
   *            the base vector, which should be normalized.
   * @param v2
   *            the target vector, which should be normalized.
   */
  public static Quaternion fromCross(Vec3 v1, Vec3 v2) {
    float dot = MathUtil.clamp(v1.dot(v2), -1f, 1f);
    float angle = (float) Math.acos(dot) * MathUtil.RADIANS_TO_DEGREES;
    return fromAxis(v1.cross(v2), angle);
  }

  /**
   * Creates quaternion from the given rotation matrix, which must not contain scaling.
   */
  public static Quaternion fromMatrix(Mat4 matrix) {
    return fromMatrix(matrix, false);
  }

  /**
   * Creates quaternion from the given matrix, optionally removing any scaling.
   */
  public static Quaternion fromMatrix(Mat4 matrix, boolean normalize) {
    Vec3 v1 = new Vec3(matrix.m00, matrix.m01, matrix.m02);
    Vec3 v2 = new Vec3(matrix.m10, matrix.m11, matrix.m12);
    Vec3 v3 = new Vec3(matrix.m20, matrix.m21, matrix.m22);
    return fromAxes(v1, v2, v3, normalize);
  }

  /**
   * Check this quaternion for identity.
   *
   * @return true if quaternion is an identity quaternion
   */
  public boolean isIdentity() {
    return MathUtil.isZero(x) && MathUtil.isZero(y) && MathUtil.isZero(z) && MathUtil.isEqual(w, 1f);
  }

  /**
   * Check this quaternion for identity with given tolerance.
   *
   * @return true if quaternion is an identity quaternion
   */
  public boolean isIdentity(float tolerance) {
    return MathUtil.isZero(x, tolerance) && MathUtil.isZero(y, tolerance) && MathUtil.isZero(z, tolerance) && MathUtil.isEqual(w, 1f, tolerance);
  }

  /**
   * Normalizes this quaternion to unit length.
   *
   * @return the normalized quaternion
   */
  public Quaternion normalize() {
    float l = length();
    if (MathUtil.isZero(l) || l == 1)
      return this;
    return new Quaternion(x / l, y / l, z / l, w / l);
  }

  /**
   * Calculates the length of this quaternion.
   *
   * @return the length of this quaternion
   */
  public float length() {
    return MathUtil.length(x, y, z, w);
  }

  /**
   * Calculates the dot product this*q.
   *
   * @return the dot product this*q
   */
  public float dot(Quaternion q) {
    return MathUtil.dot(x, y, z, w, q.x, q.y, q.z, q.w);
  }

  /**
   * Conjugate the quaternion.
   *
   * @return the conjugate quaternion
   */
  public Quaternion conjugate() {
    return new Quaternion(-x, -y, -z, w);
  }

  /**
   * Add quaternion q to this.
   *
   * @param q
   *            quaternion to be added
   * @return the quaternion this + q
   */
  public Quaternion add(Quaternion q) {
    return new Quaternion(x + q.x, y + q.y, z + q.z, w + q.w);
  }

  /**
   * Post-multiply this quaternion with result = this * q.
   *
   * @param q
   *            quaternion to multiply with
   *
   * @return the quaternion this * q
   */
  public Quaternion postMultiply(Quaternion q) {
    return new Quaternion(w * q.x + x * q.w + y * q.z - z * q.y,
                w * q.y + y * q.w + z * q.x - x * q.z,
                w * q.z + z * q.w + x * q.y - y * q.x,
                w * q.w - x * q.x - y * q.y - z * q.z);
  }

  /**
   * Pre-multiply this quaternion with result = q * this.
   *
   * @param q
   *            quaternion to multiply with
   *
   * @return the quaternion q * this
   */
  public Quaternion preMultiply(Quaternion q) {
    return new Quaternion(q.w * x + q.x * w + q.y * z - q.z * y,
                q.w * y + q.y * w + q.z * x - q.x * z,
                q.w * z + q.z * w + q.x * y - q.y * x,
                q.w * w - q.x * x - q.y * y - q.z * z);
  }

  /**
   * Spherical linear interpolation between this quaternion and the other quaternion, based on the alpha value in the
   * range [0,1]. Taken from. Taken from Bones framework for JPCT, see http://www.aptalkarga.com/bones/
   *
   * @param end
   *            the end quaternion
   * @param alpha
   *            alpha in the range [0,1]
   *
   * @return the resulting quaternion
   */
  public Quaternion slerp(Quaternion end, float alpha) {
    float dot = dot(end);
    float scale0 = 1 - alpha;
    float scale1 = alpha;

    if ((1 - dot) > 0.1) {
      double angle = Math.acos(dot);
      double invSinTheta = 1f / Math.sin(angle);

      scale0 = (float) (Math.sin((1 - alpha) * angle) * invSinTheta);
      scale1 = (float) (Math.sin((alpha * angle)) * invSinTheta);
    }

    return new Quaternion(scale0 * x + scale1 * end.x, scale0 * y + scale1 * end.y, scale0 * z + scale1 * end.z, scale0 * w + scale1 * end.w);
  }

  /**
   * Spherical linearly interpolates multiple quaternions and will return the result. Will not destroy the data
   * previously inside the elements of q. result = (q_1^w_1)*(q_2^w_2)* ... *(q_n^w_n) where w_i=1/n.
   *
   * @param q
   *            list of quaternions
   *
   * @return the resulting quaternion
   */
  public Quaternion slerp(Quaternion[] q) {
    float w = 1.0f / q.length;
    Quaternion result = q[0].exp(w);
    for (int i = 1; i < q.length; i++)
      result = result.postMultiply(q[i]).exp(w);
    return result.normalize();
  }

  /**
   * Spherical linearly interpolates multiple quaternions by the given weights and will return the result. Will not
   * destroy the data previously inside the elements of q or w. result = (q_1^w_1)*(q_2^w_2)* ... *(q_n^w_n) where the
   * sum of w_i is 1. Lists must be equal in length.
   *
   * @param q
   *            list of quaternions
   * @param w
   *            list of weights
   *
   * @return the resulting quaternion
   */
  public Quaternion slerp(Quaternion[] q, float[] w) {
    Quaternion result = q[0].exp(w[0]);
    for (int i = 1; i < q.length; i++)
      result = result.postMultiply(q[i]).exp(w[i]);
    return result.normalize();
  }

  /**
   * Calculates this^alpha where alpha is a real number.
   *
   * @param alpha
   *            exponent
   *
   * @return the quaternion this^alpha
   */
  public Quaternion exp(float alpha) {
    float norm = length();
    float normExp = (float) Math.pow(norm, alpha);

    float theta = (float) Math.acos(w / norm);

    float coeff = 0;
    if (Math.abs(theta) < 0.001)
      coeff = normExp * alpha / norm;
    else
      coeff = (float) (normExp * Math.sin(alpha * theta) / (norm * Math.sin(theta)));

    return new Quaternion(x * coeff, y * coeff, z * coeff, (float) (normExp * Math.cos(alpha * theta))).normalize();
  }

  /**
   * Get the x-rotation Euler angle ("pitch") in degrees. Requires that this quaternion is normalized.
   *
   * @return the rotation around the x axis in degrees (between -90 and +90)
   */
  // XXX do we really want this to be between -90 and +90?
  public float getXRotation() {
    int pole = getGimbalPole();
    float rad = pole == 0 ? (float) Math.asin(MathUtil.clamp(2f * (w * x - z * y), -1f, 1f)) : pole * MathUtil.PI * 0.5f;
    return rad * MathUtil.RADIANS_TO_DEGREES;
  }

  /**
   * Get the y-rotation Euler angle ("yaw") in degrees. Requires that this quaternion is normalized.
   *
   * @return the rotation around the y axis in degrees (between -180 and +180)
   */
  public float getYRotation() {
    float rad = (float) (getGimbalPole() == 0 ? Math.atan2(2f * (y * w + x * z), 1f - 2f * (y * y + x * x)) : 0f);
    return rad * MathUtil.RADIANS_TO_DEGREES;
  }

  /**
   * Get the z-rotation Euler angle ("roll") in degrees. Requires that this quaternion is normalized.
   *
   * @return the rotation around the z axis in degrees (between -180 and +180)
   */
  public float getZRotation() {
    int pole = getGimbalPole();
    float rad = (float) (pole == 0 ? Math.atan2(2f * (w * z + y * x), 1f - 2f * (x * x + z * z)) : pole * 2f * Math.atan2(y, w));
    return rad * MathUtil.RADIANS_TO_DEGREES;
  }

  /**
   * Get the pole of the gimbal lock, if any.
   *
   * @return +1 for north pole, -1 for south pole, 0 when no gimbal lock
   */
  public int getGimbalPole() {
    float t = y * x + z * w;
    return t > 0.499f ? 1 : (t < -0.499f ? -1 : 0);
  }

  /**
   * Get the angle in degrees of the rotation this quaternion represents. Does not normalize the quaternion.
   *
   * @return the angle in degrees of the rotation
   */
  public float getAngle() {
    float rad = (float) (2.0 * Math.acos((this.w > 1) ? (this.w / length()) : this.w));
    return rad * MathUtil.RADIANS_TO_DEGREES;
  }

  /**
   * Get the axis-angle representation of the rotation in degrees. The x, y and z values will be the axis of the
   * rotation and the w component returned is the angle in degrees around that axis. The result axis is a unit vector.
   * However, if this is an identity quaternion (no rotation), then the length of the axis may be zero.
   *
   * @return the axis vector (xyz) and the angle in radians (w).
   */
  public Vec4 getAxisAngle() {
    Quaternion q = this.w > 1 ? this : this.normalize();
    float angle = (float) (2.0 * Math.acos(this.w)) * MathUtil.RADIANS_TO_DEGREES;
    double s = Math.sqrt(1 - q.w * q.w);
    if (s < MathUtil.FLOAT_ROUNDING_ERROR) {
      return new Vec4(q.x, q.y, q.z, angle);
    }
    return new Vec4((float) (q.x / s), (float) (q.y / s), (float) (q.z / s), angle);
  }

  /**
   * Get the angle in degrees of the rotation around the specified axis. The axis must be normalized.
   *
   * @param axis
   *            the normalized axis for which to get the angle
   * @return the angle in degrees of the rotation around the specified axis
   */
  public float getAngleAround(Vec3 axis) {
    float d = MathUtil.dot(this.x, this.y, this.z, axis.x, axis.y, axis.z);
    float l = MathUtil.length(axis.x * d, axis.y * d, axis.z * d, this.w);
    float rad = MathUtil.isZero(l) ? 0f : (float) (2.0 * Math.acos(this.w / l));
    return rad * MathUtil.RADIANS_TO_DEGREES;
  }

  /**
   * Get the swing rotation and twist rotation for the specified axis. The twist rotation represents the rotation
   * around the specified axis. The swing rotation represents the rotation of the specified axis itself, which is the
   * rotation around an axis perpendicular to the specified axis. The swing and twist rotation can be used to
   * reconstruct the original quaternion: this = swing * twist.
   *
   * @param axis
   *            the normalized axis for which to get the swing and twist rotation
   * @param swing
   *            will receive the swing rotation: the rotation around an axis perpendicular to the specified axis
   * @param twist
   *            will receive the twist rotation: the rotation around the specified axis
   *
   * @return a pair containing the quaternions swing and twist
   */
  public Pair<Quaternion, Quaternion> getSwingTwist(Vec3 axis) {
    float d = new Vec3(x, y, z).dot(axis);

    Quaternion twist = new Quaternion(axis.x * d, axis.y * d, axis.z * d, this.w).normalize();
    Quaternion swing = twist.conjugate().preMultiply(this);
    return new Pair<>(swing, twist);
  }

  /**
   * Transforms the given vector using this quaternion.
   *
   * @param v
   *            vector to transform
   *
   * @return the transformed vector
   */
  public Vec3 transform(Vec3 v) {
    Quaternion q = conjugate().preMultiply(new Quaternion(v.x, v.y, v.z, 0)).preMultiply(this);
    return new Vec3(q.x, q.y, q.z);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (obj instanceof Quaternion) {
      Quaternion v = (Quaternion) obj;
      return (x == v.x) && (y == v.y) && (z == v.z) && (w == v.w);
    }
    return false;
  }

  @Override
  public String toString() {
    return "Q" + "[" + x + ", " + y + ", " + z + ", " + w + "]";
  }
}
