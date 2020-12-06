package name.auh.tool.seimi.enhance;

import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class RateLimitCheckRunner implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (RateLimitInterceptor.isRateLimit()) {
                Set<RateLimitConfig> rateLimitConfigs = RateLimitBoot.getRateLimitConfigs();
                for (RateLimitConfig rateLimitConfig : rateLimitConfigs) {
                    Request healthCheckRequest = rateLimitConfig.getHealthCheckRequest();
                    healthCheckRequest.setCallBack("start");
                    healthCheckRequest.setCrawlerName("RateLimit");
                    log.debug("执行健康检查 url {} ", healthCheckRequest.getUrl());
                    CrawlerCache.consumeRequest(healthCheckRequest);
                }
            }
        }
    }

}
