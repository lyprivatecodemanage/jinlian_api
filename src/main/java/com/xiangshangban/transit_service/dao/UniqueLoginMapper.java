package com.xiangshangban.transit_service.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xiangshangban.transit_service.bean.UniqueLogin;

@Mapper
public interface UniqueLoginMapper {
	
	UniqueLogin selectByPhoneFromApp(String phone);
	
	UniqueLogin selectByPhoneFromWeb(String phone);
	
	UniqueLogin selectBySessionId(String sessionId);
	
	UniqueLogin selectByToken(String token);
	
	UniqueLogin selectByTokenAndClientId(@Param("token")String token,@Param("clientId")String clientId);
	
	int insert(UniqueLogin uniqueLogin);
	
	int deleteByPhoneFromApp(String phone);
	
	int deleteByPhoneFromWeb(String phone);
	
	int deleteBySessinId(String sessionId);
	
	int deleteByTokenAndClientId(@Param("token") String token,@Param("clientId") String clientId);
	
}
