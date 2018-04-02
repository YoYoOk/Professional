package com.yj.professional.activity;

import com.yj.professional.view.DisplayResultView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * @author liaoyao
 * 登录页面活动
 */
public class ResultDetailActivity extends Activity implements OnClickListener{
	
	private ImageButton btn_return;
	private LinearLayout ll_result_display;
	private int screenWidth, screenHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_detail);
		getActionBar().hide();//隐藏actionbar
		//获取手机屏幕宽度
		DisplayMetrics outMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screenWidth = outMetrics.widthPixels;
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		ll_result_display = (LinearLayout)findViewById(R.id.ll_result_display);
		DisplayResultView view = new DisplayResultView(this, screenWidth, null);
		view.ll_display_result_view.setBackground(getResources().getDrawable(R.drawable.result_two_param));
		ll_result_display.addView(view);
		btn_return.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left_arrow:
			this.finish();
			break;
		default:
			break;
		}
	}
	
}
