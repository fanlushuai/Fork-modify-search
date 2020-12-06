package name.auh.tool.seimi.enhance;

import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;

public interface RateLimitConfig {

    boolean found(Response response);

    Request getHealthCheckRequest();

}
