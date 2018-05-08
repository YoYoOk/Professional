package com.yj.professional.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.yj.professional.domain.MyPointF;

import android.os.Environment;
import android.util.Log;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/*
 * ���ݵ��뵼��excel  ����csv����  csv������excel����
 */
public class SaveActionUtils {
	// ��ȡExcel�ļ���
	public static String getExcelDir() {
		// SD��ָ���ļ���//�õ�����/mnt/sdcard/ ��SD���ĸ�Ŀ¼
		String sdcardPath = Environment.getExternalStorageDirectory().toString();
		// File.separator ��ϵͳ�йص�Ĭ�Ϸָ���
		File dir = new File(sdcardPath + File.separator + "Excel" + File.separator + "Scan");

		if (dir.exists()) {
			return dir.toString();
			
		} else {
			// �����˳���·����ָ����Ŀ¼���������б��赫�����ڵĸ�Ŀ¼��
			dir.mkdirs();
			Log.d("BAG", "����·��������,");
			return dir.toString();
		}
	}

	public static void exportCSV(File file,List list){
		//�ֽ�list������ת����String�ĸ�ʽ
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < list.size() - 1; i++){
			sb.append(list.get(i) + ",");
		}
		sb.append(list.get(list.size() - 1));
		FileOutputStream out = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			out = new FileOutputStream(file,true);
			osw = new OutputStreamWriter(out);
			bw = new BufferedWriter(osw);
			bw.append(sb.toString()).append("\r");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
		}
	}
	public static void exportCSV(File file, MyPointF pointf){
		//�ֽ�list������ת����String�ĸ�ʽ
		String str = pointf.x + "," + pointf.y;
		FileOutputStream out = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			out = new FileOutputStream(file,true);
			osw = new OutputStreamWriter(out);
			bw = new BufferedWriter(osw);
			bw.append(str).append("\r");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
		}
	}
	
	  /* ����
     * 
     * @param file csv�ļ�(·��+�ļ�)
     * @return
     */
    public static List<String> importCsv(File file){
        List<String> dataList=new ArrayList<String>();
        
        BufferedReader br=null;
        try { 
            br = new BufferedReader(new FileReader(file));
            String line = ""; 
            while ((line = br.readLine()) != null) { 
                dataList.add(line);
            }
        }catch (Exception e) {
        }finally{
            if(br!=null){
                try {
                    br.close();
                    br=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
 
        return dataList;
    }
    
    /**
     * ɾ�������ļ�
     * @param   filePath    ��ɾ���ļ����ļ���
     * @return �ļ�ɾ���ɹ�����true�����򷵻�false
     */
    public static boolean deleteFile(String filePath) {
    	File file = new File(filePath);
        if (file.isFile() && file.exists()) {
        return file.delete();
        }
        return false;
    }
}
