package com.xiangshangban.transit_service.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiangshangban.transit_service.bean.ReturnData;
import com.xiangshangban.transit_service.bean.Uusers;
import com.xiangshangban.transit_service.service.LoginService;
import com.xiangshangban.transit_service.service.UusersService;
import com.xiangshangban.transit_service.util.HttpClientUtil;
import com.xiangshangban.transit_service.util.RedisUtil;
import com.xiangshangban.transit_service.util.RequestJSONUtil;

/**
 * 请求转发用
 */
@RestController
@RequestMapping("/redirectApi")
public class RedirectController {
	Logger logger = Logger.getLogger(LoginController.class);
	@Autowired
	UusersService userService;
	@Autowired
	LoginService loginService;

	/**
	 * 请求接口（json）
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value="/sendRequest", produces = "application/json;charset=UTF-8", method=RequestMethod.POST)
    public String sendRequest(HttpServletRequest request) {
		RedisUtil redis = RedisUtil.getInstance();
		//根据token获得当前用户id,公司id
		String token = request.getHeader("token");
		String type = request.getHeader("type");
		String phone="";
		Uusers user = new Uusers();
		if (StringUtils.isEmpty(token)) {
			String sessionId = request.getSession().getId();
			System.out.println("redirectController : "+sessionId);
			phone = redis.new Hash().hget(sessionId, "session");
			//user = userService.selectCompanyBySessionId(sessionId);
		} else {
			phone = redis.new Hash().hget(token, "token");
			//user = userService.selectCompanyByToken(token);
		}
		user = userService.selectByPhone(phone,type);

		if (user == null || StringUtils.isEmpty(user.getCompanyId()) || StringUtils.isEmpty(user.getUserid())) {
			ReturnData returnData = new ReturnData();
			returnData.setReturnCode("3003");
			returnData.setMessage("用户身份获取失败");
			return JSON.toJSONString(returnData);
		}

		String uri = request.getParameter("redirectUrl");// 转发路径
		String modeCode = request.getParameter("redirectMode");// 模块
		String sendurl = HttpClientUtil.getModeUrl(modeCode) + uri;
		// 头信息
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("companyId", user.getCompanyId());
		headers.put("accessUserId", user.getUserid());
		headers.put("type", type);
		// 请求参数
		Map<String, String[]> paramMap = (Map<String, String[]>) request.getParameterMap();
		JSONObject newParamMap = new JSONObject();
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			logger.info(entry.getKey() + ":" + entry.getValue()[0]);
			if (!entry.getKey().equals("redirectUrl") && !entry.getKey().equals("redirectMode")) {
				newParamMap.put(entry.getKey(), entry.getValue().length > 1 ? entry.getValue() : entry.getValue()[0]);
			}
		}
		String contentType = request.getHeader("content-type");
		if (contentType.contains("application/json")) {
			try {
				String jsonStr = RequestJSONUtil.getRequestJsonString(request);
				if (StringUtils.isNotEmpty(jsonStr)) {
					JSONObject jobj = JSON.parseObject(jsonStr);
					Set<String> set = jobj.keySet();
					Iterator iterator = set.iterator();
					while (iterator.hasNext()) {
						String key = iterator.next().toString();
						newParamMap.put(key, jobj.get(key));
					}
					System.out.println(newParamMap);
				}

			} catch (IOException e) {
				ReturnData returnData = new ReturnData();
				returnData.setReturnCode("3001");
				returnData.setMessage("服务器错误");
				return JSON.toJSONString(returnData);
			}

		}
		//ContentType contentType = ContentType.create(req.getHeader("content-type").split(";")[0], "UTF-8");
		String result = HttpClientUtil.sendRequet(sendurl, newParamMap, ContentType.APPLICATION_JSON, headers);//ContentType.APPLICATION_JSON);
        return result;
    }
	
	@RequestMapping(value="/exportRequest", produces = "application/json;charset=UTF-8")
    public String export(HttpServletRequest request, HttpServletResponse response) {
		RedisUtil redis = RedisUtil.getInstance();
		//根据token获得当前用户id,公司id
		String token = request.getHeader("token");
		String type = request.getHeader("type");
		Uusers user = new Uusers();
		String  phone = "";
		if(StringUtils.isEmpty(token)){
			String sessionId = request.getSession().getId();
	
			phone = redis.new Hash().hget(sessionId, "session");
			//user = userService.selectCompanyBySessionId(sessionId);
		} else {
			phone = redis.new Hash().hget(token, "token");
			//user = userService.selectCompanyByToken(token);
		}
		user = userService.selectByPhone(phone,type);
		if(user==null || StringUtils.isEmpty(user.getCompanyId()) || StringUtils.isEmpty(user.getUserid())){
			ReturnData returnData = new ReturnData();
			returnData.setReturnCode("3003");
			returnData.setMessage("用户身份获取失败");
			return JSON.toJSONString(returnData);
		}
		
		String uri = request.getParameter("redirectUrl");//转发路径
		String modeCode = request.getParameter("redirectMode");//模块
		String sendurl = HttpClientUtil.getModeUrl(modeCode)+uri;
		//头信息
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("companyId", user.getCompanyId());
		headers.put("accessUserId", user.getUserid()); 
		headers.put("type", type); 
		
		//请求参数
		Map<String,String[]> paramMap =  (Map<String,String[]>)request.getParameterMap();
		JSONObject newParamMap = new JSONObject();
		for (Map.Entry<String,String[]> entry : paramMap.entrySet()) {
			logger.info(entry.getKey()+":"+entry.getValue()[0]);
			if(!entry.getKey().equals("redirectUrl") && !entry.getKey().equals("redirectMode")){
				newParamMap.put(entry.getKey(), entry.getValue().length>1?entry.getValue():entry.getValue()[0]);
			}
		}
		String contentType = request.getHeader("content-type");
		if(StringUtils.isNotEmpty(contentType) &&
				contentType.contains("application/json")){
			try {
				String jsonStr = RequestJSONUtil.getRequestJsonString(request);
				if(StringUtils.isNotEmpty(jsonStr)){
					JSONObject jobj = JSON.parseObject(jsonStr);
					Set<String> set = jobj.keySet();
					Iterator iterator = set.iterator();
					while(iterator.hasNext()){
						String key = iterator.next().toString();
						newParamMap.put(key,jobj.get(key));
					}
					System.out.println(newParamMap);
				}
				
			} catch (IOException e) {
				ReturnData returnData = new ReturnData();
				returnData.setReturnCode("3001");
				returnData.setMessage("服务器错误");
				return JSON.toJSONString(returnData);
			}
		}
		//ContentType contentType = ContentType.create(req.getHeader("content-type").split(";")[0], "UTF-8");
		
	    try {
	    	
	    	HttpClientUtil.sendExportRequet(request, response, sendurl, newParamMap, ContentType.APPLICATION_JSON, headers);
	    	
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	    	try {
	    		response.getOutputStream().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
	    }
		ReturnData returnData = new ReturnData();
		returnData.setReturnCode("3000");
		returnData.setMessage("请求成功");
		return JSON.toJSONString(returnData);
    }
}