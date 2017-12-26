package com.xiangshangban.transit_service.controller;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.xiangshangban.transit_service.bean.CheckPendingJoinCompany;
import com.xiangshangban.transit_service.bean.Company;
import com.xiangshangban.transit_service.bean.Department;
import com.xiangshangban.transit_service.bean.Uroles;
import com.xiangshangban.transit_service.bean.UserCompanyDefault;
import com.xiangshangban.transit_service.bean.Uusers;
import com.xiangshangban.transit_service.bean.UusersRolesKey;
import com.xiangshangban.transit_service.service.CheckPendingJoinCompanyService;
import com.xiangshangban.transit_service.service.CompanyService;
import com.xiangshangban.transit_service.service.DepartmentService;
import com.xiangshangban.transit_service.service.UniqueLoginService;
import com.xiangshangban.transit_service.service.UserCompanyService;
import com.xiangshangban.transit_service.service.UusersRolesService;
import com.xiangshangban.transit_service.service.UusersService;
import com.xiangshangban.transit_service.util.FormatUtil;
import com.xiangshangban.transit_service.util.PinYin2Abbreviation;
import com.xiangshangban.transit_service.util.RedisUtil;
import com.xiangshangban.transit_service.util.RedisUtil.Hash;
@RestController
@RequestMapping("/CutCompanyController")
public class CutCompanyController {
	Logger logger = Logger.getLogger(CutCompanyController.class);
	
	@Autowired
	UniqueLoginService uniqueLoginService;
	
	@Autowired
	UserCompanyService userCompanyService;
	
	@Autowired
	UusersService uusersService;
	
	@Autowired
	CompanyService companyService;
	
	@Autowired
	UusersRolesService uusersRolesService;
	
	@Autowired
	DepartmentService departmentService;
	
	@Autowired
	CheckPendingJoinCompanyService checkPendingJoinCompanyService;
	
