package com.yj.professional.popupwindow;

import com.yj.professional.activity.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

/**
 * @author liaoyao
 * 自定义popupwindow窗口  右上角弹出选择查看条件
 */
public class PopWindowSelectCondition extends PopupWindow {
	private View contentView;  
	private Button btn_select_patient, btn_select_sample;
//	, btn_select_date;
    public PopWindowSelectCondition(final Activity context, int w, int h, View.OnClickListener mClickListener){  
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        contentView = inflater.inflate(R.layout.popup_window_select_condition, null);  
        // 设置SelectPicPopupWindow的View  
        this.setContentView(contentView);  
        // 设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(w / 2 + 40);  
        // 设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);  
        // 设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        this.setOutsideTouchable(true);  
        // 刷新状态  
        this.update();  
        // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0000000000);  
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作  
        this.setBackgroundDrawable(dw);  
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
        // 设置SelectPicPopupWindow弹出窗体动画效果  
        this.setAnimationStyle(R.style.AnimationPreview);  
        btn_select_patient = (Button)contentView.findViewById(R.id.btn_popup_select_patient);
        btn_select_sample = (Button)contentView.findViewById(R.id.btn_popup_select_sample);
//        btn_select_date = (Button)contentView.findViewById(R.id.btn_popup_select_date);
        btn_select_patient.setOnClickListener(mClickListener);
        btn_select_sample.setOnClickListener(mClickListener);
//        btn_select_date.setOnClickListener(mClickListener);
    }  
      
    /** 
     * 显示popupWindow 
     *  
     * @param parent 
     */  
    public void showPopupWindow(View parent) {  
        if (!this.isShowing()) {  
            // 以下拉方式显示popupwindow  
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 5);  
        } else {  
            this.dismiss();  
        }  
    }  
}  
