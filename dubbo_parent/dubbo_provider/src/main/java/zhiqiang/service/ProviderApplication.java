package zhiqiang.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Description: No Description
 * User: Eric
 */
public class ProviderApplication {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext act = new ClassPathXmlApplicationContext("classpath:spring_dubbo.xml");
        System.in.read();
    }
}
