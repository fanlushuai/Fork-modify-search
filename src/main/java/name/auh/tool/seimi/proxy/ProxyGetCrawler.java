package name.auh.tool.seimi.proxy;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Crawler(name = "ProxyGet", useUnrepeated = false, delay = 1, useCookie = false, httpTimeOut = 15000)
@Slf4j
public class ProxyGetCrawler extends BaseSeimiCrawler {

    @Autowired
    List<ProxyCrawlerConfig> proxyCrawlerConfigs;

    @Override
    public String[] startUrls() {
        return new String[0];
    }

    @Override
    public List<Request> startRequests() {
        List<Request> arrayList = new ArrayList<>();
        for (ProxyCrawlerConfig proxyCrawlerConfig : proxyCrawlerConfigs) {
            arrayList.add(proxyCrawlerConfig.getProxyCrawlerRequest());
        }
        return arrayList;
    }

    @Override
    public void start(Response response) {
        proxyCrawlerConfigs.forEach(
                proxyCrawlerConfig -> {
                    if (proxyCrawlerConfig.getProxyCrawlerRequest().getUrl().equals(response.getRequest().getUrl())) {
                        List proxies = proxyCrawlerConfig.parseProxy(response);
                        if (CollectionUtils.isEmpty(proxies)) {
                            return;
                        }
                        proxyCrawlerConfig.getPool().putAll(proxies);
                    }
                }
        );
    }

}
