package com.example;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@SpringBootConfiguration
class UserServiceApplicationTests {


    @Test
    void contextLoads(){
        Assertions.assertDoesNotThrow(this::doNotThrowException);
    }

    private void doNotThrowException(){

    }


}
