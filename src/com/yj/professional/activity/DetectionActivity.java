package com.yj.professional.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.achartengine.GraphicalView;

import com.yj.professional.bluetooth.BluetoothLeService;
import com.yj.professional.dialog.BluetoothScanDialog;
import com.yj.professional.domain.MyPointF;
import com.yj.professional.popupwindow.PopWindowSelectBluetooth;
import com.yj.professional.service.ChartService;
import com.yj.professional.utils.ConvertUtils;
import com.yj.professional.utils.JniCall;
import com.yj.professional.utils.SaveActionUtils;
import com.yj.professional.view.DisplayResultView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author liaoyao
 * �½����ҳ��
 */
public class DetectionActivity extends Activity implements OnClickListener, OnItemClickListener{
	private static final int REQUEST_ENABLE_BT = 1;//ѡ���Ƿ�������Ի���
	private static final int REQUEST_PARAM = 2;//�����������
	private boolean isHasTemp = true;
	private Button btn_start_detection, btn_stop_detection;//��ʼ��ⰴť
	private ImageButton btn_return, btn_bluetooth;//�������ذ�ť�������������ڰ�ť
	private EditText et_detect_descri;//��ǰ�������
	private Spinner spinner_patient_choice, spinner_type_choice;//������
	private LinearLayout ll_result_display, ll_result_curve_display;
	private ListView lv_bluetooth_scan_result;//����������ʾ�б�
	private DisplayResultView displayView;//�����ʾ��view
	private PopWindowSelectBluetooth popwindow;//���Ͻǵ���������ز˵�ѡ��
	private BluetoothScanDialog scanDialog;//�����������������ʾ�б�Ի���
	private ArrayAdapter<String> patientAdapter, typeAdapter;//�����б���������������������
	private List<String> patientList, sampleList;//���������б������б�
	private int selectPatientPosition,selectTypePosition;//ѡ���ߺ��������б�����ֵ
	private int screenWidth, screenHeight;//��Ļ�Ŀ��
	private int detectId;//�����ʼ���֮ʱ��洢���  ����ID  �ȵ������� ���µ�ǰ���Ľ��
	private SharedPreferences sp;//SharedPreferences����������ַ��Ϣ ��������ͻᱣ��������ӵ�������ַ
	//������ض���
	public static BluetoothAdapter mBluetoothAdapter;
	public static BluetoothManager bluetoothManager;
	private BluetoothLeService mBluetoothLeService;//��������
	private BluetoothGattCharacteristic mNotifyCharacteristic;
	private BluetoothGattCharacteristic characteristic;	//д����
	private BluetoothGattService mnotyGattService;
	private BluetoothGattCharacteristic readCharacteristic;	//������
	private BluetoothGattService readMnotyGattService;
	private static String mDeviceAddress = "00:15:83:00:B3:4B";// Ҫ���ӵ�Ŀ�������豸�ĵ�ַ Ĭ�ϵ�
	private LeDeviceListAdapter mLeDeviceListAdapter;//���������б�������
	private boolean mScanning;//Ĭ��ֵ��false
	private boolean mConnectedService = false;//�Ƿ��ҵ�����  ֻ�����Ϊtrue���������������Ͽ��Է��ͽ���������
	private Handler mHandler;
	private static final long SCAN_PRRIOD = 10000;//10s��ֹͣɨ��
	/* ����������� */
	private String sendString = "861101011027e02e3202800200020004050068";
	private byte[] sendData = ConvertUtils.hexStringToBytes("861101011027e02e3202800200020a00050068");// Ҫ���͵����� �ֽ����� �ʴ��Ƚ�16�����ַ���ת�����ֽڷ���
	private byte[] sendData_real = ConvertUtils.hexStringToBytes("861101011027e02e3202800200020000050068");
	private byte[] sendData_stop = ConvertUtils.hexStringToBytes("8603030168");// Ҫ����ֹͣɨ������� ��ʾ����
	private boolean isFirstSend;//�Ƿ��ǵ�һ�η���
	private MyHandler handler;// ������Ϣ���д���
	// �������õ���
	private GraphicalView mView_result;// ��ͼ��GraphicalView
	private ChartService mService_result;// ��ͼ�Ĺ����ࡣ����һ���ǻ����յ�����
	private Timer timer;// ��ʱ�� �û���̬��ͼ
	private int iAuto = 0; // ��̬�������ж��Ƿ�һ������ ���߻������������
	private boolean flag_result = false;
	private List<Double> xList;
	private List<Double> yList;// һ��һ�����߱���
	private List<Double> yListTemp;// һ��һ�����߱���
	private double[] source;
	private List<Byte> listByte;
	private int iCount = 0;// �жϵ�ǰɨ��ڼ��ε����ݵ���
	private int times_point = 10;//ϵͳ��Ļ��������ٴε�ʱ����Ĭ����3�ε�ʱ����
	private String title_result = "��Ѫ����";
	// �����ս����һЩ����
	private float interval_time = 0;// s �뼶
	private float maxValue;//���ֵ
	private Date currentDate, beforeDate;
	String tempStr = "";// ������
	private String cacheStr;
	private boolean isCache;
	private int[] frequency_value = { 100, 120, 50 };// �Ӳ������ô���������ֹƵ�� ����Ƶ�� Ĭ��Ϊ100,120,50
	private int pointCount;//һ�ζ��ٸ���
	private int times = 0;//Ĭ����10��
	private int test_times = 4;//���ԵĴ��� Ĭ����2��
	// ����excelʹ�õı���
//		private String excelPath;// ���浽sd��·��
	private String detectionDateStr;//�����ʱ��ĵ�ǰʱ���ַ���
	private String fileName;//�ļ���
	private File excel_Source_File, excel_Result_File;// excel�ļ�����ԭʼ����, ����������, ����ԭʼ�����˲�֮�������
	private MyPointF pointf;//ʱ��---ֵ 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detection);
		getActionBar().hide();//����actionbar
		//��ȡ�ֻ���Ļ���
		DisplayMetrics outMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screenWidth = outMetrics.widthPixels;
		screenHeight = outMetrics.heightPixels;
		initWidget();
		//���ݳ�ʼ��
		initParams();
		if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			//�鿴�ֻ��Ƿ�֧��4.0��������
			Toast.makeText(this, "��֧��4.0�������豸", Toast.LENGTH_SHORT).show();
			this.finish();
		}
		//��ʼ�������������� �鿴�Ƿ�API>18 
		bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		if(bluetoothManager.getAdapter() == null){
			Toast.makeText(this, "�����豸��֧������", Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		}
		mBluetoothAdapter = bluetoothManager.getAdapter();//����
		prepareData();//׼��list
		drawlineInit();//��ͼ��һЩ����
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);////��ѯ�Ƿ���Ĭ�ϵ�����  ȥSharedPreference
		mDeviceAddress = sp.getString("address", "00:15:83:00:B3:4B");
		sendString = sp.getString("defaultParams", "861101011027e02e3202800200020000050068");
		sendData_real = ConvertUtils.hexStringToBytes(sendString);//0��
		handler = new MyHandler();//�Զ������Ϣ����  Ϊ�˻�ͼ
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//ע���������һЩ����Ĺ㲥
		//ע����� �������� ---�����ķ���
		Intent gattServiceIntent = new Intent(this,BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);//������ͷ�����а󶨺��Զ���������
		//��ʱ������һֱ�����ر�  �ʴ��ڻ������ʱ������  ������
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendMessage(handler.obtainMessage(2));// ����Ϣ���з���2��� ��ʱ��Ϊ�˻�����
			}
		}, 100, 4);//��Զ�������û����ʾ�����
		//��ť�¼�
		btn_return.setOnClickListener(this);
		btn_bluetooth.setOnClickListener(this);
		btn_start_detection.setOnClickListener(this);
		btn_stop_detection.setOnClickListener(this);
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
		btn_stop_detection = (Button)findViewById(R.id.btn_stop_detection);
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		btn_bluetooth = (ImageButton)findViewById(R.id.imgbtn_add_choice_bluetooth);
		spinner_patient_choice = (Spinner)findViewById(R.id.spinner_patient_choice);
		spinner_type_choice = (Spinner)findViewById(R.id.spinner_type_choice);
		et_detect_descri = (EditText)findViewById(R.id.et_detect_descri);
		ll_result_display = (LinearLayout)findViewById(R.id.ll_result_display);
	}
	private void initParams() {
		sampleList = new ArrayList<>();
		patientList = new ArrayList<>();
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
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
	}
	
	private void prepareData(){
		patientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, patientList);
		typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sampleList);
		patientAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		typeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner_patient_choice.setAdapter(patientAdapter);
		spinner_type_choice.setAdapter(typeAdapter);
		displayView = new DisplayResultView(this, screenWidth, null);
		ll_result_curve_display = displayView.getLl_display_result_view();
		ll_result_display.addView(displayView);
	}
	private void saveRecord(){//����ɼ���¼
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		displayView.getTv_patient_name().setText(patientList.get(selectPatientPosition));
		values.put("name", patientList.get(selectPatientPosition));
		displayView.getTv_detection_type().setText(sampleList.get(selectTypePosition));
		values.put("sample", sampleList.get(selectTypePosition));
		String descri = et_detect_descri.getText().toString().trim(); 
		if(!descri.equals("")){
			values.put("descri", descri);
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
		detectionDateStr = format.format(new Date());
		displayView.getTv_detection_date().setText(detectionDateStr);
		values.put("createTime", detectionDateStr);
		db.insert("record", null, values);
		Cursor cursor = db.rawQuery("select last_insert_rowid() from record", null);
		if(cursor.moveToFirst()){
			detectId = cursor.getInt(0);//��Ϊ������������֮�� �޸Ľ�� ���ڵĽ����Ϊ��//��ȡ�²������ݵ�id  
		}
	}
	//������Ϣ���� ��ȡ����  ��һ�����������Ƿ��ǿ�ʼ�ɼ���������ڶ��������Ƿ����ٴη��Ͳɼ�����
	public void sendData(boolean isStart,boolean isRepeatStart){
		if(isStart){
			iCount = 0;// ����Ҫ���
			flag_result = false;// Ҫ��ʱ�����յ���Ѫ����
			xList.removeAll(xList);
			yList.removeAll(yList);
			listByte.removeAll(listByte);// ���������
			if(!isRepeatStart){
				mService_result.clearValue();//Ҫ�ǵڶ��λ��Ͳ������֮ǰ������
			}
			// ����x���ֵ// ÿ�ο�ʼɨ�趼����Ҫ��������x���ֵ����Ϊ����������Ҫ�����仯��
			double tempVar = frequency_value[0] * 1000;
			pointCount = (frequency_value[1] - frequency_value[0]) * 1000 / frequency_value[2] + 1;
			for (int i = 0; i < pointCount; i++) {
				xList.add(tempVar / 1000);
				tempVar = tempVar + frequency_value[2];
			}
			source = new double[pointCount];//ÿ�ζ�����̫�����ڴ���   ��������ڷ��͵�ʱ�����x������ݴ�������
			if(!isRepeatStart){
				beforeDate = new Date();
			}
		}
		read();//��ȡ����
		final int charaProp = characteristic.getProperties();
		//�����char��д
		if((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0){
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification( mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            //��ȡ���� �ڻص�������
            if(isStart){
            	if(!isRepeatStart){
            		characteristic.setValue(sendData);
	            }else{
	            	characteristic.setValue(sendData_real);//����ǵڶ��η���  ���͵����µ�����
	            }
	            mBluetoothLeService.writeCharacteristic(characteristic);
            }else{//����ֹͣ�ź�
            	characteristic.setValue(sendData_stop);
	            mBluetoothLeService.writeCharacteristic(characteristic);
//	            setScreenBrightness(getSystemScreenBrightness());//����ֹͣҲ��������
            }
//	                Toast.makeText(getApplicationContext(), "д��ɹ���", Toast.LENGTH_SHORT).show();
            Toast.makeText(DetectionActivity.this, "���ͳɹ�", Toast.LENGTH_SHORT).show();
			if(isStart){
				//���ͳɹ��ű���  ��Ȼһֱ�����ļ�
				if(!isRepeatStart){//��Ϊֻ�е�һ�η��͵�ʱ�� ���½��ļ�
					saveInit();//ִ�б������ݲ�����һЩ������ʼ��  ÿ�ε���������µ�csv�ļ�
				}
				// �˴��뱣�������й� ÿ�ο�ʼɨ���ʱ����ڵ�ǰ��Ŀ�µı��д���һ��Sheet��
				SaveActionUtils.exportCSV(excel_Source_File, xList);
//					SaveActionUtils.exportCSV(excel_Result_File, new double[]{0d,0d});
				//��Ȼ��ͼ���ֿ�����һ��ֱ��
			}
        }else{
        	Toast.makeText(DetectionActivity.this, "����ʧ�ܣ������·���", Toast.LENGTH_SHORT).show();
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
	}
	public void saveInit(){
		// ÿ�δ������ʱ��Ĭ���½�һ��excel��� �����ǵ�ǰʱ��
		fileName = detectionDateStr + "_Source" + ".csv";
//		excelPath = SaveActionUtils.getExcelDir() + File.separator + filename;
		// ��ǰ·��/mnt/sdcart/Excel/Data/��ǰʱ��.xls
		excel_Source_File = new File(SaveActionUtils.getExcelDir() + File.separator + fileName);// �õ���ǰ����ļ�=
		fileName = detectionDateStr + "_Result" + ".csv";
		excel_Result_File = new File(SaveActionUtils.getExcelDir() + File.separator + fileName);
	}
	public void drawlineInit(){
		pointf = new MyPointF();
		//ll_result_curve_display//�����յ���Ѫ���߽����LL�ؼ�
		xList = new ArrayList<Double>();
		yList = new ArrayList<Double>();
		yListTemp = new ArrayList<Double>();
		listByte = new ArrayList<Byte>();
		timer = new Timer();
		// �����ս�����ߵ�����
		mService_result = new ChartService(DetectionActivity.this);
		mService_result.setXYMultipleSeriesDataset(title_result);
		mService_result.setXYMultipleSeriesRenderer("ʱ��", "ֵ");
//		mService_result.getRenderer().setYAxisMin(0d);
		mService_result.getRenderer().setXAxisMin(6d);
		mView_result = mService_result.getGraphicalView();
		//������ʾ������ߣ�������ʾ�������ߣ��� Ȼ��ȵ�ɨ�����֮��  ������������� Ȼ����ĳɼ���Ĳ���ֵ
		ll_result_curve_display.addView(mView_result,
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left_arrow:
			finish();
			break;
		case R.id.imgbtn_add_choice_bluetooth://���������Ĵ�����
			popwindow = new PopWindowSelectBluetooth(this, screenWidth, screenHeight, this);
			popwindow.showPopupWindow(findViewById(R.id.imgview_popupwindow));
			break;
		case R.id.btn_start_detection://��ʼ��� Ҫ��������ܶ�
			if(!mConnectedService){
				Toast.makeText(DetectionActivity.this, "û������", Toast.LENGTH_SHORT).show();
				return;
			}//��������֮������к���ı������ݣ����Ͳɼ�
			/*��һ���µõ����μ��ʱ�䣬���ߡ��������� �������ݿ�*/
			saveRecord();
			/* �ɼ���� */
			isFirstSend = true;
			sendData(true,false);//��������
			break;
		case R.id.btn_stop_detection:
			sendData(false,false);//����ֹͣ����
			break;
		case R.id.btn_connect_bt://��������
			popwindow.dismiss();
			if(!mConnectedService){
				mBluetoothLeService.disconnect();
				mBluetoothLeService.connect(mDeviceAddress);
				btn_bluetooth.setBackgroundResource(R.drawable.bluetooth_connecting);
				mHandler = new Handler();
//				mHandler.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						if(!mConnectedService){
//							mBluetoothLeService.disconnect();
//						}
//					}
//				}, CONNECT_PRRIOD);//����ȴ�5s֮����û������ ���ȶϿ�
			}else{
				mBluetoothLeService.disconnect();
			}
			break;
		case R.id.btn_disconnect_bt://�Ͽ�����
			popwindow.dismiss();
			mBluetoothLeService.disconnect();
			break;
		case R.id.btn_search_bt://��������  ����ѡ��Ի���
			scanDialog = new BluetoothScanDialog(this, R.style.loading_dialog, this);
			scanDialog.show();
			lv_bluetooth_scan_result = scanDialog.getLv_bluetooth_scan_result();
			mHandler = new Handler();//��ʼɨ��
			mLeDeviceListAdapter = new LeDeviceListAdapter();//��ʼ��listviewAdapter
			lv_bluetooth_scan_result.setAdapter(mLeDeviceListAdapter);
			//�ѿ����ʱ��Ϳ�ʼɨ��
			scanLeDevice(true);
			popwindow.dismiss();
			break;
		case R.id.btn_params_setting://������������ҳ��
			Intent intent = new Intent(this, ParamsSettingActivity.class);
			startActivityForResult(intent, REQUEST_PARAM);
			popwindow.dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		//bluetoothScanAdapter.notifyDataSetInvalidated();//ˢ���б�
		final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
		if(device == null){
			return;
		}
		if(mScanning){
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mScanning = false;
		}
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
		//TODO �Ժ������������û����飬��ʾ�Ƿ񱣴�ΪĬ�ϡ����ڲ�д��  �ߡ������鷳��
		mDeviceAddress = device.getAddress();//ѡ�оͱ���ΪĬ��
		Editor editor = sp.edit();
		editor.putString("address", mDeviceAddress);
		editor.commit();
		scanDialog.dismiss();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(!mBluetoothAdapter.isEnabled()){
			if(!mBluetoothAdapter.isEnabled()){
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
		mBluetoothLeService = null; 
		unregisterReceiver(mGattUpdateReceiver);//ȡ��ע��㲥
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:		
			if(resultCode == RESULT_CANCELED){
				//�������ȡ�� ���˳���ǰ�
				this.finish();
				return;
			}
			break;
		case REQUEST_PARAM:
			if (resultCode == RESULT_OK) {
				sendString = intent.getStringExtra("params");
				frequency_value = intent.getIntArrayExtra("value");
				times = intent.getIntExtra("times", 0);//Ĭ����10
//				test_times = intent.getIntExtra("testtimes", 4);
				sendData_real = ConvertUtils.hexStringToBytes(sendString);//������ȷ Ҫ�޸ĵ���Ƶ��
				//��������Ϊ2��				
//				sendData = ConvertUtils.hexStringToBytes(sendString.substring(0, sendString.length()-10) + 
//						ParamsSettingActivity.HighExchangeLow(ParamsSettingActivity.dataConvertHex(String.valueOf(test_times))) + 
//						sendString.substring(sendString.length()-6));
//				sendData= ConvertUtils.hexStringToBytes(sendString.toLowerCase());
				Log.e("sendData", sendString);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	//����������������
	private final ServiceConnection mServiceConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(android.content.ComponentName name, android.os.IBinder service) {
			//�󶨷����ʱ����ô˷���  bindService���õ�ʱ��
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if(!mBluetoothLeService.initialize()){
				//��ʼ�������������� �鿴�Ƿ�API>18 
				Toast.makeText(DetectionActivity.this, "�����豸��֧��BLE", Toast.LENGTH_SHORT).show();
				finish();
			}//ע����ʵ��һ���е���࣬��Ϊ��MainActivity�Ѿ��ж��豸�Ƿ�֧��BLE
			//��ʼ���ɹ�֮���Զ������豸
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBluetoothLeService = null;//unBindService�������ʱ����ô˷���
		};
	};
	
	/**	 * ������ 	 */
	private void read(){
		mBluetoothLeService.setCharacteristicNotification(readCharacteristic, true);
	}
	
	//ͨ��������Ʋ�ͬ���¼�
	//ʹ�������� ʹ�ù㲥����  �������ӡ��ɶ�д��״̬
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
        	invalidateOptionsMenu();//ˢ�µ�ǰ�Ĳ˵�//������  ����û������������
        	btn_bluetooth.setBackground(getResources().getDrawable(R.drawable.bluetooth_connecting));
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            mConnectedService = false;
            invalidateOptionsMenu();//ˢ�µ�ǰ�Ĳ˵�
			//���ӶϿ���ͬʱ �رձ�����Ļ����
            btn_bluetooth.setBackground(getResources().getDrawable(R.drawable.add_bluetooth));
        } 
        //�����п�֧�ֵķ���
        else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
        	//д���ݵķ����characteristic
        	mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            characteristic = mnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            //�����ݵķ����characteristic
            readMnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            //ֻ�з��ַ����� �����Ҳŵ�������ȫ���ӳɹ���
            mConnectedService = true;
            btn_bluetooth.setBackground(getResources().getDrawable(R.drawable.bluetooth_connected));
        } 
        //��ʾ����
        else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
            	//����������  ��·�߼�  ��������ȥ�жϰ���������ǲ�������
            	cacheStr = ConvertUtils.bytesToHexString(data);
            	if(isCache){//�л���Ļ� 
            		tempStr += cacheStr;
            		if(!(tempStr.length() % 2 == 0)){
            			isCache = true;
            			return;
            		}
            	}else{//û�л���
            		if(!(cacheStr.length() % 2 == 0)){
            			isCache = true;
            			tempStr += cacheStr;
            			return;
            		}else{
            			isCache = false;
            			tempStr = cacheStr;
            		}
            	}
//            	Log.e("#####", tempStr);
            	if(!tempStr.contains("FF01")){//������2���ֽڻ���4���ֽ�  Ӧ��˵�ǲ����Ƕ����ֽڵ�����
        			for(int i = 0; i < data.length; i++){
            			listByte.add(data[i]);
            		}
        			tempStr = "";
        			return;
            	}else{//���� ff01--�����жϴ�ff01�ǲ��ǽ�β���Ǹ�ff01//�жϴ�ff01�ǲ�����Ľ�β���Ǹ�ff01 
//            		Log.e("length", tempStr + "--" + data.length);
            		if(tempStr.indexOf("FF01") % 2 != 0){
            			for(int i = 0; i < data.length; i++){
                			listByte.add(data[i]);
                		}
            			tempStr = "";
            			return;
            		}else{
            			byte[] tempByte = ConvertUtils.hexStringToBytes(tempStr.substring(0, tempStr.indexOf("FF01")));
            			//�ж��ǲ�����ff01��β�� ���ǵĻ� ˵������û�������� �������
	            		//���п��� tempByteΪ��  ��Ϊ����ff01��ͷ��  ---20171121 �����˺ܶ�� ���Ӵ���û�з���
	            		if(tempByte != null){
		            		for(int i = 0; i < tempByte.length; i++){
		            			listByte.add(tempByte[i]);
		            		}
		            		Log.e("#####", "���FF01֮ǰ��");
	            		}
	//            		//����жϵ���ff01������ǲ������
	            		//��������
	            		int tempSize = listByte.size();
	            		double tempData = (listByte.get(tempSize - 2) & 0xff) * 256 + (listByte.get(tempSize - 1) & 0xff);
	            		if(listByte.size() < (pointCount*2)){//˵�������а����� ff01
	            			if(Math.abs(tempData - 65281) > 1000){//ǰһ����������ݲ����1000����1000
		            			//˵����������
	            				listByte.removeAll(listByte);
	            				Log.e("#####", "��������");
	            				return;
		            		}else{
		            			tempByte = ConvertUtils.hexStringToBytes("FF01");
		            			for(int i = 0; i < tempByte.length; i++){
			            			listByte.add(tempByte[i]);
			            		}
		            		}
	            		}else{
	            			Log.e("######goule", "#######goule"+listByte.size());
	            			processBuffer();
	            			return;
	            		}
	            		if(!tempStr.endsWith("FF01")){
	            			tempByte = ConvertUtils.hexStringToBytes(tempStr.substring(tempStr.indexOf("FF01") + 4));
	            			for(int i = 0; i < tempByte.length; i++){
	                			listByte.add(tempByte[i]);
	                		}
	            		}//TODO �˴���Ȼ����©���ģ�������Ϊ�п��ܽ��յ������ݲ�����ż��  ��ô�ƣ�----��20171121
	            		tempStr = "";
            		}
            	}
            }
        }
    };  
    
    public void processBuffer() {
		// �Ѿ�����һ��ɨ���������// ˵��һ��ɨ���������� ����ֻ���������������	//ÿ�θ�yList�������֮ǰ�����һ��
		yList.removeAll(yList);
		yListTemp.removeAll(yListTemp);
		if(isHasTemp){//Ҫ�Ƴ���������ֽ�
			int tempSize = listByte.size();
			if(tempSize != 0){
				listByte.remove(tempSize - 1);
				listByte.remove(tempSize - 2);
			}
		}
//		source = new double[listByte.size()/2];//ÿ�ζ�����̫�����ڴ���   ��������ڷ��͵�ʱ�����x������ݳ��ȴ�������
		for (int i = 0, j = 0; i < listByte.size() - 1; i = i + 2, j++) {
//			yList.add((listByte.get(i) & 0xff) * 256 + (listByte.get(i + 1) & 0xff));//�˴�������˲�֮���޸�
			//��Ϊ���ջ�������Ӧ�����˲�֮�������
			double temp = (listByte.get(i) & 0xff) * 256 + (listByte.get(i + 1) & 0xff);
			if(j < source.length){
				source[j] = temp;
			}//�˴������Ϊ�˷�ֹ���ݳ��� û���жϵ�0xff01������յ��������ε�����
			yListTemp.add(temp);
		}
		// �����ݱ�����֮��listByte�����
		listByte.removeAll(listByte);
		handler.sendMessage(handler.obtainMessage(1));
	}
    
 // ���½����Hanlder ��
 	class MyHandler extends Handler {
 		@Override
 		public void handleMessage(Message msg) {
 			switch (msg.what) {
 			case 1:
 				// ÿ��������ʱ�򶼻�ȡ���μ��ʱ��
 				currentDate = new Date();
 				interval_time = (currentDate.getTime() - beforeDate.getTime());
 				interval_time = interval_time / 1000;
 				pointf.x = interval_time;
 				// һ��Ҫ��������ʱ��֮�󣬲�Ȼ��Ӱ����Ѫ����ʱ����ж�//��ͼ֮ǰ������ֵ֮ǰ�Ƚ����˲�����//���������double���ͣ�
 				source = new JniCall().process_Data(source);//ȥ�����ݼ��˲�
 				for(int i = 0; i < source.length; i++){
 					yList.add(source[i]);
 				}//yList��yListFilter�д洢�Ķ����˲�֮������ݣ���yList��ȥ�����ߣ�yListFilter��ȥ����
 				double tempData = Collections.max(yList);//�˴������ֵ�ǲ������   ��Ϊԭʼ������Ҫ������
 				maxValue = (float) tempData * 2 / 65536;
 				maxValue = (float)(Math.round(maxValue* 10000))/10000;//����С�������λ
 				pointf.y = maxValue;
 				//������һ�ε����� �����֮ǰ������
 				flag_result = true;
 				iCount++;
 				handler.sendMessage(handler.obtainMessage(3));
 				// �жϽ��յ���������û��ɨ��һ�ε��������� ��ʼ��ͼ// ��yList���浽excel��//TODO �´β���һ�µ������ݵ�ʱ��
 				SaveActionUtils.exportCSV(excel_Source_File,yListTemp);//����ԭʼ����
 				SaveActionUtils.exportCSV(excel_Result_File, pointf);//����������
 				break;
 			case 2:
 				if (flag_result) {
 					mService_result.updateChart(interval_time, maxValue);
 					mService_result.getRenderer().setXAxisMax(interval_time);//�������ֵ���ǵ�ǰʱ�����ֵ
 					mService_result.getRenderer().setXAxisMin(4d);//
 					mService_result.getRenderer().setYAxisMax(maxValue + maxValue*0.01); //����y�����ֵ���ǵ�ǰֵ+ 10%
 					mService_result.getRenderer().setYAxisMin(maxValue - maxValue*0.01);//����y����Сֵ������Ȼ�����ʾ������
// 					mService_result.getRenderer().setYAxisMin(maxValue - 0.01);
 					flag_result = false;// ÿ�λ����ֹͣ��
 				}
 				break;
 			case 3:
// 				Toast.makeText(ScanDisplayActivity.this, "��" + iCount + "��ɨ�����",
// 						Toast.LENGTH_SHORT).show();
 				//ÿ��ɨ�赽3�ν�����ʱ����  
 				if(iCount == times_point){
// 					setScreenBrightness(20);
 				}
 				if(iCount == times && !isFirstSend){
 					//ɨ������ָ���ϵͳ�Զ�������
// 					setScreenBrightness(getSystemScreenBrightness());
// 					btn_Start_Scan.setKeepScreenOn(false);//ɨ���������Ҫ�ٱ�����Ļ����
 				}
// 				tv_times.setText("��" + iCount + "��ɨ�����");
 				if(iCount == test_times && isFirstSend && times != 0){
 					isFirstSend = false;//˵���ǵ�һ�η���Ȼ�����¼�����ֹƵ��
 					//���ֵ����Сֵ���ڵ�Ƶ�ʵ� ǿת��int
 					double minFrequency = xList.get(yList.indexOf(Collections.max(yList))) * 100;
 					double maxFrequency = xList.get(yList.indexOf(Collections.min(yList))) * 100;
 					if(minFrequency > maxFrequency){
 						double temp = minFrequency;
 						minFrequency = maxFrequency;
 						maxFrequency = temp;
 					}//Ϊ�˷�ֹ���⣬��������Ƶ��ֵ��С ���ˡ�
 					frequency_value[0] = (int)(minFrequency - (maxFrequency - minFrequency)*2.5)/100;
 					frequency_value[1] = (int)(maxFrequency + (maxFrequency - minFrequency)/2)/100;
 					//��������Ҫ���͵�����
 					sendData_real = ConvertUtils.hexStringToBytes(sendString.substring(0, 8) + 
 							ConvertUtils.HighExchangeLow(ConvertUtils.dataConvertHex(frequency_value[0] + "00")) + 
 							ConvertUtils.HighExchangeLow(ConvertUtils.dataConvertHex(frequency_value[1] + "00")) +
 							sendString.substring(16));//���¼���
 					Log.d("#####", ConvertUtils.bytesToHexString(sendData_real));
 					//if(){//������Ϊ0�δ�����ǲ���
 						sendData(true, true);
 					//}
 				}
 				break;
 			}
 		}
 	}
    
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }	
	
	/*�����������*/	//ɨ�������豸
	private void scanLeDevice(final boolean enable){
		if(enable){
			//��һ��ɨ�����ڣ�10s��֮��ֹͣɨ��
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					scanDialog.getPb_scan().setVisibility(View.INVISIBLE);
				}
			}, SCAN_PRRIOD);//10s��ִ��run����� �� �൱��ֹͣɨ��
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		}else{
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
	//ɨ�������豸�ص� ��ɨ��֮��  �ص��ӿڱ����ڴ���LEɨ���Ľ��
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLeDeviceListAdapter.addDevice(device);
					mLeDeviceListAdapter.notifyDataSetChanged();//ʵ�ֶ�̬ˢ���б�
				}
			});
		}
	};
	//�����豸��������
	private class LeDeviceListAdapter extends BaseAdapter{
		private List<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflater;
		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflater = DetectionActivity.this.getLayoutInflater();
		}
		
		public void addDevice(BluetoothDevice device){
			if(!mLeDevices.contains(device)){
				mLeDevices.add(device);
			}
		}
		
		public BluetoothDevice getDevice(int position){
			return mLeDevices.get(position);
		}
		
		public void clear(){
			mLeDevices.clear();
		}
		
		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int position) {
			return mLeDevices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder viewHolder;
			if(view == null){
				view = mInflater.inflate(R.layout.item_bluetooth_list, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView)view.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView)view.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			BluetoothDevice device = mLeDevices.get(position);
			final String deviceName = device.getName();
			if(deviceName != null && deviceName.length() > 0){
				viewHolder.deviceName.setText(deviceName);
			}else{
				viewHolder.deviceName.setText(R.string.unknown_device);
			}
			viewHolder.deviceAddress.setText(device.getAddress());
			return view;
		}
		
	}
	static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
/*	//Start    Java��JNI����
	static{
		System.loadLibrary("CALLC");
	}
	public native double[] process_Data(double[] source);//���ط��� �������˲�������㷨
	//End      Java��JNI����
*/}
