package name.auh.tool.seimi.proxy;

public interface Proxy {

    default String type() {
        return "";
    }

    String getIp();

    String getPort();

}
