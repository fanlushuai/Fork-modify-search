package name.auh.tool.seimi.enhance;

import cn.wanghaomiao.seimi.struct.Request;

/**
 * 只能保证优先生产，不能保证优先消费
 */
public class PriorityRequest implements Comparable<PriorityRequest> {

    public Request getRequest() {
        return request;
    }

    private Request request;

    /**
     * 值越小，优先级越高
     */
    int order = 9;

    public PriorityRequest(Request request) {
        this.request = request;
    }

    public PriorityRequest(Request request, int order) {
        this.request = request;
        this.order = order;
    }

    @Override
    public int compareTo(PriorityRequest o) {
        return this.order > o.order ? -1 : 1;
    }
}
