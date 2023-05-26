package com.cynovan.foshangeyiprint.Print.service;

import com.cynovan.foshangeyiprint.Print.geyi.PrintUtils;
import com.cynovan.foshangeyiprint.Print.geyi.Result;
import org.springframework.stereotype.Service;

/**
 * @author ：xyp
 * @description：TODO
 * @date ：2022/5/13 15:56
 */
@Service
public class GeyiPrintService {
    /**
     * 医院名称
     * 订单号
     * 出库日期
     * 页脚 【共10筐，第几筐】
     * 页脚 【A: 5袋 1000ml】
     */
    public Result OrderPrint(String name, String orderNo, String date, String footContent, String footAmount){
        PrintUtils printUtils = new PrintUtils("Win32 Printer : TC35Label Printer");
        return printUtils.toPrintOrder(name,orderNo,date,footContent,footAmount);
    }
    /**
     * content:二维码内容
     * isDouble:是否两个二维码
     * */
    public Result QRPrint(String content,boolean isDouble){
        PrintUtils printUtils = new PrintUtils("Win32 Printer : TC35Label Printer");
        return printUtils.QRPrint(content,isDouble);
    }
}
