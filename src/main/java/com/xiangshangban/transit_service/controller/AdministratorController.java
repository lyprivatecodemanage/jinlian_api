package com.xiangshangban.transit_service.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
import com.xiangshangban.transit_service.util.HttpClientUtil;
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
	public Map<String,Object> administratorInit(@RequestBody String jsonString){
		Map<String,Object> map = new HashMap<>();
		List<Employee> list = new ArrayList<>();

		JSONObject obj = JSON.parseObject(jsonString);
		String companyId = obj.getString("companyId");
		
		if(StringUtils.isEmpty(companyId)){
			map.put("returnCode","3006");
			map.put("message", "必传参数为空");
			return map;
		}
		
		try{
			Company company = companyService.selectByPrimaryKey(companyId);

			UusersRolesKey uusersRolesKey = uusersRolesService.SelectAdministrator(companyId, new Uroles().admin_role);
			
			// 结果为空时代表该公司暂未有管理员
			if(null != uusersRolesKey && !"".equals(uusersRolesKey)){
				// 查看历史管理员数据
				if (StringUtils.isNotEmpty(uusersRolesKey.gethistoryUserIds())) {
					if(uusersRolesKey.gethistoryUserIds().split(",").length>1){
						String [] userids = uusersRolesKey.gethistoryUserIds().split(",");
						
						for (int i = 0; i < userids.length; i++) {
							Employee emp = uusersService.SeletctEmployeeByUserId(userids[userids.length-i-1],uusersRolesKey.getCompanyId());
							
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
						Employee emp = uusersService.SeletctEmployeeByUserId(uusersRolesKey.gethistoryUserIds(),uusersRolesKey.getCompanyId());
						
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
			// 查询当前管理员信息
			Employee employee = uusersService.SeletctEmployeeByUserId(uusersRolesKey.getUserId(),uusersRolesKey.getCompanyId());
			
			if(employee!=null && StringUtils.isNotEmpty(employee.getEmployeeImgUrl())){
				String employeeImgUrlPath = oSSFileService.getPathByKey(company.getCompany_no(),"portrait", employee.getEmployeeImgUrl());
				
				if(StringUtils.isNotEmpty(employeeImgUrlPath)){
					employee.setEmployeeImgUrl(employeeImgUrlPath);
				}
			}else{
				employee.setEmployeeImgUrl("http://xiangshangban.oss-cn-hangzhou.aliyuncs.com/test/sys/portrait/default.png");
			}
			
			map.put("admin",JSON.toJSON(employee));
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
	public Map<String,Object> administratorAuthCode(@RequestBody String jsonString){
		Map<String, Object> result = new HashMap<String, Object>();
		YtxSmsUtil sms = new YtxSmsUtil("LTAIcRopzlp5cbUd", "VnLMEEXQRukZQSP6bXM6hcNWPlphiP");
		try {
			JSONObject obj = JSON.parseObject(jsonString);
			String userId = obj.getString("userId");
			
			String phone = uusersService.selectById(userId).getPhone();
			
			Uusers user = uusersService.selectByPhone(phone);
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
			RedisUtil redis = RedisUtil.getInstance();
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
		
		JSONObject obj = JSON.parseObject(jsonString);
		String newUserId = obj.getString("newUserId");
		String companyId = obj.getString("companyId");
		
		try {
			UusersRolesKey uusersRolesKey = uusersRolesService.SelectAdministrator(companyId, new Uroles().admin_role);

			// 获取现在管理员ID
			String userId = uusersRolesKey.getUserId();
			
			// 获取历史管理员
			String huids = uusersRolesKey.gethistoryUserIds();
			
			if(userId.equals(newUserId)){
				map.put("returnCode", "4025");
				map.put("message", "更换的管理员不能是当前管理员");
				return map;
			}
			
			//如果历史管理员为空
			if(StringUtils.isEmpty(huids)){
				//删除上一位管理员的 历史管理员记录
				uusersRolesService.updateAdminClearHist(userId,new Uroles().user_role,companyId);
				
				//给新管理员添加历史管理员记录
				uusersRolesService.updateAdministrator(newUserId, companyId, userId,new Uroles().admin_role);
			}else{
				if(huids.split(",").length>2){
					
					String [] hisUserId = huids.split(",");
						
					historyUserIds = hisUserId[hisUserId.length-2]+","+hisUserId[hisUserId.length-1]+","+userId;
					
				}else{
					historyUserIds = huids +","+userId;
				}
				//删除上一位管理员的 历史管理员记录
				uusersRolesService.updateAdminClearHist(userId,new Uroles().user_role,companyId);
				
				//将 历史管理员记录更新到新的管理员记录上
				uusersRolesService.updateAdministrator(newUserId, companyId, historyUserIds,new Uroles().admin_role);
			}
			
			//将原来管理员的默认公司修改为其他拥有管理员身份的公司
			List<UusersRolesKey> urlist = uusersRolesService.selectCompanyByUserIdRoleId(userId, new Uroles().admin_role);
			
			if(urlist!=null&&urlist.size()!=0){
				
				List<UserCompanyDefault> list = new ArrayList<>();
				
				for (UusersRolesKey urk : urlist) {
					 list.add(userCompanyService.selectByUserIdAndCompanyId(userId, urk.getCompanyId(),"1"));
				}
				
				int num = userCompanyService.updateUserCompanyCoption(userId, companyId, new UserCompanyDefault().status_2);
				
				if(num>0){
					userCompanyService.updateUserCompanyCoption(userId, list.get(0).getCompanyId(), new UserCompanyDefault().status_1);
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
}
