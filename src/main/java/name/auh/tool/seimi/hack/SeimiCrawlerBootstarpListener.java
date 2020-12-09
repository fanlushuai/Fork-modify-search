package name.auh.tool.seimi.hack;

import cn.wanghaomiao.seimi.config.SeimiConfig;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.spring.boot.CrawlerProperties;
import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.CrawlerModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * hack seimiCrawler框架，通过观察启动逻辑。绕过他的启动，来自己定制启动
 * hack位置：{@cn.wanghaomiao.seimi.spring.common.SeimiCrawlerBootstrapListener#onApplicationEvent(org.springframework.context.event.ContextRefreshedEvent)}
 */
@Component
@Slf4j
public class SeimiCrawlerBootstarpListener implements ApplicationListener<ContextRefreshedEvent> {

    private ExecutorService workersPool;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //seimi.crawler.enabled 设置为false。这样就会取消掉Seimi框架自己的bootstrap。进而之后的逻辑由我们自己控制
        ApplicationContext context = event.getApplicationContext();

        if (context == null) {
            log.error("context==null?");
            return;
        }
        CrawlerProperties crawlerProperties = context.getBean(CrawlerProperties.class);

        /*
         * 如果想要让框架受到我们的控制， seimi.crawler.enabled参数不要设置为true。设置成false或者不设置都行
         * 处理位置为：{@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty}
         */
        int workerNumber = 5;
        log.warn("目前seimi框架受我们自定义控制##########################启动[woker]数量---> {}", workerNumber);

        Map<String, CrawlerModel> crawlerModelContext = CrawlerCache.getCrawlerModelContext();

        workersPool = Executors.newFixedThreadPool(workerNumber);

        String crawlerNames = crawlerProperties.getNames();
        if (StringUtils.isBlank(crawlerNames)) {
            log.info("Spring boot start [{}] as worker.", StringUtils.join(CrawlerCache.getCrawlerModelContext().keySet(), ","));
        } else {

            String[] crawlers = crawlerNames.split(",");
            for (String cn : crawlers) {

                for (Class<? extends BaseSeimiCrawler> a : SeimiCrawlerBeanPostProcessor.getCrawlers()) {
                    CrawlerModel crawlerModel = new CrawlerModel(a, context);
                    crawlerModelContext.put(crawlerModel.getCrawlerName(), crawlerModel);
                }

                CrawlerModel crawlerModel = CrawlerCache.getCrawlerModel(cn);

                //获取代理的任务
                if ("ProxyGet".equals(cn)) {
                    workersPool.execute(new SeimiProcessor(SeimiCrawlerBeanPostProcessor.getInterceptors(), crawlerModel));
                    //todo 触发定时器，去定期获取代理地址
                    crawlerModel.startRequest();
                    continue;
                }

                //健康检查任务。配合限速工作的任务
                if ("RateLimit".equals(cn)) {
                    workersPool.execute(new SeimiProcessor(SeimiCrawlerBeanPostProcessor.getInterceptors(), crawlerModel));
                    //todo 触发定时器，去操作健康检查

                    continue;
                }

                //普通任务直接按照这种规则
                for (int i = 0; i < workerNumber; i++) {
                    workersPool.execute(new SeimiProcessor(SeimiCrawlerBeanPostProcessor.getInterceptors(), crawlerModel));
                }

                crawlerModel.startRequest();
            }
        }

        //统一通用配置信息至 seimiConfig
        SeimiConfig config = new SeimiConfig();
        config.setBloomFilterExpectedInsertions(crawlerProperties.getBloomFilterExpectedInsertions());
        config.setBloomFilterFalseProbability(crawlerProperties.getBloomFilterFalseProbability());
        config.setSeimiAgentHost(crawlerProperties.getSeimiAgentHost());
        config.setSeimiAgentPort(crawlerProperties.getSeimiAgentPort());
        CrawlerCache.setConfig(config);

    }
}
