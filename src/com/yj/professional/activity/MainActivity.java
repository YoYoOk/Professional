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
 * 主页面活动
 */
public class MainActivity extends Activity implements OnClickListener{
	
	public static MyDatabaseHelper dbHelper;//此数据库本应用程序所有的都应该可以访问 在此设置为静态的
	private SlidingMenu slidingMenu;
	private ImageButton imgbtn_left_arrow, imgbtn_add_choice_type;//顶部状态栏的位置
	private Button btn_open_detection_activity, btn_open_analyze_activity, 
					btn_open_patient_activity, btn_open_type_activity, 
					btn_open_setting_activity;//侧边栏菜单按钮 
	private LinearLayout ll_add_display;
	private ListView lv_result_list;
	private int screenWidth, screenHeight;
	private PopWindowSelectCondition popwindow;
	private boolean[] patientSelect, sampleSelect;
	private List<String> patientList, sampleList;
	private List<DetectionRecord> recordList;
	private AlertDialog dialogPatient, dialogSample;
	private SimpleDateFormat dateFormat;//时间格式转换
	private DisplayResultAdapter resultAdapter;
	private int pageNum = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getActionBar().hide();//隐藏actionbar
		//获取手机屏幕宽度
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
		slidingMenu.setMode(SlidingMenu.LEFT);  //菜单从左边滑出
		slidingMenu.setBehindWidth((int) (outMetrics.widthPixels * 0.6));        //菜单的宽度
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//菜单全屏都可滑出
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.layout_left_menu);
		//按钮 事件
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
		//每次重新显示的时候 读取数据库以免数据库更改了。
		//TODO 解决方案1：应该判断数据库表是否更改了 若更改了再去读取 若未更改 就不读取。。。解决方案2：创建一个静态类保存这些列表数据，以免频繁读取数据库
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
		Cursor cursor3 = db.query("record", null, sql, params, null, null, null, pageNum + ",4");//分页查询
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
		resultAdapter.notifyDataSetChanged();//每次读取之后 就会刷新
	}
	
	private void initWidget() throws ParseException {
		imgbtn_left_arrow = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		imgbtn_add_choice_type = (ImageButton)findViewById(R.id.imgbtn_add_choice_type);
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		ll_add_display = (LinearLayout)findViewById(R.id.ll_add_display);
		lv_result_list = (ListView)findViewById(R.id.lv_result_list);
		DetectionRecord record = new DetectionRecord("无", "质控试剂", dateFormat.parse("2018-01-11 09:36:47"));
		DisplayResultView view = new DisplayResultView(this, screenWidth, record);
//		ll_add_display.addView(view);
//		DetectionRecord record1 = new DetectionRecord("无", "质控试剂", dateFormat.parse("2018-01-09 10:25:29"));
//		DisplayResultView view1 = new DisplayResultView(this, screenWidth, record1);
//		view1.getLl_display_result_view().setBackground(getResources().getDrawable(R.drawable.result_two2));
//		ll_add_display.addView(view1);
//		DisplayResultView view2 = new DisplayResultView(this, screenWidth, null);
//		ll_add_display.addView(view2);
		//设置点击事件
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
		case R.id.imgbtn_left_arrow://左侧滑菜单滑入滑出
			slidingMenu.toggle();
			break;
		case R.id.imgbtn_add_choice_type://右上角弹出popupwindow选择查看类型按钮
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
			Arrays.fill(patientSelect, false);//将数组所有的值填充为false
			AlertDialog.Builder builderPatient = new AlertDialog.Builder(this);
			builderPatient.setIcon(R.drawable.ic_launcher);
			builderPatient.setTitle("请选择查看的患者");
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
			builderPatient.setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
						//说明一个都没有选择 或者说全选了  什么都不做
						pageNum = 0;
						prepareData(false, null, null);
						return;
					}
					SQLiteDatabase db = dbHelper.getWritableDatabase();//只查询record指定患者的记录
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
					pageNum = 0;//每次选择的时候都要让pageNum从第一页开始
					prepareData(false," name in (" + partSql.toString() +")", selectName);
//					Cursor cursor = db.query("record", null, "where name in ("+ partSql.toString() +")", selectName, null, null, null);
//					if(cursor.moveToFirst()){
//						do{
//							cursor.getString(cursor.getColumnIndex("name"));
//						}while(cursor.moveToNext());
//					}
				}
			});
			builderPatient.setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
			builderSample.setTitle("请选择查看的样本");
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
			builderSample.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int count = 0;
					for(int i = 0; i < sampleSelect.length; i++){
						if(sampleSelect[i]){
							count++;
						}
					}
					if(count == 0 || count == sampleSelect.length){
						//说明一个都没有选择 或者说全选了  什么都不做
						return;
					}
					SQLiteDatabase db = dbHelper.getWritableDatabase();//只查询record指定患者的记录
					
//					db.query("record", null, "where name in ('?','?','?')", null, null, null, null);
				}
			});
			builderSample.setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
            //绘图  每单个绘图
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
