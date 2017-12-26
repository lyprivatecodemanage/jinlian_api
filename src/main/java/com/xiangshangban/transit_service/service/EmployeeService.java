package com.xiangshangban.transit_service.service;

import org.apache.ibatis.annotations.Param;

import com.xiangshangban.transit_service.bean.Employee;


public interface EmployeeService {

	// 查询单条员信息
	Employee selectByEmployee(@Param("employeeId") String employeeId, @Param("companyId") String companyId);
}
