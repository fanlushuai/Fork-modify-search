package name.auh.tool.seimi.proxy;

public class BuildInProxy extends HttpProxyAbstract implements ProxyBindCrawler {

    private ProxyBind proxyBind;

    private String anonymous;

    private String country;

    private String province;

    private String city;

    private String company;

    private Integer speed;

    private Long lastCheckTime;

    private Long liveTime;

    public BuildInProxy() {
    }

    public BuildInProxy(String anonymous, String country, String province, String city, String company, Integer speed, Long lastCheckTime, Long liveTime) {
        this.anonymous = anonymous;
        this.country = country;
        this.province = province;
        this.city = city;
        this.company = company;
        this.speed = speed;
        this.lastCheckTime = lastCheckTime;
        this.liveTime = liveTime;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Long getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Long lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public Long getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(Long liveTime) {
        this.liveTime = liveTime;
    }

    @Override
    public boolean bindCrawlerName(String crawlerName) {
        return proxyBind.bindCrawlerName(crawlerName);
    }

    @Override
    public void unBind() {
        proxyBind.unBind();
    }
}
