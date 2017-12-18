package com.xiangshangban.transit_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiangshangban.transit_service.bean.ChangePhone;
import com.xiangshangban.transit_service.bean.Employee;
import com.xiangshangban.transit_service.bean.Uroles;
import com.xiangshangban.transit_service.bean.Uusers;
import com.xiangshangban.transit_service.dao.UusersMapper;
@Service("usersService")
public class UusersServiceImpl implements UusersService {
	@Autowired
	private UusersMapper uusersMapper;
	
	@Override
	public Uusers selectByPhone(String phone) {
		
		return uusersMapper.selectByPhone(phone);
	}
	@Override
	public Uusers selectByAccount(String account) {
		// TODO Auto-generated method stub
		return uusersMapper.selectByAccount(account);
	}
	
	@Override
	public int updateSmsCode(String phone,String smsCode) {
		// TODO Auto-generated method stub
		return uusersMapper.updateSmsCode(phone,smsCode);
	}
	

	@Override
	public int deleteByPrimaryKey(String userid) {
		return uusersMapper.deleteByPrimaryKey(userid);
	}

	@Override
	public int insert(Uusers record) {
		return uusersMapper.insert(record);
	}
	@Override
	public int updateByPrimaryKey(Uusers record) {
		return uusersMapper.updateByPrimaryKey(record);
	}

	@Override
	public Uusers selectByPrimaryKey(String userid) {
		
		return uusersMapper.selectByPrimaryKey(userid);
	}

	@Override
	public int updateByPrimaryKeySelective(Uusers record) {
		return uusersMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int insertSelective(Uusers record) {
		return uusersMapper.insertSelective(record);
	}
	
	@Override
	public List<Uroles> selectRoles(String phone) {
		// TODO Auto-generated method stub
		return uusersMapper.selectRoles(phone);
	}

	@Override
	public int SelectCountByPhone(String phone) {
		return uusersMapper.SelectCountByPhone(phone);
	}
	@Override
	public Uusers selectCompanyByToken(String token) {
		
		return uusersMapper.selectCompanyByToken(token);
	}
	@Override
	public Uusers selectCompanyBySessionId(String sessionId) {
		
		return uusersMapper.selectCompanyBySessionId(sessionId);
	}
	@Override
	public int selectIdentityAuthentication(String phone, String userName, String companyName) {
		
		return uusersMapper.selectIdentityAuthentication(phone, userName, companyName);
	}
	@Override
	public List<ChangePhone> selectPersonalInformationVerification(String phone, String userName,
			String postName) {
		
		return uusersMapper.selectPersonalInformationVerification(phone, userName, postName);
	}
	@Override
	public List<Uusers> selectListsByPhone(String phone) {
		
		return uusersMapper.selectListsByPhone(phone);
	}
	@Override
	public ChangePhone selectApprovalPerson(String companyId) {
		
		return uusersMapper.selectApprovalPerson(companyId);
	}

	public Uusers selectById(String userId){
		return uusersMapper.selectById(userId);
	}
	@Override
	public Uusers selectAdminByPhone(String phone, String roleId) {
		
		return uusersMapper.selectAdminByPhone(phone, roleId);
	}
	@Override
	public int insertEmployee(Uusers uusers) {
		// TODO Auto-generated method stub
		return uusersMapper.insertEmployee(uusers);
	}
	@Override
	public int isActive(String phone) {
		return uusersMapper.isActive(phone);
	}
	@Override
	public String SelectEmployeeIdByPhone(String phone) {
		// TODO Auto-generated method stub
		return uusersMapper.SelectEmployeeIdByPhone(phone);
	}
	@Override
	public int deleteEmployee(String userId) {
		// TODO Auto-generated method stub
		return uusersMapper.deleteEmployee(userId);
	}
	@Override
	public Employee SeletctEmployeeByUserId(String userId,String companyId) {
		// TODO Auto-generated method stub
		return uusersMapper.SeletctEmployeeByUserId(userId,companyId);
	}
}
