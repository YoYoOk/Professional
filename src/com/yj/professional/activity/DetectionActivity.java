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
 * 新建检测页面活动
 */
public class DetectionActivity extends Activity implements OnClickListener, OnItemClickListener{
	private static final int REQUEST_ENABLE_BT = 1;//选择是否打开蓝牙对话框
	private static final int REQUEST_PARAM = 2;//请求参数配置
	private boolean isHasTemp = true;
	private Button btn_start_detection, btn_stop_detection;//开始检测按钮
	private ImageButton btn_return, btn_bluetooth;//顶部返回按钮，蓝牙弹出窗口按钮
	private EditText et_detect_descri;//当前检测描述
	private Spinner spinner_patient_choice, spinner_type_choice;//下拉框
	private LinearLayout ll_result_display, ll_result_curve_display;
	private ListView lv_bluetooth_scan_result;//蓝牙搜索显示列表
	private DisplayResultView displayView;//结果显示的view
	private PopWindowSelectBluetooth popwindow;//右上角弹出蓝牙相关菜单选择
	private BluetoothScanDialog scanDialog;//弹出搜索蓝牙结果显示列表对话框
	private ArrayAdapter<String> patientAdapter, typeAdapter;//下拉列表患者姓名、样本适配器。
	private List<String> patientList, sampleList;//患者姓名列表、样本列表
	private int selectPatientPosition,selectTypePosition;//选择患者和样本的列表索引值
	private int screenWidth, screenHeight;//屏幕的宽高
	private int detectId;//点击开始检测之时便存储入库  返回ID  等到检测完毕 更新当前检测的结果
	private SharedPreferences sp;//SharedPreferences保存蓝牙地址信息 搜索点击就会保存最后连接的蓝牙地址
	//蓝牙相关定义
	public static BluetoothAdapter mBluetoothAdapter;
	public static BluetoothManager bluetoothManager;
	private BluetoothLeService mBluetoothLeService;//蓝牙服务
	private BluetoothGattCharacteristic mNotifyCharacteristic;
	private BluetoothGattCharacteristic characteristic;	//写数据
	private BluetoothGattService mnotyGattService;
	private BluetoothGattCharacteristic readCharacteristic;	//读数据
	private BluetoothGattService readMnotyGattService;
	private static String mDeviceAddress = "00:15:83:00:B3:4B";// 要连接的目标蓝牙设备的地址 默认的
	private LeDeviceListAdapter mLeDeviceListAdapter;//蓝牙搜索列表适配器
	private boolean mScanning;//默认值是false
	private boolean mConnectedService = false;//是否找到服务  只有这个为true才算是真正连接上可以发送接收数据了
	private Handler mHandler;
	private static final long SCAN_PRRIOD = 10000;//10s后停止扫描
	/* 发送数据相关 */
	private String sendString = "861101011027e02e3202800200020004050068";
	private byte[] sendData = ConvertUtils.hexStringToBytes("861101011027e02e3202800200020a00050068");// 要发送的数据 字节数据 故此先将16进制字符串转换成字节发送
	private byte[] sendData_real = ConvertUtils.hexStringToBytes("861101011027e02e3202800200020000050068");
	private byte[] sendData_stop = ConvertUtils.hexStringToBytes("8603030168");// 要发送停止扫描的数据 表示用来
	private boolean isFirstSend;//是否是第一次发送
	private MyHandler handler;// 定义消息队列处理
	// 画曲线用得着
	private GraphicalView mView_result;// 画图的GraphicalView
	private ChartService mService_result;// 画图的工具类。。。一个是画最终的曲线
	private Timer timer;// 定时器 用户动态画图
	private int iAuto = 0; // 动态画曲线判断是否一条画完 曲线画点的数据增长
	private boolean flag_result = false;
	private List<Double> xList;
	private List<Double> yList;// 一条一条曲线保存
	private List<Double> yListTemp;// 一条一条曲线保存
	private double[] source;
	private List<Byte> listByte;
	private int iCount = 0;// 判断当前扫描第几次的数据到来
	private int times_point = 10;//系统屏幕大概亮多少次的时候暗屏默认是3次的时候暗屏
	private String title_result = "凝血曲线";
	// 画最终结果的一些定义
	private float interval_time = 0;// s 秒级
	private float maxValue;//最大值
	private Date currentDate, beforeDate;
	String tempStr = "";// 测试用
	private String cacheStr;
	private boolean isCache;
	private int[] frequency_value = { 100, 120, 50 };// 从参数配置传上来的起止频率 步进频率 默认为100,120,50
	private int pointCount;//一次多少个点
	private int times = 0;//默认是10次
	private int test_times = 4;//测试的次数 默认是2次
	// 保存excel使用的变量
//		private String excelPath;// 保存到sd卡路径
	private String detectionDateStr;//保存的时候的当前时间字符串
	private String fileName;//文件名
	private File excel_Source_File, excel_Result_File;// excel文件保存原始数据, 保存结果数据, 保存原始数据滤波之后的数据
	private MyPointF pointf;//时间---值 
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
		if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			//查看手机是否支持4.0以上蓝牙
			Toast.makeText(this, "不支持4.0蓝牙的设备", Toast.LENGTH_SHORT).show();
			this.finish();
		}
		//初始化蓝牙适配器， 查看是否API>18 
		bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		if(bluetoothManager.getAdapter() == null){
			Toast.makeText(this, "您的设备不支持蓝牙", Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		}
		mBluetoothAdapter = bluetoothManager.getAdapter();//蓝牙
		prepareData();//准备list
		drawlineInit();//绘图的一些定义
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);////查询是否有默认的蓝牙  去SharedPreference
		mDeviceAddress = sp.getString("address", "00:15:83:00:B3:4B");
		sendString = sp.getString("defaultParams", "861101011027e02e3202800200020000050068");
		sendData_real = ConvertUtils.hexStringToBytes(sendString);//0次
		handler = new MyHandler();//自定义的消息队列  为了画图
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//注册监听蓝牙一些服务的广播
		//注册服务 启动服务 ---蓝牙的服务
		Intent gattServiceIntent = new Intent(this,BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);//表明活动和服务进行绑定后自动创建服务
		//定时器不能一直启动关闭  故此在活动创建的时候启动  单例的
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendMessage(handler.obtainMessage(2));// 给消息队列发送2标记 定时是为了画曲线
			}
		}, 100, 4);//永远不会出现没法显示的情况
		//按钮事件
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
	private void saveRecord(){//保存采集记录
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
			detectId = cursor.getInt(0);//是为了最后结果求出来之后 修改结果 现在的结果是为空//获取新插入数据的id  
		}
	}
	//发送消息控制 抽取代码  第一个参数发送是否是开始采集命令，，，第二个参数是否是再次发送采集数据
	public void sendData(boolean isStart,boolean isRepeatStart){
		if(isStart){
			iCount = 0;// 次数要清空
			flag_result = false;// 要暂时画最终的凝血曲线
			xList.removeAll(xList);
			yList.removeAll(yList);
			listByte.removeAll(listByte);// 先清掉数据
			if(!isRepeatStart){
				mService_result.clearValue();//要是第二次画就不用清除之前的数据
			}
			// 设置x轴的值// 每次开始扫描都必须要重新设置x轴的值，因为在配置中是要发生变化的
			double tempVar = frequency_value[0] * 1000;
			pointCount = (frequency_value[1] - frequency_value[0]) * 1000 / frequency_value[2] + 1;
			for (int i = 0; i < pointCount; i++) {
				xList.add(tempVar / 1000);
				tempVar = tempVar + frequency_value[2];
			}
			source = new double[pointCount];//每次都创建太消耗内存了   解决方案在发送的时候根据x轴的数据创建数组
			if(!isRepeatStart){
				beforeDate = new Date();
			}
		}
		read();//读取数据
		final int charaProp = characteristic.getProperties();
		//如果该char可写
		if((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0){
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification( mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            //读取数据 在回调函数中
            if(isStart){
            	if(!isRepeatStart){
            		characteristic.setValue(sendData);
	            }else{
	            	characteristic.setValue(sendData_real);//如果是第二次发送  则发送的是新的数据
	            }
	            mBluetoothLeService.writeCharacteristic(characteristic);
            }else{//发送停止信号
            	characteristic.setValue(sendData_stop);
	            mBluetoothLeService.writeCharacteristic(characteristic);
//	            setScreenBrightness(getSystemScreenBrightness());//发送停止也让其亮屏
            }
//	                Toast.makeText(getApplicationContext(), "写入成功！", Toast.LENGTH_SHORT).show();
            Toast.makeText(DetectionActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
			if(isStart){
				//发送成功才保存  不然一直创建文件
				if(!isRepeatStart){//因为只有第一次发送的时候 才新建文件
					saveInit();//执行保存数据操作的一些变量初始化  每次点击都创建新的csv文件
				}
				// 此处与保存数据有关 每次开始扫描的时候就在当前项目下的表中创建一个Sheet表
				SaveActionUtils.exportCSV(excel_Source_File, xList);
//					SaveActionUtils.exportCSV(excel_Result_File, new double[]{0d,0d});
				//不然画图部分看着是一条直线
			}
        }else{
        	Toast.makeText(DetectionActivity.this, "发送失败，请重新发送", Toast.LENGTH_SHORT).show();
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
	}
	public void saveInit(){
		// 每次打开软件的时候即默认新建一个excel表格 表名是当前时间
		fileName = detectionDateStr + "_Source" + ".csv";
//		excelPath = SaveActionUtils.getExcelDir() + File.separator + filename;
		// 当前路径/mnt/sdcart/Excel/Data/当前时间.xls
		excel_Source_File = new File(SaveActionUtils.getExcelDir() + File.separator + fileName);// 得到当前这个文件=
		fileName = detectionDateStr + "_Result" + ".csv";
		excel_Result_File = new File(SaveActionUtils.getExcelDir() + File.separator + fileName);
	}
	public void drawlineInit(){
		pointf = new MyPointF();
		//ll_result_curve_display//画最终的凝血曲线结果的LL控件
		xList = new ArrayList<Double>();
		yList = new ArrayList<Double>();
		yListTemp = new ArrayList<Double>();
		listByte = new ArrayList<Byte>();
		timer = new Timer();
		// 画最终结果曲线的曲线
		mService_result = new ChartService(DetectionActivity.this);
		mService_result.setXYMultipleSeriesDataset(title_result);
		mService_result.setXYMultipleSeriesRenderer("时间", "值");
//		mService_result.getRenderer().setYAxisMin(0d);
		mService_result.getRenderer().setXAxisMin(6d);
		mView_result = mService_result.getGraphicalView();
		//上面显示结果曲线，下面显示最终曲线，， 然后等到扫描完毕之后  覆盖下面的曲线 然后将其改成计算的参数值
		ll_result_curve_display.addView(mView_result,
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left_arrow:
			finish();
			break;
		case R.id.imgbtn_add_choice_bluetooth://弹出蓝牙的创窗口
			popwindow = new PopWindowSelectBluetooth(this, screenWidth, screenHeight, this);
			popwindow.showPopupWindow(findViewById(R.id.imgview_popupwindow));
			break;
		case R.id.btn_start_detection://开始检测 要做的事情很多
			if(!mConnectedService){
				Toast.makeText(DetectionActivity.this, "没有连接", Toast.LENGTH_SHORT).show();
				return;
			}//必须连接之后才能有后面的保存数据，发送采集
			/*第一件事得到本次检测时间，患者、样本类型 保存数据库*/
			saveRecord();
			/* 采集相关 */
			isFirstSend = true;
			sendData(true,false);//发送数据
			break;
		case R.id.btn_stop_detection:
			sendData(false,false);//发送停止命令
			break;
		case R.id.btn_connect_bt://连接蓝牙
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
//				}, CONNECT_PRRIOD);//如果等待5s之后还是没有连接 则先断开
			}else{
				mBluetoothLeService.disconnect();
			}
			break;
		case R.id.btn_disconnect_bt://断开蓝牙
			popwindow.dismiss();
			mBluetoothLeService.disconnect();
			break;
		case R.id.btn_search_bt://搜索蓝牙  弹出选择对话框
			scanDialog = new BluetoothScanDialog(this, R.style.loading_dialog, this);
			scanDialog.show();
			lv_bluetooth_scan_result = scanDialog.getLv_bluetooth_scan_result();
			mHandler = new Handler();//开始扫描
			mLeDeviceListAdapter = new LeDeviceListAdapter();//初始化listviewAdapter
			lv_bluetooth_scan_result.setAdapter(mLeDeviceListAdapter);
			//已开启活动时候就开始扫描
			scanLeDevice(true);
			popwindow.dismiss();
			break;
		case R.id.btn_params_setting://弹出参数配置页面
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
		
		//bluetoothScanAdapter.notifyDataSetInvalidated();//刷新列表
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
		//TODO 以后新增，考虑用户体验，提示是否保存为默认。现在不写了  哼。。。麻烦。
		mDeviceAddress = device.getAddress();//选中就保存为默认
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
		unregisterReceiver(mGattUpdateReceiver);//取消注册广播
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:		
			if(resultCode == RESULT_CANCELED){
				//若点击了取消 就退出当前活动
				this.finish();
				return;
			}
			break;
		case REQUEST_PARAM:
			if (resultCode == RESULT_OK) {
				sendString = intent.getStringExtra("params");
				frequency_value = intent.getIntArrayExtra("value");
				times = intent.getIntExtra("times", 0);//默认是10
//				test_times = intent.getIntExtra("testtimes", 4);
				sendData_real = ConvertUtils.hexStringToBytes(sendString);//次数正确 要修改的是频率
				//将其设置为2次				
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
	//管理服务的生命周期
	private final ServiceConnection mServiceConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(android.content.ComponentName name, android.os.IBinder service) {
			//绑定服务的时候调用此方法  bindService调用的时候
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if(!mBluetoothLeService.initialize()){
				//初始化蓝牙适配器， 查看是否API>18 
				Toast.makeText(DetectionActivity.this, "您的设备不支持BLE", Toast.LENGTH_SHORT).show();
				finish();
			}//注：其实这一段有点多余，因为在MainActivity已经判断设备是否支持BLE
			//初始化成功之后自动连接设备
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBluetoothLeService = null;//unBindService解绑服务的时候调用此方法
		};
	};
	
	/**	 * 读函数 	 */
	private void read(){
		mBluetoothLeService.setCharacteristicNotification(readCharacteristic, true);
	}
	
	//通过服务控制不同的事件
	//使用匿名类 使用广播监听  蓝牙连接、可读写的状态
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
        	invalidateOptionsMenu();//刷新当前的菜单//连接了  但是没有搜索到服务
        	btn_bluetooth.setBackground(getResources().getDrawable(R.drawable.bluetooth_connecting));
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            mConnectedService = false;
            invalidateOptionsMenu();//刷新当前的菜单
			//连接断开的同时 关闭保持屏幕常亮
            btn_bluetooth.setBackground(getResources().getDrawable(R.drawable.add_bluetooth));
        } 
        //发现有可支持的服务
        else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
        	//写数据的服务和characteristic
        	mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            characteristic = mnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            //读数据的服务和characteristic
            readMnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            //只有发现服务了 ，，我才当你是完全连接成功了
            mConnectedService = true;
            btn_bluetooth.setBackground(getResources().getDrawable(R.drawable.bluetooth_connected));
        } 
        //显示数据
        else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
            	//有数据来了  短路逻辑  若包含就去判断包含的这个是不是最后的
            	cacheStr = ConvertUtils.bytesToHexString(data);
            	if(isCache){//有缓存的话 
            		tempStr += cacheStr;
            		if(!(tempStr.length() % 2 == 0)){
            			isCache = true;
            			return;
            		}
            	}else{//没有缓存
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
            	if(!tempStr.contains("FF01")){//不管是2个字节还是4个字节  应该说是不管是多少字节的数据
        			for(int i = 0; i < data.length; i++){
            			listByte.add(data[i]);
            		}
        			tempStr = "";
        			return;
            	}else{//包含 ff01--还得判断此ff01是不是结尾的那个ff01//判断此ff01是不是真的结尾的那个ff01 
//            		Log.e("length", tempStr + "--" + data.length);
            		if(tempStr.indexOf("FF01") % 2 != 0){
            			for(int i = 0; i < data.length; i++){
                			listByte.add(data[i]);
                		}
            			tempStr = "";
            			return;
            		}else{
            			byte[] tempByte = ConvertUtils.hexStringToBytes(tempStr.substring(0, tempStr.indexOf("FF01")));
            			//判断是不是以ff01结尾的 若是的话 说明后面没有数据了 再添加了
	            		//极有可能 tempByte为空  因为是以ff01起头的  ---20171121 报错了很多次 脑子呆笨没有发现
	            		if(tempByte != null){
		            		for(int i = 0; i < tempByte.length; i++){
		            			listByte.add(tempByte[i]);
		            		}
		            		Log.e("#####", "添加FF01之前的");
	            		}
	//            		//如何判断到底ff01是最后还是不是最后
	            		//丢数据了
	            		int tempSize = listByte.size();
	            		double tempData = (listByte.get(tempSize - 2) & 0xff) * 256 + (listByte.get(tempSize - 1) & 0xff);
	            		if(listByte.size() < (pointCount*2)){//说明数据中包含了 ff01
	            			if(Math.abs(tempData - 65281) > 1000){//前一个和这个数据差距在1000超过1000
		            			//说明丢数据了
	            				listByte.removeAll(listByte);
	            				Log.e("#####", "丢数据了");
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
	            		}//TODO 此处仍然是有漏洞的，，，因为有可能接收到的数据并不是偶数  怎么破？----于20171121
	            		tempStr = "";
            		}
            	}
            }
        }
    };  
    
    public void processBuffer() {
		// 已经来了一次扫描的数据了// 说明一次扫描正常结束 现在只考虑正常的情况下	//每次给yList添加数据之前都清空一次
		yList.removeAll(yList);
		yListTemp.removeAll(yListTemp);
		if(isHasTemp){//要移除最后两个字节
			int tempSize = listByte.size();
			if(tempSize != 0){
				listByte.remove(tempSize - 1);
				listByte.remove(tempSize - 2);
			}
		}
//		source = new double[listByte.size()/2];//每次都创建太消耗内存了   解决方案在发送的时候根据x轴的数据长度创建数组
		for (int i = 0, j = 0; i < listByte.size() - 1; i = i + 2, j++) {
//			yList.add((listByte.get(i) & 0xff) * 256 + (listByte.get(i + 1) & 0xff));//此处在添加滤波之后修改
			//因为最终画的曲线应该是滤波之后的曲线
			double temp = (listByte.get(i) & 0xff) * 256 + (listByte.get(i + 1) & 0xff);
			if(j < source.length){
				source[j] = temp;
			}//此处添加是为了防止数据出错 没有判断到0xff01结果接收到的是两次的数据
			yListTemp.add(temp);
		}
		// 将数据保存了之后listByte得清空
		listByte.removeAll(listByte);
		handler.sendMessage(handler.obtainMessage(1));
	}
    
 // 更新界面的Hanlder 类
 	class MyHandler extends Handler {
 		@Override
 		public void handleMessage(Message msg) {
 			switch (msg.what) {
 			case 1:
 				// 每次来数据时候都获取两次间隔时间
 				currentDate = new Date();
 				interval_time = (currentDate.getTime() - beforeDate.getTime());
 				interval_time = interval_time / 1000;
 				pointf.x = interval_time;
 				// 一定要在求两次时间之后，不然会影响凝血曲线时间的判断//画图之前计算最值之前先进行滤波处理//输入必须是double类型？
 				source = new JniCall().process_Data(source);//去对数据简单滤波
 				for(int i = 0; i < source.length; i++){
 					yList.add(source[i]);
 				}//yList和yListFilter中存储的都是滤波之后的数据，，yList是去画曲线，yListFilter是去保存
 				double tempData = Collections.max(yList);//此处求最大值是不合理的   因为原始数据需要做处理
 				maxValue = (float) tempData * 2 / 65536;
 				maxValue = (float)(Math.round(maxValue* 10000))/10000;//保留小数点后四位
 				pointf.y = maxValue;
 				//又来了一次的数据 先清空之前的数据
 				flag_result = true;
 				iCount++;
 				handler.sendMessage(handler.obtainMessage(3));
 				// 判断接收到的数据有没有扫描一次的数据以上 则开始画图// 将yList保存到excel中//TODO 下次测试一下导入数据的时间
 				SaveActionUtils.exportCSV(excel_Source_File,yListTemp);//保存原始数据
 				SaveActionUtils.exportCSV(excel_Result_File, pointf);//保存结果数据
 				break;
 			case 2:
 				if (flag_result) {
 					mService_result.updateChart(interval_time, maxValue);
 					mService_result.getRenderer().setXAxisMax(interval_time);//设置最大值就是当前时间最大值
 					mService_result.getRenderer().setXAxisMin(4d);//
 					mService_result.getRenderer().setYAxisMax(maxValue + maxValue*0.01); //设置y的最大值就是当前值+ 10%
 					mService_result.getRenderer().setYAxisMin(maxValue - maxValue*0.01);//设置y的最小值，，不然结果显示看不出
// 					mService_result.getRenderer().setYAxisMin(maxValue - 0.01);
 					flag_result = false;// 每次画完就停止画
 				}
 				break;
 			case 3:
// 				Toast.makeText(ScanDisplayActivity.this, "第" + iCount + "次扫描结束",
// 						Toast.LENGTH_SHORT).show();
 				//每次扫描到3次结束的时候暗屏  
 				if(iCount == times_point){
// 					setScreenBrightness(20);
 				}
 				if(iCount == times && !isFirstSend){
 					//扫描结束恢复到系统自动的亮度
// 					setScreenBrightness(getSystemScreenBrightness());
// 					btn_Start_Scan.setKeepScreenOn(false);//扫描结束不需要再保持屏幕常亮
 				}
// 				tv_times.setText("第" + iCount + "次扫描结束");
 				if(iCount == test_times && isFirstSend && times != 0){
 					isFirstSend = false;//说明是第一次发送然后重新计算起止频率
 					//最大值和最小值所在的频率点 强转成int
 					double minFrequency = xList.get(yList.indexOf(Collections.max(yList))) * 100;
 					double maxFrequency = xList.get(yList.indexOf(Collections.min(yList))) * 100;
 					if(minFrequency > maxFrequency){
 						double temp = minFrequency;
 						minFrequency = maxFrequency;
 						maxFrequency = temp;
 					}//为了防止意外，，，，，频率值大小 反了。
 					frequency_value[0] = (int)(minFrequency - (maxFrequency - minFrequency)*2.5)/100;
 					frequency_value[1] = (int)(maxFrequency + (maxFrequency - minFrequency)/2)/100;
 					//重新设置要发送的数据
 					sendData_real = ConvertUtils.hexStringToBytes(sendString.substring(0, 8) + 
 							ConvertUtils.HighExchangeLow(ConvertUtils.dataConvertHex(frequency_value[0] + "00")) + 
 							ConvertUtils.HighExchangeLow(ConvertUtils.dataConvertHex(frequency_value[1] + "00")) +
 							sendString.substring(16));//重新计算
 					Log.d("#####", ConvertUtils.bytesToHexString(sendData_real));
 					//if(){//若发送为0次代表仅是测试
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
	
	/*蓝牙搜索相关*/	//扫描蓝牙设备
	private void scanLeDevice(final boolean enable){
		if(enable){
			//在一个扫描周期（10s）之后停止扫描
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					scanDialog.getPb_scan().setVisibility(View.INVISIBLE);
				}
			}, SCAN_PRRIOD);//10s后执行run里面的 即 相当于停止扫描
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		}else{
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
	//扫描蓝牙设备回调 即扫描之后  回调接口被用于传输LE扫描后的结果
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLeDeviceListAdapter.addDevice(device);
					mLeDeviceListAdapter.notifyDataSetChanged();//实现动态刷新列表
				}
			});
		}
	};
	//查找设备的适配器
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
/*	//Start    Java的JNI技术
	static{
		System.loadLibrary("CALLC");
	}
	public native double[] process_Data(double[] source);//本地方法 对数据滤波处理的算法
	//End      Java的JNI技术
*/}
