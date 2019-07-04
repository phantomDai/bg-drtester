package utl;


import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author phantom
 * @Description
 * @date 2019/7/3/003
 */
public class ResponseBodyEntiy {

    /**
     * 远程调用服务成功的消息回复方法
     * @param id ：1表示调用成功；0表示调用失败
     * @param message：返回日志信息
     * @return json对象
     */
    public static JSONObject responseBody(String id, String message){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", id);
        map.put("message", message);
        JSONObject responseBody = new JSONObject(map);
        return responseBody;
    }

}
