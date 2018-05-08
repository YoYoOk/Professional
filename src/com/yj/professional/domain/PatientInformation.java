package com.yj.professional.domain;

/**
 * @author liaoyao
 * 患者信息 表字段信息
 */
public class PatientInformation {
	private Integer id;
	private String patientId;
	private String patientName;
	private Integer patientGender;
	private Double patientWeight;
	private Integer patientAge;
	public PatientInformation() {}
	public PatientInformation(Integer id, String patientId, String patientName, Integer patientGender) {
		super();
		this.id = id;
		this.patientId = patientId;
		this.patientName = patientName;
		this.patientGender = patientGender;
	}
	public PatientInformation(Integer id, String patientId, String patientName, Integer patientGender,
			Double patientWeight, Integer patientAge) {
		super();
		this.id = id;
		this.patientId = patientId;
		this.patientName = patientName;
		this.patientGender = patientGender;
		this.patientWeight = patientWeight;
		this.patientAge = patientAge;
	}
	@Override
	public String toString() {
		return "PatientInformation [id=" + id + ", patientId=" + patientId + ", patientName=" + patientName
				+ ", patientGender=" + patientGender + ", patientWeight=" + patientWeight + ", patientAge=" + patientAge
				+ "]";
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public Integer getPatientGender() {
		return patientGender;
	}
	public void setPatientGender(Integer patientGender) {
		this.patientGender = patientGender;
	}
	public Double getPatientWeight() {
		return patientWeight;
	}
	public void setPatientWeight(Double patientWeight) {
		this.patientWeight = patientWeight;
	}
	public Integer getPatientAge() {
		return patientAge;
	}
	public void setPatientAge(Integer patientAge) {
		this.patientAge = patientAge;
	}
	
}
