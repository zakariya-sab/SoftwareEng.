package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import services.AuthenticationService;
import services.ClientService;
import models.Client;

// Import all client panels
import gui.client.ClientHomePanel;
import gui.client.DepositPanel;
import gui.client.WithdrawPanel;
import gui.client.TransferPanel;
import gui.client.TransactionHistoryPanel;
import gui.client.CurrencyConverterPanel;
import gui.client.ProfilePanel;


public class ClientDashboard extends JFrame {


    private AuthenticationService authService;
    private ClientService clientService;


    private Client currentClient;


    private JPanel contentPanel;    // The area that changes (right side)
    private JPanel sidebarPanel;    // The menu (left side)

   //Sidebar buttons
    // We keep references to highlight the active button
    private JButton homeButton;
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton transferButton;
    private JButton historyButton;
    private JButton currencyButton;
    private JButton profileButton;
    private JButton logoutButton;

    // Currently selected button (to change its color)
    private JButton currentButton;

    // Colors
    // Define colors as constants for easy changes
    private static final Color SIDEBAR_COLOR = new Color(52, 73, 94);      // Dark blue-gray
    private static final Color SIDEBAR_HOVER = new Color(44, 62, 80);      // Darker on hover
    private static final Color SIDEBAR_SELECTED = new Color(41, 128, 185); // Blue when selected
    private static final Color HEADER_COLOR = new Color(41, 128, 185);     // Blue header

    // Constructor
    public ClientDashboard(AuthenticationService authService) {
        this.authService = authService;
        this.clientService = new ClientService();
        this.currentClient = authService.getCurrentClient();

        setupFrame();
        createComponents();

        // Show home panel by default
        showPanel("home");
    }

    //setup frame
    private void setupFrame() {
        setTitle("SupperBank - Client Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
    }

    //creating components
    private void createComponents() {
        // using the BorderLayout for main structure
        setLayout(new BorderLayout());

        // create the three main sections
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

        // Left side: Bank name
        JLabel bankName = new JLabel("SupperBank");
        bankName.setFont(new Font("Arial", Font.BOLD, 24));
        bankName.setForeground(Color.WHITE);

        // Right side: Client name and balance
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfo.setOpaque(false);  // Transparent background

        String welcomeText = "Welcome, " + currentClient.getFirstName() + " " +
                currentClient.getLastName();
        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);

        userInfo.add(welcomeLabel);

        header.add(bankName, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        return header;
    }

    // create sidebar
    private JPanel createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Create all menu buttons
        homeButton = createSidebarButton("  Home", "home");
        depositButton = createSidebarButton("  Deposit", "deposit");
        withdrawButton = createSidebarButton("  Withdraw", "withdraw");
        transferButton = createSidebarButton("  Transfer", "transfer");
        historyButton = createSidebarButton("  History", "history");
        currencyButton = createSidebarButton("  Currency", "currency");
        profileButton = createSidebarButton("  Profile", "profile");

        // Add buttons to sidebar
        sidebarPanel.add(homeButton);
        sidebarPanel.add(depositButton);
        sidebarPanel.add(withdrawButton);
        sidebarPanel.add(transferButton);
        sidebarPanel.add(historyButton);
        sidebarPanel.add(currencyButton);
        sidebarPanel.add(profileButton);

        // Add flexible space to push logout to bottom
        sidebarPanel.add(Box.createVerticalGlue());

        // Logout button at bottom
        logoutButton = createSidebarButton("  Logout", "logout");
        sidebarPanel.add(logoutButton);

        return sidebarPanel;
    }

    // create sidebar button
    // Helper method to create consistent sidebar buttons
    private JButton createSidebarButton(String text, String actionCommand) {
        JButton button = new JButton(text);

        // Remove default button styling
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        // Set colors and font
        button.setBackground(SIDEBAR_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));

        // Set size
        button.setMaximumSize(new Dimension(200, 45));
        button.setPreferredSize(new Dimension(200, 45));

        // Align text to left
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        // Action command is used to identify which button was clicked
        button.setActionCommand(actionCommand);

        // Add hover effect
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

        // Add click action
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
        contentPanel.setBackground(new Color(236, 240, 241));  // Light gray
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return contentPanel;
    }

    // handle sidebar click
    private void handleSidebarClick(String command, JButton clickedButton) {
        // Special case: logout
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

        // Show the appropriate panel
        showPanel(command);
    }

    // show panel
    // This method swaps the content area to show different panels
    public void showPanel(String panelName) {
        // Remove current content
        contentPanel.removeAll();

        // Create and add the appropriate panel
        JPanel newPanel;

        switch (panelName) {
            case "home":
                newPanel = new ClientHomePanel(currentClient, clientService);
                if (currentButton == null) {
                    currentButton = homeButton;
                    homeButton.setBackground(SIDEBAR_SELECTED);
                }
                break;

            case "deposit":
                newPanel = new DepositPanel(currentClient, clientService, this);
                break;

            case "withdraw":
                newPanel = new WithdrawPanel(currentClient, clientService, this);
                break;

            case "transfer":
                newPanel = new TransferPanel(currentClient, clientService, this);
                break;

            case "history":
                newPanel = new TransactionHistoryPanel(currentClient, clientService);
                break;

            case "currency":
                newPanel = new CurrencyConverterPanel();
                break;

            case "profile":
                newPanel = new ProfilePanel(currentClient, clientService, authService);
                break;

            default:
                newPanel = new ClientHomePanel(currentClient, clientService);
        }

        contentPanel.add(newPanel, BorderLayout.CENTER);

        // Refresh the display
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // refresh client data
    // Call this after deposit/withdraw/transfer to update balance
    public void refreshClientData() {
        try {
            // Get fresh data from database
            Client updatedClient = clientService.getClientByAccountNumber(
                    currentClient.getAccountNumber()
            );
            if (updatedClient != null) {
                this.currentClient = updatedClient;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // get current client
    public Client getCurrentClient() {
        return currentClient;
    }

    // perform logout
    private void performLogout() {
        // Ask for confirmation
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            // Logout from service
            authService.logout();

            // Close this window
            dispose();

            // Open login window
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }
}