package com.xiangshangban.transit_service.dao;

import org.apache.ibatis.annotations.Mapper;

import com.xiangshangban.transit_service.bean.Department;

@Mapper
public interface DepartmentMapper {
   
    int insertDepartment(Department department);
  
}