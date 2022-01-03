package com.ocelot.controller;

import com.alibaba.fastjson.JSONObject;
import com.ocelot.util.SystemHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/*登陆Controller*/
@RestController//RestController返回JSON, Controller返回页面
@RequestMapping("/api")
public class SystemLoginController {
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JSONObject studentLogin(String studentId, String studentPassword) throws IOException {
        JSONObject returnObject = new JSONObject();
        JSONObject loginObject;
        //            判断学号密码是否正确输入
        if(studentId == null|| studentPassword == null || studentId.isEmpty() || studentPassword.isEmpty() || studentPassword.isBlank() || studentId.isBlank()){
            returnObject.put("msg","请输入教务系统的账号密码!");
            returnObject.put("code", false);
            return returnObject;
        }else {
            //执行登陆
            loginObject = SystemHandler.studentLogin(studentId, studentPassword);
            //判断登陆状态
            if (loginObject.get("code").equals(true)) {
                returnObject.put("msg","登陆成功!");
                returnObject.put("code", true);

                return returnObject;
            }else{
                return loginObject;
            }
        }
    }
}
