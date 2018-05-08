package com.yj.professional.domain;

/**
 * @author liaoyao
 * 样本信息 表字段信息
 */
public class SampleInformation {
	private Integer sampleId;
	private String sampleName;
	private String sampleDescri;
	public SampleInformation() {
	}
	public SampleInformation(String sampleName) {
		super();
		this.sampleName = sampleName;
	}
	public SampleInformation(String sampleName, String sampleDescri) {
		super();
		this.sampleName = sampleName;
		this.sampleDescri = sampleDescri;
	}
	@Override
	public String toString() {
		return "SampleInformation [sampleId=" + sampleId + ", sampleName=" + sampleName + ", sampleDescri="
				+ sampleDescri + "]";
	}
	public Integer getSampleId() {
		return sampleId;
	}
	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}
	public String getSampleName() {
		return sampleName;
	}
	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	public String getSampleDescri() {
		return sampleDescri;
	}
	public void setSampleDescri(String sampleDescri) {
		this.sampleDescri = sampleDescri;
	}
	
}
