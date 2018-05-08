package com.yj.professional.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yj.professional.activity.R;
import com.yj.professional.activity.SampleActivity.ViewHolder;
import com.yj.professional.database.MyDatabaseHelper;
import com.yj.professional.domain.DetectionRecord;
import com.yj.professional.domain.PatientInformation;
import com.yj.professional.domain.SampleInformation;
import com.yj.professional.popupwindow.PopWindowSelectCondition;
import com.yj.professional.view.DisplayResultView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

/**
 * @author liaoyao
 * ��ҳ��
 */
public class MainActivity extends Activity implements OnClickListener{
	
	public static MyDatabaseHelper dbHelper;//�����ݿⱾӦ�ó������еĶ�Ӧ�ÿ��Է��� �ڴ�����Ϊ��̬��
	private SlidingMenu slidingMenu;
	private ImageButton imgbtn_left_arrow, imgbtn_add_choice_type;//����״̬����λ��
	private Button btn_open_detection_activity, btn_open_analyze_activity, 
					btn_open_patient_activity, btn_open_type_activity, 
					btn_open_setting_activity;//������˵���ť 
	private LinearLayout ll_add_display;
	private ListView lv_result_list;
	private int screenWidth, screenHeight;
	private PopWindowSelectCondition popwindow;
	private boolean[] patientSelect, sampleSelect;
	private List<String> patientList, sampleList;
	private List<DetectionRecord> recordList;
	private AlertDialog dialogPatient, dialogSample;
	private SimpleDateFormat dateFormat;//ʱ���ʽת��
	private DisplayResultAdapter resultAdapter;
	private int pageNum = 0;
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
		try {
			initWidget();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		resultAdapter = new DisplayResultAdapter();
		prepareData(false ,null, null);
		lv_result_list.setAdapter(resultAdapter);
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
	
	
	@Override
	protected void onResume() {
		super.onResume();
		//ÿ��������ʾ��ʱ�� ��ȡ���ݿ��������ݿ�����ˡ�
		//TODO �������1��Ӧ���ж����ݿ���Ƿ������ ����������ȥ��ȡ ��δ���� �Ͳ���ȡ�������������2������һ����̬�ౣ����Щ�б����ݣ�����Ƶ����ȡ���ݿ�
		sampleList = new ArrayList<>();
		patientList = new ArrayList<>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("patient", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				patientList.add(cursor.getString(cursor.getColumnIndex("name")));
			}while(cursor.moveToNext());
		}
		cursor.close();
		Cursor cursor2 = db.query("sample", null, null, null, null, null, null);
		if(cursor2.moveToFirst()){
			do{
				sampleList.add(cursor2.getString(cursor2.getColumnIndex("name")));
			}while(cursor2.moveToNext());
		}
		cursor2.close();
		patientSelect = new boolean[patientList.size()];
		sampleSelect = new boolean[sampleList.size()];
	}
	
