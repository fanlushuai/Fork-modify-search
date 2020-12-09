package name.auh.tool.seimi.proxy.example;

import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import name.auh.tool.seimi.proxy.PoolAbstract;
import name.auh.tool.seimi.proxy.ProxyCrawlerConfig;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class XXXProxyCrawlerConfigImp implements ProxyCrawlerConfig<XXXProxy> {

    @Override
    public PoolAbstract<XXXProxy> getPool() {
        return new XXXHttpProxyPool();
    }

    @Override
    public Request getProxyCrawlerRequest() {
        Request request = new Request();
        request.setUrl("http://www.goubanjia.com/");
        return request;
    }

//    display:none[;|"|>| ]{1,15}([.0-9]{1,15})[< ]

    private static final Pattern DISPLAY_NONE = Pattern.compile("display[: ]{1,5}none[;|\"|>| ]{1,15}([.0-9]{1,15})[< ]");

    private static final Pattern REPLACE_WITHOUT_NUM = Pattern.compile("[^\\d|^\\\\.]");

    @Override
    public List<XXXProxy> parseProxy(Response response) {
        List result = new ArrayList<>();
        JXDocument jxDocument = response.document();
        if (jxDocument == null) {
            return result;
        }
        List<JXNode> list = jxDocument.selN("//tbody/tr");
        for (Iterator<JXNode> iterator = list.iterator(); iterator.hasNext(); ) {
            JXNode next = iterator.next();
            XXXProxy xxxProxy = new XXXProxy();
            String tdStr = next.sel("//td[@class='ip']").get(0).toString();
            String[] tdStrSplit = tdStr.split("port");
            String ip = parseIpOrPort(tdStrSplit[0]);
            String port = parseIpOrPort(tdStrSplit[1]);

            xxxProxy.setIp(ip.trim());
            xxxProxy.setPort(port.trim());
            //todo port解析出来是不对。页面会有动态生成的js处理。日狗了
            result.add(xxxProxy);
        }

        //解析出来你的xxxProxy

        return result;
    }

    public String parseIpOrPort(String str) {
        String disableStr = DISPLAY_NONE.matcher(str).replaceAll("");
        return REPLACE_WITHOUT_NUM.matcher(disableStr).replaceAll("");
    }
}
