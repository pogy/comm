/*
package com.vipkid.security;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CORSContainerResponseFilter implements ContainerResponseFilter {
	
	@Override
	public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
		containerResponseContext.getHeaders().add( "Access-Control-Allow-Origin", "*" );
		containerResponseContext.getHeaders().add( "Access-Control-Allow-Credentials", "true" );
		containerResponseContext.getHeaders().add( "Access-Control-Allow-Headers", "X-Requested-With, content-type, accept, origin, authorization, x-csrftoken" );
		containerResponseContext.getHeaders().add( "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS" );
	}

}
*/
