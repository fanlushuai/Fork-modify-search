package name.auh.tool.seimi.proxy.example;

import name.auh.tool.seimi.proxy.HttpProxyAbstract;
import name.auh.tool.seimi.proxy.ProxyBind;
import name.auh.tool.seimi.proxy.ProxySelect;

public class XXXProxy extends HttpProxyAbstract implements ProxySelect {
    //1.继承HttpProxyCountAbstract 可实现使用代理的统计情况

    //2.本具体代理类，添加这个代理的特殊字段

    //3.实现ProxySelect接口即可实现代理的高级方法
    @Override
    public Object getHighSpendProxy() {
        return null;
    }

    @Override
    public Object getAnoProxy() {
        return null;
    }

    //4.添加proxyBind 即可实现 代理的独占功能
    private ProxyBind proxyBind;


}
