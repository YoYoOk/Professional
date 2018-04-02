package com.yj.professional.activity;

import java.util.ArrayList;
import java.util.List;

import com.yj.professional.activity.PatientActivity.PatientNameAdapter;
import com.yj.professional.activity.PatientActivity.ViewHolder;
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
 * 检测样本类型管理活动
 */
public class TypeActivity extends Activity implements OnClickListener{
	private ImageButton btn_return;
	private SideslipListView lv_type_list;
	private List<String> typeList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_type);
		getActionBar().hide();//隐藏actionbar
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		btn_return.setOnClickListener(this);
		typeList = new ArrayList<>();
		typeList.add("质控试剂");
		typeList.add("全血");
		typeList.add("高岭土激活");
		typeList.add("高岭土激活和肝素酶中和的枸橼酸化样品");
		typeList.add("高岭土激活的枸橼酸化样品");
		typeList.add("高岭土激活的肝素酶中和枸橼酸化样品");
		lv_type_list = (SideslipListView)findViewById(R.id.lv_type_list);
		lv_type_list.setAdapter(new PatientNameAdapter());//设置适配器
		//设置item点击事件
		lv_type_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (lv_type_list.isAllowItemClick()) {
                    Toast.makeText(TypeActivity.this, typeList.get(position) + "被点击了",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            return typeList.size();
        }

        @Override
        public Object getItem(int position) {
            return typeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = View.inflate(TypeActivity.this, R.layout.layout_patient_item, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_patient_item);
                viewHolder.txtv_delete = (TextView) convertView.findViewById(R.id.tv_patient_delete_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(typeList.get(position));
            final int pos = position;
            viewHolder.txtv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TypeActivity.this, typeList.get(pos) + "被删除了",
                            Toast.LENGTH_SHORT).show();
                    typeList.remove(pos);
                    notifyDataSetChanged();
                    lv_type_list.turnNormal();
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
