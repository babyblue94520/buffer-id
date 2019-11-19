package pers.clare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import pers.clare.bufferid.manager.IdManager;
import pers.clare.bufferid.manager.impl.LocalIdManager;
import pers.clare.bufferid.manager.impl.MySQLIdManager;
import pers.clare.bufferid.manager.impl.RedisIdManager;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;

@SpringBootApplication
public class BufferIdApplication {

    public static void main(String[] args) {
        SpringApplication.run(BufferIdApplication.class, args);
    }

    @Autowired
    @Bean
    public IdManager idManager(DataSource ds){
        return new MySQLIdManager(ds);
    }

//    @Autowired
//    @Bean
//    public IdManager idManager(RedisConnectionFactory connectionFactory){
//        return new RedisIdManager(connectionFactory);
//    }

//    @Bean
//    @ConditionalOnMissingBean(IdManager.class)
//    public IdManager idManager(){
//        return new LocalIdManager();
//    }

}
