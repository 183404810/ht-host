package msc.entity;

import java.io.Serializable;
import java.util.Date;

import org.ietf.jgss.Oid;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.alibaba.fastjson.annotation.JSONField;

@Document(collection = "basDivision")
public class Test implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Oid _id;
	
	@Field(value="divisionNo")
	private String divisionNo;
	
	@Field(value="divisionName")
	private String divisionName;
	
	@Field(value="creator")
	private String creator;
	
	@Field(value="modifier")
	private String modifier;
	
	@Field(value="createTime")
	@JSONField(format="yyyy-MM-dd")
	private Date createTime;
	
	@Field(value="modifyTime")
	@JSONField(format="yyyy-MM-dd")
	private Date modifyTime;
	
	public Oid get_id() {
		return _id;
	}
	public void set_id(Oid _id) {
		this._id = _id;
	}
	public String getDivisionNo() {
		return divisionNo;
	}
	public void setDivisionNo(String divisionNo) {
		this.divisionNo = divisionNo;
	}
	public String getDivisionName() {
		return divisionName;
	}
	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
}
