package com.xiangshangban.transit_service.service;


import java.util.List;

import com.xiangshangban.transit_service.bean.UniqueLogin;

public interface UniqueLoginService {
	
	UniqueLogin selectByPhoneFromApp(String phone);

	UniqueLogin selectByPhoneFromWeb(String phone);
	
	List<UniqueLogin> selectByPhoneFromWebList(String phone);
	
	UniqueLogin selectBySessionId(String sessionId);

	UniqueLogin selectByToken(String token);
	
	UniqueLogin selectByTokenAndClientId(String token, String clientId);

	int insert(UniqueLogin uniqueLogin);

	int deleteByPhoneFromApp(String phone);
	
	int deleteByPhoneFromWeb(String phone);
	
	int deleteBySessinId(String sessionId);
	
	int deleteByTokenAndClientId(String token, String clientId);
}
