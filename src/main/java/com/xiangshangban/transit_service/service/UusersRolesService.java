package com.xiangshangban.transit_service.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xiangshangban.transit_service.bean.Upermission;
import com.xiangshangban.transit_service.bean.Uroles;
import com.xiangshangban.transit_service.bean.UusersRolesKey;

public interface UusersRolesService {
	
	int deleteByPrimaryKey(UusersRolesKey key);

    int insert(UusersRolesKey record);

    int insertSelective(UusersRolesKey record);
    
	// 查看当前管理员及历史管理员
    List<UusersRolesKey> SelectAdministrator(String companyId, String roleId);

	int updateAdminClearHist(String userId,String roleId,String companyId);
	
	// 修改管理员
	int updateAdministrator(String newUserId, String companyId, String roleId);
    
	// 根据用户ID查询权限url地址
    List<Upermission> SelectUserIdByPermission(String userId,String companyId);

	// 根据用户编号 和 公司编号 查询出角色信息
	Uroles SelectRoleByUserId(String userId, String companyId);
	
	List<UusersRolesKey> selectCompanyByUserIdRoleId(String userId,String roleId);
}
