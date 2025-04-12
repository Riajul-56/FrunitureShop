import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.Timer;

public class POS extends JFrame {
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel, clockLabel;
    private double total = 0.0;
    private boolean isLoggedIn = false;
    private String currentUser = "";
    private String currentUserRole = "";
    private boolean darkMode = false;

    private final Map<String, String> users = new HashMap<>();
    private final Map<String, String> roles = new HashMap<>();

    public POS() {
        loadUserData();
        showLoginUI();
    }

    private void showLoginUI() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(400, 250);
        loginFrame.setLayout(new GridLayout(5, 2, 10, 10));
        loginFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        loginFrame.getContentPane().setBackground(new Color(153, 153, 53));

        JLabel userLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login / Register");
        loginButton.setBackground(new Color(0, 0, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(153, 153, 153), 2));

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Please enter username fields.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Please enter password fields.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String hashedPassword = hashPassword(password);

            if (users.containsKey(username)) {
                if (users.get(username).equals(hashedPassword)) {
                    isLoggedIn = true;
                    currentUser = username;
                    currentUserRole = roles.get(username);
                    loginFrame.dispose();
                    initializeMainUI();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Incorrect password!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                String role = JOptionPane.showInputDialog(loginFrame, "Enter role (admin/customer):");
                if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("customer")) {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid role. Defaulting to 'customer'.");
                    role = "customer";
                }
                users.put(username, hashedPassword);
                roles.put(username, role.toLowerCase());
                saveUserData();
                isLoggedIn = true;
                currentUser = username;
                currentUserRole = role.toLowerCase();
                loginFrame.dispose();
                initializeMainUI();
            }
        });

        loginFrame.add(userLabel);
        loginFrame.add(usernameField);
        loginFrame.add(passLabel);
        loginFrame.add(passwordField);
        loginFrame.add(new JLabel());
        loginFrame.add(loginButton);

        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private void initializeMainUI() {
        setTitle("Furniture Shop - POS System (" + currentUserRole + ")");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBackground(new Color(153, 153, 153));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 193, 195));

        JLabel titleLabel = new JLabel("Furniture Shop - Point of Sale", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        clockLabel = new JLabel("", JLabel.RIGHT);
        clockLabel.setForeground(Color.WHITE);
        updateClock();
        new Timer(1000, e -> updateClock()).start();

        JPanel topHeaderPanel = new JPanel(new GridLayout(1, 2));
        topHeaderPanel.setOpaque(false);
        topHeaderPanel.add(titleLabel);
        topHeaderPanel.add(clockLabel);

        headerPanel.add(topHeaderPanel, BorderLayout.CENTER);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        tableModel = new DefaultTableModel(new String[] { "Item", "Price" }, 0);
        cartTable = new JTable(tableModel);
        mainPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBackground(new Color(153, 153, 153));

        JButton addItem1 = new JButton("Add Chair ($50)");
        JButton addItem2 = new JButton("Add Table ($120)");
        JButton addItem3 = new JButton("Add Sofa ($300)");
        JButton addItem4 = new JButton("Add Bed ($500)");
        JButton searchButton = new JButton("Search Product");

        addHoverEffect(addItem1);
        addHoverEffect(addItem2);
        addHoverEffect(addItem3);
        addHoverEffect(addItem4);
        applyHoverAndColor(searchButton, new Color(0, 0, 153));

        addItem1.addActionListener(e -> addItem("Chair", 50.0));
        addItem2.addActionListener(e -> addItem("Table", 120.0));
        addItem3.addActionListener(e -> addItem("Sofa", 300.0));
        addItem4.addActionListener(e -> addItem("Bed", 500.0));
        searchButton.addActionListener(e -> showProductSearchDialog());

        buttonPanel.add(addItem1);
        buttonPanel.add(addItem2);
        buttonPanel.add(addItem3);
        buttonPanel.add(addItem4);
        buttonPanel.add(searchButton);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        totalLabel = new JLabel("Total: $0.00", JLabel.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton applyDiscount = new JButton("Apply 10% Discount");
        JButton receiptButton = new JButton("Generate Receipt");
        JButton viewHistoryButton = new JButton("View Receipt History");
        JButton darkModeButton = new JButton("Toggle Dark Mode");
        JButton printButton = new JButton("Print Receipt");
        JButton logoutButton = new JButton("Logout");

        applyHoverAndColor(applyDiscount, new Color(0, 123, 255));
        applyHoverAndColor(receiptButton, new Color(51, 0, 0));
        applyHoverAndColor(viewHistoryButton, new Color(0, 102, 0));
        applyHoverAndColor(darkModeButton, new Color(51, 51, 51));
        applyHoverAndColor(printButton, new Color(255, 204, 0));
        applyHoverAndColor(logoutButton, new Color(204, 0, 0));

        applyDiscount.addActionListener(e -> applyDiscount());
        receiptButton.addActionListener(e -> generateReceipt());
        viewHistoryButton.addActionListener(e -> viewHistory());
        darkModeButton.addActionListener(e -> toggleDarkMode());
        printButton.addActionListener(e -> printReceipt());
        logoutButton.addActionListener(e -> logout());

        footerPanel.add(totalLabel);
        footerPanel.add(applyDiscount);
        footerPanel.add(receiptButton);
        footerPanel.add(viewHistoryButton);
        footerPanel.add(printButton);
        footerPanel.add(darkModeButton);
        footerPanel.add(logoutButton);

        if (currentUserRole.equalsIgnoreCase("admin")) {
            JButton manageUsersButton = new JButton("Manage Users");
            JButton reportButton = new JButton("Sales Report");
            manageUsersButton.addActionListener(e -> manageUsers());
            reportButton.addActionListener(e -> showSalesReport());

            applyHoverAndColor(reportButton, new Color(255, 150, 0));
            applyHoverAndColor(manageUsersButton, new Color(0, 150, 0));

            footerPanel.add(manageUsersButton);
            footerPanel.add(reportButton);
        } else {
            footerPanel.add(new JLabel());
            footerPanel.add(new JLabel());
        }

        backgroundPanel.add(footerPanel, BorderLayout.SOUTH);
        add(backgroundPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showProductSearchDialog() {
        JTextField searchField = new JTextField();
        String[] products = { "Chair", "Table", "Sofa", "Bed" };
        double[] prices = { 50.0, 120.0, 300.0, 500.0 };

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel("Enter product name:"), BorderLayout.NORTH);
        panel.add(searchField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Product Search", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String query = searchField.getText().trim().toLowerCase();
            for (int i = 0; i < products.length; i++) {
                if (products[i].toLowerCase().contains(query)) {
                    int add = JOptionPane.showConfirmDialog(this,
                            "Product found: " + products[i] + " ($" + prices[i] + "). Add to cart?",
                            "Add Product", JOptionPane.YES_NO_OPTION);
                    if (add == JOptionPane.YES_OPTION) {
                        addItem(products[i], prices[i]);
                    }
                    return;
                }
            } 
            JOptionPane.showMessageDialog(this, "Product not found.");
        }
    }

    private void showSalesReport() {
        try {
            File dir = new File(".");
            File[] receipts = dir.listFiles((d, name) -> name.startsWith("receipt_") && name.endsWith(".txt"));

            double grandTotal = 0.0;
            int totalReceipts = 0;

            if (receipts != null) {
                for (File receipt : receipts) {
                    try (Scanner scanner = new Scanner(receipt)) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (line.startsWith("Total: $")) {
                                double amount = Double.parseDouble(line.replace("Total: $", "").trim());
                                grandTotal += amount;
                                totalReceipts++;
                            }
                        }
                    }
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Total Receipts: " + totalReceipts + "\nGrand Total Sales: $" + String.format("%.2f", grandTotal),
                    "Sales Report", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading sales data.");
        }
    }

    private void applyHoverAndColor(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(color, 2));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    private void addHoverEffect(JButton button) {
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 2));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 123, 255));
            }
        });
    }

    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        clockLabel.setText("Time: " + sdf.format(new Date()));
    }

    private void addItem(String item, double price) {
        tableModel.addRow(new Object[] { item, "$" + price });
        total += price;
        totalLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private void applyDiscount() {
        if (currentUserRole.equalsIgnoreCase("admin")) {
            total *= 0.90;
            totalLabel.setText("Total after 10% discount: $" + String.format("%.2f", total));
        } else {
            JOptionPane.showMessageDialog(this, "You must be an admin to apply a discount.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReceipt() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items in cart!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String fileName = "receipt_" + currentUser + ".txt";
            FileWriter writer = new FileWriter(fileName, true);
            writer.write("\n===== Receipt =====\n");
            writer.write("User: " + currentUser + "\n");
            writer.write("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
            writer.write("-------------------\n");

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.write(tableModel.getValueAt(i, 0) + " - " + tableModel.getValueAt(i, 1) + "\n");
            }

            writer.write("-------------------\n");
            writer.write("Total: $" + String.format("%.2f", total) + "\n");
            writer.write("===================\n");
            writer.close();

            JOptionPane.showMessageDialog(this, "Receipt saved as " + fileName);
            Desktop.getDesktop().open(new File(fileName));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing receipt!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewHistory() {
        try {
            File file = new File("receipt_" + currentUser + ".txt");
            if (file.exists())
                Desktop.getDesktop().open(file);
            else
                throw new IOException();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No receipt history found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleDarkMode() {
        darkMode = !darkMode;
        Color bg = darkMode ? Color.DARK_GRAY : Color.WHITE;
        Color fg = darkMode ? Color.WHITE : Color.BLACK;
        getContentPane().setBackground(bg);
        cartTable.setBackground(bg);
        cartTable.setForeground(fg);
        totalLabel.setForeground(fg);
        clockLabel.setForeground(fg);
        repaint();
    }

    private void printReceipt() {
        try {
            cartTable.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error printing receipt!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            isLoggedIn = false;
            currentUser = "";
            total = 0;
            tableModel.setRowCount(0);
            totalLabel.setText("Total: $0.00");
            dispose();
            showLoginUI();
        }
    }

    private void manageUsers() {
        String[] options = { "Update Password", "Delete User" };
        int choice = JOptionPane.showOptionDialog(this, "Select an option:", "User Management",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        String user = JOptionPane.showInputDialog("Enter username:");
        if (!users.containsKey(user)) {
            JOptionPane.showMessageDialog(this, "User not found.");
            return;
        }

        if (choice == 0) {
            String newPass = JOptionPane.showInputDialog("Enter new password:");
            users.put(user, hashPassword(newPass));
            saveUserData();
            JOptionPane.showMessageDialog(this, "Password updated.");
        } else if (choice == 1) {
            users.remove(user);
            roles.remove(user);
            saveUserData();
            JOptionPane.showMessageDialog(this, "User deleted.");
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("1021");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    private void saveUserData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            out.writeObject(users);
            out.writeObject(roles);
        } catch (IOException e) {
            System.err.println("Error saving users.");
        }
    }

    private void loadUserData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("users.dat"))) {
            Map<String, String> u = (Map<String, String>) in.readObject();
            Map<String, String> r = (Map<String, String>) in.readObject();
            users.putAll(u);
            roles.putAll(r);
            
        } catch (Exception e) {
            String defaultAdmin = "admin";
            String defaultAdminPassword = hashPassword("1021");

            if (!users.containsKey(defaultAdmin)) {
                users.put(defaultAdmin, defaultAdminPassword);
                roles.put(defaultAdmin, "admin");
                saveUserData();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(POS::new);
    }
}