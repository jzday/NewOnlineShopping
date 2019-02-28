import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SalesMan extends HttpServlet {
    private String error_msg;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        displaySalesMan(request, response, pw, "");
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        Utilities utility = new Utilities(request, pw);

        String username = request.getParameter("newusername");
        String password = request.getParameter("newuserpassword");
        String usertype = "customer";
        usertype = request.getParameter("userType");


        //创建order的表格
        String customerName = request.getParameter("customerName");
        String itemName = request.getParameter("itemName");
        String itemCatalog = request.getParameter("itemCatalog");
        String creditCardNo = request.getParameter("creditCardNo");
        String customerAddress = request.getParameter("customerAddress");


        HashMap<String, User> hm = new HashMap<String, User>();
        String TOMCAT_HOME = System.getProperty("catalina.home");

        //get the user details from file

        try {
            hm = MySqlDataStoreUtilities.selectUser();
        } catch (Exception e) {

        }

        if (request.getParameter("bysalesman") != null && request.getParameter("bysalesman").equals("Create Customer")) {
            //提交的是创建customer的表格


            // if the user already exist show error that already exist
            if (hm.containsKey(username)) {
                error_msg = "Username already exist.";
                displaySalesMan(request, response, pw, "bysalesman");
            } else {
                    /*create a user object and store details into hashmap
				     store the user hashmap into file  */
                MySqlDataStoreUtilities.insertUser(username, password,password,"customer");
                HttpSession session = request.getSession(true);
                session.setAttribute("login_msg", "The customer account has been created.");

                //创建customer成功
                error_msg = "The customer has been created.";
                displaySalesMan(request, response, pw, "bysalesman");
            }

        } else if (request.getParameter("order") != null && request.getParameter("order").equals("Create")) {
            if (!hm.containsKey(customerName)) {
                error_msg = "Customer doesn't exist";
                displaySalesMan(request, response, pw, "order");
            } else {
                double totalPrice;
                if (utility.isContainsStr(request.getParameter("totalPrice"))) {
                    error_msg = "Please enter number";
                    displaySalesMan(request, response, pw, "order");
                    return;
                } else {
                    totalPrice = Double.parseDouble(request.getParameter("totalPrice"));
                }
                if (utility.isItemExist(itemCatalog, itemName)) {
                    SimpleDateFormat df = new SimpleDateFormat("HHmmss");
                    int orderId = Integer.parseInt(df.format(new Date()));
                    utility.storeCreateOrder(orderId, itemName, customerName, totalPrice, customerAddress, creditCardNo);
                    error_msg = "The order created successfully";
                    displaySalesMan(request, response, pw, "order");
                } else {
                    error_msg = "Can't create this product";
                    displaySalesMan(request,response,pw,"order");
                }
            }

        }
    }
//                else {
//        // if the user already exist show error that already exist
//
//        if(!hm.containsKey(customerName)){
//            error_msg = "Username already exist " ;
//                displaySalesMan(request, response, pw, "order");
//            } else {
//                if (utility.isItemExist(itemCatalog, itemName)) {   // check the function of store whether exits
//                    SimpleDateFormat df = new SimpleDateFormat("HHmmss");//set up the date format
//                    int orderId = Integer.parseInt(df.format(new Date()));  //set the order number is the current date
//                    utility.storePayment(orderId, customerName, totalPrice, customerAddress, creditCardNo);
//                    error_msg = "The order has been created.";
//                    displaySalesMan(request, response, pw, "order");
//                } else {

