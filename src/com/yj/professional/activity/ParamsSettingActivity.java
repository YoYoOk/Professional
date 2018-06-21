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
 * ��������ҳ��
 */
public class ParamsSettingActivity extends Activity implements OnClickListener{
	// ��ԭʼ��ֵ ��ʼƵ�� ��ֹƵ�� ����Ƶ�� ��100 120 50 ����ֵ
	private int start_frq_original;
	private int end_frq_original;
	private int frq_interval_original;
	private int times_original;//����
	private String start_frq; // ��ʼƵ�� �����ֽ�
	private String end_frq; // ��ֹƵ�� �����ֽ�
	private String frq_interval;// ����Ƶ�� 1���ֽ�
	private String frq_time; // Ƶ�ʲ���ʱ�� 1���ֽ�
	private String dianping; // ֱ����ƽ 2���ֽ�
	private String enlarge; // �̿طŴ� 2���ֽ�
	private String times; // ɨ����� 2���ֽ�
	private String interval_time;// ���ɨ��ʱ����
	private EditText et_start_frq; // ��ʼƵ�� �����ֽ�
	private EditText et_end_frq; // ��ֹƵ�� �����ֽ�
	private EditText et_frq_interval;// ����Ƶ�� 1���ֽ�
	private EditText et_frq_time; // Ƶ�ʲ���ʱ�� 1���ֽ�
	private EditText et_dianping; // ֱ����ƽ 2���ֽ�
	private EditText et_enlarge; // �̿طŴ� 2���ֽ�
	private EditText et_times; // ɨ����� 2���ֽ�
	private EditText et_interval_time;// ���ɨ��ʱ����
	//����
	private EditText et_test_times;//���Դ���
	private String resultData;// ������� ������ר��16�����ַ��� ���������ֽڵ����ݵ��ֽ���ǰ �����ֽ��ں�
	private Button btn_confirm_set, btn_cancel_set;
	private SharedPreferences sp;
	private CheckBox cb_save_param;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_params_setting);
		getActionBar().hide();//����actionbar
		init();// ��������������ȡ����
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);////��ѯ�Ƿ���Ĭ�ϵ�����  ȥSharedPreference
		btn_confirm_set.setOnClickListener(this);
		btn_cancel_set.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm_set:
			getData();// �����������ȡ�������� ��ǰ��ʮ������ Ȼ����Ҫ�һ����ֽں͵��ֽ�
			// �û����� Ȼ�� �Ȳ���
			resultData = start_frq + end_frq + frq_interval + frq_time + dianping + enlarge + times + interval_time;
			resultData = "86110101" + resultData + "68";
			// ����һ���������
			Intent intent = new Intent();
			intent.putExtra("params", resultData);
			intent.putExtra("value", new int[] { start_frq_original, end_frq_original, frq_interval_original });
			intent.putExtra("times", times_original);
			setResult(RESULT_OK, intent);
			//����Ĭ��ֵ
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
	// �õ����������
	protected void getData() {
		// ���������ֽڵ���Ҫת��
		start_frq_original = Integer.parseInt(et_start_frq.getText().toString().trim());
		end_frq_original = Integer.parseInt(et_end_frq.getText().toString().trim());
		frq_interval_original = Integer.parseInt(et_frq_interval.getText().toString().trim());
		start_frq = dataConvertHex(et_start_frq.getText().toString().trim() + "00"); // ��ʼƵ��
																						// �����ֽ�
		start_frq = HighExchangeLow(start_frq);
		end_frq = dataConvertHex(et_end_frq.getText().toString().trim() + "00"); // ��ֹƵ��
																					// �����ֽ�
		end_frq = HighExchangeLow(end_frq);
		frq_interval = dataConvertHex(et_frq_interval.getText().toString().trim());// ����Ƶ��
																					// 1���ֽ�
		frq_time = dataConvertHex(et_frq_time.getText().toString().trim()); // Ƶ�ʲ���ʱ��
																			// 1���ֽ�
		dianping = dataConvertHex(et_dianping.getText().toString().trim()); // ֱ����ƽ
																			// 2���ֽ�
		dianping = HighExchangeLow(dianping);
		enlarge = dataConvertHex(et_enlarge.getText().toString().trim()); // �̿طŴ�
																			// 2���ֽ�
		enlarge = HighExchangeLow(enlarge);
		times_original = Integer.parseInt(et_times.getText().toString().trim());
		times = dataConvertHex(et_times.getText().toString().trim()); // ɨ�����
																		// 2���ֽ�
		times = HighExchangeLow(times);
		interval_time = dataConvertHex(et_interval_time.getText().toString().trim());// ���ɨ��ʱ����
		interval_time = HighExchangeLow(interval_time);
		//���Դ���
	}

	// ��ʼ���ؼ�
	private void init() {
		btn_confirm_set = (Button)findViewById(R.id.btn_confirm_set);
		btn_cancel_set = (Button)findViewById(R.id.btn_cancel_set);
		et_start_frq = (EditText) findViewById(R.id.start_frq); // ��ʼƵ�� �����ֽ�
		et_end_frq = (EditText) findViewById(R.id.end_frq); // ��ֹƵ�� �����ֽ�
		et_frq_interval = (EditText) findViewById(R.id.frq_interval);// ����Ƶ��
		et_frq_time = (EditText) findViewById(R.id.frq_time); // Ƶ�ʲ���ʱ�� 1���ֽ�
		et_dianping = (EditText) findViewById(R.id.dianping); // ֱ����ƽ 2���ֽ�
		et_enlarge = (EditText) findViewById(R.id.enlarge); // �̿طŴ� 2���ֽ�
		et_times = (EditText) findViewById(R.id.times); // ɨ����� 2���ֽ�
		et_interval_time = (EditText) findViewById(R.id.interval_time);// ���ɨ��ʱ����
		cb_save_param = (CheckBox)findViewById(R.id.cb_save_param);
//		et_test_times = (EditText) findViewById(R.id.test_times);//���Դ���Ĭ���Ĵξͺ��� 
	}
	/*
	 * ������ת��ʮ������
	 */
	public static String dataConvertHex(String data) {
		String str = Long.toHexString(Long.parseLong(data)).toUpperCase();
		str = str.length() % 2 == 0 ? str : "0" + str;
		return str;
	}

	/*
	 * �����ֽ�ת���ɵ��ֽ�
	 */
	public static String HighExchangeLow(String data) {
		int size = data.length();
		String str = "";
		switch (size) {
		case 2:// ��2���ֽ� ����Ŀǰֻ������1���ֽ���2�� 0200
			str = data + "00";
			break;
		case 4:// �����ֽ� �������ֽں͵��ֽ�
			str = data.substring(2) + data.substring(0, 2);
			break;
		default:
			break;
		}
		return str;
	}
}
