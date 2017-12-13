package ht.msc.util;

public class ImportRequest {
	
	private String colNames;
	
	private String  mustArray;
	
	private String mainKey;
	
	private String validationConditions;

	private String isValidateAll;
	
	private  Integer sheetIdx;
	
	private Integer firstLineIdx;
	

	public String getColNames() {
		return colNames;
	}

	public void setColNames(String colNames) {
		this.colNames = colNames;
	}

	public String getMustArray() {
		return mustArray;
	}

	public void setMustArray(String mustArray) {
		this.mustArray = mustArray;
	}

	

	public String getMainKey() {
		return mainKey;
	}

	public void setMainKey(String mainKey) {
		this.mainKey = mainKey;
	}



	public String getValidationConditions() {
		return validationConditions;
	}

	public void setValidationConditions(String validationConditions) {
		this.validationConditions = validationConditions;
	}

	public String getIsValidateAll() {
		return isValidateAll;
	}

	public void setIsValidateAll(String isValidateAll) {
		this.isValidateAll = isValidateAll;
	}

	public Integer getSheetIdx() {
		return sheetIdx;
	}

	public void setSheetIdx(Integer sheetIdx) {
		this.sheetIdx = sheetIdx;
	}

	public Integer getFirstLineIdx() {
		return firstLineIdx;
	}

	public void setFirstLineIdx(Integer firstLineIdx) {
		this.firstLineIdx = firstLineIdx;
	}

}
