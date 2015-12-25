/*
package com.vipkid.security;

import java.io.IOException;

import javax.annotation.Priority;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.accessor.AgentAuthAccessor;
import com.vipkid.accessor.ParentAccessor;
import com.vipkid.accessor.StaffAccessor;
import com.vipkid.accessor.StudentAccessor;
import com.vipkid.accessor.TeacherAccessor;
import com.vipkid.model.Agent;
import com.vipkid.model.Parent;
import com.vipkid.model.Portal;
import com.vipkid.model.Staff;
import com.vipkid.model.Student;
import com.vipkid.model.Teacher;
import com.vipkid.model.User;
import com.vipkid.util.Configurations;
import com.vipkid.util.TextUtils;

@Provider
@Priority(Priorities.AUTHENTICATION)
@PreMatching
public class CORSContainerRequestFilter implements ContainerRequestFilter {
	private Logger logger = LoggerFactory.getLogger(CORSContainerRequestFilter.class.getSimpleName());
	
	private static final String URI_PRIVATE = "/api/service/private/";
	private static final String HTTP_HEADER_TOKEN = "Authorization";
	
	private TeacherAccessor teacherAccessor;
	private StaffAccessor staffAccessor;
	private StudentAccessor studentAccessor;
	private ParentAccessor parentAccessor;
	private AgentAuthAccessor agentAuthAccessor;
	
	public CORSContainerRequestFilter() {
		try {
			BeanManager beanManager = (BeanManager) InitialContext.doLookup("java:comp/BeanManager");
			
			@SuppressWarnings("unchecked")
			Bean<TeacherAccessor> teacherAccessorBean = (Bean<TeacherAccessor>) beanManager.getBeans(TeacherAccessor.class).iterator().next();
	        CreationalContext<TeacherAccessor> teacherCreationalContext = beanManager.createCreationalContext(teacherAccessorBean);
	        teacherAccessor = (TeacherAccessor) beanManager.getReference(teacherAccessorBean, TeacherAccessor.class, teacherCreationalContext);
	        
			@SuppressWarnings("unchecked")
			Bean<StaffAccessor> staffAccessorBean = (Bean<StaffAccessor>) beanManager.getBeans(StaffAccessor.class).iterator().next();
	        CreationalContext<StaffAccessor> staffCreationalContext = beanManager.createCreationalContext(staffAccessorBean);
	        staffAccessor = (StaffAccessor) beanManager.getReference(staffAccessorBean, StaffAccessor.class, staffCreationalContext);
	        
	        @SuppressWarnings("unchecked")
			Bean<StudentAccessor> studentAccessorBean = (Bean<StudentAccessor>) beanManager.getBeans(StudentAccessor.class).iterator().next();
	        CreationalContext<StudentAccessor> studentCreationalContext = beanManager.createCreationalContext(studentAccessorBean);
	        studentAccessor = (StudentAccessor) beanManager.getReference(studentAccessorBean, StudentAccessor.class, studentCreationalContext);
	        
	        @SuppressWarnings("unchecked")
			Bean<ParentAccessor> parentAccessorBean = (Bean<ParentAccessor>) beanManager.getBeans(ParentAccessor.class).iterator().next();
	        CreationalContext<ParentAccessor> parentCreationalContext = beanManager.createCreationalContext(parentAccessorBean);
	        parentAccessor = (ParentAccessor) beanManager.getReference(parentAccessorBean, ParentAccessor.class, parentCreationalContext);
	        
	        @SuppressWarnings("unchecked")
			Bean<AgentAuthAccessor> agentAuthAccessorBean = (Bean<AgentAuthAccessor>) beanManager.getBeans(AgentAuthAccessor.class).iterator().next();
	        CreationalContext<AgentAuthAccessor> agentCreationalContext = beanManager.createCreationalContext(agentAuthAccessorBean);
	        agentAuthAccessor = (AgentAuthAccessor) beanManager.getReference(agentAuthAccessorBean, AgentAuthAccessor.class, agentCreationalContext);
		} catch (NamingException e) {
			logger.error("exception when find bean manager" + e);
		}
	}
	
	@Override
	public void filter(ContainerRequestContext containerRequestContext) throws IOException {
		if (containerRequestContext.getRequest().getMethod().equals("OPTIONS")) {
			containerRequestContext.abortWith(Response.status(Response.Status.OK).build());
		} else {
			if (!Configurations.Auth.BYPASS) {
				String requestURI = containerRequestContext.getUriInfo().getRequestUri().getPath();

				if (requestURI.startsWith(URI_PRIVATE)) {
					String authorization = containerRequestContext.getHeaderString(HTTP_HEADER_TOKEN);
					if (TextUtils.isEmpty(authorization)) {
						containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
					} else {
						String[] strings = authorization.split(TextUtils.SPACE);
						if (strings.length == 3 && strings[0] != null && strings[1] != null && strings[2] != null) {
							String portal = strings[0];
							long id = Long.parseLong(strings[1]);
							String token = strings[2];

							if (Portal.TEACHER.name().equalsIgnoreCase(portal)) {
								Teacher teacher = teacherAccessor.findByIdAndToken(id, token);
								if(teacher == null) {
									containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
								}else {
									CustomizedPrincipal principal = new CustomizedPrincipal(teacher);
									containerRequestContext.setSecurityContext(new CustomizedSecurityContext(principal));
								}
							} else if(Portal.PARENT.name().equalsIgnoreCase(portal)) {
								Parent parent = parentAccessor.findByIdAndToken(id, token);
								if(parent == null) {
									containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
								}else {
									CustomizedPrincipal principal = new CustomizedPrincipal(parent);
									containerRequestContext.setSecurityContext(new CustomizedSecurityContext(principal));
								}
							} else if(Portal.LEARNING.name().equalsIgnoreCase(portal)) {
								Student student = studentAccessor.findByIdAndToken(id, token);
								if(student == null) {
									containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
								}else {
									CustomizedPrincipal principal = new CustomizedPrincipal(student);
									containerRequestContext.setSecurityContext(new CustomizedSecurityContext(principal));
								}
							} else if(Portal.MANAGEMENT.name().equalsIgnoreCase(portal)) {
								Staff staff = staffAccessor.findByIdAndToken(id, token);
								if(staff == null) {
									containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
								}else {
									CustomizedPrincipal principal = new CustomizedPrincipal(staff);
									containerRequestContext.setSecurityContext(new CustomizedSecurityContext(principal));
								}
							}else if(Portal.AGENTMANAGEMENT.name().equalsIgnoreCase(portal)) {
								Agent agent = agentAuthAccessor.findByIdAndToken(id, token);
								if(agent == null) {
									containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
								}else {
									User user = new User();
									user.setName(agent.getName());
									user.setId(agent.getId());
									user.setUsername(agent.getEmail());
									user.setPassword(agent.getPassword());
									user.setToken(agent.getToken());
									CustomizedPrincipal principal = new CustomizedPrincipal(user);
									containerRequestContext.setSecurityContext(new CustomizedSecurityContext(principal));
								}
							} else if(Portal.HOME.name().equalsIgnoreCase(portal)) {
								Parent parent = parentAccessor.findByIdAndToken(id, token);
								if(parent == null) {
									containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
								}else {
									CustomizedPrincipal principal = new CustomizedPrincipal(parent);
									containerRequestContext.setSecurityContext(new CustomizedSecurityContext(principal));
								}
							} else if ( Portal.TEACHERRECRUITMENT.name().equalsIgnoreCase(portal) ) {
								// 使用teacher -- teacher recruitment pojo
								Teacher teacher = teacherAccessor.findByIdAndToken(id, token);
								if(teacher == null) {
									containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
								}else {
									CustomizedPrincipal principal = new CustomizedPrincipal(teacher);
									containerRequestContext.setSecurityContext(new CustomizedSecurityContext(principal));
								}
							} else {
								containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
							}
						} else {
							containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
						}
					}
				}
			}
		}
	}

}
*/
