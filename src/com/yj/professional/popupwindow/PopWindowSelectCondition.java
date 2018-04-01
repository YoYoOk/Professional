package com.yj.professional.popupwindow;

import com.yj.professional.activity.R;

import android.app.Activity;
import android.content.Context;
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
public class PopWindowSelectCondition extends PopupWindow {
	private View conentView;  
	private Button btn_test;
    public PopWindowSelectCondition(final Activity context, int w, int h){  
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        conentView = inflater.inflate(R.layout.popup_window_select_condition, null);  
        // ����SelectPicPopupWindow��View  
        this.setContentView(conentView);  
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
        btn_test = (Button)conentView.findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "ehheh", Toast.LENGTH_SHORT).show();
				PopWindowSelectCondition.this.dismiss();  
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
}  
