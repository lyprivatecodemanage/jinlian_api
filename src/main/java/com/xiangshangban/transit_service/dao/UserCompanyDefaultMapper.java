package com.xiangshangban.transit_service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xiangshangban.transit_service.bean.UserCompanyDefault;

@Mapper
public interface UserCompanyDefaultMapper {
	
	List<UserCompanyDefault> selectByUserId(String userId);

	int deleteByPrimaryKey(UserCompanyDefault key);

	int insert(UserCompanyDefault record);

	int insertSelective(UserCompanyDefault record);
	
	UserCompanyDefault selectBySoleUserId(String userId);
	
	UserCompanyDefault selectByUserIdAndCompanyId(@Param("userId")String userId,@Param("companyId") String companyId,@Param("type")String type);
	
	int updateUserCompanyCoption(@Param("userId")String userId,@Param("companyId")String companyId,@Param("option")String option);
}
