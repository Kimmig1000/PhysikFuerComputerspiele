package ch.fhnw.util.math;

import ch.fhnw.util.IFloatArrayCopyProvider;

public interface IVec4 extends IFloatArrayCopyProvider {
	float x();
	float y();
	float z();
	float w();
	
	Vec4 toVec4();
}
