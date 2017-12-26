package com.xiangshangban.transit_service.controller;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.xiangshangban.transit_service.bean.OSSFile;
import com.xiangshangban.transit_service.bean.ReturnData;
import com.xiangshangban.transit_service.bean.Uusers;
import com.xiangshangban.transit_service.service.CompanyService;
import com.xiangshangban.transit_service.service.OSSFileService;
import com.xiangshangban.transit_service.service.UusersService;
import com.xiangshangban.transit_service.util.HttpClientUtil;
import com.xiangshangban.transit_service.util.OSSFileUtil;
import com.xiangshangban.transit_service.util.RedisUtil;


@RestController
@RequestMapping(value = "/oss")
public class OSSController {
	@Autowired
	OSSFileService oSSFileService;
	@Autowired
	UusersService uusersService; 
	@Autowired
	CompanyService companyService;
	/**
	 * 上传文件到OSS
	 * @param file
	 * @param token 
	 * @param funcDirectory 存储模块名称
	 * @return
	 */
	@RequestMapping(value = "/upload",produces = "application/json;charset=UTF-8",method=RequestMethod.POST)
	public ReturnData appUpload(@RequestParam(value="file") MultipartFile file, 
			@RequestParam(value="funcDirectory") String funcDirectory,HttpServletRequest request){ 
		ReturnData returnData = new ReturnData();
		RedisUtil redis = RedisUtil.getInstance();
		//根据token获得当前用户id,公司id
		String token = request.getHeader("token");
		String type = request.getHeader("type");
		Uusers user = new Uusers();
		String phone = "";
		if (StringUtils.isEmpty(token)) {
			String sessionId = request.getSession().getId();
			System.out.println("redirectController : "+sessionId);
			//user = uusersService.selectCompanyBySessionId(sessionId);
			phone = redis.getJedis().hget(sessionId, "session");
		} else {
			phone = redis.getJedis().hget(token, "token");
			//user = uusersService.selectCompanyByToken(token);
		}
		user = uusersService.selectByPhone(phone,type);

		if (user == null || StringUtils.isEmpty(user.getCompanyId()) || StringUtils.isEmpty(user.getUserid())) {
			
			returnData.setReturnCode("3003");
			returnData.setMessage("用户身份获取失败");
			return returnData;
		}
		
		String companyNo = companyService.selectByPrimaryKey(user.getCompanyId()).getCompany_no();//公司编号，此编号实际应用时，应根据token去查询
		//funcDirectory = "portrait";//portrait目录存储员工头像
		//此处判断上传的funcDirectory是否是固定的几个文件名
		boolean funcDirectoryFlag = false;
		String [] funcDirectoryArray = HttpClientUtil.getFuncDirectory();
		for(String directory:funcDirectoryArray){
			if(funcDirectory.equals(directory)){
				funcDirectoryFlag=true;
			}
		}
		if(!funcDirectoryFlag){
			returnData.setReturnCode("3011");
			returnData.setMessage("funcDirectory目录不可用");
			return returnData;
		}
		if(!file.isEmpty()){		
        	OSSFile ossFile = oSSFileService.addOSSFile(companyNo, funcDirectory, file);
        	returnData.setData(ossFile);
        	returnData.setReturnCode("3000");
			returnData.setMessage("上传成功");
			return returnData;
        }
    	returnData.setReturnCode("3010");
		returnData.setMessage("上传文件失败：文件内容为空");
		return returnData;
    }
	/**
	 * 根据文件名获取全路径
	 * @param key
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/getPath",produces = "application/json;charset=UTF-8",method=RequestMethod.GET)
	public String appGetPath(String key,@RequestParam String funcDirectory,HttpServletRequest request){
		RedisUtil redis = RedisUtil.getInstance();
		String type = request.getHeader("type");
		//根据token获得当前用户id,公司id
		String token = request.getHeader("token");
		Uusers user = new Uusers();
		String phone = "";
		if (StringUtils.isEmpty(token)) {
			String sessionId = request.getSession().getId();
			System.out.println("redirectController : "+sessionId);
			//user = uusersService.selectCompanyBySessionId(sessionId);
			phone = redis.getJedis().hget(sessionId, "session");
		} else {
			phone = redis.getJedis().hget(token, "token");
			//user = uusersService.selectCompanyByToken(token);
		}
		user = uusersService.selectByPhone(phone,type);
		if (user == null || StringUtils.isEmpty(user.getCompanyId()) || StringUtils.isEmpty(user.getUserid())) {
			ReturnData returnData = new ReturnData();
			returnData.setReturnCode("3003");
			returnData.setMessage("用户身份获取失败");
			return JSON.toJSONString(returnData);
		}
		
		
		
		//Uusers user = uusersService.selectCompanyByToken(token);
		//String customerId = user.getCompanyId();
		//String customerId = "C001";//公司编号，此编号实际应用时，应根据token去查询
		String companyNo = companyService.selectByPrimaryKey(user.getCompanyId()).getCompany_no();//公司编号，此编号实际应用时，应根据token去查询
		//funcDirectory = "portrait";//portrait目录存储员工头像
				//此处判断上传的funcDirectory是否是固定的几个文件名
				/*boolean funcDirectoryFlag = false;
				String [] funcDirectoryArray = HttpClientUtil.getFuncDirectory();
				for(String directory:funcDirectoryArray){
					if(funcDirectory.equals(directory)){
						funcDirectoryFlag=true;
					}
				}*/
		
		String url = OSSFileUtil.getFilePath(companyNo, funcDirectory, key);
		return JSON.toJSONString(url);
	}
} 

