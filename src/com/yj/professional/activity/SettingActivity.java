package com.yj.professional.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author liaoyao
 * 用户配置页面活动
 */
public class SettingActivity extends Activity implements OnClickListener{
	private ImageButton btn_return;
	private Button btn_open_update_password_view, btn_confirm_update_password,
					btn_cancel_update_password, btn_logout;
	private TextView tv_username, tv_password, et_update_password, et_update_password2;
	private LinearLayout ll_update_password_view;
	private String username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		getActionBar().hide();//隐藏actionbar
		initWidget();
		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_WORLD_WRITEABLE);
		username = sp.getString("username", "admin");
		tv_username.setText(sp.getString("username", "admin"));
		tv_password.setText(sp.getString("password", "admin"));
		//注册按钮点击事件
		btn_return.setOnClickListener(this);
		btn_open_update_password_view.setOnClickListener(this);
		btn_confirm_update_password.setOnClickListener(this);
		btn_cancel_update_password.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
	}
	
	private void initWidget() {
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		btn_open_update_password_view = (Button)findViewById(R.id.btn_open_update_password_view);
		ll_update_password_view = (LinearLayout)findViewById(R.id.ll_update_password_view);
		btn_confirm_update_password = (Button)findViewById(R.id.btn_confirm_update_password);
		btn_cancel_update_password = (Button)findViewById(R.id.btn_cancel_update_password);
		btn_logout = (Button)findViewById(R.id.btn_logout);
		tv_username = (TextView)findViewById(R.id.tv_username);
		tv_password = (TextView)findViewById(R.id.tv_password);
		et_update_password = (TextView)findViewById(R.id.et_update_password);
		et_update_password2 = (TextView)findViewById(R.id.et_update_password2);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left_arrow:
			this.finish();
			break;
		case R.id.btn_open_update_password_view:
			ll_update_password_view.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_confirm_update_password:
			String password = et_update_password.getText().toString();
			String password2 = et_update_password2.getText().toString();
			if(password.equals("") || password2.equals("")){
				Toast.makeText(SettingActivity.this, "不能为空~", Toast.LENGTH_SHORT).show();
				break;
			}
			if(!password.equals(password2)){
				Toast.makeText(SettingActivity.this, "输入密码不一致~~", Toast.LENGTH_SHORT).show();
				break;
			}
			SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("password", password);
			int count = db.update("user", values, "username = ?", new String[]{username});
			if(count > 0){
				Toast.makeText(SettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
				loginOut();
			}			
			break;
		case R.id.btn_cancel_update_password:
			et_update_password.setText("");
			et_update_password2.setText("");
			ll_update_password_view.setVisibility(View.INVISIBLE);
			break;
		case R.id.btn_logout:
			//修改密码和注销登录都会回到登录页面，将intent栈中所有的活动销毁
			getSharedPreferences("userInfo", Context.MODE_WORLD_WRITEABLE).edit().putBoolean("isAutoLogin", false).commit();
//			Toast.makeText(SettingActivity.this, "重新登录", Toast.LENGTH_SHORT).show();
			loginOut();
			break;
		default:
			break;
		}
	}
	
	private void loginOut(){
		Intent logoutIntent = new Intent(this, LoginActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logoutIntent);
	}
	
}
