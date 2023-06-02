package com.cynovan.foshangeyiprint.Print.geyi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.print.PrintService;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class PrintUtils implements Printable {

    /**
     * 纸张宽度
     */
    private int paperWidth;
    /**
     * 纸张高度
     */
    private int paperHeight;

    /**
     * 医院名称
     */
    private String hospitalName;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 出库日期
     */
    private String time;
    /**
     * 页脚 【共10筐，第几筐】
     */
    private String footContent;
    /**
     * 页脚 【A: 5袋 1000ml】
     */
    private String footAmount;
    /**
    * 二维码内容
    * */
    private String QRContent;
    /**
     *是否两张二维码
     * */
    private boolean isDouble;

    /**
     *  默认模式， 宽高值为 0 则为默认
     */
    private final static String DEFAULT  = "default";
    /**
     * 测试模式
     */
    private final static String TEST  = "test";

    /**
     * 运行模式 1：正式  0：测试
     */
    private int schema = 1;

    /**
     * 打印类型 1：订单  0：血筐
     */
    private int type = 1;


    private String printerURI;

    public PrintUtils(String printerURI) {
        paperWidth = 235;
        paperHeight = 125;
        this.printerURI = printerURI;
    }

    public PrintUtils(String type, int paperWidth, int paperHeight) {
        if(DEFAULT.equalsIgnoreCase(type)){
            if(paperWidth == 0){
                paperWidth = 235;
            }
            if(paperHeight == 0){
                paperHeight = 384;
            }
            this.paperWidth = paperWidth;
            this.paperHeight = paperHeight;
            schema = 1;
        }else if(TEST.equalsIgnoreCase(type)){
            this.paperWidth = 235;
            this.paperHeight = 384;
            schema = 0;
        }

    }


    public Result toPrintOrder(String hospitalName, String orderNo,String time,String footContent,String footAmount){
        if (schema == 0){
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
            return orderPrint("第三人民医院", System.currentTimeMillis()+"",   df.format(new Date()), "共20筐，第2筐","A: 5袋 1000ml");
        }else{
            return orderPrint(hospitalName, orderNo,time, footContent,footAmount);
        }
     }


    /**
     *
     * @param hospitalName 医院名称
     * @param orderNo 订单号
     * @param time 出库日期
     * @param footContent 页脚内容
     * @return
     */
    private Result orderPrint(String hospitalName, String orderNo,String time,String footContent,String footAmount){
        this.hospitalName = hospitalName;
        this.orderNo = orderNo;
        this.time = time;
        this.footContent = footContent;
        this.footAmount = footAmount;
        this.type = 1;
        return startPrint();
    }

    public Result QRPrint(String QRContent,boolean isDouble){
        this.QRContent = QRContent;
        this.isDouble = isDouble;
        this.type = 2;
        return startPrint();
    }


    private Result startPrint(){
        Book book = new Book();
        // 打印格式
        PageFormat pf = new PageFormat();
        pf.setOrientation(PageFormat.PORTRAIT);

        // 通过Paper设置页面的空白边距和可打印区域。必须与实际打印纸张大小相符。
        Paper p = new Paper();
        p.setSize(paperWidth, paperHeight);
        p.setImageableArea(0, -20, paperWidth, paperHeight);
        pf.setPaper(p);
        book.append(this, pf);
        PrinterJob job = PrinterJob.getPrinterJob();
        try {
            for (PrintService ps : PrinterJob.lookupPrintServices()) {
                String psName = ps.toString();
                if (psName.contains(printerURI)) {
                    job.setPrintService(ps);
                }
            }
            job.setPageable(book);
            job.print();
        } catch (PrinterException e) {
            e.printStackTrace();
            return new Result(false,"打印失败");
        }
        return new Result(true,"打印成功");
    }


    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D) graphics;

        if (type == 1){
            g2d.setFont(new Font("Default", Font.PLAIN, 10));
            FontMetrics titleFm = g2d.getFontMetrics(new Font("Default", Font.PLAIN, 10));

            int titleWidth = titleFm.stringWidth("医院名称：  ");

            g2d.setFont(new Font("Default", Font.BOLD, 12));

            g2d.drawString( hospitalName, 18+titleWidth, 11);

            g2d.setFont(new Font("Default", Font.PLAIN, 10));
            g2d.drawString("医院名称：  ", 18, 11);
            g2d.drawString("订单号：" , 18, 35);
            g2d.drawString( orderNo, 18+titleWidth, 35);
            g2d.drawString("出库日期：", 18, 58);
            g2d.drawString( time, 18+titleWidth, 58);

            Font footFont = new Font("Default", Font.BOLD, 12);
            g2d.setFont(footFont);
            FontMetrics fm = g2d.getFontMetrics(footFont);
            String foot = footContent;
            int strWidth = fm.stringWidth(foot);
            int widthx = (paperWidth - strWidth)/2;
            g2d.drawString(foot, widthx, 81);
            g2d.setFont(new Font("Default", Font.PLAIN, 10));
            g2d.drawString(footAmount,18,102);
        }else if(type == 2){
            writeQrCodeContent(g2d,QRContent,isDouble);
        }

        return PAGE_EXISTS;
    }
    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int BLACK = 0xFF000000;
        int WHITE = 0xFFFFFFFF;
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    public void writeQrCodeContent(Graphics2D g2d, String url, boolean isDouble){
        int imageWIDTH=100;
        int imageHEIGHT=100;
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        // 内容所使用字符集编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //白边的宽度，可取0~4
        hints.put(EncodeHintType.MARGIN , 3);
        BitMatrix bitMatrix = null;
        BitMatrix bitMatrix1 = null;
        BitMatrix bitMatrix2 = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, imageWIDTH, imageHEIGHT, hints);
            bitMatrix1 = new MultiFormatWriter().encode(url+"L", BarcodeFormat.QR_CODE, imageWIDTH, imageHEIGHT, hints);
            bitMatrix2 = new MultiFormatWriter().encode(url+"R", BarcodeFormat.QR_CODE, imageWIDTH, imageHEIGHT, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        // 生成二维码
        BufferedImage bufferedImage = toBufferedImage(bitMatrix);
        BufferedImage bufferedImage1 = toBufferedImage(bitMatrix1);
        BufferedImage bufferedImage2 = toBufferedImage(bitMatrix2);
        if (isDouble) {
            g2d.drawImage(bufferedImage1, 0, 0, imageWIDTH, imageHEIGHT, null);
            g2d.drawImage(bufferedImage2, imageWIDTH, 0, imageWIDTH, imageHEIGHT, null);
            g2d.setColor(Color.black);
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 14)); // 字体、字型、字号
            int strWidth = g2d.getFontMetrics().stringWidth(url);
            g2d.drawString(url+"L", (imageWIDTH - strWidth) / 2,
                    bufferedImage1.getHeight() - 6);
            g2d.drawString(url+"R", (imageWIDTH - strWidth) / 2 + imageWIDTH,
                    bufferedImage2.getHeight() - 6);
        }else {
            g2d.drawImage(bufferedImage,  (paperWidth - imageWIDTH)/2, 0, imageWIDTH, imageHEIGHT, null);
            // 画文字到新的面板
            g2d.setColor(Color.black);
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            int strWidth = g2d.getFontMetrics().stringWidth(url);
            // 画文字
            g2d.drawString(url, (paperWidth- strWidth) / 2,
                    bufferedImage.getHeight() - 6);
        }
    }

}
