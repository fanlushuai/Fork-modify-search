package name.auh.tool;

import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.Request;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 上步结果和下次抓取  中继器
 * 限制于项目使用的爬虫框架的缺陷。曲线救国的办法
 * 这个写法也不是最优的。但是最简单的。最优的写法可以动态的生成队列和定时任务去调度。不用每次新增任务中继度进行代码新增
 */
@Component
public class PushNewCrawlerFromLastResultRepeater {

    final static ArrayBlockingQueue<Request> FORK_LIST_RESULT = new ArrayBlockingQueue(1314520);

    final static ArrayBlockingQueue<Request> FORK_REPO_RESULT = new ArrayBlockingQueue(1314520);

    /**
     * 定时获取数据 从forklist爬取产生的结果队列 ，进行forkRepo的进一步抓取操作
     * 可以控制多长时间，获取多少个结果，来控制抓取流量，进一步消除ratelimit
     */
    @Scheduled(fixedDelay = 2000)
    public void pushForkRepoFromForkListResult() {
        pushCrawlerFromLastResultRequestQueue(FORK_LIST_RESULT, 5, "ForkModifySearch");
    }

    /**
     * 定时获取数据 从forkRepo爬取产生的结果队列 ，进行ForkRepoCommitLog的进一步抓取操作
     * 可以控制多长时间，获取多少个结果，来控制抓取流量，进一步消除ratelimit
     */
    @Scheduled(fixedDelay = 3000)
    public void pushForkRepoCommitLogFromForkRepoResult() {
        pushCrawlerFromLastResultRequestQueue(FORK_REPO_RESULT, 5, "ForkModifySearch");
    }

    private void pushCrawlerFromLastResultRequestQueue(ArrayBlockingQueue<Request> lastResultRequestQueue, int oneTimePollCount, String crawlerName) {
        for (int i = 0; i < oneTimePollCount; i++) {
            Request request = lastResultRequestQueue.poll();
            if (request == null) {
                break;
            }
            CrawlerCache.getCrawlerModel(crawlerName).sendRequest(request);
        }
    }


}
