package com.xiangshangban.transit_service.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.xiangshangban.transit_service.bean.CheckPendingJoinCompany;
import com.xiangshangban.transit_service.bean.Company;
import com.xiangshangban.transit_service.bean.Department;
import com.xiangshangban.transit_service.bean.Employee;
import com.xiangshangban.transit_service.bean.Uroles;
import com.xiangshangban.transit_service.bean.UserCompanyDefault;
import com.xiangshangban.transit_service.bean.Uusers;
import com.xiangshangban.transit_service.bean.UusersRolesKey;
import com.xiangshangban.transit_service.service.CheckPendingJoinCompanyService;
import com.xiangshangban.transit_service.service.CompanyService;
import com.xiangshangban.transit_service.service.DepartmentService;
import com.xiangshangban.transit_service.service.EmployeeService;
import com.xiangshangban.transit_service.service.UserCompanyService;
import com.xiangshangban.transit_service.service.UusersRolesService;
import com.xiangshangban.transit_service.service.UusersService;
import com.xiangshangban.transit_service.util.FormatUtil;
import com.xiangshangban.transit_service.util.HttpClientUtil;
import com.xiangshangban.transit_service.util.PinYin2Abbreviation;
import com.xiangshangban.transit_service.util.PropertiesUtils;
import com.xiangshangban.transit_service.util.RedisUtil;

/**
 * Created by mian on 2017/10/30.
 */
@RestController
@RequestMapping("/registerController")
public class RegisterController {
    Logger logger = Logger.getLogger(RegisterController.class);
    @Autowired
    UusersService uusersService;

    @Autowired
    CompanyService companyService;

    @Autowired
    CheckPendingJoinCompanyService checkPendingJoinCompanyService;

    @Autowired
    UserCompanyService userCompanyService;

    @Autowired
    UusersRolesService uusersRolesService;
    
    @Autowired
    DepartmentService departmentService;
    
    @Autowired
    EmployeeService employeeService;
    
	/***
	 * 焦振/进行用户注册 公司注册
	 * 
	 * @param phone
	 * @param userName
	 * @param companyName
	 * @param type
	 * @return
	 */
    
