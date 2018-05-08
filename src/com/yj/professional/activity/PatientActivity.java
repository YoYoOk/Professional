package com.yj.professional.activity;

import java.util.ArrayList;
import java.util.List;

import com.yj.professional.domain.PatientInformation;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author liaoyao
 * ���߹���ҳ��
 */
public class PatientActivity extends Activity implements OnClickListener{
	
	private ImageButton btn_return;
	private Button btn_add_patient, btn_delete_input;
	private EditText et_patientId, et_patientName, et_patientWeight, et_patientAge;
	private RadioButton rbtn_unit_male, rbtn_unit_female;
	private SideslipListView lv_patient_list;
	private List<PatientInformation> patientList;//�����ݿ��ȡ
	private PatientNameAdapter patientAdapter;
	private boolean isAddPatient = true;//��ǵ�ǰ���������߻����޸Ļ�����Ϣ
	private int updateIndex;//���Ҫ�޸ĵ�index
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient);
		getActionBar().hide();//����actionbar
		initWidget();
		prepareData();
		lv_patient_list = (SideslipListView)findViewById(R.id.lv_patient_list);
		patientAdapter = new PatientNameAdapter();
		lv_patient_list.setAdapter(patientAdapter);//����������
		//����item����¼�
		lv_patient_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (lv_patient_list.isAllowItemClick()) {
                	updateIndex = position;
                    btn_add_patient.setText("�޸�");
                    isAddPatient = false;
                    PatientInformation information = patientList.get(position);//����ǰ����Ļ�����Ϣ���Ե�������У�����ID�������������޸�
                    et_patientId.setText(information.getPatientId());
                    et_patientId.setCursorVisible(false);
                    et_patientId.setFocusable(false);
                    et_patientId.setFocusableInTouchMode(false);
                    et_patientName.setText(information.getPatientName());
                    et_patientName.setCursorVisible(false);
                    et_patientName.setFocusable(false);
                    et_patientName.setFocusableInTouchMode(false);
                    if(information.getPatientWeight()== null || information.getPatientWeight() == 0){
                    	et_patientWeight.getText().clear();
                    	et_patientWeight.setHint("kg");
                    }else{
                    	et_patientWeight.setText(information.getPatientWeight().toString());
                    }
                    if(information.getPatientAge() == null || information.getPatientAge() == 0){
                    	et_patientAge.getText().clear();
                    }else{
                    	et_patientAge.setText(information.getPatientAge().toString());
                    }
                    int gender = information.getPatientGender();
                    if(gender == 1){
                    	rbtn_unit_male.setChecked(true);
                    }else{
                    	rbtn_unit_female.setChecked(true);
                    }
                }
            }
        });
		btn_return.setOnClickListener(this);
		btn_add_patient.setOnClickListener(this);
		btn_delete_input.setOnClickListener(this);
	}

	private void initWidget() {
		btn_return = (ImageButton)findViewById(R.id.imgbtn_left_arrow);
		btn_add_patient = (Button)findViewById(R.id.btn_add_patient);
		btn_delete_input = (Button)findViewById(R.id.btn_delete_input);
		et_patientId = (EditText)findViewById(R.id.et_patientId);
		et_patientName = (EditText)findViewById(R.id.et_patientName);
		et_patientWeight = (EditText)findViewById(R.id.et_patientWeight);
		et_patientAge = (EditText)findViewById(R.id.et_patientAge);
		rbtn_unit_male = (RadioButton)findViewById(R.id.rbtn_unit_male);
		rbtn_unit_female = (RadioButton)findViewById(R.id.rbtn_unit_female);
	}
	
	/**
	 * ��ȡpatient�б�
	 */
	private void prepareData(){
		patientList = new ArrayList<>();
		//��ȡ���ݿ��ȡ�����б�
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();//��ѯ���ݿ�  Ϊʲôд��WritableDatabase
		Cursor cursor = db.query("patient", null, null, null, null, null, null);
		//�Ȳ�ѯ�������е�����		//��Ŀǰָ��ָ����е����ݵ�һ��
		if(cursor.moveToFirst()){
			do{
				PatientInformation information = new PatientInformation();
				information.setId(cursor.getInt(cursor.getColumnIndex("id")));
				information.setPatientId(cursor.getString(cursor.getColumnIndex("patientId")));
				information.setPatientName(cursor.getString(cursor.getColumnIndex("name")));
				information.setPatientGender(cursor.getInt(cursor.getColumnIndex("gender")));
				information.setPatientWeight(cursor.getDouble(cursor.getColumnIndex("weight")));
				information.setPatientAge(cursor.getInt(cursor.getColumnIndex("age")));
				patientList.add(information);
			}while(cursor.moveToNext());
		}
		cursor.close();
	}
	
	private boolean deletePatientAndRecordById(Integer id, String name){
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
		int result = db.delete("patient", "id = ?", new String[] {id.toString()});
		if(result > 0 ){
			//TODO bug ����Ӧ�����Ի��ߵ�IDΪΨһ��ʶ��������������������Ϊ����Ϊ�ظ� ������˾������ɣ����������ظ�����  �ߣ���
			db.delete("record", "name = ?", new String[] {name});//������bug
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
		case R.id.btn_delete_input:
			et_patientId.getText().clear();
			et_patientName.getText().clear();
			et_patientWeight.getText().clear();
			et_patientWeight.setHint("kg");
			et_patientAge.getText().clear();
			//id�����������ָ�������״̬
			et_patientId.setCursorVisible(true);
            et_patientId.setFocusable(true);
            et_patientId.setFocusableInTouchMode(true);
            et_patientId.requestFocus();
            et_patientName.setCursorVisible(true);
            et_patientName.setFocusable(true);
            et_patientName.setFocusableInTouchMode(true);
            et_patientName.requestFocus();
			rbtn_unit_male.setChecked(true);
			btn_add_patient.setText("���");
			isAddPatient = true;
			break;
		case R.id.btn_add_patient://��Ӳ���
			//�����ť�������
			SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();//���ÿ�д������ݿ�
			ContentValues values = new ContentValues();
			if(!isAddPatient){
				//˵����ǰ���޸Ļ��ߵ���Ϣ
                values.put("gender", rbtn_unit_male.isChecked() ? 1 : 0);//��-1 Ů-0
                patientList.get(updateIndex).setPatientGender(rbtn_unit_male.isChecked() ? 1 : 0);
    			String patientWeight = et_patientWeight.getText().toString().trim();
    			if(!patientWeight.equals("") && !patientWeight.equals("kg")){
    				try {
    					values.put("weight", Double.parseDouble(patientWeight));
    					patientList.get(updateIndex).setPatientWeight(Double.parseDouble(patientWeight));
    				} catch (Exception e) {
    					Toast.makeText(PatientActivity.this, "�����������󣡣�", Toast.LENGTH_SHORT).show();
    					return;
    				}
    			}
    			String patientAge = et_patientAge.getText().toString().trim();
    			if(!patientAge.equals("")){
    				try {
    					values.put("age", Integer.parseInt(patientAge));
    					patientList.get(updateIndex).setPatientAge(Integer.parseInt(patientAge));
    				} catch (Exception e) {
    					Toast.makeText(PatientActivity.this, "�����������󣡣�", Toast.LENGTH_SHORT).show();
    					return;
    				}
    			}
    			db.update("patient", values, "patientId = ?", new String[]{patientList.get(updateIndex).getPatientId()});
    			//�޸ĳɹ�֮��Ĳ���
    			btn_delete_input.performClick();//���������������ĵ���¼�
    			Toast.makeText(PatientActivity.this, "�޸ĳɹ�", Toast.LENGTH_SHORT).show();
				return;
			}
			PatientInformation information = new PatientInformation();
			//��ʼװ��һ������  ���油����У��
			String patientId = et_patientId.getText().toString().trim(); 
			if(patientId.equals("")){
				Toast.makeText(PatientActivity.this, "����ID����Ϊ��Ŷ~~~", Toast.LENGTH_SHORT).show();
				return;
			}
			//����IDΨһ//��ѯ����ID�Ƿ��Ѿ������
			String sql = "select count(*) from patient where patientId = '" + patientId + "'";
//			db.rawQuery("select count(*) from patient where patientId = ?", new String[]{patientId});
			SQLiteStatement statement = db.compileStatement(sql);
			long count = statement.simpleQueryForLong();
			if(count > 0){
				Toast.makeText(PatientActivity.this, "�û����Ѿ���ӿ�~", Toast.LENGTH_SHORT).show();
				et_patientId.setText("");
				return;
			}
			if(et_patientName.getText().toString().equals("")){
				Toast.makeText(PatientActivity.this, "������������Ϊ��Ŷ~~~", Toast.LENGTH_SHORT).show();
				return;
			}
			values.put("patientId", et_patientId.getText().toString().trim());
			information.setPatientId(et_patientId.getText().toString().trim());
			values.put("name", et_patientName.getText().toString().trim());
			information.setPatientName(et_patientName.getText().toString().trim());
			values.put("gender", rbtn_unit_male.isChecked() ? 1 : 0);//��-1 Ů-0
			information.setPatientGender(rbtn_unit_male.isChecked() ? 1 : 0);
			String patientWeight = et_patientWeight.getText().toString().trim();
			if(!patientWeight.equals("") && !patientWeight.equals("kg")){
				try {
					values.put("weight", Double.parseDouble(patientWeight));
					information.setPatientWeight(Double.parseDouble(patientWeight));
				} catch (Exception e) {
					Toast.makeText(PatientActivity.this, "�����������󣡣�", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			String patientAge = et_patientAge.getText().toString().trim();
			if(!patientAge.equals("")){
				try {
					values.put("age", Integer.parseInt(patientAge));
					information.setPatientAge(Integer.parseInt(patientAge));
				} catch (Exception e) {
					Toast.makeText(PatientActivity.this, "�����������󣡣�", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			db.insert("patient", null, values);//�����һ������
			//��ȡ�²������ݵ�id  
			Cursor cursor = db.rawQuery("select last_insert_rowid() from patient", null);
			if(cursor.moveToFirst()){
				information.setId(cursor.getInt(0));
			}
			patientList.add(information);
			patientAdapter.notifyDataSetChanged();
//			lv_patient_list.turnNormal();
			values.clear();
			btn_delete_input.performClick();//���������������ĵ���¼�
			Toast.makeText(PatientActivity.this, "��ӳɹ�", Toast.LENGTH_SHORT).show();
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
                viewHolder.tv_patient_item = (TextView) convertView.findViewById(R.id.tv_patient_item);
                viewHolder.tv_patient_delete_item = (TextView) convertView.findViewById(R.id.tv_patient_delete_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_patient_item.setText(patientList.get(position).getPatientName());
            final int pos = position;
            viewHolder.tv_patient_delete_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	//ɾ��֮ǰ��Ҫ������ʾ�� ��ʾ�û���ɾ������ɾ�����еļ���¼ �����ѡ��
                	AlertDialog.Builder builder = new Builder(PatientActivity.this);
                	AlertDialog dialog;
                	builder.setTitle("���棡�û������м�¼����ɾ����");
                	builder.setMessage("��Ȼ��ִ��ɾ����");
                	builder.setPositiveButton("��������", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//�����ⲿ��ĺ���  ɾ��
		                    boolean deleteResult = deletePatientAndRecordById(patientList.get(pos).getId(), patientList.get(pos).getPatientName());
		                    if(deleteResult){
			                    Toast.makeText(PatientActivity.this, "��ϲ��ɹ����߳���" + patientList.get(pos).getPatientName(),
			                            Toast.LENGTH_SHORT).show();
			                    patientList.remove(pos);//�����ݿ���ɾ��
//			                    notifyDataSetChanged();
			                    notifyDataSetInvalidated();
			                    lv_patient_list.turnNormal();
		                    }else{
		                    	Toast.makeText(PatientActivity.this, "������������~����ɾ������ʧ�ܣ�",
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
        public TextView tv_patient_item;
        public TextView tv_patient_delete_item;
    }
	
}
