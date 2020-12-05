package name.auh.tool.hackseimi;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.annotation.Interceptor;
import cn.wanghaomiao.seimi.core.SeimiInterceptor;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class SeimiCrawlerBeanPostProcessor implements BeanPostProcessor {

    private final static Set<Class<? extends BaseSeimiCrawler>> crawlers = new HashSet<>();

    private final static List<SeimiInterceptor> interceptors = new LinkedList<>();

    public static Set<Class<? extends BaseSeimiCrawler>> getCrawlers() {
        return crawlers;
    }

    public static List<SeimiInterceptor> getInterceptors() {
        return interceptors;
    }

    public static void addCrawler(Class<? extends BaseSeimiCrawler> crawlerClass) {
        crawlers.add(crawlerClass);
    }

    public static void addInterceptor(SeimiInterceptor seimiInterceptor) {
        interceptors.add(seimiInterceptor);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = bean.getClass();
        Crawler crawler = (Crawler) beanClass.getAnnotation(Crawler.class);
        if (crawler != null) {
            if (BaseSeimiCrawler.class.isAssignableFrom(beanClass)) {
                //hack此处，添加自动的容器里面，同时调用这个容器的地方也会被hack
                addCrawler(beanClass);
            } else {
                log.error("Crawler={} not extends {@link cn.wanghaomiao.seimi.def.BaseSeimiCrawler}", beanClass.getName());
            }
        }
        Interceptor interceptor = (Interceptor) beanClass.getAnnotation(Interceptor.class);
        if (interceptor != null) {
            if (SeimiInterceptor.class.isAssignableFrom(beanClass)) {
                //hack此处，添加自动的容器里面，同时调用这个容器的地方也会被hack
                addInterceptor((SeimiInterceptor) bean);
            } else {
                log.error("find class = {}, has @Interceptor but not implement cn.wanghaomiao.seimi.core.SeimiInterceptor", beanClass.getName());
            }
        }
        return bean;
    }
}
