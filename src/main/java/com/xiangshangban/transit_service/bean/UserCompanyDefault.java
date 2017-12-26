package com.xiangshangban.transit_service.bean;

public class UserCompanyDefault {
	// 默认公司
	public static String status_1 = "1";
	// 备选公司
	public static String status_2 = "2";
	
	private String userId;
	private String companyId;
	private String currentOption;
	private String isActive;
	private String infoStatus;
	private String type;
	
	public UserCompanyDefault(){}
	
	public UserCompanyDefault(String userId, String companyId, String currentOption, String isActive,
			String infoStatus,String type) {
		this.userId = userId;
		this.companyId = companyId;
		this.currentOption = currentOption;
		this.isActive = isActive;
		this.infoStatus = infoStatus;
		this.type = type;
	}
	
	public UserCompanyDefault(String userId, String companyId) {
		super();
		this.userId = userId;
		this.companyId = companyId;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getCurrentOption() {
		return currentOption;
	}
	public void setCurrentOption(String currentOption) {
		this.currentOption = currentOption;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getInfoStatus() {
		return infoStatus;
	}

	public void setInfoStatus(String infoStatus) {
		this.infoStatus = infoStatus;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
