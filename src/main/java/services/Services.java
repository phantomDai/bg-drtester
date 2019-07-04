package services;

import cn.edu.ustb.webservicetester.model.OperationInfo;
import cn.edu.ustb.webservicetester.model.ParameterInfo;
import cn.edu.ustb.webservicetester.util.WsdlParser;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import utl.ResponseBodyEntiy;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author phantom
 * @Description
 * @date 2019/7/3/003
 */
@RestController
public class Services {

    private String wsdlAdress;


    @PostMapping(value = "/api/set/wsdlAdress")
    @ResponseBody
    public JSONObject getWSDLAdress(HttpServletRequest request){
        InputStreamReader inputStreamReader = null;
        String result = "";
        JSONObject jsonObject = ResponseBodyEntiy.responseBody("1", "获取wsdl地址成功！");
        try {
            inputStreamReader = new InputStreamReader(
                    request.getInputStream(),"UTF-8");
            int respInt = inputStreamReader.read();
            while(respInt != -1){
                result += (char)respInt;
                respInt = inputStreamReader.read();
            }
        } catch (IOException e) {
            jsonObject = ResponseBodyEntiy.responseBody("0", "获取wsdl地址不成功！");
            e.printStackTrace();

        }
        wsdlAdress = result;
        return jsonObject;
    }

    @GetMapping(value = "/api/get/operators")
    public JSONObject parseWSDL(){
        WsdlParser wp = new WsdlParser();
        List<OperationInfo> oplist = wp.parseWsdl(wsdlAdress);
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < oplist.size(); i++) {
            OperationInfo tempOperationInfo = oplist.get(i);
            String operatorName = tempOperationInfo.getOpName();
            Map<String, Object> parametersInfo = new LinkedHashMap<>();
            List<ParameterInfo> palist = tempOperationInfo.getParaList();
            for (ParameterInfo parameterInfo : palist) {
                parametersInfo.put(parameterInfo.getName(),parameterInfo.getClass());
            }
            map.put(operatorName, parametersInfo);
        }
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject;
    }



    @PostMapping(value = "/api/set/choices")
    @ResponseBody
    public JSONObject getChoices(HttpServletRequest request){

    }


}
