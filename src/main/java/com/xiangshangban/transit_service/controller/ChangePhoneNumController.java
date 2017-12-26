package com.xiangshangban.transit_service.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xiangshangban.transit_service.bean.ChangePhone;
import com.xiangshangban.transit_service.bean.Login;
import com.xiangshangban.transit_service.bean.Uusers;
import com.xiangshangban.transit_service.service.ChangePhoneService;
import com.xiangshangban.transit_service.service.LoginService;
import com.xiangshangban.transit_service.service.UusersService;
import com.xiangshangban.transit_service.util.FormatUtil;
import com.xiangshangban.transit_service.util.RedisUtil;

@RestController
@RequestMapping("/ChangePhoneNumController")
public class ChangePhoneNumController {
	private static Logger logger = Logger.getLogger(ChangePhoneNumController.class);
	@Autowired
	private UusersService uusersService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private ChangePhoneService changePhoneService;
	/**
	 * @author 李业/更改手机号:第一步,验证身份信息
	 * @param phone
	 * @param userName
	 * @return
	 */
	@RequestMapping("/verifyOriginalPhone")
	public Map<String,Object> verifyOriginalPhone(String phone,String userName,HttpServletRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		String type = request.getHeader("type");
		if(phone==null || "".equals(phone) || userName==null || "".equals(userName)){
			result.put("returnCode", "3007");
			result.put("message", "参数格式不正确");
			return result;
		}
		try{
			Uusers user = uusersService.selectByPhone(phone,type);
			if(user==null){
				result.put("message", "手机号不存在");
				result.put("returnCode", "4004");
				return result;
			}
			
			if(userName.equals(user.getUsername())){
				result.put("message", "信息校验成功");
				result.put("returnCode", "3000");
			}else{
				result.put("message", "身份信息不正确");
				result.put("returnCode", "4016");
			}
			return result;
	} catch (NumberFormatException e) {
		e.printStackTrace();
		logger.info(e);
		result.put("returnCode", "3007");
		result.put("message", "参数格式不正确");
		return result;
	} catch (NullPointerException e) {
		e.printStackTrace();
		logger.info(e);
		result.put("returnCode", "3006");
		result.put("message", "参数为null");
		return result;
	} catch (Exception e) {
		e.printStackTrace();
		logger.info(e);
		result.put("returnCode", "3001");
		result.put("message", "失败");
		return result;
	}
	}
	
