package com.vipkid.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Principal;

@Service
public class AppContext {
    private static final Logger logger = LoggerFactory.getLogger(AppContext.class);

    private static InheritableThreadLocal<String> domainContext = new InheritableThreadLocal<String>();
    private static InheritableThreadLocal<Principal> principalLocal = new InheritableThreadLocal<Principal>();

    private static AppContext instance;

    @PostConstruct
    private void init() {
        instance = this;
        logger.info("Call AppContext init()");
    }

    public static Principal getPrincipal() {
        return principalLocal.get();
    }

    public static void setPrincipal(Principal principal) {
        principalLocal.set(principal);
    }

    public static void setDomain(String domain) {
        domainContext.set(domain);
        if (domain != null) {
            MDC.put("domain", domain);
        }
    }

    public static String getDomain() {
        return domainContext.get();
    }

    public static void releaseAppResource() {
        try {
            domainContext.remove();
            principalLocal.remove();
        } catch (Exception e) {
        }
    }
}
