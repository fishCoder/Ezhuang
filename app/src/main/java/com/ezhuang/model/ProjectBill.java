package com.ezhuang.model;

import java.util.Date;
import java.util.List;

public class ProjectBill {
	private String pj_bill_id;
	private String pj_bill_name;
	private Date pj_bill_time;
	private float pj_bill_acount;
	private short pj_bill_state;// 订单状态  0:待审核员审核 1:待采购审核 2:已下单
	private String pj_id;
	private String pj_bill_remark;
	private String pj_bill_code;
	private List<BillDetail> pj_bill_details;

	public String getPj_bill_id() {
		return pj_bill_id;
	}

	public void setPj_bill_id(String pj_bill_id) {
		this.pj_bill_id = pj_bill_id;
	}

	public String getPj_bill_name() {
		return pj_bill_name;
	}

	public void setPj_bill_name(String pj_bill_name) {
		this.pj_bill_name = pj_bill_name;
	}

	public Date getPj_bill_time() {
		return pj_bill_time;
	}

	public void setPj_bill_time(Date pj_bill_time) {
		this.pj_bill_time = pj_bill_time;
	}

	public float getPj_bill_acount() {
		return pj_bill_acount;
	}

	public void setPj_bill_acount(float pj_bill_acount) {
		this.pj_bill_acount = pj_bill_acount;
	}

	public short getPj_bill_state() {
		return pj_bill_state;
	}

	public void setPj_bill_state(short pj_bill_state) {
		this.pj_bill_state = pj_bill_state;
	}

	public String getPj_id() {
		return pj_id;
	}

	public void setPj_id(String pj_id) {
		this.pj_id = pj_id;
	}

	public String getPj_bill_remark() {
		return pj_bill_remark;
	}

	public void setPj_bill_remark(String pj_bill_remark) {
		this.pj_bill_remark = pj_bill_remark;
	}

	public List<BillDetail> getPj_bill_details() {
		return pj_bill_details;
	}

	public void setPj_bill_details(List<BillDetail> pj_bill_details) {
		this.pj_bill_details = pj_bill_details;
	}

	public String getPj_bill_code() {
		return pj_bill_code;
	}

	public void setPj_bill_code(String pj_bill_code) {
		this.pj_bill_code = pj_bill_code;
	}

	@Override
	public String toString() {
		return "ProjectBill [pj_bill_id=" + pj_bill_id + ", pj_bill_name="
				+ pj_bill_name + ", pj_bill_time=" + pj_bill_time
				+ ", pj_bill_acount=" + pj_bill_acount + ", pj_bill_state="
				+ pj_bill_state + ", pj_id=" + pj_id + ", pj_bill_remark="
				+ pj_bill_remark + ", pj_bill_details=" + pj_bill_details + "]";
	}

}
