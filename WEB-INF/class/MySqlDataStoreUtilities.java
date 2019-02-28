import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class MySqlDataStoreUtilities {
    static Connection conn = null;

    public static void getConnection() {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/csp584?useUnicode=true&characterEncoding=utf8", "root", "dai323697");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static boolean deleteOrder(int orderId) {
        try {

            getConnection();
            String deleteOrderQuery = "Delete from orders where OrderId=?";
            PreparedStatement pst = conn.prepareStatement(deleteOrderQuery);
            pst.setInt(1, orderId);
            pst.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public static void insertOrder(int orderId, String userName, String orderName, double orderPrice, String userAddress, String creditCardNo) {
        try {
            Date current_date = new Date();

            SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            getConnection();
//            String insertIntoCustomerOrderQuery = "INSERT INTO orders (orderId,userName,orderName,orderPrice,userAddress,creditCardNo) " +
//                    "VALUES (?,?,?,?,?,?);";

            String insertIntoCustomerOrderQuery = "insert into orders (orderID, userName, orderName, orderPrice, userAddress, creditCardNo,orderTime) VALUES (?,?,?,?,?,?,?);";

            PreparedStatement pst = conn.prepareStatement(insertIntoCustomerOrderQuery);
            //set the parameter for each column and execute the prepared statement
            pst.setInt(1, orderId);
            pst.setString(2, userName);
            pst.setString(3, orderName);
            pst.setDouble(4, orderPrice);
            pst.setString(5, userAddress);
            pst.setString(6, creditCardNo);
            pst.setString(7, SimpleDateFormat.format(current_date.getTime()));



            pst.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static HashMap<Integer, ArrayList<OrderPayment>> selectOrder() {

        HashMap<Integer, ArrayList<OrderPayment>> orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();

        try {

            getConnection();
            //select the table
            String selectOrderQuery = "select * from orders";
            PreparedStatement pst = conn.prepareStatement(selectOrderQuery);
            ResultSet rs = pst.executeQuery();
            ArrayList<OrderPayment> orderList = new ArrayList<OrderPayment>();
            while (rs.next()) {
                if (!orderPayments.containsKey(rs.getInt("OrderId"))) {
                    ArrayList<OrderPayment> arr = new ArrayList<OrderPayment>();
                    orderPayments.put(rs.getInt("orderId"), arr);
                }
                ArrayList<OrderPayment> listOrderPayment = orderPayments.get(rs.getInt("OrderId"));
                System.out.println("data is" + rs.getInt("OrderId") + orderPayments.get(rs.getInt("OrderId")));

                //add to orderpayment hashmap
                OrderPayment order = new OrderPayment(rs.getInt("OrderId"), rs.getString("userName"), rs.getString("orderName"), rs.getDouble("orderPrice"), rs.getString("userAddress"), rs.getString("creditCardNo"));
                listOrderPayment.add(order);

            }


        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
        return orderPayments;
    }

    public static HashMap<String, OrderPayment> selectDailyTransaction() {
        HashMap<String, OrderPayment> hm = new HashMap<String, OrderPayment>();
        try {
            getConnection();

            String selectAcc = "SELECT count(orderTime) as soldAmount, orderTime from orders group by orderTime";
            PreparedStatement pst = conn.prepareStatement(selectAcc);
            ResultSet rs = pst.executeQuery();

            int i = 0;
            while (rs.next()) {
                OrderPayment orderPayment = new OrderPayment(rs.getInt("soldAmount"), rs.getDate("orderTime"));
                i++;
                hm.put(String.valueOf(i), orderPayment);
                //orderPayment.setId(rs.getString("Id"));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static ArrayList<OrderPayment> selectDailyTransactionChart() {
        ArrayList<OrderPayment> orderPaymentArrayList = new ArrayList<OrderPayment>();
        try {
            getConnection();

            String selectAcc = "SELECT count(orderTime) as soldAmount, orderTime from orders group by orderTime";
            PreparedStatement pst = conn.prepareStatement(selectAcc);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                OrderPayment orderPayment = new OrderPayment(rs.getInt("soldAmount"), rs.getDate("orderTime"));
                orderPaymentArrayList.add(orderPayment);
            }
        } catch (Exception e) {
        }
        return orderPaymentArrayList;
    }

    public static boolean insertUser(String username, String password, String rePassword, String userType) {
        try {

            getConnection();
            String insertIntoCustomerRegisterQuery = "INSERT INTO user(username,password,repassword,usertype) "
                    + "VALUES (?,?,?,?);";

            PreparedStatement pst = conn.prepareStatement(insertIntoCustomerRegisterQuery);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, rePassword);
            pst.setString(4, userType);
            pst.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public static HashMap<String, User> selectUser() {
        HashMap<String, User> hm = new HashMap<String, User>();
        try {
            getConnection();
            Statement stmt = conn.createStatement();
            String selectCustomerQuery = "select * from user";
            ResultSet rs = stmt.executeQuery(selectCustomerQuery);
            while (rs.next()) {
                User user = new User(rs.getString("username"), rs.getString("password"), rs.getString("usertype"));
                hm.put(rs.getString("username"), user);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return hm;
    }

    public static void insertProducts() {
        try {
            getConnection();
            String insertIntoProductQuery = "INSERT INTO Product(productType,productId,productName,productPrice,productImage,productManufacturer,productCondition,productDiscount)" +
                    "VALUES(?,?,?,?,?,?,?,?);";
            for (Map.Entry<String, Console> entry : SaxParserDataStore.consoles.entrySet()) {
                String name = "WearableTech";
                Console cons = entry.getValue();

                PreparedStatement pst = conn.prepareStatement(insertIntoProductQuery);
                pst.setString(1, name);
                pst.setString(2, cons.getId());
                pst.setString(3, cons.getName());
                pst.setDouble(4, cons.getPrice());
                pst.setString(5, cons.getImage());
                pst.setString(6, cons.getRetailer());
                pst.setString(7, cons.getCondition());
                pst.setDouble(8, cons.getDiscount());

                pst.executeUpdate();
            }

            for (Map.Entry<String, Game> entry : SaxParserDataStore.games.entrySet()) {
                String name = "Phone";
                Game gam = entry.getValue();

                PreparedStatement pst = conn.prepareStatement(insertIntoProductQuery);
                pst.setString(1, name);
                pst.setString(2, gam.getId());
                pst.setString(3, gam.getName());
                pst.setDouble(4, gam.getPrice());
                pst.setString(5, gam.getImage());
                pst.setString(6, gam.getRetailer());
                pst.setString(7, gam.getCondition());
                pst.setDouble(8, gam.getDiscount());

                pst.executeUpdate();

            }

            for (Map.Entry<String, Tablet> entry : SaxParserDataStore.tablets.entrySet()) {
                String name = "Laptop";
                Tablet tab = entry.getValue();

                PreparedStatement pst = conn.prepareStatement(insertIntoProductQuery);
                pst.setString(1, name);
                pst.setString(2, tab.getId());
                pst.setString(3, tab.getName());
                pst.setDouble(4, tab.getPrice());
                pst.setString(5, tab.getImage());
                pst.setString(6, tab.getRetailer());
                pst.setString(7, tab.getCondition());
                pst.setDouble(8, tab.getDiscount());

                pst.executeUpdate();

            }

            for (Map.Entry<String, Speaker> entry : SaxParserDataStore.speakers.entrySet()) {
                String name = "speaker";
                Speaker spk = entry.getValue();

                PreparedStatement pst = conn.prepareStatement(insertIntoProductQuery);
                pst.setString(1, name);
                pst.setString(2, spk.getId());
                pst.setString(3, spk.getName());
                pst.setDouble(4, spk.getPrice());
                pst.setString(5, spk.getImage());
                pst.setString(6, spk.getRetailer());
                pst.setString(7, spk.getCondition());
                pst.setDouble(8, spk.getDiscount());

                pst.executeUpdate();

            }

            for (Map.Entry<String, Accessory> entry : SaxParserDataStore.accessories.entrySet()) {
                String name = "Accessory";
                Accessory acc = entry.getValue();

                PreparedStatement pst = conn.prepareStatement(insertIntoProductQuery);
                pst.setString(1, name);
                pst.setString(2, acc.getId());
                pst.setString(3, acc.getName());
                pst.setDouble(4, acc.getPrice());
                pst.setString(5, acc.getImage());
                pst.setString(6, acc.getRetailer());
                pst.setString(7, acc.getCondition());
                pst.setDouble(8, acc.getDiscount());

                pst.executeUpdate();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, Console> getConsoles() {
        HashMap<String, Console> hm = new HashMap<String, Console>();
        try {
            getConnection();

            String selectConsoles = "select * from  Product where productType=?";
            PreparedStatement pst = conn.prepareStatement(selectConsoles);
            pst.setString(1, "WearableTech");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Console console = new Console(rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("productId"), console);
                console.setId(rs.getString("productId"));
            }

        } catch (Exception e) {

        }
        return hm;
    }

    public static HashMap<String, Game> getGame() {
        HashMap<String, Game> hm = new HashMap<String, Game>();
        try {
            getConnection();

            String selectGames = "select * from  Product where productType=?";
            PreparedStatement pst = conn.prepareStatement(selectGames);
            pst.setString(1, "Phone");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Game game = new Game(rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("productId"), game);
                game.setId(rs.getString("productId"));
            }

        } catch (Exception e) {

        }
        return hm;
    }

    public static HashMap<String, Tablet> getTablet() {
        HashMap<String, Tablet> hm = new HashMap<String, Tablet>();
        try {
            getConnection();

            String selectTablet = "select * from  Product where productType=?";
            PreparedStatement pst = conn.prepareStatement(selectTablet);
            pst.setString(1, "Laptop");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Tablet tablet = new Tablet(rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("productId"), tablet);
                tablet.setId(rs.getString("productId"));
            }

        } catch (Exception e) {

        }
        return hm;
    }

    public static HashMap<String, Speaker> getSpeaker() {
        HashMap<String, Speaker> hm = new HashMap<String, Speaker>();
        try {
            getConnection();

            String selectSpeaker = "select * from  Product where productType=?";
            PreparedStatement pst = conn.prepareStatement(selectSpeaker);
            pst.setString(1, "Speaker");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Speaker speaker = new Speaker(rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("productId"), speaker);
                speaker.setId(rs.getString("productId"));
            }

        } catch (Exception e) {

        }
        return hm;
    }

    public static HashMap<String, Accessory> getAccessory() {
        HashMap<String, Accessory> hm = new HashMap<String, Accessory>();
        try {
            getConnection();

            String selectAccessory = "select * from  Product where productType=?";
            PreparedStatement pst = conn.prepareStatement(selectAccessory);
            pst.setString(1, "Accessory");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Accessory accessory = new Accessory(rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("productId"), accessory);
                accessory.setId(rs.getString("productId"));
            }

        } catch (Exception e) {

        }
        return hm;
    }

    public static String addproducts(String productType,String productId,String productName,double productPrice,String productImage,String productManufacturer,String productCondition,double productDiscount,String prod)
    {
       String mgs="Product has been added";
       try {
           getConnection();
           String addpproductQuery ="INSERT INTO Product(productType,productId,productName,productPrice,productImage,productManufacturer,productCondition,productDiscount)"+
                    "VALUES(?,?,?,?,?,?,?,?);";

           String name =productType;
           PreparedStatement pst = conn.prepareStatement(addpproductQuery);
           pst.setString(1, name);
           pst.setString(2, productId);
           pst.setString(3, productName);
           pst.setDouble(4, productPrice);
           pst.setString(5, productImage);
           pst.setString(6, productManufacturer);
           pst.setString(7, productCondition);
           pst.setDouble(8, productDiscount);
           pst.executeUpdate();
            try {
                if (!prod.isEmpty()){
                    String addaprodacc = "INSERT INTO  Product_accessories(productName,accessoriesName)" +
                            "VALUES (?,?);";
                    PreparedStatement pstd = conn.prepareStatement(addaprodacc);
                    pstd.setString(1,prod);
                    pstd.setString(2,productId);
                    pstd.executeUpdate();
                }
            }catch (Exception e){
                mgs ="Error to add the products";
                e.printStackTrace();
            }
       }catch (Exception e){
           mgs ="Error to add the products";
           e.printStackTrace();

       }
       return mgs;
    }

    public static String updateproducts(String productType, String productId, String productName, double productPrice, String productImage, String productManufacturer, String productCondition, double productDiscount) {
        String mgs = "Product has been updated ";
        try {

            getConnection();
            String updateProductQuery = "UPDATE Product SET productName=?,productPrice=?,productImage=?,productManufacturer=?,productCondition=?,productDiscount=? where productId =?;";


            PreparedStatement pst = conn.prepareStatement(updateProductQuery);

            pst.setString(1, productName);
            pst.setDouble(2, productPrice);
            pst.setString(3, productImage);
            pst.setString(4, productManufacturer);
            pst.setString(5, productCondition);
            pst.setDouble(6, productDiscount);
            pst.setString(7, productId);
            pst.executeUpdate();


        } catch (Exception e) {
            mgs ="Error to update the products";
            e.printStackTrace();

        }
        return mgs;
    }

    public static String deleteproducts(String productId) {
        String msg = "Product has been deleted";
        try {

            getConnection();
            String deleteproductsQuery = "Delete from Product where productId=?";
            PreparedStatement pst = conn.prepareStatement(deleteproductsQuery);
            pst.setString(1, productId);

            pst.executeUpdate();
        } catch (Exception e) {
            msg = "Error to delete the products";
        }
        return msg;
    }

    public static HashMap<String, Product> selectInventory() {
        HashMap<String, Product> hm = new HashMap<String, Product>();
        try {
            getConnection();

            String selectAcc = "select * from Product";
            PreparedStatement pst = conn.prepareStatement(selectAcc);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Product product = new Product(rs.getString("productName"), rs.getDouble("productPrice"), Integer.parseInt(rs.getString("inventory")));
                hm.put(rs.getString("productId"), product);
                product.setId(rs.getString("productId"));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static HashMap<String,Product>selectOnSale(){
        HashMap<String,Product>hm=new HashMap<>();
        try {
            getConnection();
            String selectOnsale="select * from Product where productCondition= ?";
            PreparedStatement pst = conn.prepareStatement(selectOnsale);
            pst.setString(1,"1");
            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                Product product = new Product(rs.getString("productName"), rs.getDouble("productPrice"), Integer.parseInt(rs.getString("inventory")));
                hm.put(rs.getString("productId"),product);
                product.setId(rs.getString("productId"));
            }

        }catch (Exception e){

        }
        return hm;
    }

    public static HashMap<String, Product> selectRebate() {
        HashMap<String, Product> hm = new HashMap<String, Product>();
        try {
            getConnection();

            String selectonrebate = "select * from Product where productDiscount > 0";
            PreparedStatement pst = conn.prepareStatement(selectonrebate);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Product product = new Product(rs.getString("productName"), rs.getDouble("productPrice"), Double.parseDouble(rs.getString("productDiscount")));
                hm.put(rs.getString("productId"), product);
                product.setId(rs.getString("productId"));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static HashMap<String, OrderPayment> selectSaleAmount() {
        HashMap<String, OrderPayment> hm = new HashMap<String, OrderPayment>();
        try {
            getConnection();

            String selectasSaleAmount = "select DISTINCT(temp.orderName),temp.saleAmount,orders.orderPrice from orders, (select orderName, count(orderName) as saleAmount from orders group by orderName) as temp where orders.orderName = temp.orderName";
         //   System.out.println(selectasSaleAmount);
            PreparedStatement pst = conn.prepareStatement(selectasSaleAmount);
            ResultSet rs = pst.executeQuery();

            int i = 0;
            while (rs.next()) {
                OrderPayment orderPayment = new OrderPayment(rs.getString("orderName"), rs.getDouble("orderPrice"), rs.getInt("saleAmount"));
                i++;
                hm.put(String.valueOf(i), orderPayment);
                //orderPayment.setOrderId(Integer.parseInt(rs.getString("Id")));
            }
        } catch (Exception e) {
        }
        return hm;
    }

    public static HashMap<String, Product> getData() {
        HashMap<String, Product> hm = new HashMap<String, Product>();
        try {
            getConnection();
            Statement stmt = conn.createStatement();
            String selectCustomerQuery = "select * from  Product";
            ResultSet rs = stmt.executeQuery(selectCustomerQuery);
            while (rs.next()) {
                Product p = new Product(rs.getString("productId"), rs.getString("productName"), rs.getDouble("productPrice"), rs.getString("productImage"), rs.getString("productManufacturer"), rs.getString("productCondition"), rs.getString("ProductType"), rs.getDouble("productDiscount"));
                hm.put(rs.getString("productId"), p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hm;
    }

}