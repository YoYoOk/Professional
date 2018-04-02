package com.yj.professional.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author liaoyao
 * 参数设置页面活动
 */
public class ParamsSettingActivity extends Activity implements OnClickListener{
	
	
	
	private Button btn_confirm_set, btn_cancel_set;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_params_setting);
		getActionBar().hide();//隐藏actionbar
		btn_confirm_set = (Button)findViewById(R.id.btn_confirm_set);
		btn_cancel_set = (Button)findViewById(R.id.btn_cancel_set);
		btn_confirm_set.setOnClickListener(this);
		btn_cancel_set.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm_set:
			this.finish();
			break;
		case R.id.btn_cancel_set:
			this.finish();
			break;
		default:
			break;
		}
	}
	
}
