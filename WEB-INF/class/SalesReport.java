import com.sun.tools.corba.se.idl.constExpr.Or;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@WebServlet("/SalesReport")
public class SalesReport extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        displaySalesReport(request, response, pw);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        Utilities utility = new Utilities(request, pw);


        HashMap<String, User> hm = new HashMap<String, User>();
        String TOMCAT_HOME = System.getProperty("catalina.home");

        //get the user details from file
        try {
            hm = MySqlDataStoreUtilities.selectUser();
        } catch (Exception e) {

        }
    }

    private void displaySalesReport(HttpServletRequest request,
                                    HttpServletResponse response, PrintWriter pw)
            throws ServletException, IOException {

        Utilities utility = new Utilities(request, pw);
        utility.printHtml("Header.html");
//        utility.printHtml("LeftNavigationBar.html");
        pw.print( "<h3><Hello,  StoreManager" + "</h3>"
                +"<li><a href='StoreManager'><span class='glyphicon'>ViewProduct</span></a></li>"
                +"<li><a href='DataVisualization'><span class='glyphicon'>DataVisualization</span></a></li>"
                +"<li><a href='DataAnalytics'><span class='glyphicon'>DataAnalytics</span></a></li>"
                +"<li><a href='Inventory'><span class='glyphicon'>Inventory</span></a></li>"
                +"<li><a href='SalesReport'><span class='glyphicon'>SalesReport</span></a></li>"
                + "<li><a href='Logout'><span class='glyphicon'>Logout</span></a></li>");

        HashMap<String, OrderPayment> orderPaymentHashMap = new HashMap<String, OrderPayment>();

        //Table of all product sold
        pw.print("<div id='content'>");
        pw.print("<div class='post'>");
        pw.print("<h3 class='title'>");
        pw.print("Product Sold Report");
        pw.print("</h3>");
        pw.print("<div class='entry'>");

        pw.print("<table class='gridtable'>");
        pw.print("<tr>");
        pw.print("<td>Product Name</td>");
        pw.print("<td>Price</td>");
        pw.print("<td>Sold Amount</td>");
        pw.print("</tr>");

        try {
            orderPaymentHashMap = MySqlDataStoreUtilities.selectSaleAmount();
        } catch (Exception ignored) {

        }

        for (OrderPayment orderPayment : orderPaymentHashMap.values()) {

            pw.print("<tr>");
            pw.print("<td>" + orderPayment.getOrderName() + "</td>" +
                    "<td>" + orderPayment.getOrderPrice() + "</td>" +
                    "<td>" + orderPayment.getSaleAmount() + "</td>");
            pw.print("</tr>");

        }
        pw.print("</table></div></div>");


        //Bar Chart of that shows the product names and the total sales for every product
        pw.println("<script type='text/javascript' src=\"https://www.gstatic.com/charts/loader.js\"></script>");
        pw.println("<script type='text/javascript'>");

        // Load the Visualization API and the corechart package.
        pw.println("google.charts.load('current', {'packages':['corechart']});");

        // Set a callback to run when the Google Visualization API is loaded.
        pw.println("google.charts.setOnLoadCallback(drawChart);");

        // Callback that creates and populates a data table,
        // instantiates the pie chart, passes in the data and
        // draws it.
        pw.println("function drawChart() {");

        // Create the data table.
        pw.println("var data = new google.visualization.DataTable();");
        pw.println("data.addColumn('string', 'Product Name');");
        pw.println("data.addColumn('number', 'Sold Amount');");
        pw.println(" data.addRows([");
        for (OrderPayment orderPayment : orderPaymentHashMap.values()) {

            pw.println(" ['" + orderPayment.getOrderName() + "', " + orderPayment.getSaleAmount() + "],");
             /*pw.println(" ['Mushrooms', 3],");
              pw.println("['Onions', 1],");n
            pw.println("  ['Olives', 1],");
            pw.println("  ['Zucchini', 1],");
            pw.println("  ['Pepperoni', 2]  ");*/
        }
        pw.println("]);");
        // Set chart options
        pw.println(" var options = {'title':'Sales Report',");
        pw.println("        'width':800,");
        pw.println("       'height':500};");

        // Instantiate and draw our chart, passing in some options.
        pw.println(" var chart = new google.visualization.BarChart(document.getElementById('chart_div'));");
        pw.println("  chart.draw(data, options);     }");
        pw.println(" </script>");


        /////////</script>


        pw.print("<div id='content'>");
        pw.print("<div class='post'>");
        pw.print("<h3 class='title'>");
        pw.print("Bar Chart of Each Product Sold");
        pw.print("</h3>");
        pw.print("<div class='entry'>");
        pw.println("<div id='chart_div'></div>");
        pw.print("</div></div></div>");

//        pw.print("<table class='gridtable'>");
//        pw.print("<tr>");
//        pw.print("<td>Product Name</td>");
//        pw.print("<td>Price</td>");
//        pw.print("<td>Inventory</td>");
//        pw.print("</tr>");
//
//        pw.print("</table></div></div>");


        //Table of total daily sales transactions
        pw.print("<div id='content'>");
        pw.print("<div class='post'>");
        pw.print("<h3 class='title'>");
        pw.print("Total Daily Sales Transactions");
        pw.print("</h3>");
        pw.print("<div class='entry'>");

        pw.print("<table class='gridtable'>");
        pw.print("<tr>");
        pw.print("<td>Date</td>");
        pw.print("<td>Sold Amount</td>");
        pw.print("</tr>");

        try {
            orderPaymentHashMap = MySqlDataStoreUtilities.selectDailyTransaction();
        } catch (Exception ignored) {

        }

        for (OrderPayment orderPayment : orderPaymentHashMap.values()) {

            String orderTime = orderPayment.getOrderTime().toString().substring(0,10);
            pw.print("<tr>");
            pw.print("<td>" + orderTime + "</td>" +
                    "<td>" + orderPayment.getSaleAmount() + "</td>");
            pw.print("</tr>");

        }
        pw.print("</table></div></div></div>");
    }
}
