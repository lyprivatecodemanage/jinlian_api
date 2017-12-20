package com.xiangshangban.transit_service.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xiangshangban.transit_service.bean.ChangePhone;
import com.xiangshangban.transit_service.bean.Employee;
import com.xiangshangban.transit_service.bean.Uroles;
import com.xiangshangban.transit_service.bean.Uusers;

public interface UusersService {
	
	 Uusers selectByAccount(String account);
	 
	 Uusers selectByPrimaryKey(String userId);
	 
	 Uusers selectUserByPhone(String phone);
	 
	/**
	 * 根据phone查询
	 * 
	 * @param phone
	 * @return
	 */
	 Uusers selectByPhone(String phone);
	 
	 int deleteEmployee(String userId);
	 
	 List<Uusers> selectListsByPhone(String phone);
	 
	 int selectIdentityAuthentication(String phone,String userName,String companyName);
	 
	 List<ChangePhone> selectPersonalInformationVerification(String phone,String userName,String postName);
	 
	 int updateSmsCode(String Phone, String smsCode);
	 
	 List<Uroles> selectRoles(String phone);
	 
	 int deleteByPrimaryKey(String userid);

	 int insert(Uusers record);

 	 int updateByPrimaryKey(Uusers record);

	// 注册时检查手机号是否已被注册
	int SelectCountByPhone(String phone);

	int updateByPrimaryKeySelective(Uusers record);

	int insertSelective(Uusers record);
		
	Uusers selectCompanyByToken(String token);

	Uusers selectCompanyBySessionId(String sessionId);

	ChangePhone selectApprovalPerson(String companyId);

	Uusers selectById(String userId);
	
	Uusers selectAdminByPhone(String phone, String roleId);
	
	int updateUserByPhone(Uusers uuser);
	
	int insertEmployee(Uusers uusers);
	/**
	 * 查询是否已激活
	 * @param phone
	 * @return
	 */
	int isActive(String phone);
	
	String SelectEmployeeIdByPhone(String phone);
	
	Employee SeletctEmployeeByUserId(String userId,String companyId);
}
