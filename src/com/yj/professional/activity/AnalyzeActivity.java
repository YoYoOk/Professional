package com.yj.professional.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author liaoyao
 * �Աȷ���ҳ��
 */
public class AnalyzeActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analyze);
		getActionBar().hide();//����actionbar
	}
	
}
