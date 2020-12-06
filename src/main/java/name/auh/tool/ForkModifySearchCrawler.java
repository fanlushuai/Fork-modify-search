package name.auh.tool;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.http.HttpMethod;
import cn.wanghaomiao.seimi.http.SeimiHttpType;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import lombok.extern.slf4j.Slf4j;
import name.auh.tool.seimi.enhance.PriorityRequest;
import name.auh.tool.seimi.enhance.RateLimitFinder;
import name.auh.tool.seimi.enhance.Util;
import name.auh.tool.seimi.hack.RequestHack;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static name.auh.tool.seimi.enhance.PushNewCrawlerFromLastResultRepeater.CRAWLER_RESULT;

@Crawler(name = "ForkModifySearch", httpType = SeimiHttpType.OK_HTTP3, useUnrepeated = false, delay = 1, useCookie = false)
@Slf4j
public class ForkModifySearchCrawler extends BaseSeimiCrawler {

    /**
     * 填入你的目标项目  格式： githubUserName/projectName
     */
    private final static String TARGET_REPO = Main.TARGET_REPO;

    @Override
    public String[] startUrls() {
        String targetRepoUrl = String.format("https://github.com/%s/network/members", TARGET_REPO);
        return new String[]{targetRepoUrl};
    }

    @Override
    public void start(Response response) {
        parseForkList(response);
    }

    @RateLimitFinder(backToQueue=true)
    public void parseForkList(Response response) {
        JXDocument jxDocument = response.document();
        if(jxDocument==null){
            return;
        }
        List<Object> forkListRepo = jxDocument.sel("//div[@id='network']//div[@class='repo']/a[last()]/@href");
        if(forkListRepo.isEmpty()){
            return;
        }

        //移除第一个选取，这个是fork源的名称
        forkListRepo.remove(0);

        forkListRepo.forEach(forkRepo -> {
            log.warn("forkRepo--- {}", forkRepo);

            Request request = Request.build(String.format("https://github.com%s", forkRepo), "parseForkRepo").setCrawlerName("ForkModifySearch");
            ;
            Map<String, Object> meta = new HashMap<>();
            meta.put("forkRepo", forkRepo);
            request.setMeta(meta);

            RequestHack.magicHack(request);

            CRAWLER_RESULT.add(new PriorityRequest(request, 0));
        });
    }

    private static final Pattern COMMIT_AHEAD_NUMBER_PATTERN = Pattern.compile("^*(\\d+) commits ahead*");

    @RateLimitFinder(backToQueue=true)
    public void parseForkRepo(Response response) {
        JXDocument jxDocument = response.document();
        if(jxDocument==null){
            return;
        }
        JXNode jxNode=jxDocument.selNOne("//div[@class='d-flex flex-auto']//text()");
        if(jxNode==null){
            return;
        }

        String forkRepoState = jxNode.asString();
        log.warn("forkRepoState--- {}", forkRepoState);

        if (StringUtils.isEmpty(forkRepoState)) {
            return;
        }

        Matcher matcher = COMMIT_AHEAD_NUMBER_PATTERN.matcher(forkRepoState);
        if (matcher.find()) {
            Integer commitAheadNumber = Integer.valueOf(matcher.group(1));
            log.warn("commit ahead number---{} forkRepo --> {}", commitAheadNumber, response.getUrl());

            Map<String, Object> meta = new HashMap<>();
            meta.put("commitAheadNumber", commitAheadNumber);

            String forkRepo = (String) Util.getMate(response).get("forkRepo");

            Request request = Request.build(String.format("https://github.com/%s/commits/master", forkRepo),
                    "parseForkRepoCommitLog", HttpMethod.GET, null, meta).setCrawlerName("ForkModifySearch");
            RequestHack.magicHack(request);
            CRAWLER_RESULT.add(new PriorityRequest(request, 1));
        }


    }

    @RateLimitFinder
    public void parseForkRepoCommitLog(Response response) {
        JXDocument jxDocument = response.document();
        if(jxDocument==null){
            return;
        }
        List<JXNode> commitLogs = jxDocument.selN("//ol/li//div//text()");
        if (commitLogs.isEmpty()) {
            return;
        }

        Object commitAheadNumberObj = Util.getMate(response).get("commitAheadNumber");
        if (commitAheadNumberObj == null) {
            return;
        }
        Integer commitAheadNumber = (Integer) commitAheadNumberObj;
        for (int i = 0, oneCommitLength = 7, maxCommitLength = oneCommitLength * commitAheadNumber;
             i < commitLogs.size() && i < maxCommitLength;
             i += oneCommitLength) {
            String commitLog = commitLogs.get(i).asString().trim();
            String commitLogHash = commitLogs.get(i + 5).asString().trim();
            //commitLogHash 存在情况 ep. Update README.md fanlushuai committed May 27, 2019  commitLogHash-->Verified This commit was created on GitHub.com and signed with a verified signature using GitHub’s key. GPG key ID:
            log.warn("modifyLog [{}]-> {}  logHash-->{}", response.getUrl(), commitLog, commitLogHash.substring(0, commitLogHash.length() > 6 ? 6 : commitLogHash.length()));
        }

    }

}
