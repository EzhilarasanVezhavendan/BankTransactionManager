# BankTransactionManager
created a java Bank Transaction Manager with Rollback and Recovery
  Here is the full code explanation 



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
			// Login Module
			System.out.println("<---Welcome to Tap Bank--->");
			System.out.println("Enter Account Number:");
			int acc_no = scan.nextInt();
			System.out.println("Enter Pin:");
			int pin = scan.nextInt();
			pstmt1 = con.prepareStatement("select * from account where acc_no = ? and pin = ? ");
			pstmt1.setInt(1, acc_no);// getting value for the query strings
			pstmt1.setInt(2, pin);
			res1 = pstmt1.executeQuery();
			// execute Query() is used for to retrieve the meta data or retrieved data so only
			// it is stored in the result set
			// execute Update() is used for updating insertion and deletion process
			res1.next();
			String acc_name = res1.getString(2);
			int balance = res1.getInt(4);
			System.out.println("Welcome " + acc_name);
			System.out.println("Available balance is: " + balance);
			// 1st part
			con.setAutoCommit(false);
			// setAutoCommit(false); because if this line is not given means automatically
			// the query will executed and updated this
			// method makes the query stop updating in the rom or database but the query
			// will be get executed in the Ram and updating in the database will be done
			// only
			// when until the con.commit(); is called here the all query will get's
			// committed only after the else block
			s = con.setSavepoint();
			/*
			 * even though we give the setAutoCommit(false); before giving the yes or no is
			 * given the amount will be get detected
			 * from transferring account if we are yes means it's not an problem but even if
			 * we give the no the
			 * amount the transferring amount will get detected from the transferring
			 * account
			 * in real time scenario we call it as refund if amount is detected wrongly
			 * means that money will be refunded
			 * but here it will not happen
			 * so the savepoint() is an starting point if that particular block saved to be
			 * back to it's orginal position means
			 * the rollback(reference_of_savepoint) should be given the query everything
			 * given after this savepoint and before
			 * rollback will be get back to it;s orginal position
			 */
			System.out.println("<---Transfer Details--->");
			System.out.println("Enter the beneficiary account number:");
			int acc_nu = scan.nextInt();// account number of an person where from this account the amount will be
										// transferred
			System.out.println("Enter the transfer amount");
			int t_amount = scan.nextInt();
			pstmt2 = con.prepareStatement("update account set balance = balance - ? where acc_no = ? ");
			// the acc_no will be act as an primary key by using the where clause the
			// balance from the transferring account will be get reduced and
			// that reduced value will be updated using the PreparedStatement
			// ref=ref.execute update
			pstmt2.setInt(1, t_amount);// this values will be set for the above query strings or for the incomplete
										// query
			pstmt2.setInt(2, acc_nu);
			pstmt2.executeUpdate();
			System.out.println("<--- Incoming Credit Request --->");
			System.out.println(acc_name + " account no " + acc_no + "wants Transfer " + t_amount);
			System.out.println("Press yes to receive");
			System.out.println("Press no to reject");
			String choice = scan.next();
			if (choice.equals("yes")) {
				pstmt3 = con.prepareStatement("update account set balance = balance + ? where acc_no = ?");
				pstmt3.setInt(1, t_amount);
				pstmt3.setInt(2, acc_no);
				pstmt3.executeUpdate();
				pstmt4 = con.prepareStatement("select * from account where acc_no = ? ");
				pstmt4.setInt(1, acc_no);
				res2 = pstmt4.executeQuery();
				res2.next();

				System.out.println("Updated balance is: " + res2.getInt(4));
			} else {
				con.rollback(s);// here the transaction is not taking place so from the money request to
								// beneficiary account query to till the updating of balance
				// query in the both the accounts all that query is not been required to execute
				// som we are getting back
				// all the queries from that save point
				/*
				 * Reason for this Rollback is even though if we give the no to not transfer the
				 * amount the amount from the beneficiary
				 * account is already been detected so even though the transfer is takes place
				 * or not the amount reduction from the beneficiary account will be take
				 * places to avoid this "LOSS OF DATA " we are using the "ROLLBACK() COMMAND"
				 * here
				 */
				pstmt5 = con.prepareStatement("select * from account where acc_no= ? ");
				pstmt5.setInt(1, acc_nu);
				res2 = pstmt5.executeQuery();
				res2.next();
				System.out.println("Existing balance is: " + res2.getInt(4));
			}
			con.commit();// after the else block only all the given query will be takes places
		} catch (SQLException | ClassNotFoundException sq) {
			sq.printStackTrace();
		} catch (Exception e) {
			System.out.println("SORRY Something Went Wrong!!!1");
		} finally {

			try {// closing all the opened resources for the security purposes
				con.close();
				scan.close();
				pstmt1.close();
				pstmt2.close();
				pstmt3.close();
				res1.close();
				res2.close();

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception mm) {
				mm.printStackTrace();
			}
		}
	}

}

