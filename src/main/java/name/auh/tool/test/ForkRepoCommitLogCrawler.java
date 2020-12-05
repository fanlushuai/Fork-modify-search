package name.auh.tool.test;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.http.HttpMethod;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import lombok.extern.slf4j.Slf4j;
import name.auh.tool.BaseCrawler;
import name.auh.tool.seimi.enhance.Util;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Crawler(name = "ForkRepoCommitLog", useUnrepeated = false, useCookie = true, httpTimeOut = 5000)
@Slf4j
public class ForkRepoCommitLogCrawler extends BaseCrawler {

    @Override
    public List<Request> startRequests() {
        Map<String, Object> meta = new HashMap<>(1);
        meta.put("commitAheadNumber", 6);
        List<Request> startRequests = new ArrayList<>();
        startRequests.add(Request.build("https://github.com/fanlushuai/SeimiCrawler/commits/master", null, HttpMethod.GET, null, meta));
        return startRequests;
    }

    @Override
    public void start(Response response) {
        parseForkRepoCommitLog(response);
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
            log.info("commitLog--->{}  commitLogHash-->{}", commitLog, commitLogHash);
        }

    }

}
