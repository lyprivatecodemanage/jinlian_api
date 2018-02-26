package com.xiangshangban.transit_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiangshangban.transit_service.bean.UniqueLogin;
import com.xiangshangban.transit_service.dao.UniqueLoginMapper;
@Service("uniqueLoginService")
public class UniqueLoginServiceImpl implements UniqueLoginService{
	@Autowired
	private UniqueLoginMapper uniqueLoginMapper;
	
	
	@Override
	public UniqueLogin selectByPhoneFromApp(String phone) {
		return uniqueLoginMapper.selectByPhoneFromApp(phone);
	}
	
	@Override
	public UniqueLogin selectByPhoneFromWeb(String phone) {
		return uniqueLoginMapper.selectByPhoneFromWeb(phone);
	}
	
	@Override
	public List<UniqueLogin> selectByPhoneFromWebList(String phone) {
		return uniqueLoginMapper.selectByPhoneFromWebList(phone);
	}
	
	@Override
	public UniqueLogin selectBySessionId(String sessionId) {
		return uniqueLoginMapper.selectBySessionId(sessionId);
	}

	@Override
	public UniqueLogin selectByTokenAndClientId(String token, String clientId) {
		return uniqueLoginMapper.selectByTokenAndClientId(token, clientId);
	}

	@Override
	public int insert(UniqueLogin uniqueLogin) {
		return uniqueLoginMapper.insert(uniqueLogin);
	}

	@Override
	public int deleteByPhoneFromApp(String phone) {
		return uniqueLoginMapper.deleteByPhoneFromApp(phone);
	}
	@Override
	public int deleteByPhoneFromWeb(String phone) {
		return uniqueLoginMapper.deleteByPhoneFromWeb(phone);
	}

	@Override
	public int deleteBySessinId(String sessionId) {
		// TODO Auto-generated method stub
		return uniqueLoginMapper.deleteBySessinId(sessionId);
	}

	@Override
	public UniqueLogin selectByToken(String token) {
		// TODO Auto-generated method stub
		return uniqueLoginMapper.selectByToken(token);
	}

	@Override
	public int deleteByTokenAndClientId(String token, String clientId) {
		// TODO Auto-generated method stub
		return uniqueLoginMapper.deleteByTokenAndClientId(token, clientId);
	}



}
