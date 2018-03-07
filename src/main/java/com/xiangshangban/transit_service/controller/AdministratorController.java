package com.xiangshangban.transit_service.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiangshangban.transit_service.bean.Company;
import com.xiangshangban.transit_service.bean.Employee;
import com.xiangshangban.transit_service.bean.Uroles;
import com.xiangshangban.transit_service.bean.UserCompanyDefault;
import com.xiangshangban.transit_service.bean.Uusers;
import com.xiangshangban.transit_service.bean.UusersRolesKey;
import com.xiangshangban.transit_service.service.CompanyService;
import com.xiangshangban.transit_service.service.OSSFileService;
import com.xiangshangban.transit_service.service.UserCompanyService;
import com.xiangshangban.transit_service.service.UusersRolesService;
import com.xiangshangban.transit_service.service.UusersService;
import com.xiangshangban.transit_service.util.PropertiesUtils;
import com.xiangshangban.transit_service.util.RedisUtil;
import com.xiangshangban.transit_service.util.YtxSmsUtil;

@RestController
@RequestMapping("/administratorController")
public class AdministratorController {
	private Logger logger = Logger.getLogger(AdministratorController.class);

	@Autowired
	UusersRolesService uusersRolesService;
	
	@Autowired
	UusersService uusersService;
	
	@Autowired
	OSSFileService oSSFileService;
	
	@Autowired
	CompanyService companyService;
	
	@Autowired
	UserCompanyService userCompanyService;
	
