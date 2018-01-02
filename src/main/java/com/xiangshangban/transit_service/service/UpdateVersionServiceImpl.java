package com.xiangshangban.transit_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiangshangban.transit_service.bean.UpdateVersion;
import com.xiangshangban.transit_service.dao.UpdateVersionMapper;

@Service("updateVersionService")
public class UpdateVersionServiceImpl implements UpdateVersionService {

	@Autowired
	UpdateVersionMapper updateVersionMapper;
	
	@Override
	public UpdateVersion FindNewFile() {
		// TODO Auto-generated method stub
		return updateVersionMapper.FindNewFile();
	}

}
