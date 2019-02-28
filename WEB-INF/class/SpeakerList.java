import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SpeakerList extends HttpServlet {

    /* Speaker Page Displays all the Speakers and their Information in Game Speed */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        String name = null;
        String CategoryName = request.getParameter("maker");


        /* Checks the Tablets type whether it is microsft or sony or nintendo */

        HashMap<String, Speaker> hm = new HashMap<String, Speaker>();
        if(CategoryName==null){
            hm.putAll(SaxParserDataStore.speakers);
            name = "";
        }
        else
        {
            if(CategoryName.equals("h"))
            {
                for(Map.Entry<String,Speaker> entry : SaxParserDataStore.speakers.entrySet())
                {
                    if(entry.getValue().getRetailer().equals("h"))
                    {
                        hm.put(entry.getValue().getId(),entry.getValue());
                    }
                }
                name = "h";
            }
            else if(CategoryName.equals("ee"))
            {
                for(Map.Entry<String,Speaker> entry : SaxParserDataStore.speakers.entrySet())
                {
                    if(entry.getValue().getRetailer().equals("ee"))
                    {
                        hm.put(entry.getValue().getId(),entry.getValue());
                    }
                }
                name = "ee";
            }
            else if(CategoryName.equals("cc"))
            {
                for(Map.Entry<String,Speaker> entry : SaxParserDataStore.speakers.entrySet())
                {
                    if(entry.getValue().getRetailer().equals("cc"))
                    {
                        hm.put(entry.getValue().getId(),entry.getValue());
                    }
                }
                name = "cc";
            }
            else if(CategoryName.equals("dd"))
            {
                for(Map.Entry<String,Speaker> entry : SaxParserDataStore.speakers.entrySet())
                {
                    if(entry.getValue().getRetailer().equals("dd"))
                    {
                        hm.put(entry.getValue().getId(),entry.getValue());
                    }
                }
                name = "dd";
            }
            else if(CategoryName.equals("eee"))
            {
                for(Map.Entry<String,Speaker> entry : SaxParserDataStore.speakers.entrySet())
                {
                    if(entry.getValue().getRetailer().equals("eee"))
                    {
                        hm.put(entry.getValue().getId(),entry.getValue());
                    }
                }
                name = "eee";
            }
        }

		
		/* Header, Left Navigation Bar are Printed.

		All the Speaker and Speaker information are dispalyed in the Content Section

		and then Footer is Printed*/

        Utilities utility = new Utilities(request,pw);
        utility.printHtml("Header.html");
        utility.printHtml("LeftNavigationBar.html");
        pw.print("<div id='content'><div class='post'><h2 class='title meta'>");
        pw.print("<a style='font-size: 24px;'>"+name+" Speakers</a>");
        pw.print("</h2><div class='entry'><table id='bestseller'>");
        int i = 1; int size= 2;
        for(Map.Entry<String, Speaker> entry : hm.entrySet())
        {
            Speaker speaker = entry.getValue();
            if(i%2==1) pw.print("<tr>");
            pw.print("<td><div id='shop_item'>");
            pw.print("<h3>"+speaker.getName()+"</h3>");
            pw.print("<strong>$"+speaker.getPrice()+"</strong><ul>");
            pw.print("<li id='item'><img src='images/speaker_folder/"+speaker.getImage()+"' alt='' /></li>");

            pw.print("<li><form method='post' action='Cart'>" +
                    "<input type='hidden' name='name' value='"+entry.getKey()+"'>"+
                    "<input type='hidden' name='type' value='speakers'>"+
                    "<input type='hidden' name='maker' value='"+CategoryName+"'>"+
                    "<input type='hidden' name='access' value=''>"+
                    "<input type='submit' class='btnbuy' value='Buy Now'></form></li>");

            pw.print("<li><form method='post' action='WriteReview'>"+"<input type='hidden' name='name' value='"+entry.getKey()+"'>"+
                    "<input type='hidden' name='type' value='speakers'>"+
                    "<input type='hidden' name='maker' value='"+speaker.getRetailer()+"'>"+
                    "<input type='hidden' name='access' value=''>"+
                    "<input type='hidden' name='price' value='"+speaker.getPrice()+"'>" +
                    "<input type='submit' value='WriteReview' class='btnreview'></form></li>");

            pw.print("<li><form method='post' action='ViewReview'>"+"<input type='hidden' name='name' value='"+entry.getKey()+"'>"+
                    "<input type='hidden' name='type' value='speakers'>"+
                    "<input type='hidden' name='maker' value='"+speaker.getRetailer()+"'>"+
                    "<input type='hidden' name='access' value=''>"+
                    "<input type='submit' value='ViewReview' class='btnreview'></form></li>");
            pw.print("</ul></div></td>");
            if(i%2==0 || i == size) pw.print("</tr>");
            i++;
        }
        pw.print("</table></div></div></div>");

        utility.printHtml("Footer.html");

    }
}
