package com.xiangshangban.transit_service.bean;

public class Client {
    private String clientId;

    private String type;

    private String imei;

    private String model;

    private String createTime;
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    
    
    public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Client() {
    }

    public Client(String clientId, String type, String imei, String model) {
        this.clientId = clientId;
        this.type = type;
        this.imei = imei;
        this.model = model;
    }
}