//                    error_msg = "Order number already exist.";
//                    displaySalesMan(request, response, pw, "order");
//                }
//            }
//        }







        protected void displaySalesMan(HttpServletRequest request,
                                       HttpServletResponse response, PrintWriter pw, String flag)
            throws ServletException, IOException {
        Utilities utility = new Utilities(request, pw);
        utility.printHtml("Header.html");

        pw.print("<div class='post' id='content'");
        pw.print("<h2 class='title meta'><a style='font-size: 24px;'>Create New Customer</a></h2>"
                + "<div class='entry'>");


            if (flag.equals("bysalesman"))
                pw.print("<h4 style='color:red'>" + error_msg + "</h4>");

                pw.print("<div style='width:300px; margin:25px; margin-left: auto;margin-right: auto;'>");
//            HttpSession session = request.getSession(true);
//            if(session.getAttribute("SalesMan_msg")!=null){
//                pw.print("<h4 style='color:red'>"+session.getAttribute("SalesMan_msg")+"</h4>");
//                session.removeAttribute("SalesMan_msg");
//            }

            //create new customer
        pw.print("<form method='post' action='SalesMan'>"
                + "<table style='width:100%'><tr><td>"
                + "<h3>New User Name</h3></td><td><input type='text' name='newusername' value='' class='input' required></input>"
                + "</td></tr><tr><td>"
                + "<h3>new User Password</h3></td><td><input type='text' name='newuserpassword' value='' class='input' required></input>"
                + "</td></tr><tr><td>"
                + "<h3>User Type</h3></td><td><select name='userType' class='input'><option value='Customer' selected>Customer</option></select>"
                + "</td></tr></table>"
                + "<input type='submit' class='btnbuy' name='bysalesman' value='Create Customer' style='float: right;height: 20px margin: 20px; margin-right: 10px;'></input>"
                + "</td></tr><tr><td></td><td>"
                + "</td></tr><tr><td></td><td>"
                + "</td></tr></table>"
                + "</form></div></div>");


            //create new order
            pw.print("<div class='post'>"
            +"<h3 class='title'>Create New Order</h3>");
            pw.print("<div class='entry'>");
            if (flag.equals("order"))
                pw.print("<h4 style='color:red'>" + error_msg + "</h4>");
                pw.print("<form action='SalesMan' method='post'>"
                    +"<table style='width:100%'><tr><td>"
                    +"<h4>Customer name</h4></td><td><input type='text' name='customerName' value='' class='input' required></input>"
                    +"</td></tr><tr><td>"
                    +"<h4>Item name</h4></td><td><input type='text' name='itemName' value='' class='input' required></input>"
                    +"</td></tr><tr><td>");

            pw.print("<h4>Item catalog</h4><td><select name='itemCatalog' class='input'>" +
                    "<option value='WearableTechnology' selected>Wearable Technoloy</option>" +
                 //   "<option value='SmartWatch'>Smart watch</option>" +
                 //   "<option value='Headphone'>Headphone</option>" +
                 //   "<option value='VirtualReality'>Virtual reality</option>" +
                 //   "<option value='PetTracker'>Pet tracker</option>" +
                    "<option value='Phone'>Phones</option>" +
                    "<option value='Laptop'>Laptops</option>" +
                    "<option value='Speaker'>Speaker</option>" +
                    "<option value='Accessory'>Accessory</option></select>");
            pw.print("</td></tr></td><tr><td>");
            pw.print("<h4>Total price</h4></td><td><input type='text' name='totalPrice' value='' class='input' required></input>");
            pw.print("</td></tr><tr><td>");

            pw.print("<h4>Credit/accountNo</h4></td><td><input type='text' name='creditCardNo' value='' class='input' required></input>");
            pw.print("</td></tr><tr><td>");
            pw.print("<h4>Customer Address</h3></td><td><input type='text' name='customerAddress' value='' class='input' required></input>");
            pw.print("</td></tr><tr><td>");
            pw.print("<input type='submit' class='btnbuy' value='Create' name='order' style='float: right;height: 20px margin: 20px; margin-right: 10px;'></input>");
            pw.print("</td></tr><tr><td></td><td>");
            pw.print("</td></tr></table>");
            pw.print("</form></div></div>");


            //show order detailed
            HashMap<Integer, ArrayList<OrderPayment>> orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
            try {
                orderPayments=MySqlDataStoreUtilities.selectOrder();
            } catch (Exception e) {

            }

            pw.print("<div class='post'>");
            pw.print("<h2 class='title meta'>");
            pw.print("<a style='font-size: 24px;'>View Orders</a>");
            pw.print("</h2><div class='entry'>");

            pw.print("<table class='gridtable'>");
            pw.print("<tr>");
            pw.print("<td>Order Id:</td>");
            pw.print("<td>Username:</td>");
            pw.print("<td>Product Name:</td>");
            pw.print("<td>Price:</td></td>");
            pw.print("<td>UserAddress:</td>");
            pw.print("<td>CreditCard No:</td>");
            pw.print("</tr>");

            for (Map.Entry<Integer, ArrayList<OrderPayment>> entry : orderPayments.entrySet()) {
                for (OrderPayment od : entry.getValue()) {


                    pw.print("<form method='post' action='SalesManOrder'>");
                    pw.print("<tr>");
                    pw.print("<td>" + od.getOrderId() + "</td>" +
                            "<td>" + od.getUserName() + "</td>" +
                            "<td>" + od.getOrderName() + "</td>" +
                            "<td>" + od.getOrderPrice() + "</td>" +
                            "<td>" + od.getUserAddress() + "</td>" +
                            "<td>" + od.getCreditCardNo() + "</td>");

                    pw.print("<input type='hidden' name='orderName' value='" + od.getOrderName() + "'>"
                    +"<input type='hidden' name='orderId' value='" + od.getOrderId() + "'>"
                    +"<input type='hidden' name='username' value='" + od.getUserName() + "'>"
                    +"<input type='hidden' name='productName' value='" + od.getOrderName() + "'>"
                    +"<input type='hidden' name='price' value='" + od.getOrderPrice() + "'>"
                    +"<input type='hidden' name='address' value='" + od.getUserAddress() + "'>"
                    +"<input type='hidden' name='creditCard' value='" + od.getCreditCardNo() + "'>"
                    +"<input type='hidden' name='userType' value='Salesman'>"
                    +"<td><input type='submit' name='Order' value='Cancel' class='btnbuy'></td>"
                    +"<td><input type='submit' name='Order' value='Update' class='btnbuy'></td>"
                    +"</tr>"
                    +"</form>");

                }
            }
            pw.print("</table>");
//        pw.print("</table>");
            pw.print("</h2></div></div></div>");

        utility.printHtml("Footer.html");

    }
}
