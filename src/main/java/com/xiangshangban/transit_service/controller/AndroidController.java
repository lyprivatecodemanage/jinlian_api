package com.xiangshangban.transit_service.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.xiangshangban.transit_service.bean.UpdateVersion;
import com.xiangshangban.transit_service.service.UpdateVersionService;

@RestController
@RequestMapping("/androidController")
public class AndroidController {

	private Logger logger = Logger.getLogger(AdministratorController.class);

	@Autowired
	UpdateVersionService updateVersionService;
	
	/**
	 * 获取数据库中最新Android信息记录
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/findNewFile",method = RequestMethod.POST)
	public Map<String,Object> findNewFile(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		
		String type = request.getHeader("type");
		
		String token = request.getHeader("token");

		if(StringUtils.isEmpty(type)||StringUtils.isEmpty(token)){
			map.put("returnCode","3006");
			map.put("message", "必传参数为空");
			return map;
		}
		
		try {
			UpdateVersion uv = updateVersionService.FindNewFile();
			
			map.put("UpdateVersion",JSONObject.toJSON(uv));
			map.put("returnCode","3000");
			map.put("message", "数据请求成功");
			return map;
		}catch(NullPointerException e){
			e.printStackTrace();
			logger.info(e);
			map.put("returnCode", "4007");
			map.put("message", "结果为null");
			return map;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.info(e);
			map.put("returnCode", "3001");
			map.put("message", "服务器错误");
			return map;
		}
	}
}
