/*package com.vipkid.service.configure;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.vipkid.security.CORSContainerRequestFilter;
import com.vipkid.security.CORSContainerResponseFilter;
import com.vipkid.service.mapper.ConstraintViolationExceptionMapper;

@ApplicationPath("/")
public class Application extends ResourceConfig {
	
	public Application() {
		register(MultiPartFeature.class);
		register(ConstraintViolationExceptionMapper.class);	
		register(CORSContainerRequestFilter.class);
		register(CORSContainerResponseFilter.class);
		register(RolesAllowedDynamicFeature.class);
	}
	
	
}*/
