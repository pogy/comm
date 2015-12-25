package com.dubbo.handle.register;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.service.GenericException;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.dubbo.utils.invoke.ReflectionHandle;
import com.dubbo.utils.json.JsonFactory;
import com.vipkid.model.Parent;
import com.vipkid.rest.ParentAuthController;
import com.vipkid.service.pojo.Binding;

/**
 * 
 * @author VIPKID
 *
 */
@Service
public class RegisterServiceImpl extends ParentAuthController implements
		GenericService {

	
	public Parent bindParentOpenId(String json){
		Binding  binding = JsonFactory.getConver(JsonFactory.ConverType.JACKSON).renderJson2Obj(json, Binding.class);
		return super.bindParentOpenId(binding);
		
	}
	
	@Override
	public Object $invoke(String method, String[] parameterTypes, Object[] args)
			throws GenericException {
		// TODO Auto-generated method stub
		return ReflectionHandle.$invoke(this, method, parameterTypes, args);
	}

}
