package com.shoppiee.gui;




import javax.swing.*;

import com.google.gson.Gson;
import com.shoppiee.model.User;

import java.awt.*;
import java.io.IOException;


import okhttp3.*;

public class Shoppiee{
    private static final String BASE_URL = "http://localhost:8081/api";
    private OkHttpClient client = new OkHttpClient();

    // Main Frame
    private JFrame frame;
    private JPanel loginPanel, createAccountPanel, shoppingPanel, checkoutPanel, pendingOrdersPanel, itemModificationsPanel;
    private CardLayout cardLayout;

    // Constructor
    public Shoppiee() {
        frame = new JFrame("Shopify Application");
        cardLayout = new CardLayout();
        frame.setLayout(cardLayout);

        loginPanel = createLoginPanel();
        createAccountPanel = createAccountPanel();
        shoppingPanel = createShoppingPanel();
        checkoutPanel = createCheckoutPanel();
        pendingOrdersPanel = createPendingOrdersPanel();
        itemModificationsPanel = createItemModificationsPanel();

        frame.add(loginPanel, "Login");
        frame.add(createAccountPanel, "CreateAccount");
        frame.add(shoppingPanel, "Shopping");
        frame.add(checkoutPanel, "Checkout");
        frame.add(pendingOrdersPanel, "PendingOrders");
        frame.add(itemModificationsPanel, "ItemModifications");

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        showPanel("Login");
    }

    // Show specific panel
    private void showPanel(String panelName) {
        cardLayout.show(frame.getContentPane(), panelName);
    }

