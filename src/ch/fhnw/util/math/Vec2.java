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

import java.util.Collection;

import ch.fhnw.util.IFloatArrayCopyProvider;

/**
 * 2D vector for basic vector algebra. Instances are immutable.
 *
 * @author radar
 */
public final class Vec2 implements IFloatArrayCopyProvider {
	public static final Vec2 ZERO = new Vec2(0, 0);
	public static final Vec2 ONE = new Vec2(1, 1);
	public static final Vec2 X = new Vec2(1, 0);
	public static final Vec2 Y = new Vec2(0, 1);
	public static final Vec2 X_NEG = new Vec2(-1, 0);
	public static final Vec2 Y_NEG = new Vec2(0, -1);

	public final float x;
	public final float y;

	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2(double x, double y) {
		this((float) x, (float) y);
	}

	public float length() {
		return MathUtil.length(x, y);
	}

	public float distance(Vec2 v) {
		return (float) Math.sqrt((v.x - x) * (v.x - x) + (v.y - y) * (v.y - y));
	}

	public Vec2 add(Vec2 v) {
		return new Vec2(x + v.x, y + v.y);
	}

	public Vec2 subtract(Vec2 v) {
		return new Vec2(x - v.x, y - v.y);
	}

	public Vec2 scale(float s) {
		return new Vec2(x * s, y * s);
	}

	public Vec2 negate() {
		return scale(-1);
	}

	public Vec2 normalize() {
		float l = length();
		if (MathUtil.isZero(l) || l == 1)
			return this;
		return new Vec2(x / l, y / l);
	}

	public float dot(Vec2 a) {
		return MathUtil.dot(x, y, a.x, a.y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Vec2) {
			final Vec2 v = (Vec2) obj;
			return (x == v.x) && (y == v.y);
		}
		return false;
	}

	@Override
	public float[] toArray() {
		return new float[] { x, y };
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	public static float[] toArray(Collection<Vec2> vectors) {
		if (vectors == null)
			return null;

		float[] result = new float[2 * vectors.size()];
		int i = 0;
		for (Vec2 v : vectors) {
			result[i++] = v.x;
			result[i++] = v.y;
		}
		return result;
	}
}
