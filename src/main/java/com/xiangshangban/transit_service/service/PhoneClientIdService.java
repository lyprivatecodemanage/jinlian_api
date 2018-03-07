package com.xiangshangban.transit_service.service;

import com.xiangshangban.transit_service.bean.PhoneClientId;

public interface PhoneClientIdService {
	
	PhoneClientId selectByPhone(String phone);
	
	PhoneClientId selectByClientId(String clientId);
	
	int insertPhoneClientId(PhoneClientId phoneClientId);
	
	int deletePhoneClientIdByPhone(String phone);
	
	int deletePhoneClientIdByClientId(String clientId);
}
