package com.yj.professional.activity;


import com.yj.professional.database.MyDatabaseHelper;
import com.yj.professional.domain.PatientInformation;
import com.yj.professional.utils.ActivityController;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author liaoyao
 * 登录页面活动
 */
public class LoginActivity extends Activity implements OnClickListener{
	
	private Button btn_login, btn_turn_register;
	private EditText et_username, et_password;
	private CheckBox cb_auto_login;
	private SharedPreferences sp;
	private boolean isAutoLogin = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getActionBar().hide();//隐藏actionbar
		sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		initWidget();
		ActivityController.addActivity(this);
		et_username.setText(sp.getString("username", "admin"));
		et_password.setText(sp.getString("password", "admin"));
		if(sp.getBoolean("isAutoLogin", false)){
			//跳转界面  
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);  
            LoginActivity.this.startActivity(intent);  
            this.finish();
		}
		MainActivity.dbHelper = new MyDatabaseHelper(this, "Professional.db", null, 3);
		MainActivity.dbHelper.getWritableDatabase();//在调用这个方法的时候，，会调用重写的onCreate方法  即在此处写创建表的方法
		btn_login.setOnClickListener(this);
		btn_turn_register.setOnClickListener(this);
	}

	private void initWidget() {
		btn_login = (Button)findViewById(R.id.btn_login);
		btn_turn_register = (Button)findViewById(R.id.btn_turn_register);
		et_username = (EditText)findViewById(R.id.et_username);
		et_password = (EditText)findViewById(R.id.et_password);
		cb_auto_login = (CheckBox)findViewById(R.id.cb_auto_login);
	}

	private boolean judgeLogin(String username, String password) {
		boolean result = false;
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
		String sql = "select count(*) from user where username = '"
				+ username + "' and password = '" + password + "'";
		SQLiteStatement statement = db.compileStatement(sql);
		long count = statement.simpleQueryForLong();
		if(count > 0){
			result = true;
		}
		return result;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			String username = et_username.getText().toString().trim();
			String password = et_password.getText().toString().trim();
			if(judgeLogin(username, password)){
				isAutoLogin = cb_auto_login.isChecked();
				Editor editor = sp.edit();
				if(isAutoLogin){
					editor.putBoolean("isAutoLogin", isAutoLogin);
				}
				editor.putString("username", username);
				editor.putString("password", password);
				editor.commit();
				//跳转界面    
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);  
                LoginActivity.this.startActivity(intent); 
                this.finish();
			}else{
				Toast.makeText(LoginActivity.this, "用户信息出错啦~~~", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_turn_register:
			Intent registerIntent = new Intent(this, RegisterActivity.class);
			startActivity(registerIntent);
//			this.finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		return super.onKeyDown(keyCode, event);
	}
	
}
