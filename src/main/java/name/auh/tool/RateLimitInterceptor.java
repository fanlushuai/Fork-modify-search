package name.auh.tool;

import cn.wanghaomiao.seimi.annotation.Interceptor;
import cn.wanghaomiao.seimi.core.SeimiInterceptor;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Interceptor(everyMethod = true)
@Slf4j
public class RateLimitInterceptor implements SeimiInterceptor {

    /**
     * 20s缓存自动失效的key。来充当请求继续时间
     */
    final static Cache<Integer, String> RATE_LIMIT_STOP = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    static boolean isRateLimit() {
        String rateLimitStop = RateLimitInterceptor.RATE_LIMIT_STOP.getIfPresent(1);
        if (Boolean.TRUE.toString().equals(rateLimitStop)) {
            log.debug("found github rate limit ,cancel request push,rateLimitStop {}", rateLimitStop);
            return true;
        }
        return false;
    }

    /**
     * 魔法逻辑绕过 底层框架的问题(出错自动重试)会影响到我们自动检测的重新入队。导致重复数据
     * 绕过的逻辑代码如下：
     * cn/wanghaomiao/seimi/core/SeimiProcessor.java:90
     * cn/wanghaomiao/seimi/core/SeimiProcessor.java:124
     * cn/wanghaomiao/seimi/core/SeimiProcessor.java:128
     */
    static void magicHack(Request request) {
        request.setCurrentReqCount(2);
        request.setMaxReqCount(0);
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

        if (method.getAnnotation(RateLimitFinder.class) != null && (response.getContent() == null || response.getContent().contains("Rate limit"))) {
            Request request = response.getRequest();
            log.error("被墙了，自动|降低频率！！！ 检测链接", request.getUrl());

            RATE_LIMIT_STOP.put(1, Boolean.TRUE.toString());

            log.warn("重新放回队列 {}", request.getUrl());

            RateLimitInterceptor.magicHack(request);

            if ("parseForkRepo".equals(request.getCallBack())) {
                PushNewCrawlerFromLastResultRepeater.FORK_LIST_RESULT.add(request);
            } else if ("parseForkRepoCommitLog".equals(request.getCallBack())) {
                PushNewCrawlerFromLastResultRepeater.FORK_REPO_RESULT.add(request);
            }

        }
    }

    @Override
    public void after(Method method, Response response) {

    }
}
