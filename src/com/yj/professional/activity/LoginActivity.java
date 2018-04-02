package com.yj.professional.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * @author liaoyao
 * µÇÂ¼Ò³Ãæ»î¶¯
 */
public class LoginActivity extends Activity implements OnClickListener{
	
	private Button btn_login, btn_turn_register;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getActionBar().hide();//Òþ²Øactionbar
		btn_login = (Button)findViewById(R.id.btn_login);
		btn_turn_register = (Button)findViewById(R.id.btn_turn_register);
		btn_login.setOnClickListener(this);
		btn_turn_register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_turn_register:
			Intent registerIntent = new Intent(this, RegisterActivity.class);
			startActivity(registerIntent);
			break;
		default:
			break;
		}
	}
	
}