    @Transactional
    @RequestMapping(value = "/registerUsers", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Map<String, Object> registerUsers(String phone,String temporaryPwd,String userName,String companyName,String company_no,String type,HttpServletRequest request) {

        Map<String, Object> map = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 全局 公司ID值
        String companyId = "";
		// 用户编号
        String userId = "";
        
        if(null==type||"".equals(type)||phone==null||"".equals(phone)||temporaryPwd==null||
        		"".equals(temporaryPwd)){
        	map.put("returnCode", "3006");
			map.put("message", "参数为空");
            return map;
        }
        try {
        	Uusers uuser = uusersService.selectUserByPhone(phone);
        	
        	if(uuser==null){
        		// 从redis中获取之前存入的验证码 判断是否还在有效期
                RedisUtil redis = RedisUtil.getInstance();
                String redisTemporaryPwd = redis.new Hash().hget("smsCode_"+phone, "smsCode");
                if (temporaryPwd.equals(redisTemporaryPwd)) {
                    if(redisTemporaryPwd!=null){
    					// 生成UUID作为用户编号
                        userId = FormatUtil.createUuid();
    					// 获取系统时间作为用户创建时间
                        Date date = new Date(System.currentTimeMillis());
    					// 创建新增实体
                        Uusers uUsers = new Uusers();
                        uUsers.setUserid(userId);
                        uUsers.setPhone(phone);
                        uUsers.setTemporarypwd(temporaryPwd);
                        uUsers.setUsername(userName);
                        uUsers.setCreateTime(sdf.format(date));
                        uUsers.setStatus(Uusers.status_0);
                        uusersService.insertSelective(uUsers);
                    }else{
                        map.put("returnCode", "4001");
    					map.put("message", "验证码失效");
                        return map;
                    }
                } else {
                    map.put("returnCode", "4002");
    				map.put("message", "验证码错误");
                    return map;
                }
        	}else if(uuser!=null&&uuser.getStatus().equals("0")){
        		// 从redis中获取之前存入的验证码 判断是否还在有效期
                RedisUtil redis = RedisUtil.getInstance();
                String redisTemporaryPwd = redis.new Hash().hget("smsCode_"+phone, "smsCode");
                if (temporaryPwd.equals(redisTemporaryPwd)) {
                    if(redisTemporaryPwd!=null){
                    	userId = uuser.getUserid();
    					// 获取系统时间作为用户创建时间
                        Date date = new Date(System.currentTimeMillis());
    					// 创建新增实体
                        Uusers uUsers = new Uusers();
                        uUsers.setPhone(phone);
                        uUsers.setTemporarypwd(temporaryPwd);
                        uUsers.setUsername(userName);
                        uUsers.setCreateTime(sdf.format(date));
                        uUsers.setStatus(Uusers.status_0);
                        uusersService.updateUserByPhone(uUsers);
                    }else{
                        map.put("returnCode", "4001");
    					map.put("message", "验证码失效");
                        return map;
                    }
                } else {
                    map.put("returnCode", "4002");
    				map.put("message", "验证码错误");
                    return map;
                }
        	}
			
        }catch(NullPointerException e){
        	e.printStackTrace();
        	logger.info(e);
            map.put("returnCode", "3006");
 			map.put("message", "必传参数为空");
            return map;
        }catch (Exception e) {
            e.printStackTrace();
            logger.info(e);
            map.put("returnCode", "3001");
			map.put("message", "服务器错误");
            return map;
        }

        if (type.equals("0")) {
            try {
				// 根据前台提供注册公司名称查询是否已被注册
                int count = companyService.selectCompanyName(companyName);
                if (count > 0) {
                	uusersService.deleteByPrimaryKey(userId);
                    map.put("returnCode", "4019");
					map.put("message", "公司名称已被注册");
                    return map;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e);
                uusersService.deleteByPrimaryKey(userId);
                map.put("returnCode", "3001");
				map.put("message", "服务器错误");
                return map;
            }

            try {
            	if(companyName.indexOf("(")>0){
            		companyName = companyName.replaceAll("[\\(\\)]", "");
            	}
            	if(companyName.indexOf("（")>0){
            		companyName = companyName.replaceAll("[\\（\\）]", "");
            	}
            	// 生成公司创建时间
                Date date = new Date(System.currentTimeMillis());
				// 生成公司编号
                companyId = FormatUtil.createUuid();
				// 创建新增公司对象
                Company company = new Company();
                company.setCompany_id(companyId);
                company.setCompany_name(companyName);
                company.setCompany_creat_time(sdf.format(date));
                company.setCompany_approve("0");
                company.setUser_name(phone);
                company.setCompany_personal_name(userName);
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

			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e);
				// 创建公司失败删除用户信息
				uusersService.deleteByPrimaryKey(userId);
				map.put("returnCode", "3001");
				map.put("message", "服务器错误");
				return map;
			}
			UserCompanyDefault userCompanyKey = new UserCompanyDefault();
			try {
				// 将新创建的公司编号信息存入用户与公司关联表中

				userCompanyKey.setCompanyId(companyId);
				userCompanyKey.setUserId(userId);
				userCompanyKey.setCurrentOption(userCompanyKey.status_1);
				userCompanyKey.setIsActive(userCompanyKey.status_1);
				userCompanyKey.setInfoStatus(userCompanyKey.status_1);
				
				//web端
				userCompanyKey.setType("0");
				userCompanyService.insertSelective(userCompanyKey);
				//app端
				userCompanyKey.setType("1");
				userCompanyService.insertSelective(userCompanyKey);
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e);
				uusersService.deleteByPrimaryKey(userId);
				companyService.deleteByPrimaryKey(companyId);
				map.put("returnCode", "3001");
				map.put("message", "服务器错误");
				return map;
			}
            
			try {
				// 创建公司加入组织成功 将用户状态改为可用
				Uusers uusers = new Uusers();
				uusers.setUserid(userId);
				uusers.setStatus(uusers.status_1);
				uusersService.updateByPrimaryKeySelective(uusers);
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e);
				// 修改状态失败删除用户公司信息
				companyService.deleteByPrimaryKey(companyId);
				uusersService.deleteByPrimaryKey(userId);
				userCompanyService.deleteByPrimaryKey(userCompanyKey);
				map.put("returnCode", "3001");
				map.put("message", "服务器错误");
				return map;
			}

