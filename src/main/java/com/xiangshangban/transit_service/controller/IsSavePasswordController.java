package com.xiangshangban.transit_service.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiangshangban.transit_service.bean.Uusers;
import com.xiangshangban.transit_service.service.UusersService;
import com.xiangshangban.transit_service.util.FormatUtil;
import com.xiangshangban.transit_service.util.RedisUtil;
@RestController
@RequestMapping("/isSavePasswordController")
public class IsSavePasswordController {
	private static final Logger logger = Logger.getLogger(IsSavePasswordController.class);
	
	@Autowired
	private UusersService uusersService;
	
	/**
	 * @author 李业/是否已设置密码
	 * @return
	 */
	@RequestMapping(value="/isSavePasswd")
	public Map<String,Object> isSavePasswd(HttpServletRequest request){
		Map<String,Object> result =new HashMap<String,Object>();
		String type = request.getHeader("type");
		try{
			Subject subject = SecurityUtils.getSubject();
			String phone = subject.getPrincipal().toString();
			Uusers user = uusersService.selectByPhone(phone,type);
			if(StringUtils.isEmpty(user.getUserpwd())){
				result.put("message", "目前没有设置密码,请设置密码");
				result.put("returnCode", "4026");
				return result;
			}
			result.put("returnCode", "3000");
			return result;
		}catch(Exception e){
			logger.info(e);
			result.put("returnCode", "3001");
			result.put("message", "服务器错误");
			return result;
		}
	}
	
	/**
	 * @author 李业/验证修改手机号验证码是否正确,并返回凭证
	 * @param phone
	 * @param smsCode
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/validateSmsCode",produces="application/json;charset=utf-8",method=RequestMethod.POST)
	public Map<String,Object> validateSmsCode(@RequestBody String jsonString,HttpServletRequest request){
		Map<String,Object> result =new HashMap<String,Object>();
		JSONObject obj = JSON.parseObject(jsonString);
		String phone = obj.getString("phone");
		String smsCode = obj.getString("smsCode");
		try{
			if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(smsCode)){
				result.put("message", "必传参数为空");
				result.put("returnCode", "3006");
				return result;
			}
			boolean phoneFlag = Pattern.matches("1[345678]\\d{9}", phone);
			if(!phoneFlag){
				result.put("message", "手机号格式不正确");
				result.put("returnCode", "4024");
				return result;
			}
			boolean smsCodeFlag = Pattern.matches("[0-9]{4}", smsCode);
			if(!smsCodeFlag){
				result.put("message", "验证码格式不正确");
				result.put("returnCode", "4002");
				return result;
			}
			// 初始化redis
			RedisUtil redis = RedisUtil.getInstance();
			// 从redis取出短信验证码
			String redisSmsCode = redis.new Hash().hget("smsCode_" + phone, "smsCode");
			if (StringUtils.isEmpty(redisSmsCode)) {
				result.put("message", "验证码过期");
				result.put("returnCode", "4001");
				return result;
			} else if (!redisSmsCode.equals(smsCode)) {
				result.put("message", "验证码不正确");
				result.put("returnCode", "4002");
				return result;
			}
			if(redisSmsCode.equals(smsCode)){
				String voucher = FormatUtil.createUuid();
				redis.getJedis().hset("voucher"+phone, "voucher", voucher);
				redis.getJedis().expire("voucher"+phone, 300);
				result.put("voucher", voucher);
			}
			result.put("returnCode", "3000");
			return result;
		}catch(Exception e){
			logger.info(e);
			result.put("returnCode", "3001");
			result.put("message", "服务器错误");
			return result;
		}
	}
	
	/**
	 * @author 李业/重新设置密码
	 * @param phone
	 * @param newPassword
	 * @param voucher
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/setPassword",produces="application/json;charset=utf-8",method=RequestMethod.POST)
	public Map<String,Object> setPassword(@RequestBody String jsonString,HttpServletRequest request){
		Map<String,Object> result =new HashMap<String,Object>();
		JSONObject obj = JSON.parseObject(jsonString);
		try{
			String phone = obj.getString("phone");
			String newPassword = obj.getString("newPassword");
			String voucher = obj.getString("voucher");
			if(StringUtils.isEmpty(phone)||StringUtils.isEmpty(newPassword)||StringUtils.isEmpty(voucher)){
				result.put("message", "必传参数为空");
				result.put("returnCode", "3006");
				return result;
			}
			RedisUtil redis = RedisUtil.getInstance();
			String redisVoucher = redis.getJedis().hget("voucher"+phone, "voucher");
			if(StringUtils.isEmpty(redisVoucher)){
				result.put("message", "连接超时,请重新操作");
				result.put("returnCode", "");
				return result;
			}
			if(redisVoucher.equals(voucher)){
				Uusers user = uusersService.selectUserByPhone(phone);
				if(user == null){
					result.put("message", "此手机号不存在");
					result.put("returnCode", "");
					return result;
				}else {
					user.setUserpwd(newPassword);
					int i = uusersService.updatePaawordByPhone(user);
					if(i>0){
						result.put("message", "成功");
					}else{
						result.put("message", "未知错误,请联系管理员");
					}
				}
				
			}else{
				result.put("message", "非法凭证");
				result.put("returnCode", "");
				return result;
			}
			result.put("returnCode", "3000");
			return result;
		}catch(Exception e){
			logger.info(e);
			result.put("returnCode", "3001");
			result.put("message", "服务器错误");
			return result;
		}
	}
}
