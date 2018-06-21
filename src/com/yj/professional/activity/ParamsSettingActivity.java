package com.yj.professional.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * @author liaoyao
 * 参数设置页面活动
 */
public class ParamsSettingActivity extends Activity implements OnClickListener{
	// 最原始的值 开始频率 截止频率 步进频率 即100 120 50 的数值
	private int start_frq_original;
	private int end_frq_original;
	private int frq_interval_original;
	private int times_original;//次数
	private String start_frq; // 开始频率 两个字节
	private String end_frq; // 截止频率 两个字节
	private String frq_interval;// 步进频率 1个字节
	private String frq_time; // 频率步进时间 1个字节
	private String dianping; // 直流电平 2个字节
	private String enlarge; // 程控放大 2个字节
	private String times; // 扫描次数 2个字节
	private String interval_time;// 多次扫描时间间隔
	private EditText et_start_frq; // 开始频率 两个字节
	private EditText et_end_frq; // 截止频率 两个字节
	private EditText et_frq_interval;// 步进频率 1个字节
	private EditText et_frq_time; // 频率步进时间 1个字节
	private EditText et_dianping; // 直流电平 2个字节
	private EditText et_enlarge; // 程控放大 2个字节
	private EditText et_times; // 扫描次数 2个字节
	private EditText et_interval_time;// 多次扫描时间间隔
	//新增
	private EditText et_test_times;//测试次数
	private String resultData;// 结果数据 将数据专场16进制字符串 并且两个字节的数据低字节在前 ，高字节在后
	private Button btn_confirm_set, btn_cancel_set;
	private SharedPreferences sp;
	private CheckBox cb_save_param;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_params_setting);
		getActionBar().hide();//隐藏actionbar
		init();// 将参数配置数据取出来
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);////查询是否有默认的蓝牙  去SharedPreference
		btn_confirm_set.setOnClickListener(this);
		btn_cancel_set.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm_set:
			getData();// 将输入的数据取出来出来 当前是十六进制 然后需要兑换高字节和低字节
			// 置换好了 然后 先测试
			resultData = start_frq + end_frq + frq_interval + frq_time + dianping + enlarge + times + interval_time;
			resultData = "86110101" + resultData + "68";
			// 给上一个活动传数据
			Intent intent = new Intent();
			intent.putExtra("params", resultData);
			intent.putExtra("value", new int[] { start_frq_original, end_frq_original, frq_interval_original });
			intent.putExtra("times", times_original);
			setResult(RESULT_OK, intent);
			//保存默认值
			if(cb_save_param.isChecked()){
				Editor editor = sp.edit();
				editor.putString("defaultParams", resultData);
				editor.commit();
			}
			this.finish();
			break;
		case R.id.btn_cancel_set:
			this.finish();
			break;
		default:
			break;
		}
	}
	// 得到输入的数据
	protected void getData() {
		// 发送两个字节的需要转换
		start_frq_original = Integer.parseInt(et_start_frq.getText().toString().trim());
		end_frq_original = Integer.parseInt(et_end_frq.getText().toString().trim());
		frq_interval_original = Integer.parseInt(et_frq_interval.getText().toString().trim());
		start_frq = dataConvertHex(et_start_frq.getText().toString().trim() + "00"); // 开始频率
																						// 两个字节
		start_frq = HighExchangeLow(start_frq);
		end_frq = dataConvertHex(et_end_frq.getText().toString().trim() + "00"); // 截止频率
																					// 两个字节
		end_frq = HighExchangeLow(end_frq);
		frq_interval = dataConvertHex(et_frq_interval.getText().toString().trim());// 步进频率
																					// 1个字节
		frq_time = dataConvertHex(et_frq_time.getText().toString().trim()); // 频率步进时间
																			// 1个字节
		dianping = dataConvertHex(et_dianping.getText().toString().trim()); // 直流电平
																			// 2个字节
		dianping = HighExchangeLow(dianping);
		enlarge = dataConvertHex(et_enlarge.getText().toString().trim()); // 程控放大
																			// 2个字节
		enlarge = HighExchangeLow(enlarge);
		times_original = Integer.parseInt(et_times.getText().toString().trim());
		times = dataConvertHex(et_times.getText().toString().trim()); // 扫描次数
																		// 2个字节
		times = HighExchangeLow(times);
		interval_time = dataConvertHex(et_interval_time.getText().toString().trim());// 多次扫描时间间隔
		interval_time = HighExchangeLow(interval_time);
		//测试次数
	}

	// 初始化控件
	private void init() {
		btn_confirm_set = (Button)findViewById(R.id.btn_confirm_set);
		btn_cancel_set = (Button)findViewById(R.id.btn_cancel_set);
		et_start_frq = (EditText) findViewById(R.id.start_frq); // 开始频率 两个字节
		et_end_frq = (EditText) findViewById(R.id.end_frq); // 截止频率 两个字节
		et_frq_interval = (EditText) findViewById(R.id.frq_interval);// 步进频率
		et_frq_time = (EditText) findViewById(R.id.frq_time); // 频率步进时间 1个字节
		et_dianping = (EditText) findViewById(R.id.dianping); // 直流电平 2个字节
		et_enlarge = (EditText) findViewById(R.id.enlarge); // 程控放大 2个字节
		et_times = (EditText) findViewById(R.id.times); // 扫描次数 2个字节
		et_interval_time = (EditText) findViewById(R.id.interval_time);// 多次扫描时间间隔
		cb_save_param = (CheckBox)findViewById(R.id.cb_save_param);
//		et_test_times = (EditText) findViewById(R.id.test_times);//测试次数默认四次就好了 
	}
	/*
	 * 将数据转成十六进制
	 */
	public static String dataConvertHex(String data) {
		String str = Long.toHexString(Long.parseLong(data)).toUpperCase();
		str = str.length() % 2 == 0 ? str : "0" + str;
		return str;
	}

	/*
	 * 将高字节转换成低字节
	 */
	public static String HighExchangeLow(String data) {
		int size = data.length();
		String str = "";
		switch (size) {
		case 2:// 即2个字节 但是目前只有输入1个字节如2次 0200
			str = data + "00";
			break;
		case 4:// 两个字节 调换高字节和低字节
			str = data.substring(2) + data.substring(0, 2);
			break;
		default:
			break;
		}
		return str;
	}
}
