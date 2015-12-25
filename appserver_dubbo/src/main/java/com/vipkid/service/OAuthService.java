package com.vipkid.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.vipkid.model.json.gson.GsonManager;
import com.vipkid.service.pojo.AccessToken;

@Service
public class OAuthService {
	
	public static final String APP_ID = "wxbd56495f131c90a8"; 
	
	public static final String APP_SECRET = "8ae54a4549c1ea2aa900c233220df775";  
	
	public static final String DOMAIN = "http://parent.vipkid.com.cn";  
	
	private Logger logger = LoggerFactory.getLogger(OAuthService.class.getSimpleName());
	
	private ModelAndView getResponse(String code) throws IOException, URISyntaxException {
		return getResponse(code, null);
	}
	
	private ModelAndView getResponse(String code, String target) throws IOException, URISyntaxException {
		logger.debug("redirect to url with openid, code = {}", code);

		String httpsURL = "https://api.weixin.qq.com/sns/oauth2/access_token?"
				+ "appid=" + APP_ID + "&secret=" + APP_SECRET + "&"
				+ "code=" + code + "&grant_type=authorization_code";
		URL myurl = new URL(httpsURL);
		HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
		InputStream ins = con.getInputStream();
		InputStreamReader isr = new InputStreamReader(ins);
		BufferedReader in = new BufferedReader(isr);

		String inputLine;
		StringBuilder stringBuilder = new StringBuilder();

		while ((inputLine = in.readLine()) != null) {
			stringBuilder.append(inputLine);
		}

		in.close();
	    
	    String json = stringBuilder.toString();
	    
	    AccessToken accessToken = GsonManager.getInstance().getGson().fromJson(json, AccessToken.class);

	    if (accessToken.getOpenid() != null) {
	    	if (target != null) {
	    		//return Response.temporaryRedirect(new URI(DOMAIN + "/login?openid=" + accessToken.getOpenid() + "&target=" + target)).build();
	    		return new ModelAndView("redirect:" + DOMAIN + "/login?openid=" + accessToken.getOpenid() + "&target=" + target);
	    	} else {
	    		//return Response.temporaryRedirect(new URI(DOMAIN + "/login?openid=" + accessToken.getOpenid())).build();
	    		return new ModelAndView("redirect:" + DOMAIN + "/login?openid=" + accessToken.getOpenid());
	    	}
	    } else {
	    	//return Response.temporaryRedirect(new URI(DOMAIN)).build();
	    	return new ModelAndView("redirect:" + DOMAIN);
	    }
	}
	
	public ModelAndView findOpenidURL(String code) throws IOException, URISyntaxException {
		logger.debug("redirect to url with openid, code = {}", code);

		return getResponse(code);
	}
	
	public ModelAndView findDashboard(String code) throws IOException, URISyntaxException {
		logger.debug("redirect to url with openid, code = {}", code);

		return getResponse(code, "dashboard");
	}
	
	public ModelAndView findOrders(String code) throws IOException, URISyntaxException {
		logger.debug("redirect to url with openid, code = {}", code);

		return getResponse(code, "orders");
	}
	
	public ModelAndView findAccount(String code) throws IOException, URISyntaxException {
		logger.debug("redirect to url with openid, code = {}", code);

		return getResponse(code, "account");
	}
	
}
