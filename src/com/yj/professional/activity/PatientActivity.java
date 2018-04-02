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
 * ���߹���ҳ��
 */
public class PatientActivity extends Activity implements OnClickListener{
	
	private ImageButton btn_return;
	private SideslipListView lv_patient_list;
	private List<String> patientList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient);
		getActionBar().hide();//����actionbar
		initWidget();
		patientList = new ArrayList<>();
		patientList.add("amity");
		patientList.add("lucy");
		patientList.add("jack");
		patientList.add("frank");
		patientList.add("leo");
		lv_patient_list = (SideslipListView)findViewById(R.id.lv_patient_list);
		lv_patient_list.setAdapter(new PatientNameAdapter());//����������
		//����item����¼�
		lv_patient_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (lv_patient_list.isAllowItemClick()) {
                    Toast.makeText(PatientActivity.this, patientList.get(position) + "�������",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        //����item�����¼�
//		lv_patient_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
//                if (lv_patient_list.isAllowItemClick()) {
//                    Toast.makeText(PatientActivity.this, patientList.get(position) + "��������",
//                            Toast.LENGTH_SHORT).show();
//                    return true;//����true��ʾ�����¼��������ˣ�������
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
     * �Զ���ListView������
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
                    Toast.makeText(PatientActivity.this, patientList.get(pos) + "��ɾ����",
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
