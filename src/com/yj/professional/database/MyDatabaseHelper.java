package com.yj.professional.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author liaoyao
 * ���ݿⴴ������Ӧ��װ�����ǲ���Ū�� ��������
 */
public class MyDatabaseHelper extends SQLiteOpenHelper{
	public static final String CREATE_USER = "create table user("
			+ "id integer primary key autoincrement,"
			+ "username text,"
			+ "password text,"
			+ "hospitalName text)";
	
	public static final String CREATE_PATIENT ="create table patient("
			+ "id integer primary key autoincrement,"
			+ "patientId text,"
			+ "name text,"
			+ "gender integer,"
			+ "weight real,"
			+ "age integer)";
	
	public static final String CREATE_SAMPLE ="create table sample("
			+ "id integer primary key autoincrement,"
			+ "name text,"
			+ "descri text)";
	
	public static final String CREATE_RECORD ="create table record("
			+ "id integer primary key autoincrement,"
			+ "name text,"
			+ "sample text,"
			+ "descri text,"
			+ "value text,"
			+ "createTime datetime)";
	//����һ��������
	private Context mContext;
	public MyDatabaseHelper(Context context,String name,CursorFactory factory,int version){
		super(context,name,factory,version);
		mContext = context;
	}
	@Override
	public void onCreate(SQLiteDatabase db){
		/*
		 * 1���ڵ�һ�δ����ݿ��ʱ��               ��ִ��
		 * 2�����������֮���ٴ�����  -->�����ݿ�   ��ִ��
		 * 3��û��������ݣ�         ����ִ��
		 * 4�����ݿ�������ʱ��    ����ִ��
		 */
		db.execSQL(CREATE_USER);//ִ�д����û�������
		db.execSQL(CREATE_PATIENT);//ִ�д���������Ϣ������
		db.execSQL(CREATE_SAMPLE);//ִ�д���������Ϣ�����
		db.execSQL(CREATE_RECORD);//ִ�д�������¼������
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*
		 * 1����һ�δ������ݿ��ʱ��  ��ִ��
		 * 2��������ݺ��ٴ����У��൱�ڵ�һ�δ�����  ��ִ��
		 * 3�����ݿ��Ѿ����ڣ����Ұ汾���ߵ�ʱ��    ����
		 */
		//������ݿ� ���´���
		db.execSQL("drop table if exists user");
		db.execSQL("drop table if exists patient");
		db.execSQL("drop table if exists sample");
		db.execSQL("drop table if exists record");
		onCreate(db);
		//������ݿ�������� ֻ���һ��switch�ж�  oldVsersion
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onDowngrade(db, oldVersion, newVersion);
		/**
		* ִ�����ݿ�Ľ�������
		* 1��ֻ���°汾�Ⱦɰ汾�͵�ʱ��Ż�ִ��
		* 2�������ִ�н������������׳��쳣
		*/
		db.execSQL("drop table if exists user");
		db.execSQL("drop table if exists patient");
		db.execSQL("drop table if exists sample");
		db.execSQL("drop table if exists record");//����
		onCreate(db);
	}
}
