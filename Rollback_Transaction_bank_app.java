package jdbc23;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

            // Hash the entered PIN
            String hashedPin = hashMD5(String.valueOf(pin));

            pstmt1 = con.prepareStatement("select * from account where acc_no = ? and pin = ?");
            pstmt1.setInt(1, acc_no);
            pstmt1.setString(2, hashedPin);
            res1 = pstmt1.executeQuery();

            if (!res1.next()) {
                System.out.println("Invalid account number or PIN.");
                return;
            }

            String acc_name = res1.getString(2);
            int balance = res1.getInt(4);
            System.out.println("Welcome " + acc_name);
            System.out.println("Available balance is: " + balance);

            con.setAutoCommit(false);
            s = con.setSavepoint();
            System.out.println("<---Transfer Details--->");
            System.out.println("Enter the beneficiary account number:");
            int acc_nu = scan.nextInt();
            System.out.println("Enter the transfer amount:");
            int t_amount = scan.nextInt();

            // Debit from the sender's account
            pstmt2 = con.prepareStatement("update account set balance = balance - ? where acc_no = ?");
            pstmt2.setInt(1, t_amount);
            pstmt2.setInt(2, acc_no);
            int rowsAffected = pstmt2.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Failed to debit amount from your account. Transaction aborted.");
                con.rollback(s);
                return;
            }

            // Credit to the beneficiary's account
            pstmt3 = con.prepareStatement("update account set balance = balance + ? where acc_no = ?");
            pstmt3.setInt(1, t_amount);
            pstmt3.setInt(2, acc_nu);
            rowsAffected = pstmt3.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Failed to credit amount to beneficiary's account. Rolling back transaction.");
                con.rollback(s);
                return;
            }

            // Confirm the transfer
            System.out.println("<--- Incoming Credit Request --->");
            System.out.println("Account no " + acc_nu + " wants to receive " + t_amount);
            System.out.println("Press yes to confirm");
            System.out.println("Press no to reject");
            String choice = scan.next();

            if (choice.equals("yes")) {
                con.commit();
                System.out.println("Transaction successful!");
            } else {
                con.rollback(s);
                System.out.println("Transaction rejected. Rolling back changes.");
            }

            pstmt4 = con.prepareStatement("select * from account where acc_no = ?");
            pstmt4.setInt(1, acc_no);
            res2 = pstmt4.executeQuery();
            if (res2.next()) {
                System.out.println("Updated balance is: " + res2.getInt(4));
            }

        } catch (SQLException sq) {
            System.out.println("Database error: " + sq.getMessage());
            sq.printStackTrace();
        } catch (ClassNotFoundException cnf) {
            System.out.println("Driver not found: " + cnf.getMessage());
            cnf.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error hashing PIN: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("SORRY Something Went Wrong: " + e.getMessage());
            e.printStackTrace();
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

    // Method to hash a string using MD5
    private static String hashMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
