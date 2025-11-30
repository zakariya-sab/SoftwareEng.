package gui.admin;

import javax.swing.*;
import java.awt.*;
import services.AdminService;
import services.TransactionService;


public class AdminHomePanel extends JPanel {

    private AdminService adminService;
    private TransactionService transactionService;

    //Constructor
    public AdminHomePanel(AdminService adminService) {
        this.adminService = adminService;
        this.transactionService = new TransactionService();

        setupPanel();
        createComponents();
    }

    private void setupPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(236, 240, 241));
    }


    private void createComponents() {
        // Get data first
        int totalClients = getTotalClients();
        double totalBalance = getTotalBalance();
        double avgBalance = totalClients > 0 ? totalBalance / totalClients : 0;

        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Stats cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(new Dimension(0, 150));

        // Card 1: Total Clients
        JPanel clientsCard = createStatCard(
                "Total Clients",
                String.valueOf(totalClients),
                "registered accounts",
                new Color(52, 152, 219)  // Blue
        );

        // Card 2: Total Balance
        JPanel balanceCard = createStatCard(
                "Total Bank Balance",
                String.format("%.2f", totalBalance),
                "MAD in all accounts",
                new Color(46, 204, 113)  // Green
        );

        // Card 3: Average Balance
        JPanel avgCard = createStatCard(
                "Average Balance",
                String.format("%.2f", avgBalance),
                "MAD per client",
                new Color(155, 89, 182)  // Purple
        );

        cardsPanel.add(clientsCard);
        cardsPanel.add(balanceCard);
        cardsPanel.add(avgCard);

        // Welcome Info panel
        JPanel infoPanel = createInfoPanel();

        // Top section
        JPanel topSection = new JPanel(new BorderLayout(0, 20));
        topSection.setOpaque(false);
        topSection.add(titleLabel, BorderLayout.NORTH);
        topSection.add(cardsPanel, BorderLayout.CENTER);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Statistics");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setOpaque(true);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> refreshStatistics());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);

        add(topSection, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    private JPanel createStatCard(String title, String value, String subtitle, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Color bar at top
        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 5));

        // Content panel
        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title label
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLbl.setForeground(new Color(127, 140, 141));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        content.add(titleLbl, gbc);

        // Value label
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Arial", Font.BOLD, 32));
        valueLbl.setForeground(color);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        content.add(valueLbl, gbc);

        // Subtitle label
        JLabel subtitleLbl = new JLabel(subtitle);
        subtitleLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLbl.setForeground(new Color(127, 140, 141));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(subtitleLbl, gbc);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }


    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel infoTitle = new JLabel("Quick Actions Guide");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 18));
        infoTitle.setForeground(new Color(44, 62, 80));

        // Info content using GridBagLayout for proper spacing
        JPanel infoContent = new JPanel(new GridBagLayout());
        infoContent.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        String[] actions = {
                "Manage Clients - Add, edit, delete, and search client accounts",
                "All Transactions - View complete transaction history for all accounts",
                "Currency Rates - Update exchange rates for currency conversion"
        };

        for (int i = 0; i < actions.length; i++) {
            JLabel actionLabel = new JLabel(actions[i]);
            actionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            actionLabel.setForeground(new Color(52, 73, 94));
            gbc.gridy = i;
            infoContent.add(actionLabel, gbc);
        }

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        contentWrapper.add(infoContent, BorderLayout.NORTH);

        panel.add(infoTitle, BorderLayout.NORTH);
        panel.add(contentWrapper, BorderLayout.CENTER);

        return panel;
    }


    private int getTotalClients() {
        try {
            return adminService.getTotalClients();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    private double getTotalBalance() {
        try {
            return adminService.getTotalBankBalance();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    private void refreshStatistics() {
        // Remove all and recreate
        removeAll();
        createComponents();
        revalidate();
        repaint();

        JOptionPane.showMessageDialog(
                this,
                "Statistics refreshed!",
                "Refresh",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}