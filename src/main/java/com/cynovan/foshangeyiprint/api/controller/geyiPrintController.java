package com.cynovan.foshangeyiprint.api.controller;

import com.cynovan.foshangeyiprint.Print.geyi.Result;
import com.cynovan.foshangeyiprint.Print.service.GeyiPrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ：xyp
 * @description：TODO
 * @date ：2023/5/23 16:58
 */
@RestController
@RequestMapping("/fun")
public class geyiPrintController {

    private static Logger logger = LoggerFactory.getLogger(geyiPrintController.class);
    @Autowired
    private GeyiPrintService service;

    @RequestMapping("/test")
    public String test(){
        return "hello test";
    }

    @RequestMapping("/geyiPrint")
    public Result print(@RequestBody Map<String,String> map){
        String name = map.get("name");
        String orderNo = map.get("orderNo");
        String date = map.get("date");
        String footContent = map.get("footContent");
        String footAmount = map.get("footAmount");
        logger.info("打印请求"+map);
        return service.OrderPrint(name, orderNo, date, footContent, footAmount);
    }

}
