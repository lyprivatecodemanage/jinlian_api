package com.xiangshangban.transit_service.service;


import com.xiangshangban.transit_service.bean.CheckPendingJoinCompany;
import com.xiangshangban.transit_service.dao.CheckPendingJoinCompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mian on 2017/11/4.
 */
@Service("checkPendingJoinCompanyService")
public class CheckPendingJoinCompanyServiceImpl implements CheckPendingJoinCompanyService {

    @Autowired
    CheckPendingJoinCompanyMapper checkPendingJoinCompanyMapper;

    @Override
    public int insertSelective(CheckPendingJoinCompany record) {
        return checkPendingJoinCompanyMapper.insertSelective(record);
    }

    @Override
    public CheckPendingJoinCompany selectByPrimaryKey(String userid,String companyid) {
        return checkPendingJoinCompanyMapper.selectByPrimaryKey(userid, companyid);
    }

	@Override
	public int updateByPrimaryKeySelective(CheckPendingJoinCompany record) {
		// TODO Auto-generated method stub
		return checkPendingJoinCompanyMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(CheckPendingJoinCompany record) {
		// TODO Auto-generated method stub
		return checkPendingJoinCompanyMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteById(String userId,String companyid) {
		// TODO Auto-generated method stub
		return checkPendingJoinCompanyMapper.deleteById(userId,companyid);
	}

	@Override
	public CheckPendingJoinCompany selectRecord(String userid, String companyid, String status) {
		// TODO Auto-generated method stub
		return checkPendingJoinCompanyMapper.selectRecord(userid, companyid, status);
	}
}
