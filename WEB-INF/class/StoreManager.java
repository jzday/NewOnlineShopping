import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;


public class StoreManager extends HttpServlet {
    private String error_msg;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        displayStoreManager(request, response, pw, "");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        Utilities utility = new Utilities(request,pw);

        //Add New product
        Map<String, Object> map = new HashMap<String, Object>();

        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
        String catalog;
        try {
            List<FileItem> parseRequest = servletFileUpload.parseRequest(request);
            for (FileItem fileItem : parseRequest) {
                boolean formField = fileItem.isFormField();
                if (formField) {
                    //普通表单项
                    String fieldName = fileItem.getFieldName();
                    String fieldValue = fileItem.getString();
                    map.put(fieldName, fieldValue);
                } else {
                    //图片上传项，获得文件名称和内容

                    catalog = String.valueOf(map.get("productCatalog"));
                    String realPath = utility.getRealPath(catalog);

                    String fileName = fileItem.getName();
                    String path = this.getServletContext().getRealPath(realPath);
                    InputStream inputStream = fileItem.getInputStream();
                    OutputStream outputStream = new FileOutputStream(path + "/" + fileName);
                    IOUtils.copy(inputStream, outputStream);
                    inputStream.close();
                    outputStream.close();
                    fileItem.delete();

                    map.put("image", fileName);
                }
            }

            if (utility.storeNewProduct(map)&& AjaxUtility.storeData(map)) {
                //添加成功
                error_msg = "Completed!";
                displayStoreManager(request, response, pw, "newProduct");
            } else {
                //添加失败
                error_msg = "Cannot add new product!";
                displayStoreManager(request, response, pw, "newProduct");
            }

        } catch (FileUploadException e) {
            e.printStackTrace();
        }

    }

    protected void displayStoreManager(HttpServletRequest request,
                                       HttpServletResponse response, PrintWriter pw, String flag)
            throws ServletException, IOException {
        Utilities utility = new Utilities(request, pw);
        utility.printHtml("Header.html");
        pw.print( "<h3><Hello,  StoreManager" + "</h3>"
                +"<li><a href='StoreManager'><span class='glyphicon'>ViewProduct</span></a></li>"
                +"<li><a href='DataVisualization'><span class='glyphicon'>DataVisualization</span></a></li>"
                +"<li><a href='DataAnalytics'><span class='glyphicon'>DataAnalytics</span></a></li>"
                +"<li><a href='Inventory'><span class='glyphicon'>Inventory</span></a></li>"
                +"<li><a href='SalesReport'><span class='glyphicon'>SalesReport</span></a></li>"
                + "<li><a href='Logout'><span class='glyphicon'>Logout</span></a></li>");


        pw.print("<div id='menu' style='float: right;'><ul>");
        pw.print("<div id='content' class='post' style='float: none;'>");
        pw.print("<h3 class='title meta'><a style='font-size: 20px;'>Hello,Store Manager</a></h3>"
                + "<div class='entry'>");

        if (flag.equals("newProduct"))
            pw.print("<h4 style='color:red'>" + error_msg + "</h4>");

        pw.print("<form method='post' action='StoreManager' enctype='multipart/form-data'>"
                + "<table style='width:100%'><tr><td>"

                + "<h3>Product Id</h3></td><td><input type='text' name='productid' value='' class='input' required></input>"
                + "</td></tr><tr><td>"

                + "<h3>Product Name</h3></td><td><input type='text' name='productname' value='' class='input' required></input>"
                + "</td></tr><tr><td>"

                +"<h3>Product Catalog</h4><td><select name='productCatalog' class='input'>" +
                        "<option value='Wearable Technology' selected>Wearable Technology</option>" +
                        "<option value='Phone'>Phone</option>" +
                        "<option value='Laptop'>Laptop</option>" +
                        "<option value='VoiceAssistant'>Speaker</option>" +
                        "<option value='Accessory'>Accessory</option></select>"+
                        "</td></tr></td><tr><td>"

                + "<h3>Product price</h3></td><td><input type='text' name='price' value='' class='input' required></input>"
                + "</td></tr><tr><td>"

                + "<h3>Product manufacture</h3></td><td><input type='text' name='manufacturer' value='' class='input' required></input>"
                + "</td></tr><tr><td>"

                + "<h3>Product condition</h3></td><td><input type='text' name='condition' value='' class='input' required></input>"
                + "</td></tr><tr><td>"

                + "<h3>Product discount</h3></td><td><input type='text' name='discount' value='' class='input' required></input>"
                + "</td></tr><tr><td>"

                +"<h4>Image</h4></td><td><img id=\"preview\" /><br/><input type='file' name='image' class='input' required></input>"
                + "</td></tr></table>"

                + "<input type='submit' class='btnbuy' value='Create' style='float: right;height: 20px margin: 20px; margin-right: 10px;'></input>"
                + "</td></tr><tr><td></td><td>"
                + "</td></tr></table>"
                + "</form>" + "</div></div>");




        //显示product的详细信息

        pw.print("<div class='post'>");
        // pw.print("<form method='post' action='RemoveUpdateProduct'>");
        pw.print("<h2 class='title meta'>");
        pw.print("<a style='font-size: 24px;'>View Products</a></h2>");
        pw.print("<div class='entry'>");
        pw.print("<table class='gridtable'>");

        if (flag.equals("StoreManagerProduct"))
            pw.print("<h4 style='color:red'>" + error_msg + "</h4>");


        //按钮显示
//        pw.print("<div align='left' style='float:left'>");
//        pw.print("<input type='submit' name='Product' value='Update Product' class='btnbuy'>");
//        pw.print("</div>");
//        pw.print("<div align='right'>");
//        pw.print("<input type='submit' name='Product' value='Remove Product' class='btnbuy'>");
//        pw.print("</div>");
//        pw.print("<br>");


        //表头
        pw.print("<tr>");
        pw.print("<td>Product Name</td>");
        pw.print("<td>Price</td>");
        pw.print("<td>Manufacturer</td>");
        pw.print("<td>Condition</td>");
        pw.print("<td>Discount</td>");
        pw.print("<td>Catalog</td>");
        pw.print("</tr>");

        //内容  //Wearable Technology
        for (Map.Entry<String,Console> entry : SaxParserDataStore.consoles.entrySet()) {
            Console console = entry.getValue();
            pw.print("<form method='post' action='StoreManagerProduct'>");
            pw.print("<tr>");
//            pw.print("<td><input type='radio' name='productId' value='" + fitnessWatch.getId() + "'></td>");  //修改为商品ID

            pw.print("<td>" + console.getName() + "</td>" +
                    "<td>" + console.getPrice() + "</td>" +
                    "<td>" + console.getRetailer() + "</td>" +
                    "<td>" + console.getCondition() + "</td>" +
                    "<td>" + console.getDiscount() + "</td>" +
                    "<td>Wearable Technology </td>");

            pw.print("<input type='hidden' name='productId' value='" + console.getId() + "'>");
            pw.print("<input type='hidden' name='productName' value='" + console.getName() + "'>");
            pw.print("<input type='hidden' name='price' value='" + console.getPrice() + "'>");
            pw.print("<input type='hidden' name='manufacturer' value='" + console.getRetailer() + "'>");
            pw.print("<input type='hidden' name='condition' value='" + console.getCondition() + "'>");
            pw.print("<input type='hidden' name='discount' value='" + console.getDiscount() + "'>");
            pw.print("<input type='hidden' name='catalog' value='Wearable Technology'>");
            pw.print("<input type='hidden' name='image' value='" + console.getImage() + "'>");
            pw.print("</tr>");

            pw.print("<tr>");
            //pw.print("<td></td>");
            pw.print("<td><div align=\"left\" style=\"float:left\"><input type='submit' name='Product' value='Update' class='btnbuy'></div>");
            pw.print("<div align=\"right\"><input type='submit' name='Product' value='Remove' class='btnbuy'></div></td>");
            pw.print("</tr>");
            pw.print("</form>");
            //pw.print("<br>");
        }

        //内容  //Phone
        for (Map.Entry<String, Game> entry : SaxParserDataStore.games.entrySet()) {
            Game game = entry.getValue();
            pw.print("<form method='post' action='StoreManagerProduct'>");
            pw.print("<tr>");
            // pw.print("<td><input type='radio' name='productId' value='" + phone.getId() + "'></td>");  //修改为商品ID

            pw.print("<td>" + game.getName() + "</td>" +
                    "<td>" + game.getPrice() + "</td>" +
                    "<td>" + game.getRetailer() + "</td>" +
                    "<td>" + game.getCondition() + "</td>" +
                    "<td>" + game.getDiscount() + "</td>" +
                    "<td>Phone</td>");
            pw.print("<input type='hidden' name='productId' value='" + game.getId() + "'>");
            pw.print("<input type='hidden' name='productName' value='" + game.getName() + "'>");
            pw.print("<input type='hidden' name='price' value='" + game.getPrice() + "'>");
            pw.print("<input type='hidden' name='manufacturer' value='" + game.getRetailer() + "'>");
            pw.print("<input type='hidden' name='condition' value='" + game.getCondition() + "'>");
            pw.print("<input type='hidden' name='discount' value='" + game.getDiscount() + "'>");
            pw.print("<input type='hidden' name='catalog' value='Phone'>");
            pw.print("<input type='hidden' name='image' value='" + game.getImage() + "'>");
            pw.print("</tr>");
            pw.print("<tr>");
            //pw.print("<td></td>");
            pw.print("<td><div align=\"left\" style=\"float:left\"><input type='submit' name='Product' value='Update' class='btnbuy'></div>");
            pw.print("<div align=\"right\"><input type='submit' name='Product' value='Remove' class='btnbuy'></div></td>");
            pw.print("</tr>");
            pw.print("</form>");
        }

        //内容  //Laptop
        for (Map.Entry<String, Tablet> entry : SaxParserDataStore.tablets.entrySet()) {
            Tablet tablet = entry.getValue();
            pw.print("<form method='post' action='StoreManagerProduct'>");
            pw.print("<tr>");
            //pw.print("<td><input type='radio' name='productId' value='" + laptop.getId() + "'></td>");  //修改为商品ID

            pw.print("<td>" + tablet.getName() + "</td>" +
                    "<td>" + tablet.getPrice() + "</td>" +
                    "<td>" + tablet.getRetailer() + "</td>" +
                    "<td>" + tablet.getCondition() + "</td>" +
                    "<td>" + tablet.getDiscount() + "</td>" +
                    "<td>Laptop</td>");
            pw.print("<input type='hidden' name='productId' value='" + tablet.getId() + "'>");
            pw.print("<input type='hidden' name='productName' value='" + tablet.getName() + "'>");
            pw.print("<input type='hidden' name='price' value='" + tablet.getPrice() + "'>");
            pw.print("<input type='hidden' name='manufacturer' value='" + tablet.getRetailer() + "'>");
            pw.print("<input type='hidden' name='condition' value='" + tablet.getCondition() + "'>");
            pw.print("<input type='hidden' name='discount' value='" + tablet.getDiscount() + "'>");
            pw.print("<input type='hidden' name='catalog' value='Laptop'>");
            pw.print("<input type='hidden' name='image' value='" + tablet.getImage() + "'>");
            pw.print("</tr>");
            pw.print("<tr>");
            //pw.print("<td></td>");
            pw.print("<td><div align=\"left\" style=\"float:left\"><input type='submit' name='Product' value='Update' class='btnbuy'></div>");
            pw.print("<div align=\"right\"><input type='submit' name='Product' value='Remove' class='btnbuy'></div></td>");
            pw.print("</tr>");
            pw.print("</form>");
        }

        //内容  //Voice Assistant
        for (Map.Entry<String,Speaker> entry : SaxParserDataStore.speakers.entrySet()) {
            Speaker speaker = entry.getValue();
            pw.print("<form method='post' action='StoreManagerProduct'>");
            pw.print("<tr>");
            // pw.print("<td><input type='radio' name='productId' value='" + voiceAssistant.getId() + "'></td>");  //修改为商品ID

            pw.print("<td>" + speaker.getName() + "</td>" +
                    "<td>" + speaker.getPrice() + "</td>" +
                    "<td>" + speaker.getRetailer() + "</td>" +
                    "<td>" + speaker.getCondition() + "</td>" +
                    "<td>" + speaker.getDiscount() + "</td>" +
                    "<td>Voice Assistant</td>");
            pw.print("<input type='hidden' name='productId' value='" + speaker.getId() + "'>");
            pw.print("<input type='hidden' name='productName' value='" + speaker.getName() + "'>");
            pw.print("<input type='hidden' name='price' value='" + speaker.getPrice() + "'>");
            pw.print("<input type='hidden' name='manufacturer' value='" + speaker.getRetailer() + "'>");
            pw.print("<input type='hidden' name='condition' value='" + speaker.getCondition() + "'>");
            pw.print("<input type='hidden' name='discount' value='" + speaker.getDiscount() + "'>");
            pw.print("<input type='hidden' name='catalog' value='Voice Assistant'>");
            pw.print("<input type='hidden' name='image' value='" + speaker.getImage() + "'>");
            pw.print("</tr>");
            pw.print("<tr>");
            //pw.print("<td></td>");
            pw.print("<td><div align=\"left\" style=\"float:left\"><input type='submit' name='Product' value='Update' class='btnbuy'></div>");
            pw.print("<div align=\"right\"><input type='submit' name='Product' value='Remove' class='btnbuy'></div></td>");
            pw.print("</tr>");
            pw.print("</form>");
        }

        //内容  //Accessory
        for (Map.Entry<String, Accessory> entry : SaxParserDataStore.accessories.entrySet()) {
            Accessory accessory = entry.getValue();
            pw.print("<form method='post' action='StoreManagerProduct'>");
            if (accessory.getName() == null || accessory.getName().isEmpty()) {
                continue;
            }
            pw.print("<tr>");
            // pw.print("<td><input type='radio' name='productId' value='" + accessory.getId() + "'></td>");  //修改为商品ID

            pw.print("<td>" + accessory.getName() + "</td>" +
                    "<td>" + accessory.getPrice() + "</td>" +
                    "<td>" + accessory.getRetailer() + "</td>" +
                    "<td>" + accessory.getCondition() + "</td>" +
                    "<td>" + accessory.getDiscount() + "</td>" +
                    "<td>Accessory</td>");
            pw.print("<input type='hidden' name='productId' value='" + accessory.getId() + "'>");
            pw.print("<input type='hidden' name='productName' value='" + accessory.getName() + "'>");
            pw.print("<input type='hidden' name='price' value='" + accessory.getPrice() + "'>");
            pw.print("<input type='hidden' name='manufacturer' value='" + accessory.getRetailer() + "'>");
            pw.print("<input type='hidden' name='condition' value='" + accessory.getCondition() + "'>");
            pw.print("<input type='hidden' name='discount' value='" + accessory.getDiscount() + "'>");
            pw.print("<input type='hidden' name='catalog' value='Accessory'>");
            pw.print("<input type='hidden' name='image' value='" + accessory.getImage() + "'>");
            pw.print("</tr>");
            pw.print("<tr>");
            //pw.print("<td></td>");
            pw.print("<td><div align=\"left\" style=\"float:left\"><input type='submit' name='Product' value='Update' class='btnbuy'></div>");
            pw.print("<div align=\"right\"><input type='submit' name='Product' value='Remove' class='btnbuy'></div></td>");
            pw.print("</tr>");
            pw.print("</form>");
        }

        pw.print("</table>");
        pw.print("</div></div></div>");

    }


}

