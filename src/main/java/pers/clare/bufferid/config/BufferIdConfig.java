package pers.clare.bufferid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pers.clare.bufferid.manager.IdManager;
import pers.clare.bufferid.manager.impl.MySQLIdManager;
import pers.clare.bufferid.service.BufferIdService;
import pers.clare.bufferid.service.MultiBufferIdService;
import pers.clare.bufferid.service.SingleBufferIdService;

import javax.sql.DataSource;

@Configuration
public class BufferIdConfig {
    public static final String Single = "singleBufferIdService";
    public static final String Multi = "multiBufferIdService";

    @Bean
    public IdManager idManager(DataSource ds){
        return new MySQLIdManager(ds);
    }


    @Primary
    @Bean(Single)
    public BufferIdService singleBufferIdService(IdManager idManager){
        return new SingleBufferIdService(idManager);
    }

    @Bean(Multi)
    public BufferIdService multiBufferIdService(IdManager idManager){
        return new MultiBufferIdService(idManager);
    }
}
