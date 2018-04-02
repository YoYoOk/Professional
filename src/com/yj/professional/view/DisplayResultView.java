package com.yj.professional.view;

import java.text.SimpleDateFormat;

import com.yj.professional.activity.R;
import com.yj.professional.domain.DetectionRecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayResultView extends LinearLayout {
	
	private TextView tv_patient_name, tv_detection_type, tv_detection_date;
	public LinearLayout ll_display_result_view;
	
	public DisplayResultView(Context context) {
		super(context);
	}
	
	public DisplayResultView(Context context,int screenWidth, DetectionRecord record){
		super(context);
		//加载布局
		LayoutInflater.from(context).inflate(R.layout.layout_display_result, this);
		//获取子控件
		tv_patient_name = (TextView)findViewById(R.id.tv_patient_name);
		tv_detection_type = (TextView)findViewById(R.id.tv_detection_type);
		tv_detection_date = (TextView)findViewById(R.id.tv_detection_date);
		ll_display_result_view = (LinearLayout)findViewById(R.id.ll_display_result_view);
		ll_display_result_view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)(screenWidth * 0.6)));
		//动态设置值
		if(record != null){
			if(record.getPatientName() != null && record.getPatientName() != ""){
				tv_patient_name.setText(record.getPatientName());
			}
			if(record.getDetectionType() != null && record.getPatientName() != ""){
				tv_detection_type.setText(record.getDetectionType());
			}
			if(record.getDetectionDate() != null){
				tv_detection_date.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.getDetectionDate()));
			}
		}
	}
}
