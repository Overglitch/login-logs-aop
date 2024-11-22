package com.example.loginapp.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    @AfterReturning(pointcut = "execution(* com.example.loginapp.service.UserService.authenticate(..)) && args(username, password)", returning = "result", argNames = "username, password, result")
    public void logFailedLogin(String username, String password, boolean result) {
        if (!result) {
            logger.warn("Failed login attempt for username: {}", username);
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.loginapp.service.UserService.authenticate(..)) && args(username, password)", returning = "result", argNames = "username, password, result")
    public void logSuccessfulLogin(String username, String password, boolean result) {
        if (result) {
            logger.info("Successful login for username: {}", username);
        }
    }

}
