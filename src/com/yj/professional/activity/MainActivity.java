package com.yj.professional.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.achartengine.GraphicalView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yj.professional.activity.R;
import com.yj.professional.database.MyDatabaseHelper;
import com.yj.professional.domain.DetectionRecord;
import com.yj.professional.popupwindow.PopWindowSelectCondition;
import com.yj.professional.service.ChartService;
import com.yj.professional.utils.JniCall;
import com.yj.professional.utils.SaveActionUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
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
public class MainActivity extends Activity implements OnClickListener, OnScrollListener{
	
	public static MyDatabaseHelper dbHelper;//�����ݿⱾӦ�ó������еĶ�Ӧ�ÿ��Է��� �ڴ�����Ϊ��̬��
	private SlidingMenu slidingMenu;
	private ImageButton imgbtn_left_arrow, imgbtn_add_choice_type;//����״̬����λ��
	private Button btn_open_detection_activity, btn_open_analyze_activity, 
					btn_open_patient_activity, btn_open_type_activity, 
					btn_open_setting_activity;//������˵���ť 
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
	private List<Double> xList_time;//x�����ʾʱ��
	private List<Double> yList_value;//y�����ʾ��ֵ
//	private GraphicalView mView_result;//���View
//	private ChartService mChartService_result;
	
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
		dbHelper.getWritableDatabase();
		try {
			initWidget();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		initDrawLine();
//		try {
//			prepareSqlite();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}//׼�����ݿ� ����֮ǰ���ʿ�����
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
//		btn_open_analyze_activity = (Button)findViewById(R.id.btn_open_analyze_activity);
		btn_open_patient_activity = (Button)findViewById(R.id.btn_open_patient_activity);
		btn_open_type_activity = (Button)findViewById(R.id.btn_open_type_activity);
		btn_open_setting_activity = (Button)findViewById(R.id.btn_open_setting_activity);
		btn_open_detection_activity.setOnClickListener(this);
//		btn_open_analyze_activity.setOnClickListener(this);
		btn_open_patient_activity.setOnClickListener(this); 
		btn_open_type_activity.setOnClickListener(this);
		btn_open_setting_activity.setOnClickListener(this);
		lv_result_list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setTitle("ȷ��ɾ����¼ ��");
				builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//TODO ɾ����Ӧ�����ݿ�ļ�¼
						boolean result = SaveActionUtils.deleteFile(SaveActionUtils.getExcelDir() + File.separator + dateFormat.format(recordList.get(position).getDetectionDate()) + "_Result.csv");
						if(result){
							deleteRecordById(recordList.get(position).getDetectionId());
						}
						SaveActionUtils.deleteFile(SaveActionUtils.getExcelDir() + File.separator + dateFormat.format(recordList.get(position).getDetectionDate()) + "_Source.csv");
						if(result){
							recordList.remove(position);
							resultAdapter.notifyDataSetChanged();
						}
					}
				});
				builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
				return false;
			}
		});
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
	
	private void prepareSqlite() throws ParseException{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("delete from record");
		ContentValues values = new ContentValues();
		values.put("name", "��");
		values.put("sample", "�ʿ��Լ�");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
		values.put("createTime", format.format(format.parse("2018-01-09 14_29_35")));
		long count = db.insert("record", null, values);
		Log.e("#####", count + "");//��Ϊ������������֮�� �޸Ľ�� ���ڵĽ����Ϊ��//��ȡ�²������ݵ�id
		values.clear();
		values.put("name", "��");
		values.put("sample", "�ʿ��Լ�");
		values.put("createTime", format.format(format.parse("2018-01-22 14_15_29")));
		db.insert("record", null, values);
		values.clear();
		values.put("name", "��");
		values.put("sample", "�ʿ��Լ�");
		values.put("createTime", format.format(format.parse("2018-01-25 15_14_02")));
		db.insert("record", null, values);
		values.clear();
		values.put("name", "��");
		values.put("sample", "�ʿ��Լ�");
		values.put("createTime", format.format(format.parse("2018-01-25 15_44_19")));
		db.insert("record", null, values);
		values.clear();
		values.put("name", "��");
		values.put("sample", "�ʿ��Լ�");
		values.put("createTime", format.format(format.parse("2018-01-25 16_12_53")));
		db.insert("record", null, values);
		
		db.execSQL("delete from patient");
		values.clear();
		values.put("patientId", "S15478654");
		values.put("name", "Jack");
		db.insert("patient", null, values);
		values.clear();
		values.put("patientId", "S88888888");
		values.put("name", "��");
		db.insert("patient", null, values);
		values.clear();
		values.put("patientId", "S00000001");
		values.put("name", "��");
		db.insert("patient", null, values);
		
		db.execSQL("delete from sample");
		values.clear();
		values.put("name", "�ʿ��Լ�");
		db.insert("sample", null, values);
		values.clear();
		values.put("name", "����");
		db.insert("sample", null, values);
		values.clear();
		values.put("name", "ȫѪ");
		db.insert("sample", null, values);
		values.clear();
		values.put("name", "�����������ȫѪ");
		db.insert("sample", null, values);
		
	}
	
	private void prepareData(boolean isAdd, String sql, String[] params){
		recordList = new ArrayList<>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();		
//		Cursor cursor3 = db.query("record", null, null, null, null, null, null);
//		Cursor cursor3 = db.rawQuery("select * from where " + sql, params);
		Cursor cursor3 = db.query("record", null, sql, params, null, null, null, pageNum + ",40");//��ҳ��ѯ
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
				Log.e("��������", record.toString());
				//��ȥ�鿴�Ƿ��ж�Ӧ��Դ�ļ�  Ȼ������ӵ��б���
				String tempPath = SaveActionUtils.getExcelDir() + File.separator + dateFormat.format(record.getDetectionDate()) + "_Result.csv";
				if(judgeHasFile(tempPath)
						&& judgeHasFile(SaveActionUtils.getExcelDir() + File.separator + dateFormat.format(record.getDetectionDate()) + "_Source.csv")){
					Log.e("#####", record.toString());
					recordList.add(record);
				}
			}while(cursor3.moveToNext());
		}
		resultAdapter.notifyDataSetChanged();//ÿ�ζ�ȡ֮�� �ͻ�ˢ��
	}
	
	private void initDrawLine(){
		xList_time = new ArrayList<Double>();
		yList_value = new ArrayList<Double>();
	}
	
	private void initWidget() throws ParseException {
		imgbtn_left_arrow = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		imgbtn_add_choice_type = (ImageButton)findViewById(R.id.imgbtn_add_choice_type);
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
		lv_result_list = (ListView)findViewById(R.id.lv_result_list);
	}
	
	private boolean judgeHasFile(String path){
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public void readCSV(String path){
//		xList_time.removeAll(xList_time);
//		yList_value.removeAll(yList_value);
//		List<String> tempList = SaveActionUtils.importCsv(new File(path));
//		int tempIndex = 0;
//		double[] source = new double[tempList.size()];
//		for(String str : tempList){
//			xList_time.add(Double.parseDouble(str.split(",")[0]));
//			source[tempIndex++] = Double.parseDouble(str.split(",")[1]);
//		}
//		source = new JniCall().process_Data(source);
//		for(int i = 0; i < source.length; i++){
//			yList_value.add(source[i]);
//		}//yList��yListFilter�д洢�Ķ����˲�֮������ݣ���yList��ȥ�����ߣ�yListFilter��ȥ����
//		mChartService_result = new ChartService(MainActivity.this);
//		mChartService_result.setXYMultipleSeriesDataset("��Ѫ����");
//		mChartService_result.setXYMultipleSeriesRenderer("ʱ��", "ֵ");
////		mChartService_result.getRenderer().setXAxisMax(Collections.max(xList_time));
////		mChartService_result.getRenderer().setYAxisMax(Collections.max(yList_value) + Collections.max(yList_value)/30);
////		mChartService_result.getRenderer().setYAxisMin(Collections.min(yList_value) - Collections.min(yList_value)/30);
//		mView_result = mChartService_result.getGraphicalView();
//		mChartService_result.getRenderer().setClickEnabled(false);
//		mChartService_result.getRenderer().setPanEnabled(false);
////		mChartService_result.clearValue();
//		Log.e("xList_time", xList_time.size() + "");
//		mChartService_result.updateChart(xList_time, yList_value);
//		return tempView;
	}
	
	private boolean deleteRecordById(Integer id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result = db.delete("record", "id = ?", new String[] {id.toString()});
		if(result > 0 ){
			return true;
		}
		return false;
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
		/*case R.id.btn_open_analyze_activity:
			Intent analyzeIntent = new Intent(this, AnalyzeActivity.class);
			startActivity(analyzeIntent);
			break;*/
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
//					Toast.makeText(MainActivity.this, partSql.toString(), Toast.LENGTH_SHORT).show();
					pageNum = 0;//ÿ��ѡ���ʱ��Ҫ��pageNum�ӵ�һҳ��ʼ
					prepareData(false," name in (" + partSql.toString() +")", selectName);
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
			Arrays.fill(sampleSelect, false);//���������е�ֵ���Ϊfalse
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
					if(count == 0){
						//˵��һ����û��ѡ�� ����˵ȫѡ��  ʲô������
						return;
					}
					if(count == sampleSelect.length){
						pageNum = 0;
						prepareData(false, null, null);
						return;
					}
					String[] selectSampName = new String[count];
					StringBuilder partSqlSample = new StringBuilder();
					boolean isFirstSample = true;
					count = 0;
					for (int i = 0; i < sampleSelect.length; i++) {
						if (sampleSelect[i]) {
							if(isFirstSample){
								partSqlSample.append("?");
								isFirstSample = false;
								selectSampName[count++] = sampleList.get(i);
							}else{
								partSqlSample.append(",?");
								selectSampName[count++] = sampleList.get(i);
							}
						}
					}
					pageNum = 0;
					prepareData(false, "sample in (" + partSqlSample.toString() + ")", selectSampName);
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
		/*case R.id.btn_popup_select_date:
			
			popwindow.dismiss();
			break;*/
		default:
			break;
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
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
            viewHolder.tv_detection_date.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(recordList.get(position).getDetectionDate()));
            viewHolder.ll_display_result_view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)(screenWidth * 0.6)));
