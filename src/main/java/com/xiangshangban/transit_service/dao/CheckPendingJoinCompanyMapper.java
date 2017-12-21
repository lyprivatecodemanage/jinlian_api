package com.xiangshangban.transit_service.dao;

import com.xiangshangban.transit_service.bean.CheckPendingJoinCompany;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.junit.runners.Parameterized.Parameters;

@Mapper
public interface CheckPendingJoinCompanyMapper {
    int deleteByPrimaryKey(String userid);

    int insert(CheckPendingJoinCompany record);

    int insertSelective(CheckPendingJoinCompany record);

    CheckPendingJoinCompany selectByPrimaryKey(String userid,String companyid);
    
    CheckPendingJoinCompany selectRecord(@Param("userid")String userid,@Param("companyid")String companyid,@Param("status")String status);

    int updateByPrimaryKeySelective(CheckPendingJoinCompany record);

    int updateByPrimaryKey(CheckPendingJoinCompany record);
    
    int deleteById(String userId,String companyid);
}