    // Login Panel
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordText = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());
            if (authenticateUser(username, password)) {
                String userType = getUserType(username);
                if (userType.equals("customer")) {
                    showPanel("Shopping");
                } else {
                    showPanel("PendingOrders");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password");
            }
        });

        panel.add(userLabel);
        panel.add(userText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(loginButton);

        return panel;
    }

    // Authenticate user
    private boolean authenticateUser(String username, String password) {
        User user = new User(username, password);
        RequestBody body = RequestBody.create(user.toJson(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(BASE_URL + "/login")
            .post(body)
            .build();
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful() && Boolean.parseBoolean(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get user type
    private String getUserType(String username) {
        Request request = new Request.Builder()
            .url(BASE_URL + "/getUserType/" + username)
            .get()
            .build();
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful() ? response.body().string() : "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Create Account Panel
    private JPanel createAccountPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordText = new JPasswordField();
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameText = new JTextField();
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameText = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailText = new JTextField();
        JLabel addressLabel = new JLabel("Address:");
        JTextField addressText = new JTextField();
        JLabel positionLabel = new JLabel("Position:");
        JTextField positionText = new JTextField();
        JButton createButton = new JButton("Create Account");

        createButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());
            String firstName = firstNameText.getText();
            String lastName = lastNameText.getText();
            String email = emailText.getText();
            String address = addressText.getText();
            String position = positionText.getText();
            User user = new User(username, password, firstName, lastName, email, address, position);
            createUser(user);
        });

        panel.add(userLabel);
        panel.add(userText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(firstNameLabel);
        panel.add(firstNameText);
        panel.add(lastNameLabel);
        panel.add(lastNameText);
        panel.add(emailLabel);
        panel.add(emailText);
        panel.add(addressLabel);
        panel.add(addressText);
        panel.add(positionLabel);
        panel.add(positionText);
        panel.add(createButton);

        return panel;
    }

    // Create user
    private void createUser(User user) {
        RequestBody body = RequestBody.create(user.toJson(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(BASE_URL + "/createAccount")
            .post(body)
            .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JOptionPane.showMessageDialog(frame, "Account created successfully");
                showPanel("Login");
            } else {
                JOptionPane.showMessageDialog(frame, "Error creating account");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error creating account");
        }
    }

    // Shopping Panel
    private JPanel createShoppingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTextArea itemList = new JTextArea();
        JTextField itemField = new JTextField();
        JTextField quantityField = new JTextField();
        JButton addButton = new JButton("Add to Cart");
        JButton checkoutButton = new JButton("Checkout");

        panel.add(new JScrollPane(itemList), BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new GridLayout(3, 2));
        bottomPanel.add(new JLabel("Item:"));
        bottomPanel.add(itemField);
        bottomPanel.add(new JLabel("Quantity:"));
        bottomPanel.add(quantityField);
        bottomPanel.add(addButton);
        bottomPanel.add(checkoutButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            String item = itemField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            addToCart(item, quantity);
        });

        checkoutButton.addActionListener(e -> showPanel("Checkout"));

        return panel;
    }

    // Add to cart
    private void addToCart(String item, int quantity) {
        String json = new Gson().toJson(new CartItem(item, quantity));
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(BASE_URL + "/addToCart")
            .post(body)
            .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JOptionPane.showMessageDialog(frame, "Item added to cart");
            } else {
                JOptionPane.showMessageDialog(frame, "Error adding item to cart");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding item to cart");
        }
    }

    // Checkout Panel
    private JPanel createCheckoutPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JTextArea cartList = new JTextArea();
        JButton completeButton = new JButton("Complete Order");

        panel.add(new JScrollPane(cartList));
        panel.add(completeButton);

        completeButton.addActionListener(e -> completeOrder());

        return panel;
    }

    // Complete order
    private void completeOrder() {
        Request request = new Request.Builder()
            .url(BASE_URL + "/completeOrder")
            .post(RequestBody.create("", MediaType.parse("application/json")))
            .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JOptionPane.showMessageDialog(frame, "Order completed successfully");
            } else {
                JOptionPane.showMessageDialog(frame, "Error completing order");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error completing order");
        }
    }

    // Pending Orders Panel
    private JPanel createPendingOrdersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTextArea orderList = new JTextArea();
        JButton processButton = new JButton("Process Order");

        panel.add(new JScrollPane(orderList), BorderLayout.CENTER);
        panel.add(processButton, BorderLayout.SOUTH);

        processButton.addActionListener(e -> processOrder());

        return panel;
    }

    // Process order
    private void processOrder() {
        Request request = new Request.Builder()
            .url(BASE_URL + "/processOrder")
            .post(RequestBody.create("", MediaType.parse("application/json")))
            .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JOptionPane.showMessageDialog(frame, "Order processed successfully");
            } else {
                JOptionPane.showMessageDialog(frame, "Error processing order");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error processing order");
        }
    }

    // Item Modifications Panel
    private JPanel createItemModificationsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTextArea itemList = new JTextArea();
        JTextField itemField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JButton updateButton = new JButton("Update Item");
        JButton addButton = new JButton("Add Item");
        JButton deleteButton = new JButton("Delete Item");

        panel.add(new JScrollPane(itemList), BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new GridLayout(5, 2));
        bottomPanel.add(new JLabel("Item:"));
        bottomPanel.add(itemField);
        bottomPanel.add(new JLabel("Quantity:"));
        bottomPanel.add(quantityField);
        bottomPanel.add(new JLabel("Price:"));
        bottomPanel.add(priceField);
        bottomPanel.add(updateButton);
        bottomPanel.add(addButton);
        bottomPanel.add(deleteButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> {
            String item = itemField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            updateItem(item, quantity, price);
        });

        addButton.addActionListener(e -> {
            String item = itemField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            addItem(item, quantity, price);
        });

        deleteButton.addActionListener(e -> {
            String item = itemField.getText();
            deleteItem(item);
        });

        return panel;
    }

    // Update item
    private void updateItem(String item, int quantity, double price) {
        String json = new Gson().toJson(new Item(item, quantity, price));
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(BASE_URL + "/updateItem")
            .post(body)
            .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JOptionPane.showMessageDialog(frame, "Item updated successfully");
            } else {
                JOptionPane.showMessageDialog(frame, "Error updating item");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating item");
        }
    }

    // Add item
    private void addItem(String item, int quantity, double price) {
        String json = new Gson().toJson(new Item(item, quantity, price));
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(BASE_URL + "/addItem")
            .post(body)
            .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JOptionPane.showMessageDialog(frame, "Item added successfully");
            } else {
                JOptionPane.showMessageDialog(frame, "Error adding item");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding item");
        }
    }

    // Delete item
    private void deleteItem(String item) {
        String json = new Gson().toJson(new Item(item, 0, 0.0)); // quantity and price are not needed
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(BASE_URL + "/deleteItem")
            .post(body)
            .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JOptionPane.showMessageDialog(frame, "Item deleted successfully");
            } else {
                JOptionPane.showMessageDialog(frame, "Error deleting item");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error deleting item");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Shoppiee());
    }
}
