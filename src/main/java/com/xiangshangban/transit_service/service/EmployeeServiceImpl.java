package com.xiangshangban.transit_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiangshangban.transit_service.bean.Employee;
import com.xiangshangban.transit_service.dao.EmployeeMapper;

@Service("employeeServiceImpl")
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	EmployeeMapper employeeMapper;
	
	@Override
	public Employee selectByEmployee(String employeeId, String companyId) {
		// TODO Auto-generated method stub
		return employeeMapper.selectByEmployee(employeeId, companyId);
	}

}
