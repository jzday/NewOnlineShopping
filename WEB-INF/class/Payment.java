import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class Payment extends HttpServlet {
	
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();

		String Customername = "";
		HttpSession session = request.getSession(true);

		Utilities utility = new Utilities(request, pw);
		if(!utility.isLoggedin())
		{
			session.setAttribute("login_msg", "Please Login to Pay");
			response.sendRedirect("Login");
			return;
		}
		// get the payment details like credit card no address processed from cart servlet	

		//String username = request.getParameter("")
		String userAddress=request.getParameter("userAddress");
		String creditCardNo=request.getParameter("creditCardNo");
	//	System.out.print("the user address is" +userAddress);
	//	System.out.print(creditCardNo);
		if(!userAddress.isEmpty() && !creditCardNo.isEmpty() )
		{
			Random rand = new Random();
			int orderId = rand.nextInt(700000);
			//int orderId=utility.getOrderPaymentSize()+1;

			//iterate through each order

			for (OrderItem oi : utility.getCustomerOrders())
			{

				//set the parameter for each column and execute the prepared statement

				utility.storePayment(orderId,oi.getName(),oi.getPrice(),userAddress,creditCardNo,Customername);
			}


			//remove the order details from cart after processing


			OrdersHashMap.orders.remove(utility.username());

			Date now = new Date();
			Date end = new Date();
			Date canceldate = new Date();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String today = format.format(now);

			Calendar cl = Calendar.getInstance();
			Calendar cn = Calendar.getInstance();

			cl.add(Calendar.DATE,14);
			cn.add(Calendar.DATE,9);

			end = cl.getTime();
			canceldate = cn.getTime();
			String enddate = format.format(end);
			String cancelDate = format.format(canceldate);


			utility.printHtml("Header.html");
			utility.printHtml("LeftNavigationBar.html");
			pw.print("<div id='content'><div class='post'><h2 class='title meta'>");
			pw.print("<a style='font-size: 24px;'>Order</a>");
			pw.print("</h2><div class='entry'>");
		
			pw.print("<h2>Your Order");
			pw.print("&nbsp&nbsp");  
			pw.print("has been stored ");
			pw.print("<h3>Hello"
					+"<br>Your Order No is :"+(orderId)
					+"<br>Your order date is :"+(today)
					+"<br>Your Order deliver date is :"+(enddate)
					+"<br>You can cancel your order before:" + (cancelDate));
			pw.print("</h2></div></div></div>");
			utility.printHtml("Footer.html");
		}else
		{
			utility.printHtml("Header.html");
			utility.printHtml("LeftNavigationBar.html");
			pw.print("<div id='content'><div class='post'><h2 class='title meta'>");
			pw.print("<a style='font-size: 24px;'>Order</a>");
			pw.print("</h2><div class='entry'>");
		
			pw.print("<h4 style='color:red'>Please enter valid address and creditcard number</h4>");
			pw.print("</h2></div></div></div>");		
			utility.printHtml("Footer.html");
		}	
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		Utilities utility = new Utilities(request, pw);
		
		
	}
}
