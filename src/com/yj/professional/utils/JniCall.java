package com.yj.professional.utils;

public class JniCall {
	//Start    Java��JNI����
  	static{
  		System.loadLibrary("CALLC");
  	}
  	public native double[] process_Data(double[] source);//���ط��� �������˲�������㷨
  	//End      Java��JNI����
}
