package name.auh.tool;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.http.HttpMethod;
import cn.wanghaomiao.seimi.http.SeimiHttpType;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Crawler(name = "ForkModifySearch", httpType = SeimiHttpType.OK_HTTP3, useUnrepeated = false, delay = 2, useCookie = false)
@Slf4j
public class ForkModifySearchCrawler extends BaseCrawler {

    /**
     *  填入你的目标项目  格式： githubUserName/projectName
     */
    private final static String targetRepo = "tychxn/jd-assistant";

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

        //移除第一个选取，这个是fork源的名称
        forkListRepo.remove(0);

        forkListRepo.forEach(v -> {
            log.info("forkRepo--- {}", v);
            Request request = Request.build("https://github.com" + v, "parseForkRepo");
            Map<String, Object> meta = new HashMap<>();
            meta.put("forkRepo", v);
            request.setMeta(meta);
            push(request);
        });
    }

    private static final Pattern commitAheadNumberPattern = Pattern.compile("^*(\\d+) commits ahead*");

    public void parseForkRepo(Response response) {
        JXDocument jxDocument = response.document();

        try {

            String forkRepoState = jxDocument.selNOne("//div[@class='d-flex flex-auto']//text()").asString();
            log.info("forkRepoState--- {}", forkRepoState);

            if (StringUtils.isEmpty(forkRepoState)) {
                return;
            }

            Matcher matcher = commitAheadNumberPattern.matcher(forkRepoState);
            if (matcher.find()) {
                Integer commitAheadNumber = Integer.valueOf(matcher.group(1));
                log.info("commit ahead number---{} forkRepo --> {}", commitAheadNumber, response.getUrl());

                Map<String, Object> meta = new HashMap<>();
                meta.put("commitAheadNumber", commitAheadNumber);

                String forkRepo = (String) Util.getMate(response).get("forkRepo");

                push(Request.build(String.format("https://github.com/%s/commits/master", forkRepo), "parseForkRepoCommitLog", HttpMethod.GET, null, meta));
            }

        } catch (NullPointerException e) {
            log.error("-------->{}", response.getContent());
        }

    }

    public void parseForkRepoCommitLog(Response response) {
        JXDocument jxDocument = response.document();
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
            log.error("commitLog--->{}  commitLogHash-->{}", commitLog, commitLogHash);
        }

    }

}
