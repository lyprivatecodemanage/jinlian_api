package com.xiangshangban.transit_service.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiangshangban.transit_service.bean.Upermission;
import com.xiangshangban.transit_service.bean.Uroles;
import com.xiangshangban.transit_service.bean.UusersRolesKey;
import com.xiangshangban.transit_service.dao.UusersRolesMapper;

@Service("uusersRolesService")
public class UusersRolesServiceImpl implements UusersRolesService {

	@Autowired
	UusersRolesMapper uusersRolesMapper;
	
	@Override
	public int deleteByPrimaryKey(UusersRolesKey key) {
		// TODO Auto-generated method stub
		return uusersRolesMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(UusersRolesKey record) {
		// TODO Auto-generated method stub
		return uusersRolesMapper.insert(record);
	}

	@Override
	public int insertSelective(UusersRolesKey record) {
		// TODO Auto-generated method stub
		return uusersRolesMapper.insertSelective(record);
	}

	@Override
	public List<UusersRolesKey> SelectAdministrator(String companyId, String roleId) {
		// TODO Auto-generated method stub
		return uusersRolesMapper.SelectAdministrator(companyId, roleId);
	}

	@Override
	public int updateAdministrator(String newUserId, String companyId,
			String roleId) {
		// TODO Auto-generated method stub
		return uusersRolesMapper.updateAdministrator(newUserId, companyId, roleId);
	}

	@Override
	public List<Upermission> SelectUserIdByPermission(String userId,String companyId) {
		// TODO Auto-generated method stub
		return uusersRolesMapper.SelectUserIdByPermission(userId,companyId);
	}

	@Override
	public Uroles SelectRoleByUserId(String userId, String companyId) {
		// TODO Auto-generated method stub
		return uusersRolesMapper.SelectRoleByUserId(userId, companyId);
	}

	@Override
	public int updateAdminClearHist(String userId,String roleId,String companyId) {
		// TODO Auto-generated method stub
		return uusersRolesMapper.updateAdminClearHist(userId, roleId,companyId);
	}

	@Override
	public List<UusersRolesKey> selectCompanyByUserIdRoleId(String userId, String roleId) {
		// TODO Auto-generated method stub
		return uusersRolesMapper.selectCompanyByUserIdRoleId(userId, roleId);
	}

}
