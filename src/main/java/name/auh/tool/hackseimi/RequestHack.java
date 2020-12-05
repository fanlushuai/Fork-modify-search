package name.auh.tool.hackseimi;

import cn.wanghaomiao.seimi.struct.Request;

public class RequestHack {

    /**
     * 魔法逻辑绕过 底层框架的问题(出错自动重试)会影响到我们自动检测的重新入队。导致重复数据
     * 绕过的逻辑代码如下：
     * cn/wanghaomiao/seimi/core/SeimiProcessor.java:90
     * cn/wanghaomiao/seimi/core/SeimiProcessor.java:124
     * cn/wanghaomiao/seimi/core/SeimiProcessor.java:128
     */
    public static void magicHack(Request request) {
        request.setCurrentReqCount(2);
        request.setMaxReqCount(0);
    }

}
