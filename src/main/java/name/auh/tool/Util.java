package name.auh.tool;

import cn.wanghaomiao.seimi.struct.Response;

import java.util.HashMap;
import java.util.Map;

public class Util {

    private static final Map<String, Object> nullMete=new HashMap<>(1);

    public static Map<String, Object> getMate(Response response) {
        if (response == null) {
            return nullMete;
        }
        Map<String, Object> meta = response.getMeta();
        return meta==null?nullMete:meta;
    }


}
