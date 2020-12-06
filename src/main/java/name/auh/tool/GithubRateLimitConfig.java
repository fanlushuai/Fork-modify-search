package name.auh.tool;

import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import name.auh.tool.seimi.enhance.RateLimitConfig;
import org.springframework.util.StringUtils;

import static name.auh.tool.Main.TARGET_REPO;

public class GithubRateLimitConfig implements RateLimitConfig {

    @Override
    public boolean found(Response response) {
        return response==null ||StringUtils.isEmpty(response.getContent()) || response.getContent().contains("Rate limit");
    }

    @Override
    public Request getHealthCheckRequest() {
        String url=String.format("https://github.com/%s/network/members", TARGET_REPO);
        return new Request(url,"");
    }

}
