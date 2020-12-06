package name.auh.tool.seimi.enhance;

import cn.wanghaomiao.seimi.annotation.Interceptor;
import cn.wanghaomiao.seimi.core.SeimiInterceptor;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import name.auh.tool.seimi.hack.RequestHack;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static name.auh.tool.seimi.enhance.PushNewCrawlerFromLastResultRepeater.CRAWLER_RESULT;

/**
 * 通用的限制检测模块
 */
@Interceptor(everyMethod = true)
@Slf4j
public class RateLimitInterceptor implements SeimiInterceptor {

    /**
     * 20s缓存自动失效的key。来充当请求继续时间
     */
    final static Cache<Integer, String> RATE_LIMIT_STOP = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    public static boolean isRateLimit() {
        return Boolean.TRUE.toString().equals(RateLimitInterceptor.RATE_LIMIT_STOP.getIfPresent(1));
    }

    @Override
    public Class<? extends Annotation> getTargetAnnotationClass() {
        return null;
    }

    @Override
    public int getWeight() {
        return 0;
    }

    @Override
    public void before(Method method, Response response) {
        if (method == null) {
            //使用lambda的无法拦截  获取方法签名
            return;
        }

        Set<RateLimitConfig> rateLimitConfigs = RateLimitBoot.getRateLimitConfigs();

        boolean limited = false;
        for (RateLimitConfig rateLimitFound : rateLimitConfigs) {
            if (rateLimitFound.found(response)) {
                limited = true;
                break;
            }
        }

        RateLimitFinder rateLimitFinder = method.getDeclaredAnnotation(RateLimitFinder.class);
        if (rateLimitFinder == null) {
            return;
        }

        if (limited) {
            Request request = response.getRequest();

            log.error("已消费请求，收到被强，检测链接", request.getUrl());

            RATE_LIMIT_STOP.put(1, Boolean.TRUE.toString());

            if (rateLimitFinder.backToQueue()) {
                log.warn("重新放回队列 {}", request.getUrl());

                RequestHack.magicHack(request);

                //被强之后，会被重新放回队列，但是自定义优先级丢失
                CRAWLER_RESULT.add(new PriorityRequest(request));
            }
        } else {
            if (rateLimitFinder.isHealthCheckUrl() && isRateLimit()) {
                RateLimitInterceptor.RATE_LIMIT_STOP.put(1, Boolean.FALSE.toString());
                log.warn("健康检查发现没问题，开启生产者和消费者的运行开关");
            }
        }

    }

    @Override
    public void after(Method method, Response response) {

    }

}
