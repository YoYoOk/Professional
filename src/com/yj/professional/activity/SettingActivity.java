package com.yj.professional.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * @author liaoyao
 * 用户配置页面活动
 */
public class SettingActivity extends Activity implements OnClickListener{
	private ImageButton btn_return;
	private Button btn_open_update_password_view, btn_confirm_update_password,
					btn_cancel_update_password;
	private LinearLayout ll_update_password_view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		getActionBar().hide();//隐藏actionbar
		initWidget();
		btn_return.setOnClickListener(this);
		btn_open_update_password_view.setOnClickListener(this);
		btn_confirm_update_password.setOnClickListener(this);
		btn_cancel_update_password.setOnClickListener(this);
	}
	
	private void initWidget() {
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		btn_open_update_password_view = (Button)findViewById(R.id.btn_open_update_password_view);
		ll_update_password_view = (LinearLayout)findViewById(R.id.ll_update_password_view);
		btn_confirm_update_password = (Button)findViewById(R.id.btn_confirm_update_password);
		btn_cancel_update_password = (Button)findViewById(R.id.btn_cancel_update_password);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left_arrow:
			this.finish();
			break;
		case R.id.btn_open_update_password_view:
			ll_update_password_view.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_confirm_update_password:
			ll_update_password_view.setVisibility(View.INVISIBLE);
			break;
		case R.id.btn_cancel_update_password:
			ll_update_password_view.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
	}
	
}
