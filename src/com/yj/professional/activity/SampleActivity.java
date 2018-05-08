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
 * 检测样本类型管理活动
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
		getActionBar().hide();//隐藏actionbar
		initWidget();
		prepareData();
		sampleAdapter = new SampleNameAdapter();
		lv_sample_list.setAdapter(sampleAdapter);//设置适配器
		//设置item点击事件
		lv_sample_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (lv_sample_list.isAllowItemClick()) {
                	SampleInformation information = sampleList.get(position);
                	AlertDialog.Builder builder  = new Builder(SampleActivity.this);
                	builder.setTitle("样本信息详情" ) ;
                	if(information.getSampleDescri() == null){
                		builder.setMessage(information.getSampleName());
                	}else{
                		builder.setMessage(information.getSampleName() + "  " + information.getSampleDescri());
                	}
                	builder.setPositiveButton("确认" , null);
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
		//读取数据库获取患者列表
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();//查询数据库  为什么写入WritableDatabase
		Cursor cursor = db.query("sample", null, null, null, null, null, null);
		//先查询表中所有的数据		//将目前指针指向表中的数据第一行
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
//		typeList.add("质控试剂");
//		typeList.add("全血");
//		typeList.add("高岭土激活");
//		typeList.add("高岭土激活和肝素酶中和的枸橼酸化样品");
//		typeList.add("高岭土激活的枸橼酸化样品");
//		typeList.add("高岭土激活的肝素酶中和枸橼酸化样品");
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
			//点击按钮添加数据
			SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();//调用可写入的数据库
			ContentValues values = new ContentValues();
			SampleInformation information = new SampleInformation();
			//开始装第一条数据  后面补数据校验
			String sampleName = et_sample_name.getText().toString().trim(); 
			if(sampleName.equals("") || sampleName.equals("请输入")){
				Toast.makeText(SampleActivity.this, "样本名称不能为空哦~~~", Toast.LENGTH_SHORT).show();
				return;
			}
			//患者ID唯一//查询患者ID是否已经添加了
			String sql = "select count(*) from sample where name = '" + sampleName + "'";
//			db.rawQuery("select count(*) from patient where patientId = ?", new String[]{patientId});
			SQLiteStatement statement = db.compileStatement(sql);
			long count = statement.simpleQueryForLong();
			if(count > 0){
				Toast.makeText(SampleActivity.this, "该样本已经添加咯~", Toast.LENGTH_SHORT).show();
				et_sample_name.setText("");
				return;
			}
			values.put("name", sampleName);
			information.setSampleName(sampleName);
			String sampleDescri = et_sample_descri.getText().toString().trim();
			if(!sampleDescri.equals("") && !sampleDescri.equals("请输入")){
				values.put("descri", sampleDescri);
				information.setSampleDescri(sampleDescri);
			}
			db.insert("sample", null, values);//插入第一条数据
			//获取新插入数据的id  
			Cursor cursor = db.rawQuery("select last_insert_rowid() from sample", null);
			if(cursor.moveToFirst()){
				information.setSampleId(cursor.getInt(0));
			}
			sampleList.add(information);
			sampleAdapter.notifyDataSetChanged();
//			lv_patient_list.turnNormal();
			values.clear();
			Toast.makeText(SampleActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
	/**
     * 自定义ListView适配器
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
                  //删除之前需要弹出警示框 提示用户若删除将会删除所有的检测记录 请谨慎选择
                	AlertDialog.Builder builder = new Builder(SampleActivity.this);
                	AlertDialog dialog;
                	builder.setTitle("警告！该患者所有记录将会删除！");
                	builder.setMessage("依然固执的删除吗？");
                	builder.setPositiveButton("残忍抛弃", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//调用外部类的函数  删除
		                    boolean deleteResult = deletePatientById(sampleList.get(pos).getSampleId());
		                    if(deleteResult){
			                    Toast.makeText(SampleActivity.this, "恭喜你成功的踢出了" + sampleList.get(pos).getSampleName(),
			                            Toast.LENGTH_SHORT).show();
			                    sampleList.remove(pos);//从数据库中删除
//			                    notifyDataSetChanged();
			                    notifyDataSetInvalidated();
			                    lv_sample_list.turnNormal();
		                    }else{
		                    	Toast.makeText(SampleActivity.this, "遭遇不明攻击~导致删除任务失败！",
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
        public TextView tv_sample_name;
        public TextView tv_delete;
    }

}
