package com.example.user_service.security;

import com.example.user_service.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.example.user_service.util.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static com.example.user_service.util.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;

/**
 * This is a security class for interceptor to authenticate a valid user
 */
@Service
public class AuthenticationHandler implements HandlerInterceptor {

    JwtUtil jwtUtil;
    UserDetailsService userDetailService;
    Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

    public AuthenticationHandler(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailService = userDetailsService;
        this.jwtUtil= jwtUtil;
    }

    public AuthenticationHandler() {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        logger.info(STARTING_METHOD_EXECUTION);
        final String authorizationHeader = request.getHeader("Authorization");

        String jwt = null;
        String username = null;
        final String id = request.getParameter("userId");
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
                logger.info(username);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        if (username != null) {
            try {

                UserDetails userDetails = userDetailService.loadUserByUsername(id);
                if (Boolean.FALSE.equals(jwtUtil.validateToken(jwt.trim(), userDetails))) {
                    if (request.getAttribute("expired").equals("true")) {
                        logger.info("expired");
                        response.setStatus(401);
                        logger.info(EXITING_METHOD_EXECUTION);
                        return false;
                    }
                    response.setStatus(403);
                    return false;
                } else {
                    logger.info(userDetails.getUsername());
                    response.setHeader("jwt",jwt);
                    logger.info(EXITING_METHOD_EXECUTION);
                    return true;
                }
            } catch (Exception usernameNotFoundException) {
                response.setStatus(404);
                String content = "{\n" +
                        "    \"status\": \"failed\",\n" +
                        "    \"message\": \"User invalid\",\n" +
                        "}";
                response.setContentType("application/json");
                response.getWriter().write(content);
                logger.info(EXITING_METHOD_EXECUTION);
                return false;
            }

        }
        response.setStatus(401);
        logger.info(EXITING_METHOD_EXECUTION);
        return false;
    }
}
