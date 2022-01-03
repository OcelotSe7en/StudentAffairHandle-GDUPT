package com.ocelot.controller;

import com.ocelot.util.HttpPoolUtil;
import com.ocelot.util.SystemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/*素拓分Controller*/
@RestController//RestController返回JSON, Controller返回页面
@RequestMapping("/api")
public class CheckHttpStatusController {
    private static final Logger logger = LoggerFactory.getLogger(SystemHandler.class);

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public void checkStatus(){
        logger.info("当前Http连接池状态为 [{}]",HttpPoolUtil.getTotalStats());
    }
}
