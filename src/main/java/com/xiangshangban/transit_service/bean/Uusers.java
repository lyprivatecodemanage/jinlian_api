package com.xiangshangban.transit_service.bean;

import java.util.List;

public class Uusers {
	 //禁止登录
    public static String status_0 = "0";
    //允许登录
    public static String status_1 = "1";
	
    private String userid;
    private String account;
    private String userpwd;
    private String temporarypwd;
    private String salt;
    private String username;
    private String phone;
    private String companyId;
    private String createTime;
    private String lastLoginTime;
    private String status;
    private String wechatId;
    private String wechatCode;
    private String wechatName;
    private String isActive;
    private String departmentId;
    private String operaterTime;
    private List<Uroles> UrolesList;

    public Uusers() {
	}

	public Uusers(String userid, String account, String userpwd, String temporarypwd, String salt, String username,
			String phone, String createTime, String lastLoginTime, String status, String wechatId,
			String wechatCode, String wechatName, List<Uroles> urolesList) {
		this.userid = userid;
		this.account = account;
		this.userpwd = userpwd;
		this.temporarypwd = temporarypwd;
		this.salt = salt;
		this.username = username;
		this.phone = phone;
		this.createTime = createTime;
		this.lastLoginTime = lastLoginTime;
		this.status = status;
		this.wechatId = wechatId;
		this.wechatCode = wechatCode;
		this.wechatName = wechatName;
		UrolesList = urolesList;
	}
	
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username== null ? null : username.trim();
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getWechatId() {
		return wechatId;
	}

	public void setWechatId(String wechatId) {
		this.wechatId = wechatId== null ? null : wechatId.trim();
	}

	public String getWechatCode() {
		return wechatCode;
	}

	public void setWechatCode(String wechatCode) {
		this.wechatCode = wechatCode== null ? null : wechatCode.trim();
	}

	public String getWechatName() {
		return wechatName;
	}

	public void setWechatName(String wechatName) {
		this.wechatName = wechatName== null ? null : wechatName.trim();
	}

	public List<Uroles> getUrolesList() {
		return UrolesList;
	}

	public void setUrolesList(List<Uroles> urolesList) {
		UrolesList = urolesList;
	}

	public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid== null ? null : userid.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account== null ? null : account.trim();
    }

    public String getUserpwd() {
        return userpwd;
    }

    public void setUserpwd(String userpwd) {
        this.userpwd = userpwd;
    }

    public String getTemporarypwd() {
        return temporarypwd;
    }

    public void setTemporarypwd(String temporarypwd) {
        this.temporarypwd = temporarypwd== null ? null : temporarypwd.trim();
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt== null ? null : salt.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone== null ? null : phone.trim();
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status== null ? null : status.trim();
    }

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getOperaterTime() {
		return operaterTime;
	}

	public void setOperaterTime(String operaterTime) {
		this.operaterTime = operaterTime;
	}
	
}