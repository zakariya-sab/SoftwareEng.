package gui.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import services.CurrencyService;
import models.CurrencyRate;


public class CurrencyConverterPanel extends JPanel {

    private CurrencyService currencyService;

    private JComboBox<String> fromCurrencyCombo;
    private JComboBox<String> toCurrencyCombo;
    private JTextField amountField;
    private JLabel resultLabel;
    private JLabel rateLabel;

    // Store currency data
    private List<CurrencyRate> currencyRates;

    // constructor
    public CurrencyConverterPanel() {
        this.currencyService = new CurrencyService();

        setupPanel();
        createComponents();
        loadCurrencies();
    }

    // setup panel
    private void setupPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(236, 240, 241));
    }

    // create components
    private void createComponents() {
        // Title
        JLabel titleLabel = new JLabel("Currency Converter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Main card
        JPanel converterCard = new JPanel();
        converterCard.setLayout(new BoxLayout(converterCard, BoxLayout.Y_AXIS));
        converterCard.setBackground(Color.WHITE);
        converterCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        converterCard.setMaximumSize(new Dimension(550, 450));

        // Amount input
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.PLAIN, 18));
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Currency selection panel
        JPanel currencyPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        currencyPanel.setOpaque(false);
        currencyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        currencyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // From currency
        JPanel fromPanel = new JPanel();
        fromPanel.setLayout(new BoxLayout(fromPanel, BoxLayout.Y_AXIS));
        fromPanel.setOpaque(false);

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        fromCurrencyCombo = new JComboBox<>();
        fromCurrencyCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        fromPanel.add(fromLabel);
        fromPanel.add(Box.createVerticalStrut(5));
        fromPanel.add(fromCurrencyCombo);

        // Swap button
        JPanel swapPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        swapPanel.setOpaque(false);
        JButton swapButton = new JButton("<->");
        swapButton.setFont(new Font("Arial", Font.BOLD, 20));
        swapButton.setFocusPainted(false);
        swapButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        swapButton.setToolTipText("Swap currencies");
        swapPanel.add(swapButton);

        // To currency
        JPanel toPanel = new JPanel();
        toPanel.setLayout(new BoxLayout(toPanel, BoxLayout.Y_AXIS));
        toPanel.setOpaque(false);

        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        toCurrencyCombo = new JComboBox<>();
        toCurrencyCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        toPanel.add(toLabel);
        toPanel.add(Box.createVerticalStrut(5));
        toPanel.add(toCurrencyCombo);

        currencyPanel.add(fromPanel);
        currencyPanel.add(swapPanel);
        currencyPanel.add(toPanel);

        // Convert button
        JButton convertButton = new JButton("Convert");
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setBackground(new Color(155, 89, 182));  // Purple
        convertButton.setForeground(Color.WHITE);
        convertButton.setFocusPainted(false);
        convertButton.setBorderPainted(false);
        convertButton.setOpaque(true);
        convertButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        convertButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        convertButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Result panel
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(155, 89, 182));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        resultPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        resultLabel = new JLabel("0.00");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 32));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        rateLabel = new JLabel("Enter amount and click Convert");
        rateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        rateLabel.setForeground(new Color(230, 230, 230));
        rateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resultPanel.add(rateLabel);
        resultPanel.add(Box.createVerticalStrut(5));
        resultPanel.add(resultLabel);

        // Add everything to card
        converterCard.add(amountLabel);
        converterCard.add(Box.createVerticalStrut(10));
        converterCard.add(amountField);
        converterCard.add(Box.createVerticalStrut(25));
        converterCard.add(currencyPanel);
        converterCard.add(Box.createVerticalStrut(25));
        converterCard.add(convertButton);
        converterCard.add(Box.createVerticalStrut(25));
        converterCard.add(resultPanel);

        // Center wrapper
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setOpaque(false);
        centerWrapper.add(converterCard);

        // Event listeners
        convertButton.addActionListener(e -> performConversion());

        swapButton.addActionListener(e -> {
            int fromIndex = fromCurrencyCombo.getSelectedIndex();
            int toIndex = toCurrencyCombo.getSelectedIndex();
            fromCurrencyCombo.setSelectedIndex(toIndex);
            toCurrencyCombo.setSelectedIndex(fromIndex);
        });

        amountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performConversion();
                }
            }
        });

        add(titleLabel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
    }

    // load currencies
    private void loadCurrencies() {
        try {
            currencyRates = currencyService.getAllCurrencyRates();

            if (currencyRates != null && !currencyRates.isEmpty()) {
                for (CurrencyRate rate : currencyRates) {
                    String item = rate.getCurrencyCode() + " - " + rate.getCurrencyName();
                    fromCurrencyCombo.addItem(item);
                    toCurrencyCombo.addItem(item);
                }

                // Set default selections
                // Find MAD and set as default 'from'
                for (int i = 0; i < currencyRates.size(); i++) {
                    if (currencyRates.get(i).getCurrencyCode().equals("MAD")) {
                        fromCurrencyCombo.setSelectedIndex(i);
                        break;
                    }
                }

                // Set USD as default 'to' if exists
                for (int i = 0; i < currencyRates.size(); i++) {
                    if (currencyRates.get(i).getCurrencyCode().equals("USD")) {
                        toCurrencyCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading currencies: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    // perform conversion
    private void performConversion() {
        String amountText = amountField.getText().trim();

        if (amountText.isEmpty()) {
            rateLabel.setText("Please enter an amount");
            resultLabel.setText("0.00");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            rateLabel.setText("Invalid amount");
            resultLabel.setText("0.00");
            return;
        }

        if (amount < 0) {
            rateLabel.setText("Amount cannot be negative");
            resultLabel.setText("0.00");
            return;
        }

        // Get selected currencies
        int fromIndex = fromCurrencyCombo.getSelectedIndex();
        int toIndex = toCurrencyCombo.getSelectedIndex();

        if (fromIndex < 0 || toIndex < 0) {
            rateLabel.setText("Please select currencies");
            return;
        }

        String fromCurrency = currencyRates.get(fromIndex).getCurrencyCode();
        String toCurrency = currencyRates.get(toIndex).getCurrencyCode();

        try {
            // Perform conversion
            double result = currencyService.convertCurrency(amount, fromCurrency, toCurrency);

            // Get exchange rate for display
            double rate = currencyService.getExchangeRate(fromCurrency, toCurrency);

            // Update display
            resultLabel.setText(String.format("%.2f %s", result, toCurrency));
            rateLabel.setText(String.format("1 %s = %.4f %s", fromCurrency, rate, toCurrency));

        } catch (Exception e) {
            rateLabel.setText("Error: " + e.getMessage());
            resultLabel.setText("0.00");
            e.printStackTrace();
        }
    }
}