package name.auh.tool.seimi.enhance;

import cn.wanghaomiao.seimi.annotation.Interceptor;
import cn.wanghaomiao.seimi.core.SeimiInterceptor;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import name.auh.tool.seimi.hack.RequestHack;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
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
        String rateLimitStop = RateLimitInterceptor.RATE_LIMIT_STOP.getIfPresent(1);
        if (Boolean.TRUE.toString().equals(rateLimitStop)) {
            log.debug("found github rate limit ,cancel request push,rateLimitStop {}", rateLimitStop);
            return true;
        }
        return false;
    }

    @Override
    public Class<? extends Annotation> getTargetAnnotationClass() {
        return RateLimitFinder.class;
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

        if (rateLimitFounds == null) {
            rateLimitFounds = new HashSet<>();

            //保证就算类被拷贝走也不用修改直接用
            String[] splitPackageName = RateLimitInterceptor.class.getPackage().getName().split(".");
            StringBuffer reflectPackageName = new StringBuffer();
            for (int i = 0; i < splitPackageName.length; i++) {
                String s = splitPackageName[i];
                reflectPackageName.append(s);
                if (i == 1) {
                    break;
                }
            }

            Reflections reflections = new Reflections(reflectPackageName.toString());
            Set<Class<? extends RateLimitFound>> rateLimitFoundsClass = reflections.getSubTypesOf(RateLimitFound.class);
            for (Class<? extends RateLimitFound> rateLimitFound : rateLimitFoundsClass) {
                try {
                    rateLimitFounds.add(rateLimitFound.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        for (RateLimitFound rateLimitFound : rateLimitFounds) {
            if (rateLimitFound.found(response)) {
                Request request = response.getRequest();

                log.error("被墙了，自动|降低频率！！！ 检测链接", request.getUrl());

                RATE_LIMIT_STOP.put(1, Boolean.TRUE.toString());

                log.warn("重新放回队列 {}", request.getUrl());

                RequestHack.magicHack(request);

                //被强之后，会被重新放回队列，但是自定义优先级丢失
                CRAWLER_RESULT.add(new PriorityRequest(request));

                return;
            }
        }

    }

    @Override
    public void after(Method method, Response response) {

    }

    private static Set<RateLimitFound> rateLimitFounds = null;

    public interface RateLimitFound {

        boolean found(Response response);
    }
}
