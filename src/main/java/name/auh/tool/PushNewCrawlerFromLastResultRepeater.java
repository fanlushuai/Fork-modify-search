package name.auh.tool;

import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 上步结果和下次抓取  中继器
 * 限制于项目使用的爬虫框架的缺陷。曲线救国的办法
 * 这个写法也不是最优的。但是最简单的。最优的写法可以动态的生成队列和定时任务去调度。不用每次新增任务中继度进行代码新增
 */
@Component
@Slf4j
public class PushNewCrawlerFromLastResultRepeater {

    final static ArrayBlockingQueue<Request> FORK_LIST_RESULT = new ArrayBlockingQueue(1314520);

    final static ArrayBlockingQueue<Request> FORK_REPO_RESULT = new ArrayBlockingQueue(1314520);

    private final static AtomicInteger REQUEST_COUNT =new AtomicInteger(0);

    /**
     * 总体 抓取请求并发控制
     * @throws IllegalAccessException
     */
    @Scheduled(fixedDelay = 6000)
    public void controlRequest() throws IllegalAccessException {
        /*
        1. 假设消费者消费能力很强，会出现瞬间并发oneTimeRequestCount的请求。所有要考虑到github瞬间的能力，来确定最大的oneTimeRequestCount
        2. 假设消费者消费能力不行，可能会导致上个周期产生的任务，积压到下个周期。导致某个周期出现超过oneTimeRequestCount的瞬间并发
         所以fixedDelay要保证，oneTimeRequestCount能在一个fixedDelay消费完，来确定最小的fixedDelay
        3.  又因为，github可能设置一些频率限制规则。固定窗口，或者滑动窗口，或者一些自创的方式。要对fixedDelay和oneTimeRequuestCount进一步调整
        4. 总之慢慢调整吧，哈哈.奈何用了一个不开窍（有缺陷）的框架
         */
        int oneTimeRequestCount = 20;

        Field[] fields=PushNewCrawlerFromLastResultRepeater.class.getDeclaredFields();
        for (Field field : fields) {
           if(!field.getType().equals(ArrayBlockingQueue.class)){
               continue;
           }
            field.setAccessible(true);
            ArrayBlockingQueue<Request> arrayBlockingQueue=(ArrayBlockingQueue<Request>)field.get(this);
            oneTimeRequestCount=pushCrawlerFromLastResultRequestQueue(arrayBlockingQueue,oneTimeRequestCount,"ForkModifySearch");
            if(oneTimeRequestCount==0){
                return;
            }
        }
    }

    private int pushCrawlerFromLastResultRequestQueue(ArrayBlockingQueue<Request> lastResultRequestQueue, int oneTimePollCount, String crawlerName) {
        int leftTimes= oneTimePollCount;
        for (int i = 0; i < oneTimePollCount; i++) {
            Request request = lastResultRequestQueue.poll();
            if (request == null) {
                return leftTimes;
            }
            CrawlerCache.getCrawlerModel(crawlerName).sendRequest(request);
            log.debug("request Counter # {}", REQUEST_COUNT.incrementAndGet());
            leftTimes-=1;
        }
        return leftTimes;
    }


}
