package com.xiangshangban.transit_service.dao;

import org.apache.ibatis.annotations.Mapper;

import com.xiangshangban.transit_service.bean.UpdateVersion;

@Mapper
public interface UpdateVersionMapper {

	//查看android 最新版本信息
	UpdateVersion FindNewFile();
}
