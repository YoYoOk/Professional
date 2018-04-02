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
 * ×¢²áÒ³Ãæ»î¶¯
 */
public class RegisterActivity extends Activity implements OnClickListener{
	
	private ImageButton btn_return;
	private Button btn_register;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();//Òþ²Øactionbar
		setContentView(R.layout.activity_register);
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		btn_register = (Button)findViewById(R.id.btn_register);
		btn_return.setOnClickListener(this);
		btn_register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left_arrow:
			this.finish();
			break;
		case R.id.btn_register:
			Intent intent = new Intent(this,MainActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
}	
