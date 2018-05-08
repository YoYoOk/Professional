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
 * 患者管理页面活动
 */
public class PatientActivity extends Activity implements OnClickListener{
	
	private ImageButton btn_return;
	private Button btn_add_patient, btn_delete_input;
	private EditText et_patientId, et_patientName, et_patientWeight, et_patientAge;
	private RadioButton rbtn_unit_male, rbtn_unit_female;
	private SideslipListView lv_patient_list;
	private List<PatientInformation> patientList;//从数据库读取
	private PatientNameAdapter patientAdapter;
	private boolean isAddPatient = true;//标记当前是新增患者还是修改患者信息
	private int updateIndex;//点击要修改的index
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient);
		getActionBar().hide();//隐藏actionbar
		initWidget();
		prepareData();
		lv_patient_list = (SideslipListView)findViewById(R.id.lv_patient_list);
		patientAdapter = new PatientNameAdapter();
		lv_patient_list.setAdapter(patientAdapter);//设置适配器
		//设置item点击事件
		lv_patient_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (lv_patient_list.isAllowItemClick()) {
                	updateIndex = position;
                    btn_add_patient.setText("修改");
                    isAddPatient = false;
                    PatientInformation information = patientList.get(position);//将当前点击的患者信息回显到上面框中，并且ID和姓名不允许修改
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
	 * 读取patient列表
	 */
	private void prepareData(){
		patientList = new ArrayList<>();
		//读取数据库获取患者列表
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();//查询数据库  为什么写入WritableDatabase
		Cursor cursor = db.query("patient", null, null, null, null, null, null);
		//先查询表中所有的数据		//将目前指针指向表中的数据第一行
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
			//TODO bug 患者应该是以患者的ID为唯一标识，，而不是以姓名。因为姓名为重复 不想改了就这样吧！！！不管重复病人  哼！！
			db.delete("record", "name = ?", new String[] {name});//这里有bug
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
			//id和姓名输入框恢复可输入状态
			et_patientId.setCursorVisible(true);
            et_patientId.setFocusable(true);
            et_patientId.setFocusableInTouchMode(true);
            et_patientId.requestFocus();
            et_patientName.setCursorVisible(true);
            et_patientName.setFocusable(true);
            et_patientName.setFocusableInTouchMode(true);
            et_patientName.requestFocus();
			rbtn_unit_male.setChecked(true);
			btn_add_patient.setText("添加");
			isAddPatient = true;
			break;
		case R.id.btn_add_patient://添加操作
			//点击按钮添加数据
			SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();//调用可写入的数据库
			ContentValues values = new ContentValues();
			if(!isAddPatient){
				//说明当前是修改患者的信息
                values.put("gender", rbtn_unit_male.isChecked() ? 1 : 0);//男-1 女-0
                patientList.get(updateIndex).setPatientGender(rbtn_unit_male.isChecked() ? 1 : 0);
    			String patientWeight = et_patientWeight.getText().toString().trim();
    			if(!patientWeight.equals("") && !patientWeight.equals("kg")){
    				try {
    					values.put("weight", Double.parseDouble(patientWeight));
    					patientList.get(updateIndex).setPatientWeight(Double.parseDouble(patientWeight));
    				} catch (Exception e) {
    					Toast.makeText(PatientActivity.this, "体重输入有误！！", Toast.LENGTH_SHORT).show();
    					return;
    				}
    			}
    			String patientAge = et_patientAge.getText().toString().trim();
    			if(!patientAge.equals("")){
    				try {
    					values.put("age", Integer.parseInt(patientAge));
    					patientList.get(updateIndex).setPatientAge(Integer.parseInt(patientAge));
    				} catch (Exception e) {
    					Toast.makeText(PatientActivity.this, "年龄输入有误！！", Toast.LENGTH_SHORT).show();
    					return;
    				}
    			}
    			db.update("patient", values, "patientId = ?", new String[]{patientList.get(updateIndex).getPatientId()});
    			//修改成功之后的操作
    			btn_delete_input.performClick();//触发清空所有输入的点击事件
    			Toast.makeText(PatientActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
				return;
			}
			PatientInformation information = new PatientInformation();
			//开始装第一条数据  后面补数据校验
			String patientId = et_patientId.getText().toString().trim(); 
			if(patientId.equals("")){
				Toast.makeText(PatientActivity.this, "患者ID不能为空哦~~~", Toast.LENGTH_SHORT).show();
				return;
			}
			//患者ID唯一//查询患者ID是否已经添加了
			String sql = "select count(*) from patient where patientId = '" + patientId + "'";
//			db.rawQuery("select count(*) from patient where patientId = ?", new String[]{patientId});
			SQLiteStatement statement = db.compileStatement(sql);
			long count = statement.simpleQueryForLong();
			if(count > 0){
				Toast.makeText(PatientActivity.this, "该患者已经添加咯~", Toast.LENGTH_SHORT).show();
				et_patientId.setText("");
				return;
			}
			if(et_patientName.getText().toString().equals("")){
				Toast.makeText(PatientActivity.this, "患者姓名不能为空哦~~~", Toast.LENGTH_SHORT).show();
				return;
			}
			values.put("patientId", et_patientId.getText().toString().trim());
			information.setPatientId(et_patientId.getText().toString().trim());
			values.put("name", et_patientName.getText().toString().trim());
			information.setPatientName(et_patientName.getText().toString().trim());
			values.put("gender", rbtn_unit_male.isChecked() ? 1 : 0);//男-1 女-0
			information.setPatientGender(rbtn_unit_male.isChecked() ? 1 : 0);
			String patientWeight = et_patientWeight.getText().toString().trim();
			if(!patientWeight.equals("") && !patientWeight.equals("kg")){
				try {
					values.put("weight", Double.parseDouble(patientWeight));
					information.setPatientWeight(Double.parseDouble(patientWeight));
				} catch (Exception e) {
					Toast.makeText(PatientActivity.this, "体重输入有误！！", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			String patientAge = et_patientAge.getText().toString().trim();
			if(!patientAge.equals("")){
				try {
					values.put("age", Integer.parseInt(patientAge));
					information.setPatientAge(Integer.parseInt(patientAge));
				} catch (Exception e) {
					Toast.makeText(PatientActivity.this, "年龄输入有误！！", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			db.insert("patient", null, values);//插入第一条数据
			//获取新插入数据的id  
			Cursor cursor = db.rawQuery("select last_insert_rowid() from patient", null);
			if(cursor.moveToFirst()){
				information.setId(cursor.getInt(0));
			}
			patientList.add(information);
			patientAdapter.notifyDataSetChanged();
//			lv_patient_list.turnNormal();
			values.clear();
			btn_delete_input.performClick();//触发清空所有输入的点击事件
			Toast.makeText(PatientActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
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
                	//删除之前需要弹出警示框 提示用户若删除将会删除所有的检测记录 请谨慎选择
                	AlertDialog.Builder builder = new Builder(PatientActivity.this);
                	AlertDialog dialog;
                	builder.setTitle("警告！该患者所有记录将会删除！");
                	builder.setMessage("依然固执的删除吗？");
                	builder.setPositiveButton("残忍抛弃", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//调用外部类的函数  删除
		                    boolean deleteResult = deletePatientAndRecordById(patientList.get(pos).getId(), patientList.get(pos).getPatientName());
		                    if(deleteResult){
			                    Toast.makeText(PatientActivity.this, "恭喜你成功的踢出了" + patientList.get(pos).getPatientName(),
			                            Toast.LENGTH_SHORT).show();
			                    patientList.remove(pos);//从数据库中删除
//			                    notifyDataSetChanged();
			                    notifyDataSetInvalidated();
			                    lv_patient_list.turnNormal();
		                    }else{
		                    	Toast.makeText(PatientActivity.this, "遭遇不明攻击~导致删除任务失败！",
			                            Toast.LENGTH_SHORT).show();
		                    }
						}
					});
                	builder.setNegativeButton("留下吧", new DialogInterface.OnClickListener() {
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
