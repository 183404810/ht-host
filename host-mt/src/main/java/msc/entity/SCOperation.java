package msc.entity;

import java.io.Serializable;
import java.util.Date;

public class SCOperation implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long operationId;
	private String operation;
	private Integer value;
	private String describe;
	private String creator;
	private Date createTime;
	private String remarks;
	public Long getOperationId() {
		return operationId;
	}
	public void setOperationId(Long operatioinId) {
		this.operationId = operatioinId;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
