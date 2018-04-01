package com.yj.professional.popupwindow;

import com.yj.professional.activity.ParamsSettingActivity;
import com.yj.professional.activity.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * @author liaoyao
 * �Զ���popupwindow����  ���Ͻǵ���ѡ��鿴����
 */
public class PopWindowSelectBluetooth extends PopupWindow implements OnClickListener{
	private Activity context;
	private View contentView;  
	private Button btn_test;
	private Button btn_params_setting;
	
    public PopWindowSelectBluetooth(final Activity context, int w, int h){  
    	this.context = context;
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        contentView = inflater.inflate(R.layout.popup_window_select_bluetooth, null);  
        // ����SelectPicPopupWindow��View  
        this.setContentView(contentView);  
        // ����SelectPicPopupWindow��������Ŀ�  
        this.setWidth(w / 2 + 40);  
        // ����SelectPicPopupWindow��������ĸ�  
        this.setHeight(LayoutParams.WRAP_CONTENT);  
        // ����SelectPicPopupWindow��������ɵ��  
        this.setFocusable(true);  
        this.setOutsideTouchable(true);  
        // ˢ��״̬  
        this.update();  
        // ʵ����һ��ColorDrawable��ɫΪ��͸��  
        ColorDrawable dw = new ColorDrawable(0000000000);  
        // ��back���������ط�ʹ����ʧ,������������ܴ���OnDismisslistener �����������ؼ��仯�Ȳ���  
        this.setBackgroundDrawable(dw);  
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
        // ����SelectPicPopupWindow�������嶯��Ч��  
        this.setAnimationStyle(R.style.AnimationPreview);  
        btn_test = (Button)contentView.findViewById(R.id.btn_connect_bt);
        btn_params_setting = (Button)contentView.findViewById(R.id.btn_params_setting);
        btn_params_setting.setOnClickListener(this);
        btn_test.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "ehheh", Toast.LENGTH_SHORT).show();
				PopWindowSelectBluetooth.this.dismiss();  
			}
		});
    }  
      
    /** 
     * ��ʾpopupWindow 
     *  
     * @param parent 
     */  
    public void showPopupWindow(View parent) {  
        if (!this.isShowing()) {  
            // ��������ʽ��ʾpopupwindow  
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 5);  
        } else {  
            this.dismiss();  
        }  
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_params_setting:
			Intent intent = new Intent(context, ParamsSettingActivity.class);
			context.startActivity(intent);
			this.dismiss();
			break;

		default:
			break;
		}
	}  
}  
