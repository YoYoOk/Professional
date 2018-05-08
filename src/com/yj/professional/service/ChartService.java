package com.yj.professional.service;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.yj.professional.activity.R;

import android.content.Context;
import android.graphics.Color;

/*
 * achartengine��ͼ������
 */
public class ChartService {
	private GraphicalView mGraphicalView;
	private XYMultipleSeriesDataset dataset;// ���ݼ�����
	private XYMultipleSeriesRenderer renderer;// ��Ⱦ��
	private XYSeries mSeries;//�����������ݼ�
	private XYSeriesRenderer mRender;//����������Ⱦ
	private Context context;
//	private int count = 0;
	
	public ChartService(Context context) {
		this.context = context;
	}

	// ��ȡͼ��
	public GraphicalView getGraphicalView() {
		this.mGraphicalView = ChartFactory.getLineChartView(context, dataset, renderer);
		return mGraphicalView;
	}

	// ��ȡ���ݼ� ��xy����ļ���
	public void setXYMultipleSeriesDataset(String curveTitle) {
		this.dataset = new XYMultipleSeriesDataset();
		mSeries = new XYSeries(curveTitle);
		dataset.addSeries(mSeries);
	}

	public void setXYMultipleSeriesRenderer(double maxX, double minX, String xTitle, String yTitle) {
		renderer = new XYMultipleSeriesRenderer();
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		// ֻ����x��ķ�Χ
		renderer.setXAxisMax(maxX);
		renderer.setXAxisMin(minX);
		renderer.setXLabels(10);
		renderer.setYLabels(10);
		renderer.setLabelsTextSize(20);// ��������̶������С
		renderer.setLabelsColor(Color.RED);
		renderer.setPointSize(0.4f);// �������ߴ�
//		renderer.setZoomButtonsVisible(true);
		renderer.setBackgroundColor(Color.parseColor("#00000000"));// ����ɫ
		renderer.setApplyBackgroundColor(true);
		renderer.setMarginsColor(Color.parseColor("#2688AF"));// �߾౳��ɫ��Ĭ�ϱ���ɫΪ��ɫ�������޸�Ϊ��ɫ
		mRender = new XYSeriesRenderer();
		renderer.addSeriesRenderer(mRender);
	}

	public void setXYMultipleSeriesRenderer(String xTitle, String yTitle) {
		renderer = new XYMultipleSeriesRenderer();
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		// ֻ����x��ķ�Χ
		// renderer.setXAxisMax(maxX);
		// renderer.setXAxisMin(minX);
		renderer.setXLabels(10);
		renderer.setYLabels(10);
		renderer.setLabelsTextSize(20);// ��������̶������С
		renderer.setLabelsColor(Color.RED);
		renderer.setPointSize(0.4f);// �������ߴ�
//		renderer.setZoomButtonsVisible(true);
		renderer.setBackgroundColor(Color.parseColor("#00000000"));// ����ɫ
		renderer.setApplyBackgroundColor(true);
//		renderer.setMarginsColor(Color.TRANSPARENT);// �߾౳��ɫ��Ĭ�ϱ���ɫΪ��ɫ�������޸�Ϊ��ɫ
		renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0));// ����4������͸��  
		mRender = new XYSeriesRenderer();
		renderer.addSeriesRenderer(mRender);
	}
	// �����¼ӵ����ݣ��������ߣ�ֻ�����������߳�
	public void updateChart(double x, double y) {
		// ���count=0 ���������Ҫ�Ȱ�֮ǰ���������
//		this.dataset.getSeriesAt(count).add(x, y);
		mSeries.add(x, y);
		this.mGraphicalView.repaint();

	}
	//�¼�ȫ��������
	public void updateChart(List<Double> xList,List<Double> yList){
		for(int i = 0; i < xList.size(); i++){
			mSeries.add(xList.get(i), yList.get(i));
		}
		this.mGraphicalView.repaint();
	}
	//��������һ����������ֵ
	public void updateRender(double maxX, double minX){
		if(this.renderer.getXAxisMax() != maxX){
			this.renderer.setXAxisMax(maxX);
		}
		if(this.renderer.getXAxisMin() != minX){
			this.renderer.setXAxisMin(minX);
		}
		this.mGraphicalView.repaint();
	}
	
	
	// ÿ���һ�����߶�Ӧ����� һ��XYSeries �� XYSeriesRenderer
//	public void updateChart() {
//		this.dataset.addSeries(new XYSeries(count + 1 + ""));
//		this.renderer.addSeriesRenderer(new XYSeriesRenderer());
//	}

	// ֻ��һ�����ߵ�ʱ�� ÿ�ζ�Ҫ����title Ȼ���������
//	public void clearValue(String title) {
//		this.dataset.getSeriesAt(count).clear();
//		this.dataset.getSeriesAt(count).setTitle(title);
//		this.mGraphicalView.repaint();
//	}
	public void clearValue(){
		this.mSeries.clearSeriesValues();//�������
		this.mGraphicalView.repaint();
	}
	
//	public int getCount() {
//		return count;
//	}
//
//	public void setCount(int count) {
//		this.count = count;
//	}

	public XYMultipleSeriesDataset getDataset() {
		return dataset;
	}

	public void setDataset(XYMultipleSeriesDataset dataset) {
		this.dataset = dataset;
	}

	public XYMultipleSeriesRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(XYMultipleSeriesRenderer renderer) {
		this.renderer = renderer;
	}

	public XYSeries getmSeries() {
		return mSeries;
	}

	public void setmSeries(XYSeries mSeries) {
		this.mSeries = mSeries;
	}

	public XYSeriesRenderer getmRender() {
		return mRender;
	}

	public void setmRender(XYSeriesRenderer mRender) {
		this.mRender = mRender;
	}
}