//            Log.e("###", SaveActionUtils.getExcelDir() + 
//							File.separator + dateFormat.format(recordList.get(position).getDetectionDate()) + "_Result.csv");
//            readCSV(SaveActionUtils.getExcelDir() + 
//							File.separator + dateFormat.format(recordList.get(position).getDetectionDate()) + "_Result.csv");
            xList_time.removeAll(xList_time);
    		yList_value.removeAll(yList_value);
    		List<String> tempList = SaveActionUtils.importCsv(new File(SaveActionUtils.getExcelDir() + 
					File.separator + dateFormat.format(recordList.get(position).getDetectionDate()) + "_Result.csv"));
    		int tempSize = tempList.size();
    		if(tempSize > 400){
    			int tempIndex = 0;
        		double[] source = new double[tempSize];
        		for(String str : tempList){
        			xList_time.add(Double.parseDouble(str.split(",")[0]));
        			source[tempIndex++] = Double.parseDouble(str.split(",")[1]);
        		}
        		source = new JniCall().process_Data(source);
        		for(int i = 0; i < source.length; i++){
        			yList_value.add(source[i]);
        		}//yList��yListFilter�д洢�Ķ����˲�֮������ݣ���yList��ȥ�����ߣ�yListFilter��ȥ����
    		}else{
    			for(String str : tempList){
        			xList_time.add(Double.parseDouble(str.split(",")[0]));
        			yList_value.add(Double.parseDouble(str.split(",")[1]));
        		}
    		}
    		ChartService mChartService_result = new ChartService(MainActivity.this);
    		mChartService_result.setXYMultipleSeriesDataset("��Ѫ����");
    		mChartService_result.setXYMultipleSeriesRenderer("ʱ��", "ֵ");
    		mChartService_result.getRenderer().setClickEnabled(false);
    		mChartService_result.getRenderer().setPanEnabled(false);
    		if(tempSize < 201){
    			mChartService_result.getRenderer().setXAxisMax(200d);
    			mChartService_result.getRenderer().setYAxisMax(yList_value.get(0) + 0.01);
    			mChartService_result.getRenderer().setYAxisMin(yList_value.get(0) - 0.01);
    		}
    		GraphicalView mView_result = mChartService_result.getGraphicalView();
    		mChartService_result.updateChart(xList_time, yList_value);
    		viewHolder.ll_display_result_view.removeAllViews();//����Ϊ��һ�� ������һ���� ����
            viewHolder.ll_display_result_view.addView(mView_result, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            
            //��ͼ  ÿ������ͼ
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
