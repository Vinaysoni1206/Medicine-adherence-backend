package com.example.user_service.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

class JwtUtilTest {

    JwtUtil jwtUtil= new JwtUtil();

    @Mock
    UserDetails userDetails;

    @Test
    void extractUsername() {
        String token = jwtUtil.generateToken("73578dfd-e7c9-4381-a348-113e72d80fa2");
        String Username = jwtUtil.extractUsername(token);
        Assertions.assertEquals("73578dfd-e7c9-4381-a348-113e72d80fa2", Username);
    }

    @Test
    void extractExpiration() {
        String token = jwtUtil.generateToken("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Date expexted= new Date();
        Date real = jwtUtil.extractExpiration(token);
        Assertions.assertEquals(expexted.getClass(),real.getClass());
    }

    @Test
    void isTokenExpired() {
        String token = jwtUtil.generateToken("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Boolean real= jwtUtil.isTokenExpired(token);
        Assertions.assertEquals(false,real);
    }

    @Test
    void generateToken() {
        String token = jwtUtil.generateToken("73578dfd-e7c9-4381-a348-113e72d80fa2");
        String actualToken = token;
        Assertions.assertEquals(actualToken, token);
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    void validateToken() {
        MockHttpServletRequest request= new MockHttpServletRequest();
        String token= jwtUtil.generateToken("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Boolean validate=jwtUtil.validateToken(token, userDetails);
        Assertions.assertEquals(false, validate);
    }
}