	/***
	 * 焦振/系统设置 --> 更改管理员界面初始化显示 当前管理员和历史管理员(姓名、登录名、头像)
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/administratorInit",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
	public Map<String,Object> administratorInit(HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		List<Employee> list = new ArrayList<>();

		// 初始化redis
		RedisUtil redis = RedisUtil.getInstance();
				// 从redis取出短信验证码
		String phone = redis.new Hash().hget(request.getSession().getId(), "session");
		
		Uusers user = uusersService.selectByPhone(phone,"0");
				
		String companyId = userCompanyService.selectBySoleUserId(user.getUserid(),"0").getCompanyId();
		
		if(StringUtils.isEmpty(companyId)){
			map.put("returnCode","3006");
			map.put("message", "必传参数为空");
			return map;
		}
		
		try{
			Company company = companyService.selectByPrimaryKey(companyId);

			List<UusersRolesKey> uusersRolesKey = uusersRolesService.SelectAdministrator(companyId, new Uroles().admin_role);
			
			Company c = companyService.selectByPrimaryKey(companyId);
			
			if(c!=null){
				// 查看历史管理员数据
				if (StringUtils.isNotEmpty(c.getHistory_user_ids())) {
					if(c.getHistory_user_ids().split(",").length>1){
						String [] userids = c.getHistory_user_ids().split(",");
						
						for (int i = 0; i < userids.length; i++) {
							Employee emp = uusersService.SeletctEmployeeByUserId(userids[userids.length-i-1],companyId);
							
							if(emp!=null && StringUtils.isNotEmpty(emp.getEmployeeImgUrl())){
								String employeeImgUrlPath = oSSFileService.getPathByKey(company.getCompany_no(),"portrait", emp.getEmployeeImgUrl());
								
								if(StringUtils.isNotEmpty(employeeImgUrlPath)){
									emp.setEmployeeImgUrl(employeeImgUrlPath);
								}
							}else{
								emp.setEmployeeImgUrl("http://xiangshangban.oss-cn-hangzhou.aliyuncs.com/test/sys/portrait/default.png");
							}
							list.add(emp);
						}
					}else{
						Employee emp = uusersService.SeletctEmployeeByUserId(c.getHistory_user_ids(),companyId);
						
						if(emp!=null && StringUtils.isNotEmpty(emp.getEmployeeImgUrl())){
							String employeeImgUrlPath = oSSFileService.getPathByKey(company.getCompany_no(),"portrait", emp.getEmployeeImgUrl());
							
							if(StringUtils.isNotEmpty(employeeImgUrlPath)){
								emp.setEmployeeImgUrl(employeeImgUrlPath);
							}
						}else{
							emp.setEmployeeImgUrl("http://xiangshangban.oss-cn-hangzhou.aliyuncs.com/test/sys/portrait/default.png");
						}
						list.add(emp);
					}
					// 历史管理员信息
					map.put("data",JSON.toJSON(list));
				}
			}
			
			List<Employee> empList = new ArrayList<>();
			
			for (UusersRolesKey urk : uusersRolesKey) {
				// 查询当前管理员信息
				Employee employee = uusersService.SeletctEmployeeByUserId(urk.getUserId(),companyId);
				
				if(employee!=null && StringUtils.isNotEmpty(employee.getEmployeeImgUrl())){
					String employeeImgUrlPath = oSSFileService.getPathByKey(company.getCompany_no(),"portrait", employee.getEmployeeImgUrl());
					
					if(StringUtils.isNotEmpty(employeeImgUrlPath)){
						employee.setEmployeeImgUrl(employeeImgUrlPath);
					}
				}else{
					employee.setEmployeeImgUrl("http://xiangshangban.oss-cn-hangzhou.aliyuncs.com/test/sys/portrait/default.png");
				}
				
				empList.add(employee);
			}
			map.put("admin",JSON.toJSON(empList));
			map.put("returnCode","3000");
			map.put("message", "数据请求成功");
			return map;
		}catch(NullPointerException e){
			e.printStackTrace();
			logger.info(e);
			map.put("returnCode", "4007");
			map.put("message", "结果为null");
			return map;
		}
		catch(Exception e){
			e.printStackTrace();
			logger.info(e);
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
			return map;
		}
	}
	
	/***
	 * 焦振 / 给管理员发送短信
	 * @param userId
	 * @return
	 */
	@RequestMapping(value="/adminAuthCode",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
	public Map<String,Object> administratorAuthCode(@RequestBody String jsonString,HttpServletRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		String type = request.getHeader("type");
		// 初始化redis
		RedisUtil redis = RedisUtil.getInstance();
		
		YtxSmsUtil sms = new YtxSmsUtil("LTAIcRopzlp5cbUd", "VnLMEEXQRukZQSP6bXM6hcNWPlphiP");
		try {
			JSONObject obj = JSON.parseObject(jsonString);
			String userId = obj.getString("userId");
			// 从redis取出短信验证码
			String phone = redis.new Hash().hget(request.getSession().getId(), "session");
							
			Uusers user = uusersService.selectByPhone(phone,"0");
			// 获取验证码
			String smsCode = "";
			//测试环境或者测试账号
			if("test".equals(PropertiesUtils.ossProperty("ossEnvironment")) || "15995611270".equals(phone)){
				smsCode = "6666";
			}else{
				smsCode = sms.sendIdSms(phone);
			}
			// user不为null,说明是登录获取验证码
			if (user != null) {
				// 更新数据库验证码记录,当做登录凭证
				uusersService.updateSmsCode(phone, smsCode);
			}
			// 设值
			redis.new Hash().hset("smsCode_" + phone, "smsCode", smsCode);
			// 设置redis保存时间
			redis.expire("smsCode_" + phone, 120);
			// 设置返回结果
			//result.put("smsCode", smsCode);
			result.put("phone", phone);
			result.put("message", "成功");
			result.put("returnCode", "3000");
			return result;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			result.put("returnCode", "3007");
			result.put("message", "参数格式不正确");
			logger.info(e);
			return result;
		} catch (NullPointerException e) {
			e.printStackTrace();
			result.put("returnCode", "3006");
			result.put("message", "参数为null");
			logger.info(e);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("returnCode", "3001");
			result.put("message", "失败");
			logger.info(e);
			return result;
		}
	}
	
	/***
	 * 焦振/更换管理员
	 * 
	 * @param newUserId
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateadministrator",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
	public Map<String,Object> updateadministrator(@RequestBody String jsonString,HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		String historyUserIds = "";
		
		// 初始化redis
		RedisUtil redis = RedisUtil.getInstance();
		// 从redis取出手机号
		String phone = redis.new Hash().hget(request.getSession().getId(), "session");
		
		Uusers user = uusersService.selectByPhone(phone,"0");
		
		JSONObject obj = JSON.parseObject(jsonString);
		String newUserId = obj.getString("newUserId");
		
		if(StringUtils.isEmpty(newUserId)){
			map.put("returnCode","3006");
			map.put("message", "必传参数为空");
			return map;
		}
		
		String companyId = userCompanyService.selectBySoleUserId(user.getUserid(),"0").getCompanyId();
		
		try {
			Company company = companyService.selectByPrimaryKey(companyId);
			
			// 获取现在管理员ID
			String userId = user.getUserid();
			
			// 获取历史管理员
			String huids = company.getHistory_user_ids();
			
			if(userId.equals(newUserId)){
				map.put("returnCode", "4025");
				map.put("message", "更换的管理员不能是当前管理员");
				return map;
			}
			
			//更换管理员角色为普通员工角色
			uusersRolesService.updateAdminClearHist(userId,new Uroles().user_role,companyId);
			
			//给新管理员更换为管理员角色
			uusersRolesService.updateAdministrator(newUserId, companyId,new Uroles().admin_role);
			
			//如果历史管理员为空
			if(StringUtils.isEmpty(huids)){
				
				Company cp = new Company();
				cp.setCompany_id(companyId);
				cp.setHistory_user_ids(userId);
				
				companyService.updateByPrimaryKeySelective(cp);
				
				//被设为管理员的人员 若之前不存在管理员角色时 被该公司被修改为默认公司
				List<UusersRolesKey> urList = uusersRolesService.selectCompanyByUserIdRoleId(newUserId, new Uroles().admin_role);
				
				if(urList.size()==1){
				 	UserCompanyDefault uc = userCompanyService.selectBySoleUserId(newUserId,"0");
					
				 	userCompanyService.updateUserCompanyCoption(newUserId, uc.getCompanyId(), "2","0");
				 	
				 	userCompanyService.updateUserCompanyCoption(newUserId, companyId, "1","0");
				}
			}else{
				if(huids.split(",").length>2){
					
					String [] hisUserId = huids.split(",");
						
					historyUserIds = hisUserId[hisUserId.length-2]+","+hisUserId[hisUserId.length-1]+","+userId;
					
				}else{
					historyUserIds = huids +","+userId;
				}
				//新建公司对象 给与公司ID和更改后的历史管理员列表
				Company cp = new Company();
				cp.setCompany_id(companyId);
				cp.setHistory_user_ids(historyUserIds);
				
				//将 历史管理员记录更新到公司历史管理员列上
				companyService.updateByPrimaryKeySelective(cp);
				
				Uusers u = uusersService.selectById(newUserId);
				
				//修改公司信息里面联系人姓名 与 登录号
				Company c = new Company();
				c.setCompany_id(companyId);
				c.setCompany_personal_name(u.getUsername());
				c.setUser_name(u.getPhone());
				
				companyService.updateByPrimaryKeySelective(c);
				
				//被设为管理员的人员 若之前不存在管理员角色时 被该公司被修改为默认公司
				List<UusersRolesKey> urList = uusersRolesService.selectCompanyByUserIdRoleId(newUserId, new Uroles().admin_role);
				
				if(urList.size()==1){
				 	UserCompanyDefault uc = userCompanyService.selectBySoleUserId(newUserId,"0");
					
				 	userCompanyService.updateUserCompanyCoption(newUserId, uc.getCompanyId(), "2","0");
				 	
				 	userCompanyService.updateUserCompanyCoption(newUserId, companyId, "1","0");
				}
			}
			
			//将原来管理员的默认公司修改为其他拥有管理员身份的公司
			List<UusersRolesKey> urlist = uusersRolesService.selectCompanyByUserIdRoleId(userId, new Uroles().admin_role);
			
			if(urlist!=null&&urlist.size()!=0){
				
				List<UserCompanyDefault> list = new ArrayList<>();
				
				for (UusersRolesKey urk : urlist) {
					 list.add(userCompanyService.selectByUserIdAndCompanyId(userId, urk.getCompanyId(),"0"));
				}
				
				int num = userCompanyService.updateUserCompanyCoption(userId, companyId, new UserCompanyDefault().status_2,"0");
				
				if(num>0){
					userCompanyService.updateUserCompanyCoption(userId, list.get(0).getCompanyId(), new UserCompanyDefault().status_1,"0");
				}
			}
			
			map.put("returnCode", "3000");
			map.put("message", "数据请求成功");
			return map;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.info(e);
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
			return map;
		}
	}
	
	/**
	 * 焦振 / 新增管理员
	 * @param jsonString
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/InsertAdministrator",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
	public Map<String,Object> InsertAdministrator(@RequestBody String jsonString,HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		
		JSONObject obj = JSON.parseObject(jsonString);
		String userId = obj.getString("userId");
		
		if(StringUtils.isEmpty(userId)){
			map.put("returnCode","3006");
			map.put("message", "必传参数为空");
			return map;
		}
		// 初始化redis
		RedisUtil redis = RedisUtil.getInstance();
		// 从redis取出手机号
		String phone = redis.new Hash().hget(request.getSession().getId(), "session");
		
		String userid = uusersService.selectByPhone(phone,"0").getUserid();
		
		if(userid.equals(userId)){
			map.put("returnCode", "4033");
			map.put("message", "新增的管理员不能是当前管理员");
			return map;
		}
		
		try {
			String companyId = userCompanyService.selectBySoleUserId(userId,"0").getCompanyId();
			
			//更换管理员角色为普通员工角色
			uusersRolesService.updateAdminClearHist(userId,new Uroles().admin_role,companyId);
			
			map.put("returnCode", "3000");
			map.put("message", "数据请求成功");
			return map;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.info(e);
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
			return map;
		}
	}
	
	
	/**
	 * 焦振 / 删除管理员
	 * @param jsonString
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/deleteAdministrator",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
	public Map<String,Object> deleteAdministrator(@RequestBody String jsonString,HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		
		JSONObject obj = JSON.parseObject(jsonString);
		String userId = obj.getString("userId");
		
		if(StringUtils.isEmpty(userId)){
			map.put("returnCode","3006");
			map.put("message", "必传参数为空");
			return map;
		}
		
		try {
			String companyId = userCompanyService.selectBySoleUserId(userId,"0").getCompanyId();
			
			List<UusersRolesKey> list = uusersRolesService.SelectAdministrator(companyId, new Uroles().admin_role);
			
			//当该公司的管理员人数大于1人则可操作
			if(list.size()>1){
				//更换管理员角色为普通员工角色
				uusersRolesService.updateAdminClearHist(userId,new Uroles().user_role,companyId);
				
				map.put("returnCode", "3000");
				map.put("message", "数据请求成功");
				return map;
			}else{
				map.put("returnCode", "4032");
				map.put("message", "管理员不能为空");
				return map;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.info(e);
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
			return map;
		}
	}
}
