import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ElectronicsShop {

    // Database Connection Method
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/electronics_shop";
        String user = "root";
        String password = "Aayush@16"; // Update with your database password
        return DriverManager.getConnection(url, user, password);
    }

    // Main method to start the application
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "MySQL Driver not found!");
            return;
        }
        new LoginFrame(); // Open the login screen first
    }

    // ---------- Login Frame ----------
    static class LoginFrame extends JFrame {
        JTextField usernameField;
        JPasswordField passwordField;

        LoginFrame() {
            setTitle("Electronics Shop Login");
            setSize(300, 200);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new GridLayout(4, 1));

            usernameField = new JTextField();
            passwordField = new JPasswordField();
            JButton loginBtn = new JButton("Login");

            add(new JLabel("Username:"));
            add(usernameField);
            add(new JLabel("Password:"));
            add(passwordField);
            add(loginBtn);

            loginBtn.addActionListener(e -> authenticate());

            setVisible(true);
        }

        void authenticate() {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = ElectronicsShop.getConnection()) {
                String query = "SELECT * FROM users WHERE username=? AND password=?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    dispose();
                    new DashboardFrame(); // Open the dashboard after successful login
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        }
    }

    // ---------- Dashboard Frame ----------
    static class DashboardFrame extends JFrame {
        DashboardFrame() {
            setTitle("Dashboard - Electronics Shop");
            setSize(600, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new GridLayout(10, 1)); // Increased grid rows for all buttons

            // Buttons for the dashboard
            JButton addProductBtn = new JButton("Add Product");
            JButton viewProductBtn = new JButton("View Products");
            JButton manageStockBtn = new JButton("Manage Stock");
            JButton generateInvoiceBtn = new JButton("Generate Invoice");
            JButton staffManagementBtn = new JButton("Staff Management");
            JButton customerDetailsBtn = new JButton("Customer Details");
            JButton salesRecordsBtn = new JButton("Sales Records");
            JButton reportBtn = new JButton("Generate Reports");

            // Add buttons to the frame
            add(addProductBtn);
            add(viewProductBtn);
            add(manageStockBtn);
            add(generateInvoiceBtn);
            add(staffManagementBtn);
            add(customerDetailsBtn);
            add(salesRecordsBtn);
            add(reportBtn);

            // ActionListeners for buttons
            addProductBtn.addActionListener(e -> new AddProductFrame());
            viewProductBtn.addActionListener(e -> new ViewProductFrame());
            manageStockBtn.addActionListener(e -> new ManageStockFrame());
            generateInvoiceBtn.addActionListener(e -> new BillingFrame());
            staffManagementBtn.addActionListener(e -> new StaffManagementFrame());
            customerDetailsBtn.addActionListener(e -> new CustomerDetailsFrame());
            salesRecordsBtn.addActionListener(e -> new SalesRecordsFrame());
            reportBtn.addActionListener(e -> new ReportFrame());

            setVisible(true);
        }
    }

    // ---------- Add Product ----------
    static class AddProductFrame extends JFrame {
        JTextField nameField, priceField, qtyField;

        AddProductFrame() {
            setTitle("Add Product");
            setSize(300, 250);
            setLocationRelativeTo(null);
            setLayout(new GridLayout(5, 2));

            nameField = new JTextField();
            priceField = new JTextField();
            qtyField = new JTextField();
            JButton addBtn = new JButton("Add");

            add(new JLabel("Name:"));
            add(nameField);
            add(new JLabel("Price:"));
            add(priceField);
            add(new JLabel("Quantity:"));
            add(qtyField);
            add(new JLabel(""));
            add(addBtn);

            addBtn.addActionListener(e -> addProduct());

            setVisible(true);
        }

        void addProduct() {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int qty = Integer.parseInt(qtyField.getText());

            try (Connection conn = ElectronicsShop.getConnection()) {
                String query = "INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, qty);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Product added!");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // ---------- Billing Frame ----------
    static class BillingFrame extends JFrame {
        JTextField productField, quantityField, priceField, totalField;
        JButton generateInvoiceBtn;

        BillingFrame() {
            setTitle("Billing - Electronics Shop");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setLayout(new GridLayout(5, 2));

            productField = new JTextField();
            quantityField = new JTextField();
            priceField = new JTextField();
            totalField = new JTextField();
            totalField.setEditable(false);
            generateInvoiceBtn = new JButton("Generate Invoice");

            add(new JLabel("Product:"));
            add(productField);
            add(new JLabel("Quantity:"));
            add(quantityField);
            add(new JLabel("Price:"));
            add(priceField);
            add(new JLabel("Total:"));
            add(totalField);
            add(generateInvoiceBtn);

            generateInvoiceBtn.addActionListener(e -> generateInvoice());

            setVisible(true);
        }

        void generateInvoice() {
            String product = productField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            double total = quantity * price;

            totalField.setText(String.valueOf(total));

            try (Connection conn = ElectronicsShop.getConnection()) {
                String query = "INSERT INTO sales (product_name, quantity, total) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, product);
                stmt.setInt(2, quantity);
                stmt.setDouble(3, total);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Invoice Generated!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error generating invoice: " + ex.getMessage());
            }
        }
    }

    // ---------- Sales Records ----------
    static class SalesRecordsFrame extends JFrame {
        SalesRecordsFrame() {
            setTitle("Sales Records");
            setSize(600, 400);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            String[] columns = {"ID", "Product", "Quantity", "Total", "Sale Date"};
            String[][] data = new String[50][5];

            try (Connection conn = ElectronicsShop.getConnection()) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM sales");

                int i = 0;
                while (rs.next()) {
                    data[i][0] = String.valueOf(rs.getInt("id"));
                    data[i][1] = rs.getString("product_name");
                    data[i][2] = String.valueOf(rs.getInt("quantity"));
                    data[i][3] = String.valueOf(rs.getDouble("total"));
                    data[i][4] = rs.getDate("sale_date").toString();
                    i++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error fetching records.");
            }

            JTable table = new JTable(data, columns);
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            setVisible(true);
        }
    }

    // ---------- Report Frame ----------
    static class ReportFrame extends JFrame {
        ReportFrame() {
            setTitle("Sales Reports");
            setSize(600, 400);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JButton generateReportBtn = new JButton("Generate Report");
            add(generateReportBtn, BorderLayout.SOUTH);

            generateReportBtn.addActionListener(e -> generateReport());

            setVisible(true);
        }

        void generateReport() {
            // Logic to generate reports like monthly/annual sales or revenue
            try (Connection conn = getConnection()) {
                String sql = "SELECT MONTH(sale_date) AS month, SUM(total) AS total_sales FROM sales WHERE YEAR(sale_date) = ? GROUP BY MONTH(sale_date)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, java.time.LocalDate.now().getYear());
                ResultSet rs = ps.executeQuery();

                StringBuilder report = new StringBuilder();
                report.append("Monthly Sales Report - ").append(java.time.LocalDate.now().getYear()).append("\n\n");

                while (rs.next()) {
                    int month = rs.getInt("month");
                    double totalSales = rs.getDouble("total_sales");
                    report.append("Month ").append(month).append(": $").append(totalSales).append("\n");
                }

                JOptionPane.showMessageDialog(this, report.toString(), "Sales Report", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
            }
        }
    }
}
