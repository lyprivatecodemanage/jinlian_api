package com.xiangshangban.transit_service.service;

import com.xiangshangban.transit_service.bean.UpdateVersion;


public interface UpdateVersionService {

	//查看android 最新版本信息
	UpdateVersion FindNewFile();
}
