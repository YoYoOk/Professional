package com.yj.professional.domain;


/**
 * @author liaoyao
 * ��дһ��point��   Ŀ�ĵ���Ϊ���ܹ���ʹ��JNI��ʱ��   �ܹ�ʹ��  ����
 */
public class MyPointF{
	public float x;
    public float y;
    
    public MyPointF() {
    	super();
    }

    public MyPointF(float x, float y) {
    	super();
        this.x = x;
        this.y = y; 
    }

	@Override
	public String toString() {
		return "MyPointF [x=" + x + ", y=" + y + "]";
	}
    
}
