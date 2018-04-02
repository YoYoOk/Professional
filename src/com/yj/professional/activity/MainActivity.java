package com.yj.professional.activity;

import java.util.Date;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yj.professional.activity.R;
import com.yj.professional.domain.DetectionRecord;
import com.yj.professional.popupwindow.PopWindowSelectCondition;
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
 * ��ҳ��
 */
public class MainActivity extends Activity implements OnClickListener{
	
	private SlidingMenu slidingMenu;
	private ImageButton imgbtn_left_arrow, imgbtn_add_choice_type;//����״̬����λ��
	private Button btn_open_detection_activity, btn_open_analyze_activity, 
					btn_open_patient_activity, btn_open_type_activity, 
					btn_open_setting_activity;//������˵���ť 
	private LinearLayout ll_add_display;
	private int screenWidth, screenHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getActionBar().hide();//����actionbar
		//��ȡ�ֻ���Ļ���
		DisplayMetrics outMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screenWidth = outMetrics.widthPixels;
		screenHeight = outMetrics.heightPixels;
		initWidget();
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);  //�˵�����߻���
		slidingMenu.setBehindWidth((int) (outMetrics.widthPixels * 0.6));        //�˵��Ŀ��
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//�˵�ȫ�����ɻ���
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.layout_left_menu);
		//��ť �¼�
		imgbtn_left_arrow.setOnClickListener(this);
		imgbtn_add_choice_type.setOnClickListener(this);
		btn_open_detection_activity = (Button)findViewById(R.id.btn_open_detection_activity);
		btn_open_analyze_activity = (Button)findViewById(R.id.btn_open_analyze_activity);
		btn_open_patient_activity = (Button)findViewById(R.id.btn_open_patient_activity);
		btn_open_type_activity = (Button)findViewById(R.id.btn_open_type_activity);
		btn_open_setting_activity = (Button)findViewById(R.id.btn_open_setting_activity);
		btn_open_detection_activity.setOnClickListener(this);
		btn_open_analyze_activity.setOnClickListener(this);
		btn_open_patient_activity.setOnClickListener(this); 
		btn_open_type_activity.setOnClickListener(this);
		btn_open_setting_activity.setOnClickListener(this);
	}
	
	private void initWidget() {
		imgbtn_left_arrow = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		imgbtn_add_choice_type = (ImageButton)findViewById(R.id.imgbtn_add_choice_type);
		
		ll_add_display = (LinearLayout)findViewById(R.id.ll_add_display);
		DetectionRecord record = new DetectionRecord("��", "�ʿ��Լ�", new Date());
		DisplayResultView view = new DisplayResultView(this, screenWidth, record);
		ll_add_display.addView(view);
		DetectionRecord record1 = new DetectionRecord("����1", "ȫѪ", new Date());
		DisplayResultView view1 = new DisplayResultView(this, screenWidth, null);
		view1.ll_display_result_view.setBackground(getResources().getDrawable(R.drawable.result_two2));
		ll_add_display.addView(view1);
		DisplayResultView view2 = new DisplayResultView(this, screenWidth, null);
		ll_add_display.addView(view2);
		//���õ���¼�
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,ResultDetailActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left_arrow://��໬�˵����뻬��
			slidingMenu.toggle();
			break;
		case R.id.imgbtn_add_choice_type://���Ͻǵ���popupwindowѡ��鿴���Ͱ�ť
			PopWindowSelectCondition popwindow = new PopWindowSelectCondition(this, screenWidth, screenHeight);
			popwindow.showPopupWindow(findViewById(R.id.imgview_popupwindow));
			break;
		case R.id.btn_open_detection_activity:
			Intent detectionIntent = new Intent(this, DetectionActivity.class);
			startActivity(detectionIntent);
			break;
		case R.id.btn_open_analyze_activity:
			Intent analyzeIntent = new Intent(this, AnalyzeActivity.class);
			startActivity(analyzeIntent);
			break;
		case R.id.btn_open_patient_activity:
			Intent patientIntent = new Intent(this, PatientActivity.class);
			startActivity(patientIntent);
			break;
		case R.id.btn_open_type_activity:
			Intent typeIntent = new Intent(this, TypeActivity.class);
			startActivity(typeIntent);
			break;
		case R.id.btn_open_setting_activity:
			Intent settingIntent = new Intent(this, SettingActivity.class);
			startActivity(settingIntent);
			break;
		default:
			break;
		}
	}
}
