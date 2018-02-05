package com.xiangshangban.transit_service.dao;

import org.apache.ibatis.annotations.Mapper;

import com.xiangshangban.transit_service.bean.UpdateVersion;

@Mapper
public interface UpdateVersionMapper {

	/**
	 * 查看最新版本信息
	 * 
	 * @param appType 0：安卓 1：ios
	 * @return
	 */
	UpdateVersion FindNewFile(String appType);
}
