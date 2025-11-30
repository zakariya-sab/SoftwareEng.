package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import services.AuthenticationService;
import services.AdminService;
import models.Admin;

// Import all admin panels
import gui.admin.AdminHomePanel;
import gui.admin.ClientManagementPanel;
import gui.admin.AllTransactionsPanel;
import gui.admin.CurrencyManagementPanel;


public class AdminDashboard extends JFrame {

    // Services
    private AuthenticationService authService;
    private AdminService adminService;

    // Current admin
    private Admin currentAdmin;

    // main panels
    private JPanel contentPanel;
    private JPanel sidebarPanel;

    // sidebar buttons
    private JButton dashboardButton;
    private JButton clientsButton;
    private JButton transactionsButton;
    private JButton currencyButton;
    private JButton logoutButton;

    private JButton currentButton;

    // colors:
    // Slightly different colors for admin (to distinguish from client)
    private static final Color SIDEBAR_COLOR = new Color(44, 62, 80);       // Dark blue
    private static final Color SIDEBAR_HOVER = new Color(52, 73, 94);       // Lighter on hover
    private static final Color SIDEBAR_SELECTED = new Color(231, 76, 60);   // Red when selected
    private static final Color HEADER_COLOR = new Color(192, 57, 43);       // Red header

    // constructor
    public AdminDashboard(AuthenticationService authService) {
        this.authService = authService;
        this.adminService = new AdminService();
        this.currentAdmin = authService.getCurrentAdmin();

        setupFrame();
        createComponents();

        // Show dashboard by default
        showPanel("dashboard");
    }

    // setup frame
    private void setupFrame() {
        setTitle("SupperBank - Admin Dashboard");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
    }

    // create componenets
    private void createComponents() {
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);
        add(createContentArea(), BorderLayout.CENTER);
    }

    // create header
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Left side: Bank name + Admin label
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        JLabel bankName = new JLabel("SupperBank");
        bankName.setFont(new Font("Arial", Font.BOLD, 24));
        bankName.setForeground(Color.WHITE);

        JLabel adminBadge = new JLabel("ADMIN");
        adminBadge.setFont(new Font("Arial", Font.BOLD, 12));
        adminBadge.setForeground(Color.WHITE);
        adminBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        leftPanel.add(bankName);
        leftPanel.add(adminBadge);

        // Right side: Admin name
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfo.setOpaque(false);

        String welcomeText = "Admin: " + currentAdmin.getFirstName() + " " +
                currentAdmin.getLastName();
        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);

        userInfo.add(welcomeLabel);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        return header;
    }

    // create sidebar
    private JPanel createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Create menu buttons
        dashboardButton = createSidebarButton("  Dashboard", "dashboard");
        clientsButton = createSidebarButton("  Manage Clients", "clients");
        transactionsButton = createSidebarButton("  All Transactions", "transactions");
        currencyButton = createSidebarButton("  Currency Rates", "currency");

        // Add buttons to sidebar
        sidebarPanel.add(dashboardButton);
        sidebarPanel.add(clientsButton);
        sidebarPanel.add(transactionsButton);
        sidebarPanel.add(currencyButton);

        // Push logout to bottom
        sidebarPanel.add(Box.createVerticalGlue());

        logoutButton = createSidebarButton("  Logout", "logout");
        sidebarPanel.add(logoutButton);

        return sidebarPanel;
    }

    // create sidebar button
    private JButton createSidebarButton(String text, String actionCommand) {
        JButton button = new JButton(text);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.setBackground(SIDEBAR_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));

        button.setMaximumSize(new Dimension(220, 50));
        button.setPreferredSize(new Dimension(220, 50));

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        button.setActionCommand(actionCommand);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != currentButton) {
                    button.setBackground(SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != currentButton) {
                    button.setBackground(SIDEBAR_COLOR);
                }
            }
        });

        // Click action
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSidebarClick(e.getActionCommand(), button);
            }
        });

        return button;
    }

    // create content area
    private JPanel createContentArea() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(236, 240, 241));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return contentPanel;
    }

    // handle sidebar click
    private void handleSidebarClick(String command, JButton clickedButton) {
        if (command.equals("logout")) {
            performLogout();
            return;
        }

        // Update button colors
        if (currentButton != null) {
            currentButton.setBackground(SIDEBAR_COLOR);
        }
        clickedButton.setBackground(SIDEBAR_SELECTED);
        currentButton = clickedButton;

        showPanel(command);
    }

    // show panel
    public void showPanel(String panelName) {
        contentPanel.removeAll();

        JPanel newPanel;

        switch (panelName) {
            case "dashboard":
                newPanel = new AdminHomePanel(adminService);
                if (currentButton == null) {
                    currentButton = dashboardButton;
                    dashboardButton.setBackground(SIDEBAR_SELECTED);
                }
                break;

            case "clients":
                newPanel = new ClientManagementPanel(adminService);
                break;

            case "transactions":
                newPanel = new AllTransactionsPanel();
                break;

            case "currency":
                newPanel = new CurrencyManagementPanel();
                break;

            default:
                newPanel = new AdminHomePanel(adminService);
        }

        contentPanel.add(newPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // perform logout
    private void performLogout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }
}