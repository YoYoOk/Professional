package com.yj.professional.activity;


import com.yj.professional.utils.ActivityController;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * @author liaoyao
 * 注册页面活动
 */
public class RegisterActivity extends Activity implements OnClickListener{
	
	private ImageButton btn_return;
	private Button btn_register;
	private EditText et_username, et_password, et_password2, et_hospital;
	private CheckBox cb_agree;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();//隐藏actionbar
		setContentView(R.layout.activity_register);
		initWidget();
		ActivityController.addActivity(this);
		btn_return.setOnClickListener(this);
		btn_register.setOnClickListener(this);
	}

	private void initWidget() {
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		btn_register = (Button)findViewById(R.id.btn_register);
		et_username = (EditText)findViewById(R.id.et_register_username);
		et_password = (EditText)findViewById(R.id.et_register_password);
		et_password2 = (EditText)findViewById(R.id.et_register_password_again);
		et_hospital = (EditText)findViewById(R.id.et_register_hospital);
		cb_agree = (CheckBox)findViewById(R.id.cb_agree);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left_arrow:
			Intent loginIntent = new Intent(this, LoginActivity.class);
			startActivity(loginIntent);
			this.finish();
			break;
		case R.id.btn_register:
			String username = et_username.getText().toString().trim();
			String password = et_password.getText().toString().trim();
			String password2 = et_password2.getText().toString().trim();
			String hospital = et_hospital.getText().toString().trim();
			if(username.equals("") || username.equals("用户名：") || 
					password.equals("") || password.equals("密 码：") ||
					password2.equals("") || password2.equals("确认密码：")){
				Toast.makeText(this, "输入不能为空~~", Toast.LENGTH_SHORT).show();
				break;
			}
			if(!password.equals(password2)){
				Toast.makeText(this, "密码不统一~~", Toast.LENGTH_SHORT).show();
				break;
			}
			if(!cb_agree.isChecked()){
				Toast.makeText(this, "请同意~~", Toast.LENGTH_SHORT).show();
				break;
			}
			SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("username", username);
			values.put("password", password);
			if(!hospital.equals("") && !hospital.equals("医院名称：")){
				values.put("hospitalName", hospital);
			}
			db.insert("user", null, values);
			//当前登录 用户  保存到SharedPreference
			SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("username", username);
			editor.putString("password", password);
			editor.commit();
			Intent mainIntent = new Intent(this,MainActivity.class);
			startActivity(mainIntent);
			ActivityController.finishAll();
			break;
		default:
			break;
		}
	}
	
}	
