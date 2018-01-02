package com.xiangshangban.transit_service.bean;

public class UpdateVersion {

	private String id;
	
	private String code;
	
	private String name;
	
	private String address;
	
	private String appType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public UpdateVersion() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UpdateVersion(String id, String code, String name, String address, String appType) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.address = address;
		this.appType = appType;
	}
}
