package scm.common;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "scm.common", annotationClass = Mapper.class)
public class SkaxSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkaxSpringApplication.class, args);
    }

}
