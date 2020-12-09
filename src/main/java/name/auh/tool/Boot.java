package name.auh.tool;

import name.auh.tool.seimi.enhance.RateLimitBoot;
import name.auh.tool.seimi.proxy.ProxyCrawlerConfig;
import name.auh.tool.seimi.proxy.example.XXXHttpProxyPool;
import name.auh.tool.seimi.proxy.example.XXXProxyCrawlerConfigImp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Import(RateLimitBoot.class)
@SpringBootApplication
public class Boot {

    public static void main(String[] args) {
        SpringApplication.run(Boot.class, args);
    }

    @Bean
    public List<ProxyCrawlerConfig> xxxHttpProxyPools() {
        List list = new ArrayList<>();
        list.add(new XXXProxyCrawlerConfigImp());
        return list;
    }

    @Bean
    public XXXHttpProxyPool xxxHttpProxyPool() {
        return new XXXHttpProxyPool();
    }

}
