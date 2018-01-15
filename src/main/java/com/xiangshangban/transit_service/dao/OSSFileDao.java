package com.xiangshangban.transit_service.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.xiangshangban.transit_service.bean.OSSFile;

@Mapper
public interface OSSFileDao {

	public void addOSSFile(OSSFile oSSFile);
	
}
