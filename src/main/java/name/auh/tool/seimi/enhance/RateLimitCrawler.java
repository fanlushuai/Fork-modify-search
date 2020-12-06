package name.auh.tool.seimi.enhance;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Response;
import lombok.extern.slf4j.Slf4j;

@Crawler(name = "RateLimit", useUnrepeated = false, delay = 1, useCookie = false, httpTimeOut = 15000)
@Slf4j
public class RateLimitCrawler extends BaseSeimiCrawler {

    @Override
    public String[] startUrls() {
        return new String[0];
    }

    @Override
    @RateLimitFinder(isHealthCheckUrl = true)
    public void start(Response response) {

    }
}
