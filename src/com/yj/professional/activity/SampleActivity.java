package com.yj.professional.activity;

import java.util.ArrayList;
import java.util.List;

import com.yj.professional.domain.PatientInformation;
import com.yj.professional.domain.SampleInformation;
import com.yj.professional.view.SideslipListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author liaoyao
 * ����������͹���
 */
public class SampleActivity extends Activity implements OnClickListener{
	private ImageButton btn_return;
	private Button btn_add_sample;
	private EditText et_sample_name, et_sample_descri;
	private SideslipListView lv_sample_list;
	private List<SampleInformation> sampleList;
	private SampleNameAdapter sampleAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		getActionBar().hide();//����actionbar
		initWidget();
		prepareData();
		sampleAdapter = new SampleNameAdapter();
		lv_sample_list.setAdapter(sampleAdapter);//����������
		//����item����¼�
		lv_sample_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (lv_sample_list.isAllowItemClick()) {
                	SampleInformation information = sampleList.get(position);
                	AlertDialog.Builder builder  = new Builder(SampleActivity.this);
                	builder.setTitle("������Ϣ����" ) ;
                	if(information.getSampleDescri() == null){
                		builder.setMessage(information.getSampleName());
                	}else{
                		builder.setMessage(information.getSampleName() + "  " + information.getSampleDescri());
                	}
                	builder.setPositiveButton("ȷ��" , null);
                	builder.show(); 
                }
            }
        });
		btn_return.setOnClickListener(this);
		btn_add_sample.setOnClickListener(this);
	}
	
	private void initWidget() {
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		btn_add_sample = (Button)findViewById(R.id.btn_add_sample);
		lv_sample_list = (SideslipListView)findViewById(R.id.lv_sample_list);
		et_sample_name = (EditText)findViewById(R.id.et_sample_name);
		et_sample_descri = (EditText)findViewById(R.id.et_sample_descri);
	}
	
	private void prepareData() {
		sampleList = new ArrayList<>();
		//��ȡ���ݿ��ȡ�����б�
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();//��ѯ���ݿ�  Ϊʲôд��WritableDatabase
		Cursor cursor = db.query("sample", null, null, null, null, null, null);
		//�Ȳ�ѯ�������е�����		//��Ŀǰָ��ָ����е����ݵ�һ��
		if(cursor.moveToFirst()){
			do{
				SampleInformation information = new SampleInformation();
				information.setSampleId(cursor.getInt(cursor.getColumnIndex("id")));
				information.setSampleName(cursor.getString(cursor.getColumnIndex("name")));
				information.setSampleDescri(cursor.getString(cursor.getColumnIndex("descri")));
				sampleList.add(information);
			}while(cursor.moveToNext());
		}
		cursor.close();
//		typeList.add("�ʿ��Լ�");
//		typeList.add("ȫѪ");
//		typeList.add("����������");
//		typeList.add("����������͸���ø�к͵������ữ��Ʒ");
//		typeList.add("����������������ữ��Ʒ");
//		typeList.add("����������ĸ���ø�к������ữ��Ʒ");
	}
	private boolean deletePatientById(Integer id){
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
		int result = db.delete("sample", "id = ?", new String[] {id.toString()});
		if(result > 0 ){
			return true;
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left_arrow:
			this.finish();
			break;
		case R.id.btn_add_sample:
			//�����ť�������
			SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();//���ÿ�д������ݿ�
			ContentValues values = new ContentValues();
			SampleInformation information = new SampleInformation();
			//��ʼװ��һ������  ���油����У��
			String sampleName = et_sample_name.getText().toString().trim(); 
			if(sampleName.equals("") || sampleName.equals("������")){
				Toast.makeText(SampleActivity.this, "�������Ʋ���Ϊ��Ŷ~~~", Toast.LENGTH_SHORT).show();
				return;
			}
			//����IDΨһ//��ѯ����ID�Ƿ��Ѿ������
			String sql = "select count(*) from sample where name = '" + sampleName + "'";
//			db.rawQuery("select count(*) from patient where patientId = ?", new String[]{patientId});
			SQLiteStatement statement = db.compileStatement(sql);
			long count = statement.simpleQueryForLong();
			if(count > 0){
				Toast.makeText(SampleActivity.this, "�������Ѿ���ӿ�~", Toast.LENGTH_SHORT).show();
				et_sample_name.setText("");
				return;
			}
			values.put("name", sampleName);
			information.setSampleName(sampleName);
			String sampleDescri = et_sample_descri.getText().toString().trim();
			if(!sampleDescri.equals("") && !sampleDescri.equals("������")){
				values.put("descri", sampleDescri);
				information.setSampleDescri(sampleDescri);
			}
			db.insert("sample", null, values);//�����һ������
			//��ȡ�²������ݵ�id  
			Cursor cursor = db.rawQuery("select last_insert_rowid() from sample", null);
			if(cursor.moveToFirst()){
				information.setSampleId(cursor.getInt(0));
			}
			sampleList.add(information);
			sampleAdapter.notifyDataSetChanged();
//			lv_patient_list.turnNormal();
			values.clear();
			Toast.makeText(SampleActivity.this, "��ӳɹ�", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
	/**
     * �Զ���ListView������
     */
    class SampleNameAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return sampleList.size();
        }

        @Override
        public Object getItem(int position) {
            return sampleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = View.inflate(SampleActivity.this, R.layout.layout_patient_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_sample_name = (TextView) convertView.findViewById(R.id.tv_patient_item);
                viewHolder.tv_delete = (TextView) convertView.findViewById(R.id.tv_patient_delete_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_sample_name.setText(sampleList.get(position).getSampleName());
            final int pos = position;
            viewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //ɾ��֮ǰ��Ҫ������ʾ�� ��ʾ�û���ɾ������ɾ�����еļ���¼ �����ѡ��
                	AlertDialog.Builder builder = new Builder(SampleActivity.this);
                	AlertDialog dialog;
                	builder.setTitle("���棡�û������м�¼����ɾ����");
                	builder.setMessage("��Ȼ��ִ��ɾ����");
                	builder.setPositiveButton("��������", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//�����ⲿ��ĺ���  ɾ��
		                    boolean deleteResult = deletePatientById(sampleList.get(pos).getSampleId());
		                    if(deleteResult){
			                    Toast.makeText(SampleActivity.this, "��ϲ��ɹ����߳���" + sampleList.get(pos).getSampleName(),
			                            Toast.LENGTH_SHORT).show();
			                    sampleList.remove(pos);//�����ݿ���ɾ��
//			                    notifyDataSetChanged();
			                    notifyDataSetInvalidated();
			                    lv_sample_list.turnNormal();
		                    }else{
		                    	Toast.makeText(SampleActivity.this, "������������~����ɾ������ʧ�ܣ�",
			                            Toast.LENGTH_SHORT).show();
		                    }
						}
					});
                	builder.setNegativeButton("���°�", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
                	dialog = builder.show();
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        public TextView tv_sample_name;
        public TextView tv_delete;
    }

}
