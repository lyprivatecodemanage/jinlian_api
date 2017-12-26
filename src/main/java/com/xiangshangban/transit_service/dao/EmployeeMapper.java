package com.xiangshangban.transit_service.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xiangshangban.transit_service.bean.Employee;

@Mapper
public interface EmployeeMapper {

	// 查询单条员信息
	Employee selectByEmployee(@Param("employeeId") String employeeId, @Param("companyId") String companyId);
}