			try {
				// 赋予创建公司用户角色
				UusersRolesKey urk = new UusersRolesKey();
				urk.setUserId(userId);
				urk.setRoleId(new Uroles().admin_role);
				urk.setCompanyId(companyId);
				uusersRolesService.insertSelective(urk);
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e);
				uusersService.deleteByPrimaryKey(userId);
				companyService.deleteByPrimaryKey(companyId);
				userCompanyService.deleteByPrimaryKey(userCompanyKey);
				map.put("returnCode", "3001");
				map.put("message", "服务器错误");
				return map;
			}

			try {
				// 将用户信息打包数据做员工信息新增
				Uusers uu = new Uusers();
				uu.setUserid(userId);
				uu.setUsername(userName);
				uu.setCompanyId(companyId);
				uu.setPhone(phone);
				uu.setDepartmentId(companyId);
				uu.setOperaterTime(sdf.format(new Date()));
				uusersService.insertEmployee(uu);

				//给设备发送更新人员信息
				Employee employee = employeeService.selectByEmployee(userId,companyId);
				Company company = companyService.selectByPrimaryKey(companyId);
			    employee.setCompanyNo(company.getCompany_no());
				List<Employee> cmdlist=new ArrayList<Employee>();
				cmdlist.add(employee);
				try {
					String result = HttpClientUtil.sendRequet(PropertiesUtils.pathUrl("commandGenerate"), cmdlist);
					logger.info("设备访问成功"+result);
				} catch (IOException e) {
					logger.info("将人员信息更新到设备模块时，获取路径出错");
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e);
				uusersService.deleteByPrimaryKey(userId);
				companyService.deleteByPrimaryKey(companyId);
				userCompanyService.deleteByPrimaryKey(userCompanyKey);
				uusersRolesService.deleteByPrimaryKey(new UusersRolesKey(new Uroles().admin_role, userId, companyId));
				map.put("returnCode", "3001");
				map.put("message", "服务器错误");
				return map;
			}
			
			try {
				Department department = new Department();
				department.setDepartmentId(companyId);
				department.setDepartmentName("全公司");
				department.setDepartmentParentId("0");
				department.setCompanyId(companyId);
				
				departmentService.insertDepartment(department);
				
				map.put("companyId", companyId);
				map.put("companyName", companyName);
				map.put("user_name", userName);
				map.put("returnCode", "3000");
				map.put("message", "数据请求成功");
				return map;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				logger.info(e);
				uusersService.deleteByPrimaryKey(userId);
				companyService.deleteByPrimaryKey(companyId);
				userCompanyService.deleteByPrimaryKey(userCompanyKey);
				uusersRolesService.deleteByPrimaryKey(new UusersRolesKey(new Uroles().admin_role, userId, companyId));
				uusersService.deleteEmployee(userId);
				map.put("returnCode", "3001");
				map.put("message", "服务器错误");
				return map;
			}
			
        }

        if (type.equals("1")) {
            try {
				// 根据前台提供注册公司名称查询是否存在
                int count = companyService.selectByCompany(company_no);
                if (count > 0) {
                	
                	//使用手机号码查询出EmployeeID
                    String employeeId = uusersService.SelectEmployeeIdByPhone(phone);
                    //根据company_no查询出companyID
                    Company company = companyService.selectByCompanyName(company_no);
                    //根据EmployeeID 与 companyID查询 usercompany表  看是否存在记录 
                    
                    String WebAppType = request.getHeader("type");
                    
                  //存在记录则已加入公司直接返回  不存在则继续操作
                    UserCompanyDefault ucd = userCompanyService.selectByUserIdAndCompanyId(employeeId, company.getCompany_id(),WebAppType);
                	
                    if(ucd==null){
                    	CheckPendingJoinCompany cpjc = checkPendingJoinCompanyService.selectRecord(userId, company.getCompany_id(),"0");
                    	if(cpjc==null){
		                    Date joinDate = new Date();
							// 加入公司 新增待审核表记录
			                CheckPendingJoinCompany checkPendingJoinCompany = new CheckPendingJoinCompany();
			                checkPendingJoinCompany.setUserid(userId);
			                checkPendingJoinCompany.setCompanyid(company.getCompany_id());
			                checkPendingJoinCompany.setStatus(checkPendingJoinCompany.status_0);
			                checkPendingJoinCompany.setApplyTime(sdf.format(joinDate));
			                checkPendingJoinCompanyService.insertSelective(checkPendingJoinCompany);
                    	}
                    }
					// 审核通过
					// if(1==1){
					// try{
					// Date upDate = new Date();
					// // 待审核通过后 修改待审核表中的状态
					// CheckPendingJoinCompany cpjc = new
					// CheckPendingJoinCompany();
					// cpjc.setUserid(userId);
					// cpjc.setCompanyid(company.getCompany_id());
					// cpjc.setApplyTime(sdf.format(upDate));
					// cpjc.setStatus(CheckPendingJoinCompany.status_1);
					// checkPendingJoinCompanyService.updateByPrimaryKeySelective(cpjc);
					// }catch(Exception e){
					// e.printStackTrace();
					// logger.info(e);
					// uusersService.deleteByPrimaryKey(userId);
					// checkPendingJoinCompanyService.deleteById(userId,company.getCompany_id());
					// map.put("returnCode", "3001");
					// map.put("message", "服务器错误");
					// return map;
					// }
					//
					// try{
					// // 待审核通过后 将加入的公司编号信息存入用户与公司关联表中
					// UserCompanyDefault userCompanyKey = new
					// UserCompanyDefault();
					// userCompanyKey.setCompanyId(company.getCompany_id());
					// userCompanyKey.setUserId(userId);
					// userCompanyKey.setCurrentOption(userCompanyKey.status_1);
					// userCompanyService.insertSelective(userCompanyKey);
					// }catch(Exception e){
					// e.printStackTrace();
					// logger.info(e);
					// uusersService.deleteByPrimaryKey(userId);
					// checkPendingJoinCompanyService.deleteById(userId,company.getCompany_id());
					// map.put("returnCode", "3001");
					// map.put("message", "服务器错误");
					// return map;
					// }
					//
					// try {
					// // 审核通过修改用户表中状态
					// Uusers u = new Uusers();
					// u.setUserid(userId);
					// u.setStatus(u.status_1);
					// uusersService.updateByPrimaryKeySelective(u);
					// } catch (Exception e) {
					// e.printStackTrace();
					// logger.info(e);
					// uusersService.deleteByPrimaryKey(userId);
					// checkPendingJoinCompanyService.deleteById(userId,company.getCompany_id());
					// userCompanyService.deleteByPrimaryKey(new
					// UserCompanyDefault(userId,company.getCompany_id()));
					// map.put("returnCode", "3001");
					// map.put("message", "服务器错误");
					// return map;
					// }
					// UusersRolesKey urk = new UusersRolesKey();
					// try {
					// // 赋予加入公司用户角色
					// urk.setUserId(userId);
					// urk.setRoleId(new Uroles().user_role);
					// urk.setCompanyId(company.getCompany_id());
					// uusersRolesService.insertSelective(urk);
					// } catch (Exception e) {
					// e.printStackTrace();
					// logger.info(e);
					// uusersService.deleteByPrimaryKey(userId);
					// checkPendingJoinCompanyService.deleteById(userId,company.getCompany_id());
					// userCompanyService.deleteByPrimaryKey(new
					// UserCompanyDefault(userId,company.getCompany_id()));
					// map.put("returnCode", "3001");
					// map.put("message", "服务器错误");
					// return map;
					// }
					//
					// // // 将用户信息打包数据做员工信息新增
					// // Map<String, String> userMap = new HashMap<>();
					// // userMap.put("employeeName", userName);
					// // userMap.put("userName", phone);
					// //
					// // String url =
					// //
					// "http://192.168.0.242:8093/organization/EmployeeController/insertEmployee";
					// // String str = HttpClientUtil.sendRequet(url, "{}",
					// // ContentType.APPLICATION_JSON, userMap);
					// // JSONObject jobj = JSON.parseObject(str);
					// //
					// // if ("3000".equals(jobj.get("returnCode"))) {
					// map.put("companyId", company.getCompany_id());
					// map.put("companyName", company.getCompany_name());
					// map.put("user_name", company.getUser_name());
					// map.put("returnCode", "3000");
					// map.put("message", "数据请求成功");
					// return map;
					// // } else {
					// // uusersService.deleteByPrimaryKey(userId);
					// //
					// checkPendingJoinCompanyService.deleteById(userId,company.getCompany_id());
					// // userCompanyService.deleteByPrimaryKey(new
					// //
					// UserCompanyDefault(userId,company.getCompany_id(),null));
					// // uusersRolesService.deleteByPrimaryKey(urk);
					// // map.put("returnCode", "3001");
					// // map.put("message", "服务器错误");
					// // return map;
					// // }
					// }

					map.put("companyId", company.getCompany_id());
					map.put("companyName", company.getCompany_name());
					map.put("user_name", company.getUser_name());
					map.put("returnCode", "3000");
					map.put("message", "数据请求成功");
					return map;
                } else {
                	uusersService.deleteByPrimaryKey(userId);
                    map.put("returnCode", "4006");
					map.put("message", "加入公司不存在");
                    return map;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e);
                uusersService.deleteByPrimaryKey(userId);
                map.put("returnCode", "3001");
				map.put("message", "服务器错误");
                return map;
            }
        }
		uusersService.deleteByPrimaryKey(userId);
		map.put("returnCode", "3001");
		map.put("message", "服务器错误");
		return map;
    }

    /***
	 * 焦振/查询手机号是否已被注册
	 * 
	 * @param phone
	 * @return
	 */
    @RequestMapping(value = "/SelectByPhone")
    public Map<String, Object> SelectByPhone(String phone,HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Uusers uuser = uusersService.selectUserByPhone(phone);
			// 判断手机号是否被注册
            if (uuser!=null&&uuser.getStatus().equals("1")) {
                map.put("status", "1");
                map.put("returnCode", "4005");
				map.put("message", "已注册");
				request.setAttribute("map", map);
                return map;
            } else {
                map.put("status", "0");
                map.put("returnCode", "3000");
				map.put("message", "未注册");
				request.setAttribute("map", map);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e);
            map.put("returnCode", "3001");
			map.put("message", "服务器错误");
			request.setAttribute("map", map);
            return map;
        }
    }

	@RequestMapping(value = "/LoginOut")
	public Map<String, Object> LoginOut() {
		Map<String, Object> map = new HashMap<>();
		System.out.println("LoginOut=================>");
		map.put("returnCode", "3003");
		map.put("message", "用户身份信息缺失");
		return map;
	}
}