	/***
	 * 焦振/查看登录用户所属所有公司
	 * @param userId
	 * @param request
	 * @return
	 */
	@Transactional
	@RequestMapping(value="/selectCompanyGather",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
	public Map<String,Object> selectCompanyGather(HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		List<UserCompanyDefault> userCompanyList = new ArrayList<>();
		List<Company> conpanyList = new ArrayList<>();
		// 初始化redis
		RedisUtil redis = RedisUtil.getInstance();
		// 从redis取出短信验证码
		String phone = redis.new Hash().hget(request.getSession().getId(), "session");
						
		Uusers user = uusersService.selectByPhone(phone,"0");
		
		String userId = user.getUserid();
		
		try {
			if(StringUtils.isEmpty(userId)){
				map.put("returnCode","3006");
				map.put("message", "必传参数为空");
				return map;
			}
			
			List<UusersRolesKey> urlist = uusersRolesService.selectCompanyByUserIdRoleId(userId, new Uroles().admin_role);
			
			List<UserCompanyDefault> list = new ArrayList<>();
			
			for (UusersRolesKey uusersRolesKey : urlist) {
				 list.add(userCompanyService.selectByUserIdAndCompanyId(userId, uusersRolesKey.getCompanyId(),"0"));
			}
			
			for (UserCompanyDefault userCompanyDefault : list) {
				if(!"1".equals(userCompanyDefault.getCurrentOption().trim()) && userCompanyDefault.getCurrentOption().trim()!="1"){
					userCompanyList.add(userCompanyDefault);
				}
			}
			
			for (UserCompanyDefault userCompanyDefault : userCompanyList) {
				conpanyList.add(companyService.selectByPrimaryKey(userCompanyDefault.getCompanyId()));
			}
			
			Object companyGather = JSON.toJSON(conpanyList);
			
			map.put("data",companyGather);
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
	
	/***
	 * 焦振/切换公司
	 * @param userId
	 * @param cutCompanyId
	 * @param request
	 * @param response
	 * @return
	 */
	@Transactional
	@RequestMapping(value="/cutCompany",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
	public Map<String,Object> cutCompany(@RequestBody String jsonString,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = new HashMap<>();
		
		// 初始化redis
		RedisUtil redis = RedisUtil.getInstance();
		// 从redis取出短信验证码
		String phone = redis.new Hash().hget(request.getSession().getId(), "session");
								
		Uusers user = uusersService.selectByPhone(phone,"0");
		
		JSONObject obj = JSON.parseObject(jsonString);
		String userId = user.getUserid();
		String cutCompanyId = obj.getString("cutCompanyId");
		
		try {
			if(userId==null||"".equals(userId)||cutCompanyId==null||"".equals(cutCompanyId)){
				map.put("returnCode","3006");
				map.put("message", "必传参数为空");
				return map;
			}
			
			List<UserCompanyDefault> list = userCompanyService.selectByUserId(userId,"0");
			String companyId = "";
			
			for (UserCompanyDefault userCompanyDefault : list) {
				if("1".equals(userCompanyDefault.getCurrentOption().trim())||userCompanyDefault.getCurrentOption().trim()=="1"){
					companyId=userCompanyDefault.getCompanyId();
				}
			}
			
			int num = userCompanyService.updateUserCompanyCoption(userId, companyId, new UserCompanyDefault().status_2,"0");
			
			if(num>0){
				int flag = userCompanyService.updateUserCompanyCoption(userId, cutCompanyId, new UserCompanyDefault().status_1,"0");
				
				if(flag>0){
					map.put("returnCode", "3000");
					map.put("message", "数据请求成功");
					return map;
				}
			}
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
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
	
	/***
	 *  焦振/创建公司
	 * @param userId
	 * @param companyName
	 * @return
	 */
	@Transactional
	@RequestMapping(value="/createCompany", produces = "application/json;charset=UTF-8", method=RequestMethod.POST)
	public Map<String,Object> createCompany(@RequestBody String jsonString,HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		// 初始化redis
		RedisUtil redis = RedisUtil.getInstance();
		// 从redis取出短信验证码
		String phone = redis.new Hash().hget(request.getSession().getId(), "session");
								
		Uusers user = uusersService.selectByPhone(phone,"0");
		
		JSONObject obj = JSON.parseObject(jsonString);
		
		String userId = user.getUserid();
		String companyName = obj.getString("companyName");
		
		if(userId==null||"".equals(userId)||companyName==null||"".equals(companyName)){
			map.put("returnCode", "3006");
            map.put("message", "必传参数为空");
            return map;
		}
		
		try {
			// 根据前台提供注册公司名称查询是否已被注册
            int count = companyService.selectCompanyName(companyName);
            if (count > 0) {
                map.put("returnCode", "4019");
                map.put("message", "公司名称已被注册");
                return map;
            }
         } catch (Exception e) {
               e.printStackTrace();
               logger.info(e);
               map.put("returnCode", "3001");
               map.put("message", "服务器错误");
               return map;
         }
		
		//根据前台传入的用户ID 查询用户信息
		Uusers uuser = uusersService.selectById(userId);
		// 创建新增公司对象
        Company company = new Company();
        
		try {
			// 生成公司创建时间
            Date date = new Date(System.currentTimeMillis());
			
            company.setCompany_id(FormatUtil.createUuid());
            company.setCompany_name(companyName);
            company.setCompany_creat_time(sdf.format(date));
            company.setCompany_approve("0");
            company.setCompany_personal_name(uuser.getUsername());
            company.setUser_name(uuser.getPhone());
			company.setCompany_type("0");
			// 注册公司名称首字母缩写
            String companyNameLo = "";
			if (companyName.length() > 4) {
				// 根据公司名称生成前四位字母小写
                companyNameLo = new PinYin2Abbreviation().cn2py(companyName).substring(0,4);
            } else {
                companyNameLo = new PinYin2Abbreviation().cn2py(companyName);
            }
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
            String sDate = sdf1.format(date);
            // 模糊查询今天是否有同音公司名称的注册信息
            int num = companyService.selectCompanyNo(sDate + companyNameLo);
			// 将查询出来的条数+1存入 以便区别公司编号
            num += 1;
            if (num > 9) {
				// 公司编号
                company.setCompany_no(sDate + companyNameLo + "0" + num);
            } else {
                company.setCompany_no(sDate + companyNameLo + "00" + num);
            }
            //公司二维码生成
            String format ="http://www.xiangshangban.com/show?shjncode=invite_";
            Map<String, String> invite = new HashMap<>();
			invite.put("companyNo", company.getCompany_no());
			invite.put("companyName", company.getCompany_name());
			invite.put("companyPersonalName", company.getCompany_personal_name());
			String qrcode =  format + JSON.toJSONString(invite);
			company.setCompany_code(qrcode);
                
			// 对创建公司的信息进行插入操作
			companyService.insertSelective(company);
			
		}catch(NullPointerException e){
			e.printStackTrace();
			logger.info(e);
			map.put("returnCode", "4007");
			map.put("message", "结果为空");
            return map;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.info(e);
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
            return map;
		}
		
		UserCompanyDefault ucd = new UserCompanyDefault();
		try {
			//公司与用户关联表
			ucd.setUserId(uuser.getUserid());
			ucd.setCompanyId(company.getCompany_id());
			ucd.setCurrentOption(ucd.status_2);
			ucd.setIsActive(ucd.status_1);
			ucd.setInfoStatus(ucd.status_1);
			//创建web端 关联关系
			ucd.setType("0");
			userCompanyService.insertSelective(ucd);
			//创建App端 关联关系
			ucd.setType("1");
			userCompanyService.insertSelective(ucd);
			
		}catch(NullPointerException e){
			e.printStackTrace();
			logger.info(e);
			companyService.deleteByPrimaryKey(company.getCompany_id());
			userCompanyService.deleteByPrimaryKey(ucd);
			map.put("returnCode", "4007");
			map.put("message", "结果为空");
            return map;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.info(e);
			companyService.deleteByPrimaryKey(company.getCompany_id());
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
            return map;
		}
		
		//用户表与角色表
		UusersRolesKey urk = new UusersRolesKey();
		try {
			urk.setUserId(userId);
			urk.setCompanyId(company.getCompany_id());
			urk.setRoleId(new Uroles().admin_role);
			
			uusersRolesService.insertSelective(urk);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.info(e);
			companyService.deleteByPrimaryKey(company.getCompany_id());
			userCompanyService.deleteByPrimaryKey(ucd);
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
            return map;
		}
		try {
			//生成  员工表
			uuser.setCompanyId(company.getCompany_id());
			uuser.setDepartmentId(company.getCompany_id());
			uusersService.insertEmployee(uuser);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.info(e);
			companyService.deleteByPrimaryKey(company.getCompany_id());
			userCompanyService.deleteByPrimaryKey(ucd);
			uusersRolesService.deleteByPrimaryKey(urk);
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
            return map;
		}
		
		try {
			//创建公司默认生成   全公司   部门
			Department department = new Department();
			department.setDepartmentId(company.getCompany_id());
			department.setDepartmentName("全公司");
			department.setDepartmentParentId("0");
			department.setCompanyId(company.getCompany_id());
			
			departmentService.insertDepartment(department);
			
			map.put("companyName",company.getCompany_name());
			map.put("companyNo",company.getCompany_no());
			map.put("companyCode",company.getCompany_code());
			map.put("companyId", company.getCompany_id());
			map.put("returnCode", "3000");
			map.put("message", "数据请求成功");
			return map;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.info(e);
			companyService.deleteByPrimaryKey(company.getCompany_id());
			userCompanyService.deleteByPrimaryKey(ucd);
			uusersRolesService.deleteByPrimaryKey(urk);
			uusersService.deleteEmployee(userId);
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
            return map;
		}
	}
	
	/***
	 *  焦振/APP 查看登录用户所属所有公司
	 * @param request
	 * @return
	 */
	@Transactional
	@RequestMapping(value="/appSelectCompanyGather",method=RequestMethod.POST)
	public Map<String,Object> appSelectCompanyGather(HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		List<Map<String,Object>> conpanyList = new ArrayList<>();
		String token = request.getHeader("token");
		
		try {
			if(StringUtils.isEmpty(token)){
				map.put("returnCode","3006");
				map.put("message", "必传参数为空");
				return map;
			}
			
			// 初始化redis
			RedisUtil redis = RedisUtil.getInstance();
			// 从redis取出短信验证码
			String phone = redis.new Hash().hget(token, "token");
									
			Uusers user = uusersService.selectByPhone(phone,"1");
			
			String userId = user.getUserid();
			
			List<UserCompanyDefault> list = userCompanyService.selectByUserId(userId,"1");
			
			for (UserCompanyDefault userCompanyDefault : list) {
				if(userCompanyDefault.getCurrentOption()!="1"&&!"1".equals(userCompanyDefault.getCurrentOption())){
					Company company = companyService.selectByPrimaryKey(userCompanyDefault.getCompanyId());
					Map<String,Object> comMap = new HashMap<>();
					comMap.put("companyName", company.getCompany_name());
					comMap.put("companyId", company.getCompany_id());
					comMap.put("companyPersonalName", company.getCompany_personal_name());
					comMap.put("userPhone", company.getUser_name());
					comMap.put("DefaultOption","0");
					conpanyList.add(comMap);
				}else{
					Company company = companyService.selectByPrimaryKey(userCompanyDefault.getCompanyId());
					Map<String,Object> comMap = new HashMap<>();
					comMap.put("companyName", company.getCompany_name());
					comMap.put("companyId", company.getCompany_id());
					comMap.put("companyPersonalName", company.getCompany_personal_name());
					comMap.put("userPhone", company.getUser_name());
					comMap.put("DefaultOption","1");
					conpanyList.add(comMap);
				}
			}
			
			Object companyGather = JSON.toJSON(conpanyList);
			
			map.put("data",companyGather);
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
	
	/***
	 * 焦振/ APP 切换公司
	 * @param cutCompanyId
	 * @param request
	 * @return
	 */
	@Transactional
	@RequestMapping(value="/appCutCompany",method = RequestMethod.POST)
	public Map<String,Object> appCutCompany(String cutCompanyId,HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		String token = request.getHeader("token");
		
		// 初始化redis
		RedisUtil redis = RedisUtil.getInstance();
		
		try {
			if(token==null||"".equals(token)||cutCompanyId==null||"".equals(cutCompanyId)){
				map.put("returnCode","3006");
				map.put("message", "必传参数为空");
				return map;
			}
			// 从redis取出短信验证码
			String phone = redis.new Hash().hget(token, "token");
									
			Uusers user = uusersService.selectByPhone(phone,"1");
			
			String userId = user.getUserid();
			
			List<UserCompanyDefault> list = userCompanyService.selectByUserId(userId,"1");
			String companyId = "";
			
			for (UserCompanyDefault userCompanyDefault : list) {
				if("1".equals(userCompanyDefault.getCurrentOption().trim())||userCompanyDefault.getCurrentOption().trim()=="1"){
					companyId=userCompanyDefault.getCompanyId();
				}
			}
			
			int num = userCompanyService.updateUserCompanyCoption(userId, companyId, new UserCompanyDefault().status_2,"1");
			
			if(num>0){
				int flag = userCompanyService.updateUserCompanyCoption(userId, cutCompanyId, new UserCompanyDefault().status_1,"1");
				
				if(flag>0){
					map.put("returnCode", "3000");
					map.put("message", "数据请求成功");
					return map;
				}
			}
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
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
	
	/***
	 *  焦振/ APP 创建公司
	 * @param companyName
	 * @return
	 */
	@Transactional
	@RequestMapping(value="/appCreateCompany",method=RequestMethod.POST)
	public Map<String,Object> appCreateCompany(String companyName,HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String token = request.getHeader("token");
		
		// 初始化redis
		RedisUtil redis = RedisUtil.getInstance();
		
		if(token==null||"".equals(token)||companyName==null||"".equals(companyName)){
			map.put("returnCode", "3006");
            map.put("message", "必传参数为空");
            return map;
		}
		
		try {
			// 从redis取出短信验证码
			String phone = redis.new Hash().hget(token, "token");
									
			Uusers user = uusersService.selectByPhone(phone,"1");
			
			String userId = user.getUserid();
			
			try {
				// 根据前台提供注册公司名称查询是否已被注册
	            int count = companyService.selectCompanyName(companyName);
	            if (count > 0) {
	                map.put("returnCode", "4019");
	                map.put("message", "公司名称已被注册");
	                return map;
	            }
	         } catch (Exception e) {
	               e.printStackTrace();
	               logger.info(e);
	               map.put("returnCode", "3001");
	               map.put("message", "服务器错误");
	               return map;
	         }
			
			//根据前台传入的用户ID 查询用户信息
			Uusers uuser = uusersService.selectById(userId);
			// 创建新增公司对象
	        Company company = new Company();
	        
			try {
				// 生成公司创建时间
	            Date date = new Date(System.currentTimeMillis());
				
	            company.setCompany_id(FormatUtil.createUuid());
	            company.setCompany_name(companyName);
	            company.setCompany_creat_time(sdf.format(date));
	            company.setCompany_approve("0");
	            company.setCompany_personal_name(uuser.getUsername());
	            company.setUser_name(uuser.getPhone());
				company.setCompany_type("0");
				// 注册公司名称首字母缩写
	            String companyNameLo = "";
				if (companyName.length() > 4) {
					// 根据公司名称生成前四位字母小写
	                companyNameLo = new PinYin2Abbreviation().cn2py(companyName).substring(0,4);
	            } else {
	                companyNameLo = new PinYin2Abbreviation().cn2py(companyName);
	            }
	            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	            String sDate = sdf1.format(date);
	            // 模糊查询今天是否有同音公司名称的注册信息
	            int num = companyService.selectCompanyNo(sDate + companyNameLo);
				// 将查询出来的条数+1存入 以便区别公司编号
	            num += 1;
	            if (num > 9) {
					// 公司编号
	                company.setCompany_no(sDate + companyNameLo + "0" + num);
	            } else {
	                company.setCompany_no(sDate + companyNameLo + "00" + num);
	            }
	            //公司二维码生成
	            String format ="http://www.xiangshangban.com/show?shjncode=invite_";
	            Map<String, String> invite = new HashMap<>();
				invite.put("companyNo", company.getCompany_no());
				invite.put("companyName", company.getCompany_name());
				invite.put("companyPersonalName", company.getCompany_personal_name());
				String qrcode =  format + JSON.toJSONString(invite);
				company.setCompany_code(qrcode);
	                
				// 对创建公司的信息进行插入操作
				companyService.insertSelective(company);
				
			}catch(NullPointerException e){
				e.printStackTrace();
				logger.info(e);
				map.put("returnCode", "4007");
				map.put("message", "结果为空");
	            return map;
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				logger.info(e);
				map.put("returnCode", "3001");
				map.put("message", "服务器错误");
	            return map;
			}
			
			UserCompanyDefault ucd = new UserCompanyDefault();
			try {
				//公司与用户关联表
				ucd.setUserId(uuser.getUserid());
				ucd.setCompanyId(company.getCompany_id());
				ucd.setCurrentOption(ucd.status_2);
				ucd.setIsActive(ucd.status_1);
				ucd.setInfoStatus(ucd.status_1);
				//创建app端关联关系
				ucd.setType("1");
				userCompanyService.insertSelective(ucd);
				//创建web端关联关系
				ucd.setType("0");
				userCompanyService.insertSelective(ucd);
			}catch(NullPointerException e){
				e.printStackTrace();
				logger.info(e);
				companyService.deleteByPrimaryKey(company.getCompany_id());
				userCompanyService.deleteByPrimaryKey(ucd);
				map.put("returnCode", "4007");
				map.put("message", "结果为空");
	            return map;
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				logger.info(e);
				companyService.deleteByPrimaryKey(company.getCompany_id());
				map.put("returnCode", "3001");
				map.put("message", "服务器错误");
	            return map;
			}
			
			//用户表与角色表
			UusersRolesKey urk = new UusersRolesKey();
			try {
				urk.setUserId(userId);
				urk.setCompanyId(company.getCompany_id());
				urk.setRoleId(new Uroles().admin_role);
				
				uusersRolesService.insertSelective(urk);
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				logger.info(e);
				companyService.deleteByPrimaryKey(company.getCompany_id());
				userCompanyService.deleteByPrimaryKey(ucd);
				map.put("returnCode", "3001");
				map.put("message", "服务器错误");
	            return map;
			}
	
			try {
				//生成  员工表
				uuser.setCompanyId(company.getCompany_id());
				uusersService.insertEmployee(uuser);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				logger.info(e);
				companyService.deleteByPrimaryKey(company.getCompany_id());
				userCompanyService.deleteByPrimaryKey(ucd);
				uusersRolesService.deleteByPrimaryKey(urk);
				map.put("returnCode", "3001");
				map.put("message", "服务器错误");
	            return map;
			}
			
			try {
				//创建公司默认生成   全公司   部门
				Department department = new Department();
				department.setDepartmentId(FormatUtil.createUuid());
				department.setDepartmentName("全公司");
				department.setDepartmentParentId("0");
				department.setCompanyId(company.getCompany_id());
				
				departmentService.insertDepartment(department);
				
				map.put("companyName",company.getCompany_name());
				map.put("companyNo",company.getCompany_no());
				map.put("companyCode",company.getCompany_code());
				map.put("companyId", company.getCompany_id());
				map.put("returnCode", "3000");
				map.put("message", "数据请求成功");
				return map;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				logger.info(e);
				companyService.deleteByPrimaryKey(company.getCompany_id());
				userCompanyService.deleteByPrimaryKey(ucd);
				uusersRolesService.deleteByPrimaryKey(urk);
				uusersService.deleteEmployee(userId);
				map.put("returnCode", "3001");
				map.put("message", "服务器错误");
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
	
	/***
	 *  焦振 / APP 根据公司编号查看公司信息
	 * @param companyNo
	 * @param request
	 * @return
	 */
	@Transactional
	@RequestMapping(value="/appSelectCompanyByCompanyNo",method=RequestMethod.POST)
	public Map<String,Object> appSelectCompanyByCompanyNo(String companyNo,HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();

		try {
			Company company = companyService.selectByCompanyName(companyNo);
			
			map.put("company",company);
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
	
	/***
	 *  焦振 / APP 加入其它公司
	 * @param companyNo
	 * @param request
	 * @return
	 */
	@Transactional
	@RequestMapping(value="/appJoinCompany",method=RequestMethod.POST)
	public Map<String,Object> appJoinCompany(String companyNo,HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String token = request.getHeader("token");
		// 初始化redis
		RedisUtil redis = RedisUtil.getInstance();
		try {
			if(token==null||"".equals(token)||companyNo==null||"".equals(companyNo)){
				map.put("returnCode","3006");
				map.put("message", "必传参数为空");
				return map;
			}
			// 从redis取出短信验证码
			String phone = redis.new Hash().hget(token, "token");
									
			Uusers user = uusersService.selectByPhone(phone,"1");
			
			String userId = user.getUserid();
			
			// 根据前台提供注册公司名称查询是否存在
            int count = companyService.selectByCompany(companyNo);
            if (count > 0) {
            	
                //根据company_no查询出companyID
                Company company = companyService.selectByCompanyName(companyNo);
                //根据 userId 与 companyID查询 usercompany表  看是否存在记录 
                //存在记录则已加入公司直接返回  不存在则继续操作
                UserCompanyDefault ucd = userCompanyService.selectByUserIdAndCompanyId(userId,company.getCompany_id(),"1");
                
                if(ucd==null){
                
                    Date joinDate = new Date();
                    
					// 加入公司 新增待审核表记录
	                CheckPendingJoinCompany checkPendingJoinCompany = new CheckPendingJoinCompany();
	                checkPendingJoinCompany.setUserid(userId);
	                checkPendingJoinCompany.setCompanyid(company.getCompany_id());
	                checkPendingJoinCompany.setStatus(checkPendingJoinCompany.status_0);
	                checkPendingJoinCompany.setApplyTime(sdf.format(joinDate));
	                checkPendingJoinCompanyService.insertSelective(checkPendingJoinCompany);
            	
                }
            
				map.put("companyName", company.getCompany_name());
				map.put("companyPersonalName", company.getCompany_personal_name());
	            map.put("userPhone",user.getPhone());
				map.put("userName",user.getUsername());
				map.put("returnCode", "3000");
				map.put("message", "数据请求成功");
				return map;
            }else{
            	 map.put("returnCode", "4006");
				 map.put("message", "加入公司不存在");
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