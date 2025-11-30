package gui;


import javax.swing.*;
// AWT = older GUI library, we use it for layouts and events
import java.awt.*;
import java.awt.event.*;
// Our service class for login
import services.AuthenticationService;
import models.OperationResult;


public class LoginFrame extends JFrame {

    // compontents
    // These are the GUI elements (declared here so all methods can access them)
    private JTextField emailField;          // For typing email
    private JPasswordField passwordField;   // For typing password (hidden characters)
    private JButton loginButton;            // The login button
    private JLabel messageLabel;            // Shows error/success messages

    // service
    // We need AuthenticationService to check login credentials
    private AuthenticationService authService;

    // constructor
    // The constructor sets up the entire window
    public LoginFrame() {
        // Create the authentication service
        authService = new AuthenticationService();

        // Setup the window properties
        setupFrame();

        // Create and add all components
        createComponents();
    }

    // setup frame
    // Configure the window itself
    private void setupFrame() {
        // Window title (appears in title bar)
        setTitle("SupperBank - Login");

        // Window size (width, height in pixels)
        setSize(400, 300);

        // Center the window on screen
        setLocationRelativeTo(null);

        // EXIT_ON_CLOSE = close the entire application
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // set window icon
        // Load image from file
        ImageIcon icon = new ImageIcon("C:\\Users\\DELL\\IdeaProjects\\java_test\\src\\cobweb.png");
        setIconImage(icon.getImage());


        // Prevent user from resizing (keeps our layout clean)
        setResizable(false);
    }

    // create components
    // Build all the GUI elements
    private void createComponents() {
        // main panel
        // JPanel is a container that holds other components
        // We use BorderLayout to organize: NORTH, CENTER, SOUTH
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Add padding around the edges (top, left, bottom, right)
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // title label(north)
        JLabel titleLabel = new JLabel("Welcome to SupperBank", SwingConstants.CENTER);
        // Make the title bigger and bold
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        // Add some space below the title
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // form panel(center)
        // GridLayout creates a grid: 3 rows, 2 columns, with gaps
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));

        // Row 1: Email
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        // Set preferred size for better appearance
        emailField.setPreferredSize(new Dimension(200, 30));

        // Row 2: Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        // Row 3: Empty space + Login button
        JLabel emptyLabel = new JLabel(""); // Placeholder for grid alignment
        loginButton = new JButton("Login");

        // Add components to form panel (order matters for GridLayout!)
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(emptyLabel);
        formPanel.add(loginButton);

        // message label(south)
        // This shows errors like "Invalid password"
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);  // Error messages in red

        // add to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(messageLabel, BorderLayout.SOUTH);

        // add main panel to frame
        add(mainPanel);

        // add event listeners
        setupEventListeners();
    }

    // event listeners:
    // Define what happens when user interacts with the GUI
    private void setupEventListeners() {

        // Button click:
        // When user clicks the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call our login method
                performLogin();
            }
        });

        // Enter key on password field
        // When user presses Enter in password field, also login
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // KeyEvent.VK_ENTER = the Enter key
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
    }

    // performing login:
    // This method is called when user clicks Login or presses Enter
    private void performLogin() {
        // Get the text from input fields
        String email = emailField.getText().trim();  // trim() removes extra spaces

        // getPassword() returns char[] for security, convert to String
        String password = new String(passwordField.getPassword());

        // validation:
        // Check if fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both email and password");
            return;  // Stop here, don't try to login
        }

        // try to login
        try {
            // Call the authentication service
            OperationResult result = authService.login(email, password);

            if (result.isSuccess()) {
                // Login successful!
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText(result.getMessage());

                // Open the appropriate dashboard
                openDashboard();

            } else {
                // Login failed
                messageLabel.setForeground(Color.RED);
                messageLabel.setText(result.getMessage());

                // Clear password field for security
                passwordField.setText("");
            }

        } catch (Exception ex) {
            // Something went wrong (database error, etc.)
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: " + ex.getMessage());
            //if you want more deati about the exception use this :
          //  ex.printStackTrace();  // Print details to console for debugging
        }
    }

    // open dashboard
    // After successful login, open the right dashboard
    private void openDashboard() {
        // Hide the login window
        setVisible(false);

        // Check what type of user logged in
        if (authService.isAdmin()) {
            // Open Admin Dashboard
            AdminDashboard adminDashboard = new AdminDashboard(authService);
            adminDashboard.setVisible(true);

        } else if (authService.isClient()) {
            // Open Client Dashboard
            ClientDashboard clientDashboard = new ClientDashboard(authService);
            clientDashboard.setVisible(true);
        }

        //we can close the login window ,or we can keep it to login into more accounts in one single session
        dispose();
    }


}
