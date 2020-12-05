package name.auh.tool;

import cn.wanghaomiao.seimi.struct.Response;
import name.auh.tool.seimi.enhance.RateLimitInterceptor;
import org.springframework.util.StringUtils;

public class GithubRateLimit implements RateLimitInterceptor.RateLimitFound {

    @Override
    public boolean found(Response response) {
        if (response == null || StringUtils.isEmpty(response.getContent())) {
            return false;
        }
        return response.getContent().contains("Rate limit");
    }
}
