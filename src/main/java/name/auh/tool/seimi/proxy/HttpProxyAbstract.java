package name.auh.tool.seimi.proxy;

public abstract class HttpProxyAbstract implements Proxy {

    private String ip;

    private String port;

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public String getPort() {
        return port;
    }
}
