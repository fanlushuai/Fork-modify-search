package name.auh.tool.hackseimi;

import cn.wanghaomiao.seimi.Constants;
import cn.wanghaomiao.seimi.annotation.EnableSeimiCrawler;
import cn.wanghaomiao.seimi.spring.boot.CrawlerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 通过下面的配置，来关闭seimi内置的自动配置。同时，触发我们自己的自动配置
 * seimi.crawler.enabled=false
 */
@Configuration
@ConditionalOnProperty(name = {Constants.SEIMI_CRAWLER_BOOTSTRAP_ENABLED}, havingValue = "false")
@EnableConfigurationProperties({CrawlerProperties.class})
@ComponentScan({"**/crawlers", "**/queues", "**/interceptors", "cn.wanghaomiao.seimi"})
@EnableSeimiCrawler
public class SeimiCrawlerAutoConfiguration {

}
