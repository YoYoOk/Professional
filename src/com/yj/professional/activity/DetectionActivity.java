package com.yj.professional.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yj.professional.domain.DetectionRecord;
import com.yj.professional.popupwindow.PopWindowSelectBluetooth;
import com.yj.professional.view.DisplayResultView;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author liaoyao
 * 新建检测页面活动
 */
public class DetectionActivity extends Activity implements OnClickListener{
	private Button btn_start_detection;
	private ImageButton btn_return, btn_bluetooth;
	private Spinner spinner_patient_choice, spinner_type_choice;
	private LinearLayout ll_result_display;
	private ArrayAdapter<String> patientAdapter, typeAdapter;
	private List<String> patientList, typeList;
	private int selectPatientPosition,selectTypePosition;
	private int screenWidth, screenHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detection);
		getActionBar().hide();//隐藏actionbar
		//获取手机屏幕宽度
		DisplayMetrics outMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screenWidth = outMetrics.widthPixels;
		screenHeight = outMetrics.heightPixels;
		initWidget();
		//数据初始化
		initParams();
		patientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, patientList);
		typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeList);
		patientAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		typeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner_patient_choice.setAdapter(patientAdapter);
		spinner_type_choice.setAdapter(typeAdapter);
		DisplayResultView display = new DisplayResultView(this, screenWidth, null);
		ll_result_display.addView(display);
		//按钮事件
		btn_return.setOnClickListener(this);
		btn_bluetooth.setOnClickListener(this);
		btn_start_detection.setOnClickListener(this);
		spinner_patient_choice.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectPatientPosition = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		spinner_type_choice.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectTypePosition = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}
	
	private void initWidget(){
		btn_start_detection = (Button)findViewById(R.id.btn_start_detection);
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		btn_bluetooth = (ImageButton)findViewById(R.id.imgbtn_add_choice_bluetooth);
		spinner_patient_choice = (Spinner)findViewById(R.id.spinner_patient_choice);
		spinner_type_choice = (Spinner)findViewById(R.id.spinner_type_choice);
		ll_result_display = (LinearLayout)findViewById(R.id.ll_result_display);
	}
	private void initParams() {
		patientList = new ArrayList<>();
		patientList.add("hehe");
		patientList.add("xixi");
		patientList.add("heihei");
		patientList.add("haha");
		typeList = new ArrayList<>();
		typeList.add("K高岭土激活");
		typeList.add("KH");
		typeList.add("CK");
		typeList.add("CKH");
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left_arrow:
			finish();
			break;
		case R.id.imgbtn_add_choice_bluetooth:
			PopWindowSelectBluetooth popwindow = new PopWindowSelectBluetooth(this, screenWidth, screenHeight);
			popwindow.showPopupWindow(findViewById(R.id.imgview_popupwindow));
		case R.id.btn_start_detection:
			DetectionRecord record = new DetectionRecord();
			record.setPatientName(patientList.get(selectPatientPosition));
			record.setDetectionType(typeList.get(selectTypePosition));
			record.setDetectionDate(new Date());
			DisplayResultView display = new DisplayResultView(this, screenWidth, record);
			ll_result_display.removeAllViews();
			display.ll_display_result_view.setBackground(getResources().getDrawable(R.drawable.result_one));
			ll_result_display.addView(display);
			break;
		default:
			break;
		}
	}
	
}
