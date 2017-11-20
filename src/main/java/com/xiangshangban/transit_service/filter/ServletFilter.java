package com.xiangshangban.transit_service.filter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xiangshangban.transit_service.bean.UniqueLogin;
import com.xiangshangban.transit_service.service.UniqueLoginService;
import com.xiangshangban.transit_service.util.HttpClientUtil;

@WebFilter(filterName = "ServletFilter", urlPatterns = "/*")
public class ServletFilter implements Filter {

	private UniqueLoginService uniqueLoginService;

	public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		FileOutputStream out = new FileOutputStream(filePath + fileName);
		out.write(file);
		out.flush();
		out.close();
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext sc = filterConfig.getServletContext();
		WebApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(sc);
		if (cxt != null && cxt.getBean("uniqueLoginService") != null && uniqueLoginService == null)
			uniqueLoginService = (UniqueLoginService) cxt.getBean("uniqueLoginService");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		res.setContentType("textml;charset=UTF-8");
		// 这里填写你允许进行跨域的主机ip
		res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
		res.setHeader("Access-Control-Allow-Credentials", "true");
		// 允许的访问方法
		res.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
		/* res.setHeader("Access-Control-Allow-Methods", req.getMethod()); */
		// Access-Control-Max-Age 用于 CORS 相关配置的缓存
		res.setHeader("Access-Control-Max-Age", "3600");
		// Content-Disposition文件下载时设置文件名
		res.setHeader("Access-Control-Allow-Headers",
				"Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, type");
		res.setHeader("XDomainRequestAllowed", "1");
		// res.setHeader("Content-Disposition",
		// "attachment;filename="+java.net.URLEncoder.encode("报表.xls","UTF-8"));
		// res.setHeader("Access-Control-Allow-Methods", req.getMethod());
		boolean flag=true;
		boolean redirect = false;
		String redirectUrl = "";
		if (!"OPTIONS".equals(req.getMethod())) {
			if (!uri.contains("/loginController/offsiteLogin")) {
				/*System.out.println("doFilter :\t" + req.getMethod());
				System.out.println(uri);
				System.out.println("doFilter=======================>" + req.getSession().getId());
				System.out.println("doFilter=======================>" + req.getHeader("type"));*/
				String type = req.getHeader("type");
				if ("0".equals(type)) {
					Object phone = req.getSession().getAttribute("phone");
					if (StringUtils.isEmpty(phone)) {
						//System.out.println("首次登录");
					} else {
						UniqueLogin uniqueLogin = uniqueLoginService.selectByPhone(phone.toString());
						if (uniqueLogin != null) {
							String oldSessionId = uniqueLogin.getSessionId();
							if (!oldSessionId.equals(req.getSession().getId())) {
								flag=false;
								req.getRequestDispatcher("/loginController/offsiteLogin").forward(req, res);
								return;
							}
						}
					}
				}
				if ("1".equals(type)) {
					String token = req.getHeader("ACCESS_TOKEN");
					String clientId = req.getHeader("clientId");
					if (!StringUtils.isEmpty(token)) {
						UniqueLogin uniqueLogin = uniqueLoginService.selectByToken(token);
						if (!StringUtils.isEmpty(uniqueLogin) && !clientId.equals(uniqueLogin.getClientId())) {
							flag=false;
							req.getRequestDispatcher("/loginController/offsiteLogin").forward(req, res);
							return;
						}
					}
				}

			}
		if(flag){
		String[] includeMode = HttpClientUtil.getIncludeMode();
		for (String mode : includeMode) {
			String checkUrl = "/api/" + mode + "/";
			if (uri.contains(checkUrl)) {
				String sendurl = URLDecoder.decode(uri.replaceAll(checkUrl, ""), "UTF-8");
				if (uri.contains("/export/")) {
					redirectUrl = "/redirectApi/exportRequest?redirectUrl=" + sendurl + "&redirectMode=" + mode;
				} else {
					redirectUrl = "/redirectApi/sendRequest?redirectUrl=" + sendurl + "&redirectMode=" + mode;
				}
				redirect = true;
			}
		}
		}
		if (redirect) {
			req.getRequestDispatcher(redirectUrl).forward(req, res);
		} else {
			chain.doFilter(req, res);
		}
		}
	}

	public void destroy() {
	}
}
