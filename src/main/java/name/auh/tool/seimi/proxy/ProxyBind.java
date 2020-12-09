package name.auh.tool.seimi.proxy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProxyBind implements ProxyBindCrawler {

    private Lock lock = new ReentrantLock();

    private String currentCrawlerName;

    public String getCurrentCrawlerName() {
        return currentCrawlerName;
    }

    @Override
    public boolean bindCrawlerName(String crawlerName) {
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                currentCrawlerName = crawlerName;
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void unBind() {
        lock.unlock();
    }
}
