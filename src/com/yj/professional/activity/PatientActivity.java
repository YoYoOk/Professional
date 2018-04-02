package com.yj.professional.activity;

import java.util.ArrayList;
import java.util.List;

import com.yj.professional.view.SideslipListView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author liaoyao
 * 患者管理页面活动
 */
public class PatientActivity extends Activity implements OnClickListener{
	
	private ImageButton btn_return;
	private SideslipListView lv_patient_list;
	private List<String> patientList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient);
		getActionBar().hide();//隐藏actionbar
		initWidget();
		patientList = new ArrayList<>();
		patientList.add("amity");
		patientList.add("lucy");
		patientList.add("jack");
		patientList.add("frank");
		patientList.add("leo");
		lv_patient_list = (SideslipListView)findViewById(R.id.lv_patient_list);
		lv_patient_list.setAdapter(new PatientNameAdapter());//设置适配器
		//设置item点击事件
		lv_patient_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (lv_patient_list.isAllowItemClick()) {
                    Toast.makeText(PatientActivity.this, patientList.get(position) + "被点击了",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        //设置item长按事件
//		lv_patient_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
//                if (lv_patient_list.isAllowItemClick()) {
//                    Toast.makeText(PatientActivity.this, patientList.get(position) + "被长按了",
//                            Toast.LENGTH_SHORT).show();
//                    return true;//返回true表示本次事件被消耗了，若返回
//                }
//                return false;
//            }
//        });
		btn_return.setOnClickListener(this);
	}

	private void initWidget() {
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left_arrow:
			this.finish();
			break;
		default:
			break;
		}
	}
	
	/**
     * 自定义ListView适配器
     */
    class PatientNameAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return patientList.size();
        }

        @Override
        public Object getItem(int position) {
            return patientList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = View.inflate(PatientActivity.this, R.layout.layout_patient_item, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_patient_item);
                viewHolder.txtv_delete = (TextView) convertView.findViewById(R.id.tv_patient_delete_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(patientList.get(position));
            final int pos = position;
            viewHolder.txtv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PatientActivity.this, patientList.get(pos) + "被删除了",
                            Toast.LENGTH_SHORT).show();
                    patientList.remove(pos);
                    notifyDataSetChanged();
                    lv_patient_list.turnNormal();
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        public TextView textView;
        public TextView txtv_delete;
    }
	
}
