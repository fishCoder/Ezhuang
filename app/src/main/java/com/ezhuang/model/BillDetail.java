package com.ezhuang.model;

public class BillDetail {

	private String bill_d_id;
	private String bill_d_main;
	private String bill_d_m_id;// 物料id
	private String bill_d_dosage;// 用量
	private String pj_bill_id;
	private String bill_d_remark;// 备注
	private String bill_d_img;//图片
	private Integer bill_d_state;// 状态 1:未下单 2:已下单 3:已发货 4:已收货

	public String getBill_d_id() {
		return bill_d_id;
	}

	public void setBill_d_id(String bill_d_id) {
		this.bill_d_id = bill_d_id;
	}

	public String getBill_d_main() {
		return bill_d_main;
	}

	public void setBill_d_main(String bill_d_main) {
		this.bill_d_main = bill_d_main;
	}

	public String getBill_d_m_id() {
		return bill_d_m_id;
	}

	public void setBill_d_m_id(String bill_d_m_id) {
		this.bill_d_m_id = bill_d_m_id;
	}

	public String getBill_d_dosage() {
		return bill_d_dosage;
	}

	public void setBill_d_dosage(String bill_d_dosage) {
		this.bill_d_dosage = bill_d_dosage;
	}

	public String getPj_bill_id() {
		return pj_bill_id;
	}

	public void setPj_bill_id(String pj_bill_id) {
		this.pj_bill_id = pj_bill_id;
	}

	public String getBill_d_remark() {
		return bill_d_remark;
	}

	public void setBill_d_remark(String bill_d_remark) {
		this.bill_d_remark = bill_d_remark;
	}

	public Integer getBill_d_state() {
		return bill_d_state;
	}

	public void setBill_d_state(Integer bill_d_state) {
		this.bill_d_state = bill_d_state;
	}

	public String getBill_d_img() {
		return bill_d_img;
	}

	public void setBill_d_img(String bill_d_img) {
		this.bill_d_img = bill_d_img;
	}

}
