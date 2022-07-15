package com.example.user_service.security;

import com.example.user_service.util.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.PrintWriter;
import static org.mockito.Mockito.mock;


@SpringBootTest
class AuthenticationHandlerTest {
    @Mock
    UserDetails userDetails;

    AuthenticationHandler authenticationHandler;
    @Mock
    UserDetailsService userDetailsService;

    @Mock
    JwtUtil jwtUtil;

    HttpServletRequest request;
    HttpServletResponse response;
    Object object;

    @BeforeEach
    void initUseCase() {
        authenticationHandler = new AuthenticationHandler(userDetailsService, jwtUtil);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @BeforeEach
    public void before() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        object = mock(Object.class);
    }
    @Test
    @ExtendWith(MockitoExtension.class)
    void preHandle() throws Exception {
        Mockito.when(request.getHeader("Authorization"))
                .thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo");
        Mockito.when(request.getParameter("userId")).thenReturn("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Mockito.when(jwtUtil.extractUsername("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo"))
                .thenReturn("vinay");
        Mockito.when(userDetailsService.loadUserByUsername("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userDetails);
        FileWriter file = new FileWriter("output.txt");
        PrintWriter printWriter= new PrintWriter(file);
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        Boolean real = authenticationHandler.preHandle(request,response,object);
        Assertions.assertEquals(false,real);
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void preHandleExtractUsernameException() throws Exception {
        Mockito.when(request.getHeader("Authorization"))
                .thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo");
        Mockito.when(request.getParameter("userId")).thenReturn("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Mockito.when(jwtUtil.extractUsername("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo"))
                .thenThrow(NullPointerException.class);
        Boolean real = authenticationHandler.preHandle(request,response,object);
        Assertions.assertEquals(false,real);

    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void preHandleAuthorizationHeaderIf() throws Exception {
        Mockito.when(request.getHeader("Authorization"))
                .thenReturn("Beare eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo");
        Mockito.when(request.getParameter("userId")).thenReturn("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Boolean real = authenticationHandler.preHandle(request,response,object);
        Assertions.assertEquals(false,real);

    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void preHandleAuthorizationHeaderIfSecond() throws Exception {
        Mockito.when(request.getHeader("Authorization"))
                .thenReturn(null);
        Mockito.when(request.getParameter("userId")).thenReturn("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Boolean real = authenticationHandler.preHandle(request,response,object);
        Assertions.assertEquals(false,real);

    }
    @Test
    @ExtendWith(MockitoExtension.class)
    void preHandleUsernameNull() throws Exception {
        Mockito.when(request.getHeader("Authorization"))
                .thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo");
        Mockito.when(request.getParameter("userId")).thenReturn("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Mockito.when(jwtUtil.extractUsername("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo"))
                .thenReturn(null);
        Boolean real= authenticationHandler.preHandle(request,response,object);
        Assertions.assertEquals(false,real);
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void preHandleUsername() throws Exception {
        Mockito.when(request.getHeader("Authorization"))
                .thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo");
        Mockito.when(request.getParameter("userId")).thenReturn("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Mockito.when(jwtUtil.extractUsername("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo"))
                .thenReturn("vinay");
        Mockito.when(userDetailsService.loadUserByUsername("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userDetails);
        Mockito.when(request.getAttribute("expired")).thenReturn("something");
        Boolean real= authenticationHandler.preHandle(request,response,object);
        Assertions.assertEquals(false,real);
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void preHandleUsernameValidateFail() throws Exception {
        Mockito.when(request.getHeader("Authorization"))
                .thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo");
        Mockito.when(request.getParameter("userId")).thenReturn("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Mockito.when(jwtUtil.extractUsername("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo"))
                .thenReturn("vinay");
        Mockito.when(userDetailsService.loadUserByUsername("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userDetails);
        String token= "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo";
        Mockito.when(jwtUtil.validateToken(token,userDetails)).thenReturn(true);
        Boolean real= authenticationHandler.preHandle(request,response,object);
        Assertions.assertEquals(true,real);
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void preHandleExpired() throws Exception {
        Mockito.when(request.getHeader("Authorization"))
                .thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo");
        Mockito.when(request.getParameter("userId")).thenReturn("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Mockito.when(jwtUtil.extractUsername("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJzaGl0LnJhajEwYUBnbWFpbC5jb20iLCJleHAiOjE2NTE4ODI2MjQsImlhdCI6MTY1MTUyMjYyNH0.hsMnTM5-k4JWnfMdT7i95Xc1kTHKvtClF1A0OGzigPo"))
                .thenReturn("vinay");
        Mockito.when(userDetailsService.loadUserByUsername("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userDetails);
        Mockito.when(request.getAttribute("expired")).thenReturn("true");
        Boolean real= authenticationHandler.preHandle(request,response,object);
        Assertions.assertEquals(false,real);
    }
}