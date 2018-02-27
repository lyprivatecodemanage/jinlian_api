package com.xiangshangban.transit_service.service;

import com.xiangshangban.transit_service.bean.UpdateVersion;


public interface UpdateVersionService {

	/**
	 * 
	 * @param appType 0：安卓  1：ios
	 * @return
	 */
	UpdateVersion FindNewFile(String appType);
}
