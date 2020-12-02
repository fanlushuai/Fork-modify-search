package name.auh.tool;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.struct.Response;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;

import java.util.List;

@Crawler(name = "ForkRepo",useUnrepeated = false, useCookie = false, httpTimeOut = 5000)
@Slf4j
public class ForkRepoCrawler extends BaseCrawler {

    @Override
    public String[] startUrls() {
        return new String[]{"https://github.com/fanlushuai/SeimiCrawler"};
    }

    @Override
    public void start(Response response) {
        parseForkRepo(response);
    }

    public void parseForkRepo(Response response) {
        JXDocument jxDocument = response.document();
        List<Object> forkListRepo=jxDocument.sel("//div[@class='d-flex flex-auto']//text()");
        forkListRepo.forEach(v->{
            log.info("forkRepoState--- {}",v);
        });
    }

}
