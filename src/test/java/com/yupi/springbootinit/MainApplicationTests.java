package com.yupi.springbootinit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 主类测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class MainApplicationTests {

    @Test
    void contextLoads() {

            Girl g1=new MMGirl(); //向上转型
            g1.smile();

            MMGirl mmg=(MMGirl)g1; //向下转型,编译和运行皆不会出错
            mmg.smile();
            mmg.c();

        String strOne = "one";
        String strTwo = "two";
        String strOne2 = "one";
        String strTwo2 = "two";

        int hashCode1 = Objects.hash(strOne, strTwo);
        int hashCode2 = Objects.hash(strOne2, strTwo2);

        assertEquals(hashCode1, hashCode2);

    }
    }

class Girl {
    public void smile(){
        System.out.println("girl smile()...");
    }
}
class MMGirl extends Girl{

    @Override
    public void smile() {

        System.out.println("MMirl smile sounds sweet...");
    }
    public void c(){
        System.out.println("MMirl c()...");
    }
}
