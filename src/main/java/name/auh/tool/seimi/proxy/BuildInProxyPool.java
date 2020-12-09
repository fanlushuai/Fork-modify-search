package name.auh.tool.seimi.proxy;

import java.util.ArrayList;
import java.util.List;

public class BuildInProxyPool<T> extends PoolAbstract<BuildInProxy> implements ProxySelect {

    List<BuildInProxy> proxies = new ArrayList<>();

    @Override
    public void put(BuildInProxy t) {
        proxies.add(t);
    }

    @Override
    public BuildInProxy get() {
        return proxies.get(0);
    }

    @Override
    public BuildInProxy getHighSpendProxy() {
        return proxies.get(0);
    }

    @Override
    public BuildInProxy getAnoProxy() {
        return proxies.get(0);
    }


}
