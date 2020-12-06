package name.auh.tool.seimi.enhance;

import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * rateLimit 模块的使用：
 * 1. 实现接口 name.auh.tool.seimi.enhance.RateLimitFound
 * 2. 初始化RateLimitBoot类。
 */
@Slf4j
public class RateLimitBoot {

    private static Set<RateLimitConfig> rateLimitConfigs = null;

    public static Set<RateLimitConfig> getRateLimitConfigs() {
        return rateLimitConfigs == null ? new HashSet<>() : rateLimitConfigs;
    }

    RateLimitBoot() {
        init();
        initHealthCheckRunner();
    }

    private void init() {
        if (rateLimitConfigs == null) {
            rateLimitConfigs = new HashSet<>();

            Reflections reflections = ReflectUtil.getReflections(ReflectUtil.getPrePartPackageName(RateLimitBoot.class, 2));

            Set<Class<? extends RateLimitConfig>> rateLimitFoundsClass = reflections.getSubTypesOf(RateLimitConfig.class);
            for (Class<? extends RateLimitConfig> rateLimitFound : rateLimitFoundsClass) {
                try {
                    rateLimitConfigs.add(rateLimitFound.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initHealthCheckRunner() {
        if (CollectionUtils.isEmpty(rateLimitConfigs)) {
            log.warn("未配置ratelimit模块");
            return;
        }

        for (Iterator<RateLimitConfig> iterator = rateLimitConfigs.iterator(); iterator.hasNext(); ) {
            RateLimitConfig next = iterator.next();
            if (next.getHealthCheckRequest() != null) {
                new Thread(new RateLimitCheckRunner()).start();
                log.info("启动健康检查 runner,检查地址 {}", next.getHealthCheckRequest().getUrl());
                return;
            }
        }

    }

}