	/**
	 * @author 李业/更改手机号:判断新手机号是否注册以及是否活跃
	 * @param phone
	 * @return
	 *//*
	@RequestMapping("/changePhoneTask")
	public Map<String, Object> changePhoneTask(String phone) {
		Map<String, Object> result = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Uusers user = uusersService.selectByPhone(phone);
			// 系统中是否有新手机号
			// 否
			if (user == null) {
				result.put("message", "未注册");
				result.put("returnCode", "4004");
				return result;
			} else {
				// 查看是否活跃
				Login login = loginService.selectOneByPhone(phone);
				Date lastLoginTime = sdf.parse(login.getCreateTime());
				Date nowTime = new Date();
				Long lastTime = lastLoginTime.getTime();
				Long firstTime = nowTime.getTime();
				Long dayNum = (firstTime - lastTime) / (1000 * 60 * 60 * 24);
				// 判断用户是否活跃
				if (dayNum < 90) {
					// 活跃
					result.put("message", "当前手机处于活跃状态,不可被重新注册");
					result.put("returnCode", "4010");
					return result;
				} else {
					// 不活跃
					result.put("message", "当前手机处于不活跃状态,请输入验证码继续...");
					result.put("returnCode", "4011");
					return result;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3007");
			result.put("message", "参数格式不正确");
			return result;
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3006");
			result.put("message", "参数为null");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3001");
			result.put("message", "失败");
			return result;
		}
	}
*/
	/**
	 * @author 李业/不活跃号码判断是否在保护期
	 * @param phone
	 * @param smsCode
	 * @return
	 */
	@RequestMapping("/isProtect")
	public Map<String, Object> isProtect(String phone, String smsCode,HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		String type = request.getHeader("type");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			RedisUtil redis = RedisUtil.getInstance();
			String redisSmsCode = redis.new Hash().hget("smsCode_" + phone, "smsCode");
			if (redisSmsCode == null) {
				result.put("message", "验证码已过期失效,请重新获取");
				result.put("returnCode", "4001");
				return result;
			}
			if (!smsCode.equals(redisSmsCode)) {
				result.put("message", "验证码不正确");
				result.put("returnCode", "4002");
				return result;
			}
			Uusers user = uusersService.selectByPhone(phone,type);
			// 系统中是否有新手机号
			// 否
			if (user == null) {
				result.put("message", "未注册");
				result.put("returnCode", "4004");
				return result;
			}else {
				// 查看是否活跃
				Login login = loginService.selectOneByPhone(phone);
				Date lastLoginTime = sdf.parse(login.getCreateTime());
				Date nowTime = new Date();
				Long lastTime = lastLoginTime.getTime();
				Long firstTime = nowTime.getTime();
				Long dayNum = (firstTime - lastTime) / (1000 * 60 * 60 * 24);
				// 判断用户是否活跃
				if (dayNum < 90) {
					// 活跃
					result.put("message", "当前手机处于活跃状态,不可被重新注册");
					result.put("returnCode", "4010");
					return result;
				} else {
					// 不活跃
					// 判断手机号是否在保护期内
					if (dayNum < 180) {
						// 在保护期
						result.put("message", "该手机号当前处于保护期状态,请继续填写身份信息认证以完成申请...");
						result.put("returnCode", "4012");
						return result;
					} else {
						// 不在保护期
						result.put("message", "该手机号不处于保护期");
						result.put("returnCode", "4013");
						return result;
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3007");
			result.put("message", "参数格式不正确");
			return result;
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3006");
			result.put("message", "参数为null");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3001");
			result.put("message", "失败");
			return result;
		}
	}

	/**
	 * @author 李业/号码在保护期,进行的身份认证
	 * @param phone
	 * @param userName
	 * @param companyName
	 * @return
	 */
	@RequestMapping("/identityAuthentication")
	public Map<String, Object> identityAuthentication(String phone, String userName, String companyName) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			int i = uusersService.selectIdentityAuthentication(phone, userName, companyName);
			// 判断输入的身份信息是否正确
			if (i > 0) {
				// 正确
				result.put("message", "处于保护期,提出修改认证信息通过");
				result.put("returnCode", "3000");
				return result;
			}
			result.put("message", "处于保护期,提出修改认证信息未通过");
			result.put("returnCode", "4015");
			return result;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3007");
			result.put("message", "参数格式不正确");
			return result;
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3006");
			result.put("message", "参数为null");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3001");
			result.put("message", "失败");
			return result;
		}
	}
	/**
	 * @author 李业/用户个人信息认证
	 * @param oldPhone
	 * @param newPhone
	 * @param userName
	 * @param postName
	 * @return
	 */
	@RequestMapping("/personalInformationVerification")
	public Map<String, Object> personalInformationVerification(String oldPhone, String newPhone, String userName,
			String postName) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<String> list = new ArrayList<String>();
			List<ChangePhone> changePhoneList = uusersService.selectPersonalInformationVerification(newPhone, userName,
					postName);
			if (changePhoneList.size() > 0) {
				for (ChangePhone piv : changePhoneList) {
					if (userName.equals(piv.getUserName()) && postName.equals(piv.getPostName())) {
						// 个人信息正确
						// 生成一个随机验证码
						int verificationCode = (int) Math.floor(Math.random() * 100000);
						result.put("verificationCode", verificationCode);
						for (ChangePhone piv1 : changePhoneList) {
							list.add(piv1.getCompanyId());
						}
						// 去重
						Set<String> set = new HashSet<String>(list);
						Iterator<String> iterator = set.iterator();
						int num = 0;
						while (iterator.hasNext()) {
							num += 1;
							// 认证人信息
							ChangePhone changePhone = uusersService.selectApprovalPerson(iterator.next());
							result.put("company" + num, changePhone);
						}
					}
				}
				result.put("postName", postName);
				result.put("userName", userName);
				result.put("newPhone", newPhone);
				result.put("oldPhone", oldPhone);
				result.put("message", "本人认证信息通过");
				result.put("returnCode", "3000");
				return result;
			}
			result.put("message", "本人认证信息未通过");
			result.put("returnCode", "4017");
			return result;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3007");
			result.put("message", "参数格式不正确");
			return result;
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3006");
			result.put("message", "参数为null");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3001");
			result.put("message", "失败");
			return result;
		}
	}
	/**
	 * @author 李业/个人信息认证通过后,选择本次申请认证人员后提交申请信息
	 * @param changePhone
	 * @return
	 */
	@RequestMapping("/submitInformation")
	public Map<String, Object> submitInformation(ChangePhone changePhone,HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		String type = request.getHeader("type");
		try {

			String oldPhone = changePhone.getOldPhone();
			Uusers user = uusersService.selectByPhone(oldPhone,type);
			changePhone.setUserId(user.getUserid());
			changePhone.setId(FormatUtil.createUuid());
			changePhone.setVerificationStatus("0");
			int i = changePhoneService.insertActiviti(changePhone);
			if (i > 0) {
				result.put("message", "申请提交成功");
				result.put("returnCode", "3000");
			}
			result.put("returnCode", "3001");
			result.put("message", "失败");
			return result;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3007");
			result.put("message", "参数格式不正确");
			return result;
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3006");
			result.put("message", "参数为null");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e);
			result.put("returnCode", "3001");
			result.put("message", "失败");
			return result;
		}
	}
	
	public Map<String,Object> getApprovalList(String userId,String companyId){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			List<ChangePhone> list = changePhoneService.selectListByApprovalPersonId(userId, companyId);
			result.put("list", list);
			result.put("message", "成功");
			result.put("returnCode", "3000");
			return result;
	} catch (NumberFormatException e) {
		e.printStackTrace();
		logger.info(e);
		result.put("returnCode", "3007");
		result.put("message", "参数格式不正确");
		return result;
	} catch (NullPointerException e) {
		e.printStackTrace();
		logger.info(e);
		result.put("returnCode", "3006");
		result.put("message", "参数为null");
		return result;
	} catch (Exception e) {
		e.printStackTrace();
		logger.info(e);
		result.put("returnCode", "3001");
		result.put("message", "失败");
		return result;
	}
	}
	
	public Map<String,Object> startApproval(String id,String verificationCode){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			ChangePhone changePhone = changePhoneService.selectByPramaryKey(id);
			if(!verificationCode.equals(changePhone.getVerificationCode())){
				result.put("message", "认证码错误");
				result.put("returnCode", "");
				return result;
			}
			int i = changePhoneService.updateVerificationStatus(id);
			if(i<=0){
				result.put("message", "失败");
				result.put("returnCode", "3001");
				return result;
			}
			result.put("message", "成功");
			result.put("returnCode", "3000");
			return result;
	} catch (NumberFormatException e) {
		e.printStackTrace();
		logger.info(e);
		result.put("returnCode", "3007");
		result.put("message", "参数格式不正确");
		return result;
	} catch (NullPointerException e) {
		e.printStackTrace();
		logger.info(e);
		result.put("returnCode", "3006");
		result.put("message", "参数为null");
		return result;
	} catch (Exception e) {
		e.printStackTrace();
		logger.info(e);
		result.put("returnCode", "3001");
		result.put("message", "失败");
		return result;
	}
	}
}
