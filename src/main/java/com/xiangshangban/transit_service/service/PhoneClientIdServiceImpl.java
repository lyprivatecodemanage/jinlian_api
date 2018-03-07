package com.xiangshangban.transit_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiangshangban.transit_service.bean.PhoneClientId;
import com.xiangshangban.transit_service.dao.PhoneClientIdMapper;
@Service("phoneClientIdService")
public class PhoneClientIdServiceImpl implements PhoneClientIdService {
	@Autowired
	private PhoneClientIdMapper phoneClientIdMapper;
	@Override
	public PhoneClientId selectByPhone(String phone) {
		
		return phoneClientIdMapper.selectByPhone(phone);
	}
	@Override
	public PhoneClientId selectByClientId(String clientId) {
		
		return phoneClientIdMapper.selectByClientId(clientId);
	}
	@Override
	public int insertPhoneClientId(PhoneClientId phoneClientId) {
		
		return phoneClientIdMapper.insertPhoneClientId(phoneClientId);
	}
	@Override
	public int deletePhoneClientIdByPhone(String phone) {
		
		return phoneClientIdMapper.deletePhoneClientIdByPhone(phone);
	}
	@Override
	public int deletePhoneClientIdByClientId(String clientId) {
		
		return phoneClientIdMapper.deletePhoneClientIdByClientId(clientId);
	}

}
