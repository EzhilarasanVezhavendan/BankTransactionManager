# BankTransactionManager
created a java Bank Transaction Manager with Rollback and Recovery
  Here is the full code explanation 

# Rollback Transaction Bank Application

## Description

- The Rollback Transaction Bank Application is a Java console application.
- Simulates a banking transaction system with rollback capabilities.
- Allows fund transfer between accounts with rollback on failure or rejection.
- Handles incorrect PIN and account number entries with appropriate feedback.

## Features

- User login with account number and PIN.
- Transfer funds between accounts.
- Rollback transactions if transfer is rejected or errors occur.
- View updated balances after transactions.

## Requirements

- Java Development Kit (JDK) 8 or higher.
- MySQL database.
- MySQL JDBC Driver.

## Setup Instructions

1. **Install MySQL:**
   - Ensure MySQL is installed and running.
   - Create a database named `jdbc`.

2. **Create Database Schema:**
   - Run the following SQL script to set up the `account` table:

    sql
     CREATE TABLE account (
         acc_no INT PRIMARY KEY,
         acc_name VARCHAR(100),
         pin INT,
         balance DECIMAL(10, 2)
     );
    

   - Insert sample data:

    sql
     INSERT INTO account (acc_no, acc_name, pin, balance) VALUES
     (1001, 'John Doe', 1234, 1000.00),
     (1002, 'Jane Smith', 5678, 2000.00);
    

3. **Configure Database Connection:**
   - Update the connection details in `Rollback_Transaction_bank_app`:

    java
     String url = "jdbc:mysql://localhost:3306/jdbc";
     String un = "root";
     String pwd = "12345";
    

4. **Add MySQL JDBC Driver to Classpath:**
   - Download MySQL Connector/J from the [official MySQL website](https://dev.mysql.com/downloads/connector/j/).
   - Add the JAR file to your project's classpath.

5. **Compile and Run:**
   - Compile the Java program:

     
     javac -cp .;mysql-connector-java-8.x.x.jar jdbc23/Rollback_Transaction_bank_app.java
    

   - Run the Java program:

     
     java -cp .;mysql-connector-java-8.x.x.jar jdbc23.Rollback_Transaction_bank_app
    

## Usage

- **Start the Application:**
  - Run the application from your command line or IDE.

- **Login:**
  - Enter your account number and PIN when prompted.

- **Transfer Funds:**
  - Provide beneficiary account number and amount to transfer.
  - Confirm or reject the transaction as prompted.

- **Check Balances:**
  - Application displays the updated balance after a successful transaction or rollback.

## Troubleshooting

- **Invalid Account Number or PIN:**
  - Verify correct account number and PIN.
  - Ensure the account exists in the database.

- **Database Errors:**
  - Check database connection settings.
  - Ensure MySQL server is running.

- **JDBC Driver Issues:**
  - Verify MySQL JDBC driver JAR file is included in classpath.

## Contact

- For issues or suggestions, please contact ezhilarasanvezhavendan@gmail.com.
