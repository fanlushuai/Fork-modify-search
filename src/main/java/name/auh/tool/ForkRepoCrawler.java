package name.auh.tool;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.struct.Response;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern commitAheadNumberPattern = Pattern.compile("^*(\\d+) commits ahead*");

    public void parseForkRepo(Response response) {
        JXDocument jxDocument = response.document();
        String forkRepoState=jxDocument.selNOne("//div[@class='d-flex flex-auto']//text()").asString();
        log.info("forkRepoState--- {}",forkRepoState);

        if(StringUtils.isEmpty(forkRepoState)){
            return;
        }

        Matcher matcher=commitAheadNumberPattern.matcher(forkRepoState);
        if(matcher.find()){
            Integer commitAheadNumber=Integer.valueOf(matcher.group(1));
            log.info("commit ahead number---{}",commitAheadNumber);
        }

    }


}
