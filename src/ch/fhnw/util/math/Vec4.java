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

import java.util.List;

/**
 * 4D vector for basic vector algebra. Instances are immutable.
 *
 * @author radar
 */
public class Vec4 implements IVec4 {
  public static final Vec4 ZERO = new Vec4(0, 0, 0, 0);
  public static final Vec4 ONE = new Vec4(1, 1, 1, 1);
  public static final Vec4 X = new Vec4(1, 0, 0, 0);
  public static final Vec4 Y = new Vec4(0, 1, 0, 0);
  public static final Vec4 Z = new Vec4(0, 0, 1, 0);
  public static final Vec4 W = new Vec4(0, 0, 0, 1);
  public static final Vec4 X_NEG = new Vec4(-1, 0, 0);
  public static final Vec4 Y_NEG = new Vec4(0, -1, 0, 0);
  public static final Vec4 Z_NEG = new Vec4(0, 0, -1, 0);
  public static final Vec4 W_NEG = new Vec4(0, 0, 0, -1);

  public final float x;
  public final float y;
  public final float z;
  public final float w;

  public Vec4(float x, float y, float z, float w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public Vec4(double x, double y, double z, double w) {
    this((float) x, (float) y, (float) z, (float) w);
  }

  public Vec4(float x, float y, float z) {
    this(x, y, z, 1);
  }

  public Vec4(double x, double y, double z) {
    this((float) x, (float) y, (float) z);
  }

  public Vec4(Vec3 v) {
    this(v.x, v.y, v.z, 1);
  }

  @Override
  public float x() {
    return x;
  }

  @Override
  public float y() {
    return y;
  }

  @Override
  public float z() {
    return z;
  }

  @Override
  public float w() {
    return w;
  }

  public float length() {
    return MathUtil.length(x, y, z, w);
  }

  public float distance(Vec4 v) {
    return (float) Math.sqrt((v.x - x) * (v.x - x) + (v.y - y) * (v.y - y) + (v.z - z) * (v.z - z) + (v.w - w) * (v.w - w));
  }

  public Vec4 add(Vec4 v) {
    return new Vec4(x + v.x, y + v.y, z + v.z, w + v.w);
  }

  public Vec4 subtract(Vec4 v) {
    return new Vec4(x - v.x, y - v.y, z - v.z, w - v.w);
  }

  public Vec4 scale(float s) {
    return new Vec4(x * s, y * s, z * s, w * s);
  }

  public Vec4 negate() {
    return scale(-1);
  }

  public Vec4 normalize() {
    float l = length();
    if (MathUtil.isZero(l) || l == 1)
      return this;
    return new Vec4(x / l, y / l, z / l, w / l);
  }

  public float dot(Vec4 a) {
    return MathUtil.dot(x, y, z, w, a.x, a.y, a.z, a.w);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof Vec4) {
      final Vec4 v = (Vec4) obj;
      return (x == v.x) && (y == v.y) && (z == v.z) && (w == v.w);
    }
    return false;
  }

  @Override
  public Vec4 toVec4() {
    return this;
  }

  @Override
  public float[] toArray() {
    return new float[] { x, y, z, w };
  }

  @Override
  public String toString() {
    return "[" + x + ", " + y + ", " + z + ", " + w + "]";
  }

  public static float[] toArray(List<? extends Vec4> vectors) {
    if (vectors == null)
      return null;

    float[] result = new float[vectors.size() * 4];
    int i = 0;
    for (Vec4 v : vectors) {
      result[i++] = v.x;
      result[i++] = v.y;
      result[i++] = v.z;
      result[i++] = v.w;
    }
    return result;
  }
}
