package com.yj.professional.domain;

import java.util.Date;

public class DetectionRecord {
	private Integer detectionId;
	private String patientName;
	private String detectionType;
	private String detectionDescri;
	private String detectionValue;
	private Date detectionDate;
	
	public DetectionRecord() {
		
	}
	public DetectionRecord(String patientName, String type, Date date){
		this.patientName = patientName;
		this.detectionType = type;
		this.detectionDate = date;
	}
	
	@Override
	public String toString() {
		return "DetectionRecord [detectionId=" + detectionId + ", patientName=" + patientName + ", detectionType="
				+ detectionType + ", detectionDescri=" + detectionDescri + ", detectionValue=" + detectionValue
				+ ", detectionDate=" + detectionDate + "]";
	}
	public Integer getDetectionId() {
		return detectionId;
	}
	public void setDetectionId(Integer detectionId) {
		this.detectionId = detectionId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getDetectionType() {
		return detectionType;
	}
	public void setDetectionType(String detectionType) {
		this.detectionType = detectionType;
	}
	public String getDetectionDescri() {
		return detectionDescri;
	}
	public void setDetectionDescri(String detectionDescri) {
		this.detectionDescri = detectionDescri;
	}
	public String getDetectionValue() {
		return detectionValue;
	}
	public void setDetectionValue(String detectionValue) {
		this.detectionValue = detectionValue;
	}
	public Date getDetectionDate() {
		return detectionDate;
	}
	public void setDetectionDate(Date detectionDate) {
		this.detectionDate = detectionDate;
	}
	
}
