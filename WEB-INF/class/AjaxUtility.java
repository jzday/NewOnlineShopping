import java.io.*;

import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;
import java.util.*;
import java.text.*;

import java.sql.*;

import java.io.IOException;
import java.io.*;


public class AjaxUtility {
    StringBuffer sb = new StringBuffer();
    boolean namesAdded = false;
    static Connection conn = null;
    static String message;

    public static String getConnection() {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CSP584?useUnicode=true&characterEncoding=utf-8", "root", "dai323697");
            message = "Successful";
            return message;
        } catch (Exception e) {
            message = "unsuccessful";
            return message;
        }
    }

    public StringBuffer readdata(String searchId) {
        HashMap<String, Product> data;
        data = getData();

        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pi = (Map.Entry) it.next();
            if (pi != null) {
                Product p = (Product) pi.getValue();
                if (p.getName().toLowerCase().startsWith(searchId)) {
                    sb.append("<product>");
                    sb.append("<id>" + p.getId() + "</id>");
                    sb.append("<productName>" + p.getName() + "</productName>");
                    sb.append("</product>");
                }
            }
        }

        return sb;
    }

    public static HashMap<String, Product> getData() {
        HashMap<String, Product> hm = new HashMap<String, Product>();
        try {
            getConnection();

            String selectproduct = "select * from Product";
            PreparedStatement pst = conn.prepareStatement(selectproduct);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Product p = new Product(rs.getString("productId"), rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getString("ProductType"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("productId"), p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hm;
    }

    public static boolean storeData(Map<String, Object> map) {
        try {

            getConnection();
            String insertIntoProductQuery = "INSERT INTO Product(productId,productName,productImage,productManufacturer,productCondition,productPrice,productType,productDiscount) "
                    + "VALUES (?,?,?,?,?,?,?,?);";

            String id = String.valueOf(map.get("productid"));
            String name = String.valueOf(map.get("productname"));
            double price = Double.parseDouble(String.valueOf(map.get("price")));
            String image = String.valueOf(map.get("image"));
            String retailer = String.valueOf(map.get("manufacturer"));
            String productCondition = String.valueOf(map.get("condition"));
            double discount = Double.parseDouble(String.valueOf(map.get("discount")));
            String catalog = String.valueOf(map.get("productCatalog"));

            int condition = 0;

            switch (productCondition) {
                case "New":
                    condition = 0;
                    break;
                case "Used":
                    condition = 1;
                    break;
                case "Refurbished":
                    condition = 2;
                    break;

            }

            PreparedStatement pst = conn.prepareStatement(insertIntoProductQuery);
            //set the parameter for each column and execute the prepared statement
            pst.setString(1, id);
            pst.setString(2, name);
            pst.setString(3, image);
            pst.setString(4, retailer);
            pst.setInt(5, condition);
            pst.setDouble(6, price);
            pst.setString(7, catalog);
            pst.setDouble(8, discount);
            pst.execute();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteProduct(String productId) {
        try {
            getConnection();
            String deleteProductsQuery = "Delete from Product where productId=?";
            PreparedStatement pst = conn.prepareStatement(deleteProductsQuery);
            pst.setString(1, productId);
            pst.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateProducts(String productType, String productId, String productName, double productPrice, String productImage, String productManufacturer, String productCondition, double productDiscount) {
        try {

            int condition = 0;

            switch (productCondition) {
                case "New":
                    condition = 0;
                    break;
                case "Used":
                    condition = 1;
                    break;
                case "Refurbished":
                    condition = 2;
                    break;

            }

            getConnection();
            String updateProductQuery = "UPDATE Product SET productName=?,productPrice=?,productImage=?,productManufacturer=?,productCondition=?,productDiscount=?, productType=? where productId =?;";


            PreparedStatement pst = conn.prepareStatement(updateProductQuery);

            pst.setString(1, productName);
            pst.setDouble(2, productPrice);
            pst.setString(3, productImage);
            pst.setString(4, productManufacturer);
            pst.setInt(5, condition);
            pst.setDouble(6, productDiscount);
            pst.setString(7, productType);
            pst.setString(8, productId);
            pst.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }


}
