package com.xiangshangban.transit_service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xiangshangban.transit_service.bean.ChangePhone;
import com.xiangshangban.transit_service.bean.Employee;
import com.xiangshangban.transit_service.bean.Uroles;
import com.xiangshangban.transit_service.bean.Uusers;
@Mapper
public interface UusersMapper {
    int deleteByPrimaryKey(String userid);

    int insert(Uusers record);

    Uusers selectByPrimaryKey(String userId);
    
    Uusers selectByPhone(@Param("phone")String phone);
    
    List<Uroles> selectRoles(String phone);
    
    Uusers selectByAccount(String account);
    
    ChangePhone selectApprovalPerson(String companyId);
    
    int selectIdentityAuthentication(@Param("phone") String phone,@Param("userName") String userName,@Param("companyName") String companyName);
    
    List<ChangePhone> selectPersonalInformationVerification(@Param("phone")String phone,@Param("userName")String userName,@Param("postName")String postName);
    
    int updateByPrimaryKey(Uusers record);
    
    int updateSmsCode(@Param("phone")String phone,@Param("temporarypwd")String smsCode);

    List<Uusers> selectListsByPhone(String phone);
    
    Uusers selectCompanyByToken(String token);

    Uusers selectCompanyBySessionId(String sessionId);

	// 注册时检查手机号是否已被注册
    int SelectCountByPhone(String phone);

	// 修改用户状态(当注册为加入公司情况时，待审核加入情况用户为不可用，加入后需对用户账号状态进行修改)
    int updateByPrimaryKeySelective(Uusers record);

	// 注册用户（生成UUID为主键、存入手机号、姓名、盐值、以及默认为‘不可用’的初始状态）
    int insertSelective(Uusers record);
    
    Uusers selectById(String userId);
    
	Uusers selectAdminByPhone(@Param("phone") String phone, @Param("roleId") String roleId);
	
	//新增人员表
	int insertEmployee(Uusers uusers);
	//产出人员
	int deleteEmployee(String userId);

	Employee SeletctEmployeeByUserId(@Param("userId")String userId,@Param("companyId")String companyId);
	
	int isActive(String phone);
	
	//根据手机号码查询人员ID
	String SelectEmployeeIdByPhone(String phone);
	
	//根据用户ID查询人员信息
	Employee SelectByUserId(String userId);
}