	private void prepareData(boolean isAdd, String sql, String[] params){
		recordList = new ArrayList<>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();		
//		Cursor cursor3 = db.query("record", null, null, null, null, null, null);
//		Cursor cursor3 = db.rawQuery("select * from where " + sql, params);
		Cursor cursor3 = db.query("record", null, sql, params, null, null, null, pageNum + ",4");//��ҳ��ѯ
		pageNum++;
		if(!isAdd){
			recordList.removeAll(recordList);
		}
		if(cursor3.moveToFirst()){
			do{
				DetectionRecord record = new DetectionRecord();
				record.setDetectionId(cursor3.getInt(cursor3.getColumnIndex("id")));
				record.setPatientName(cursor3.getString(cursor3.getColumnIndex("name")));
				record.setDetectionType(cursor3.getString(cursor3.getColumnIndex("sample")));
				record.setDetectionDescri(cursor3.getString(cursor3.getColumnIndex("descri")));
				record.setDetectionValue(cursor3.getString(cursor3.getColumnIndex("value")));
				try {
					record.setDetectionDate(dateFormat.parse(cursor3.getString(cursor3.getColumnIndex("createTime"))));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				recordList.add(record);
			}while(cursor3.moveToNext());
		}
		resultAdapter.notifyDataSetChanged();//ÿ�ζ�ȡ֮�� �ͻ�ˢ��
	}
	
	private void initWidget() throws ParseException {
		imgbtn_left_arrow = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		imgbtn_add_choice_type = (ImageButton)findViewById(R.id.imgbtn_add_choice_type);
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		ll_add_display = (LinearLayout)findViewById(R.id.ll_add_display);
		lv_result_list = (ListView)findViewById(R.id.lv_result_list);
		DetectionRecord record = new DetectionRecord("��", "�ʿ��Լ�", dateFormat.parse("2018-01-11 09:36:47"));
		DisplayResultView view = new DisplayResultView(this, screenWidth, record);
//		ll_add_display.addView(view);
//		DetectionRecord record1 = new DetectionRecord("��", "�ʿ��Լ�", dateFormat.parse("2018-01-09 10:25:29"));
//		DisplayResultView view1 = new DisplayResultView(this, screenWidth, record1);
//		view1.getLl_display_result_view().setBackground(getResources().getDrawable(R.drawable.result_two2));
//		ll_add_display.addView(view1);
//		DisplayResultView view2 = new DisplayResultView(this, screenWidth, null);
//		ll_add_display.addView(view2);
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
			popwindow = new PopWindowSelectCondition(this, screenWidth, screenHeight, this);
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
			Intent typeIntent = new Intent(this, SampleActivity.class);
			startActivity(typeIntent);
			break;
		case R.id.btn_open_setting_activity:
			Intent settingIntent = new Intent(this, SettingActivity.class);
			startActivity(settingIntent);
			break;
		case R.id.btn_popup_select_patient:
			popwindow.dismiss();
			Arrays.fill(patientSelect, false);//���������е�ֵ���Ϊfalse
			AlertDialog.Builder builderPatient = new AlertDialog.Builder(this);
			builderPatient.setIcon(R.drawable.ic_launcher);
			builderPatient.setTitle("��ѡ��鿴�Ļ���");
			builderPatient.setMultiChoiceItems(patientList.toArray(new String[patientList.size()]), null, new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					if(isChecked){
						patientSelect[which] = true;
					}else{
						patientSelect[which] = false;
					}
				}
			});
			builderPatient.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int count = 0;
					for(int i = 0; i < patientSelect.length; i++){
						if(patientSelect[i]){
							count++;
						}
					}
					if(count == 0){
						return;
					}
					if(count == patientSelect.length){
						//˵��һ����û��ѡ�� ����˵ȫѡ��  ʲô������
						pageNum = 0;
						prepareData(false, null, null);
						return;
					}
					SQLiteDatabase db = dbHelper.getWritableDatabase();//ֻ��ѯrecordָ�����ߵļ�¼
					String[] selectName = new String[count];
					StringBuilder partSql = new StringBuilder();
					boolean isFirstName = true;
					count = 0;
					for(int i = 0; i < patientSelect.length; i++){
						if(patientSelect[i]){
							if(isFirstName){
								partSql.append("?");
								isFirstName = false;
								selectName[count++] = patientList.get(i);
							}else{
								partSql.append(",?");
								selectName[count++] = patientList.get(i);
							}
						}
					}
					Toast.makeText(MainActivity.this, partSql.toString(), Toast.LENGTH_SHORT).show();
					pageNum = 0;//ÿ��ѡ���ʱ��Ҫ��pageNum�ӵ�һҳ��ʼ
					prepareData(false," name in (" + partSql.toString() +")", selectName);
//					Cursor cursor = db.query("record", null, "where name in ("+ partSql.toString() +")", selectName, null, null, null);
//					if(cursor.moveToFirst()){
//						do{
//							cursor.getString(cursor.getColumnIndex("name"));
//						}while(cursor.moveToNext());
//					}
				}
			});
			builderPatient.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialogPatient.cancel();
				}
			});
			dialogPatient = builderPatient.show();
			break;
		case R.id.btn_popup_select_sample:
			popwindow.dismiss();
			AlertDialog.Builder builderSample = new AlertDialog.Builder(this);
			builderSample.setIcon(R.drawable.ic_launcher);
			builderSample.setTitle("��ѡ��鿴������");
			builderSample.setMultiChoiceItems(sampleList.toArray(new String[sampleList.size()]), null, new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					if(isChecked){
						sampleSelect[which] = true;
					}else{
						sampleSelect[which] = false;
					}
				}
			});
			builderSample.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int count = 0;
					for(int i = 0; i < sampleSelect.length; i++){
						if(sampleSelect[i]){
							count++;
						}
					}
					if(count == 0 || count == sampleSelect.length){
						//˵��һ����û��ѡ�� ����˵ȫѡ��  ʲô������
						return;
					}
					SQLiteDatabase db = dbHelper.getWritableDatabase();//ֻ��ѯrecordָ�����ߵļ�¼
					
//					db.query("record", null, "where name in ('?','?','?')", null, null, null, null);
				}
			});
			builderSample.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialogSample.cancel();
				}
			});
			dialogSample = builderSample.show();
			break;
		case R.id.btn_popup_select_date:
			
			popwindow.dismiss();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbHelper.getWritableDatabase().close();
	}
	
	class DisplayResultAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return recordList.size();
		}

		@Override
		public Object getItem(int position) {
			return recordList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
            if (null == convertView) {
                convertView = View.inflate(MainActivity.this, R.layout.layout_display_result, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_patient_name = (TextView)convertView.findViewById(R.id.tv_patient_name);
                viewHolder.tv_detection_type = (TextView)convertView.findViewById(R.id.tv_detection_type);
                viewHolder.tv_detection_date = (TextView)convertView.findViewById(R.id.tv_detection_date);
                viewHolder.ll_display_result_view = (LinearLayout)convertView.findViewById(R.id.ll_display_result_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_patient_name.setText(recordList.get(position).getPatientName());
            viewHolder.tv_detection_type.setText(recordList.get(position).getDetectionType());
            viewHolder.tv_detection_date.setText(dateFormat.format(recordList.get(position).getDetectionDate()));
            viewHolder.ll_display_result_view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)(screenWidth * 0.6)));
            //��ͼ  ÿ������ͼ
            final int pos = position;
            
            return convertView;
		}
		
	}
	
	class ViewHolder {
        public TextView tv_patient_name;
        public TextView tv_detection_type;
        public TextView tv_detection_date;
    	public LinearLayout ll_display_result_view;
    }
	
}
