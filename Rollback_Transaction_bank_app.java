import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Scanner;

public class Rollback_Transaction_bank_app {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/jdbc";
        String un = "root";
        String pwd = "12345";
        Connection con = null;
        Scanner scan = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        PreparedStatement pstmt4 = null;
        PreparedStatement pstmt5 = null;
        ResultSet res1 = null;
        ResultSet res2 = null;
        Savepoint s = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, un, pwd);
            scan = new Scanner(System.in);
            System.out.println("<---Welcome to Tap Bank--->");
            System.out.println("Enter Account Number:");
            int acc_no = scan.nextInt();
            System.out.println("Enter Pin:");
            int pin = scan.nextInt();
            pstmt1 = con.prepareStatement("select * from account where acc_no = ? and pin = ?");
            pstmt1.setInt(1, acc_no);
            pstmt1.setInt(2, pin);
            res1 = pstmt1.executeQuery();
            res1.next();
            String acc_name = res1.getString(2);
            int balance = res1.getInt(4);
            System.out.println("Welcome " + acc_name);
            System.out.println("Available balance is: " + balance)
            con.setAutoCommit(false);
            s = con.setSavepoint();
            System.out.println("<---Transfer Details--->");
            System.out.println("Enter the beneficiary account number:");
            int acc_nu = scan.nextInt();
            System.out.println("Enter the transfer amount");
            int t_amount = scan.nextInt();
            pstmt2 = con.prepareStatement("update account set balance = balance - ? where acc_no = ?");
            pstmt2.setInt(1, t_amount);
            pstmt2.setInt(2, acc_nu);
            pstmt2.executeUpdate();
            System.out.println("<--- Incoming Credit Request --->");
            System.out.println(acc_name + " account no " + acc_no + " wants Transfer " + t_amount);
            System.out.println("Press yes to receive");
            System.out.println("Press no to reject");
            String choice = scan.next();
            if (choice.equals("yes")) {
                pstmt3 = con.prepareStatement("update account set balance = balance + ? where acc_no = ?");
                pstmt3.setInt(1, t_amount);
                pstmt3.setInt(2, acc_no);
                pstmt3.executeUpdate();
                pstmt4 = con.prepareStatement("select * from account where acc_no = ?");
                pstmt4.setInt(1, acc_no);
                res2 = pstmt4.executeQuery();
                res2.next();
                System.out.println("Updated balance is: " + res2.getInt(4));
            } else {
                con.rollback(s);
                pstmt5 = con.prepareStatement("select * from account where acc_no= ?");
                pstmt5.setInt(1, acc_nu);
                res2 = pstmt5.executeQuery();
                res2.next();
                System.out.println("Existing balance is: " + res2.getInt(4));
            }
            con.commit();
        } catch (SQLException | ClassNotFoundException sq) {
            sq.printStackTrace();
        } catch (Exception e) {
            System.out.println("SORRY Something Went Wrong!!!1");
        } finally {
            try {
                if (con != null) con.close();
                if (scan != null) scan.close();
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (pstmt3 != null) pstmt3.close();
                if (pstmt4 != null) pstmt4.close();
                if (pstmt5 != null) pstmt5.close();
                if (res1 != null) res1.close();
                if (res2 != null) res2.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception mm) {
                mm.printStackTrace();
            }
        }
    }
}
