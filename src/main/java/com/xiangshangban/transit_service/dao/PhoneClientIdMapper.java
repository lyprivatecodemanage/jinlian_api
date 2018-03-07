package com.xiangshangban.transit_service.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xiangshangban.transit_service.bean.PhoneClientId;

@Mapper
public interface PhoneClientIdMapper {
	
	PhoneClientId selectByPhone(String phone);
	
	PhoneClientId selectByClientId(String clientId);
	
	Integer insertPhoneClientId(PhoneClientId phoneClientId);
	
	Integer deletePhoneClientIdByPhone(String phone);
	
	Integer deletePhoneClientIdByClientId(@Param("clientId")String clientId);
}
