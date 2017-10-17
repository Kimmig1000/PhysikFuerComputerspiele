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

/**
 * Created by radar on 05/12/13.
 */
// MV = T * R * S * T-Origin
// MN = (R * S).inverse().transpose()
public class Transform implements ITransformable {
	private Vec3 origin = Vec3.ZERO;
	private Vec3 translation = Vec3.ZERO;
	private Vec3 rotation = Vec3.ZERO;
	private Vec3 scale = Vec3.ONE;

	private Mat4 vertexTransform;
	private Mat3 normalTransform;

	@Override
	public Vec3 getOrigin() {
		return origin;
	}

	@Override
	public void setOrigin(Vec3 origin) {
		this.origin = origin;
	}

	@Override
	public Vec3 getTranslation() {
		return translation;
	}

	@Override
	public void setTranslation(Vec3 translation) {
		this.translation = translation;
		vertexTransform = null;
	}

	@Override
	public Vec3 getRotation() {
		return rotation;
	}

	@Override
	public void setRotation(Vec3 rotation) {
		this.rotation = rotation;
		vertexTransform = null;
		normalTransform = null;
	}

	@Override
	public Vec3 getScale() {
		return scale;
	}

	@Override
	public void setScale(Vec3 scale) {
		this.scale = scale;
		vertexTransform = null;
		normalTransform = null;
	}

	public float[] transformVertices(float[] vertices) {
		validateVertexTransform(origin);
		return vertexTransform.transform(vertices);
	}

	public float[] transformNormals(float[] normals) {
		validateNormalTransform();
		return normalTransform.transform(normals);
	}

	private void validateVertexTransform(Vec3 origin) {
		if (vertexTransform == null) {
			vertexTransform = Mat4.multiply(Mat4.translate(translation), 
											Mat4.rotate(rotation.x, Vec3.X), Mat4.rotate(rotation.y, Vec3.Y), Mat4.rotate(rotation.z, Vec3.Z),
											Mat4.scale(scale),
											Mat4.translate(origin.negate()));
		}
	}

	private void validateNormalTransform() {
		if (normalTransform == null) {
			normalTransform = Mat3.multiply(Mat3.rotate(rotation.x, Vec3.X), Mat3.rotate(rotation.y, Vec3.Y), Mat3.rotate(rotation.z, Vec3.Z), Mat3.scale(scale));
			normalTransform = normalTransform.inverse().transpose();
		}
	}

	@Override
	public String toString() {
		validateVertexTransform(origin);
		return vertexTransform.toString();
	}
}
