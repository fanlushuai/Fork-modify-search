package name.auh.tool.seimi.proxy;

import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;

import java.util.List;

public interface ProxyCrawlerConfig<T> {

    PoolAbstract<T> getPool();

    Request getProxyCrawlerRequest();

    List<T> parseProxy(Response response);

}
