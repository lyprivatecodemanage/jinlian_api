package com.xiangshangban.transit_service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xiangshangban.transit_service.bean.Upermission;
import com.xiangshangban.transit_service.bean.Uroles;
import com.xiangshangban.transit_service.bean.UusersRolesKey;
@Mapper
public interface UusersRolesMapper {
    int deleteByPrimaryKey(UusersRolesKey key);

    int insert(UusersRolesKey record);

    int insertSelective(UusersRolesKey record);
    
	// 查看当前管理员及历史管理员
	List<UusersRolesKey> SelectAdministrator(@Param("companyId") String companyId, @Param("roleId") String roleId);
	
	int updateAdminClearHist(@Param("userId") String userId,@Param("roleId")String roleId,@Param("companyId")String companyId);
    
	// 修改管理员
	int updateAdministrator(@Param("newUserId") String newUserId,
			@Param("companyId") String companyId,
			@Param("roleId") String roleId);

	// 根据用户ID查询权限url地址
    List<Upermission> SelectUserIdByPermission(@Param("userId")String userId,@Param("companyId")String companyId);
    
	// 根据用户编号 和 公司编号 查询出角色信息
	Uroles SelectRoleByUserId(@Param("userId") String userId, @Param("companyId") String companyId);
	
	List<UusersRolesKey> selectCompanyByUserIdRoleId(@Param("userId")String userId,@Param("roleId")String roleId);
}