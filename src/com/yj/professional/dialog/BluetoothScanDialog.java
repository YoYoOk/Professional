package com.yj.professional.dialog;

import com.yj.professional.activity.R;
import com.yj.professional.domain.PatientInformation;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class BluetoothScanDialog extends Dialog {
	
	private Activity context;
	private ListView lv_bluetooth_scan_result;
	private ProgressBar pb_scan;
	private OnItemClickListener mItemClickListener;
	public BluetoothScanDialog(Activity context) {
		super(context);
		this.context = context;
	}
	
	public BluetoothScanDialog(Activity context, int theme, OnItemClickListener mItemClickListener) {
		super(context, theme);
		this.context = context;
		this.mItemClickListener = mItemClickListener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_bluetooth_scan);
		lv_bluetooth_scan_result = (ListView)findViewById(R.id.lv_bluetooth_scan_result);
		pb_scan = (ProgressBar)findViewById(R.id.pb_scan);
		/* 
         * ��ȡʥ����Ĵ��ڶ��󼰲����������޸ĶԻ���Ĳ�������, ����ֱ�ӵ���getWindow(),��ʾ������Activity��Window 
         * ����,�����������ͬ���ķ�ʽ�ı����Activity������. 
         */  
        Window dialogWindow = this.getWindow();  
  
        WindowManager m = context.getWindowManager();  
        Display d = m.getDefaultDisplay(); // ��ȡ��Ļ������  
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // ��ȡ�Ի���ǰ�Ĳ���ֵ 
		DisplayMetrics outMetrics = new DisplayMetrics();
		m.getDefaultDisplay().getMetrics(outMetrics);
        // p.height = (int) (d.getHeight() * 0.6); // �߶�����Ϊ��Ļ��0.6  
        p.width = (int) (outMetrics.widthPixels * 0.8); // �������Ϊ��Ļ��0.8 
        dialogWindow.setAttributes(p); 
        //��ť����¼�
        lv_bluetooth_scan_result.setOnItemClickListener(mItemClickListener);
        this.setCancelable(true); 
	}

	public ListView getLv_bluetooth_scan_result() {
		return lv_bluetooth_scan_result;
	}

	public ProgressBar getPb_scan() {
		return pb_scan;
	}
	
	
}
