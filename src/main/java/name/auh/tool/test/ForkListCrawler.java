package name.auh.tool.test;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Response;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;

import java.util.List;

@Crawler(name = "ForkList", useUnrepeated = false, useCookie = true, httpTimeOut = 5000)
@Slf4j
public class ForkListCrawler extends BaseSeimiCrawler {

    private final static String targetRepo = "zhegexiaohuozi/SeimiCrawler";

    @Override
    public String[] startUrls() {
        String targetRepoUrl = String.format("https://github.com/%s/network/members", targetRepo);
        return new String[]{targetRepoUrl};
    }

    @Override
    public void start(Response response) {
        parseForkList(response);
    }

    public void parseForkList(Response response) {
        JXDocument jxDocument = response.document();
        List<Object> forkListRepo = jxDocument.sel("//div[@id='network']//div[@class='repo']/a[last()]/@href");
        forkListRepo.forEach(v -> {
            log.info("forkRepo--- {}", v);
        });
    }

}
