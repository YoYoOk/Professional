package com.yj.professional.exception;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * UncaughtException�����࣬��������Uncaught�쳣��ʱ���ɸ������ӹܳ��򣬲���¼���ʹ��󱨸档
 * @author liaoyao
 */
public class CrashHandler implements UncaughtExceptionHandler {
	
	private static final String TAG = "CrashHangler";
	private Thread.UncaughtExceptionHandler mDefaultHandler;//ϵͳĬ�ϵ�UncaughtException������
	private static CrashHandler INSTANCE = new CrashHandler();//����ģʽ  CrashHandlerʵ��
	private Context mContext;//�����Context����
	private Map<String,String> info = new HashMap<String,String>();//�����洢�豸��Ϣ���쳣��Ϣ
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//��ֻ֤��һ��CrashHandlerʵ��
	private CrashHandler(){}
	//��ȡCrashHandlerʵ��������ģʽ
	public static CrashHandler getInstance(){
		return INSTANCE;
	}
	
	/**
	 * ��ʼ��
	 * @param context
	 */
	public void init(Context context){
		this.mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();//��ȡϵͳĬ�ϵ�UncaughtException������
		Thread.setDefaultUncaughtExceptionHandler(this);//���ø�CrashHandlerΪ����Ĭ�ϴ�����������һ����װ�������ģʽ
	}
	
	/** 
	 * ��UncaughtException����ʱ��ת�����д�ķ���������
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(!handlerException(ex) && mDefaultHandler != null){
			//����û�û�д�����ϵͳĬ�ϵ��쳣������������
			mDefaultHandler.uncaughtException(thread, ex);
		}else{
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error: " , e);
			}
		}
		//�˳�����
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}
	
	/**
	 * �Զ���������ռ�������Ϣ�����ʹ��󱨸�Ȳ������ڴ���ɡ�
	 * @param ex
	 * @return
	 */
	public boolean handlerException(Throwable ex){
		if(ex == null){
			return false;
		}
		new Thread(){
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "�ܱ�Ǹ����������쳣��", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		//�ֻ��豸������Ϣ
//		collectDeviceInfo(mContext);
		//������־�ļ�
		saveCrashInfo2File(ex);
		return true;
	}
	
	/**
	 * �Ѽ��豸������Ϣ
	 * @param context
	 */
	public void collectDeviceInfo(Context context){
		try {
			PackageManager pm = context.getPackageManager();//��ð�������
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);//�õ���Ӧ�õ���Ϣ������Activity
			if(pi != null){
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				info.put("versionName", versionName);
				info.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Field[] fields = Build.class.getDeclaredFields();//�������
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				info.put(field.getName(), field.get("").toString());
				Log.d(TAG, field.getName() + ":" + field.get(""));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String saveCrashInfo2File(Throwable ex){
		StringBuffer sb = new StringBuffer();
		for(Map.Entry<String, String> entry : info.entrySet()){
			sb.append(entry.getKey() + "=" + entry.getValue() + "\r\n");
		}
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);  
        ex.printStackTrace(pw);  
        Throwable cause = ex.getCause();  
        // ѭ���Ű����е��쳣��Ϣд��writer��  
        while (cause != null) {  
            cause.printStackTrace(pw);  
            cause = cause.getCause();  
        }  
        pw.close();// �ǵùر�  
        String result = writer.toString();  
        sb.append(result);  
        // �����ļ�  
        long timetamp = System.currentTimeMillis();  
        String time = format.format(new Date());  
        String fileName = "crash-" + time + "-" + timetamp + ".log";  
//        if (Environment.getExternalStorageState().equals(  
//                Environment.MEDIA_MOUNTED)) {  
        try {  
            File dir = new File(Environment.getExternalStorageDirectory().toString() + 
            		File.separator + "Excel" + File.separator + "Crash");  
            Log.i("CrashHandler", dir.toString());  
            if (!dir.exists())  
                dir.mkdir();  
            FileOutputStream fos = new FileOutputStream(new File(dir,  
                     fileName));  
            fos.write(sb.toString().getBytes());  
            fos.close();  
            return fileName;  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
//        }  
        return null;  
	}
	
}
