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
 * ����������͹���
 */
public class TypeActivity extends Activity implements OnClickListener{
	private ImageButton btn_return;
	private SideslipListView lv_type_list;
	private List<String> typeList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_type);
		getActionBar().hide();//����actionbar
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		btn_return.setOnClickListener(this);
		typeList = new ArrayList<>();
		typeList.add("�ʿ��Լ�");
		typeList.add("ȫѪ");
		typeList.add("����������");
		typeList.add("����������͸���ø�к͵������ữ��Ʒ");
		typeList.add("����������������ữ��Ʒ");
		typeList.add("����������ĸ���ø�к������ữ��Ʒ");
		lv_type_list = (SideslipListView)findViewById(R.id.lv_type_list);
		lv_type_list.setAdapter(new PatientNameAdapter());//����������
		//����item����¼�
		lv_type_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (lv_type_list.isAllowItemClick()) {
                    Toast.makeText(TypeActivity.this, typeList.get(position) + "�������",
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
     * �Զ���ListView������
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
                    Toast.makeText(TypeActivity.this, typeList.get(pos) + "��ɾ����",
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
