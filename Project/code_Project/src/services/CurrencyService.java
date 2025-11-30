package services;

import models.CurrencyRate;
import models.OperationResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencyService {

    public List<CurrencyRate> getAllCurrencyRates() throws SQLException, ClassNotFoundException {
        List<CurrencyRate> currencyRates = new ArrayList<>();

        Connection conn = DataBaseConnection.getConnection();

        String query = "SELECT * FROM currency_rates ORDER BY currency_code";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            CurrencyRate rate = new CurrencyRate(
                    rs.getString("currency_code"),
                    rs.getString("currency_name"),
                    rs.getDouble("rate_to_mad"),
                    rs.getTimestamp("last_updated").toLocalDateTime()
            );
            currencyRates.add(rate);
        }

        rs.close();
        stmt.close();
        conn.close();

        return currencyRates;
    }

    public CurrencyRate getCurrencyRate(String currencyCode) throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();

        String query = "SELECT * FROM currency_rates WHERE currency_code = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, currencyCode.toUpperCase());

        ResultSet rs = stmt.executeQuery();

        CurrencyRate rate = null;
        if (rs.next()) {
            rate = new CurrencyRate(
                    rs.getString("currency_code"),
                    rs.getString("currency_name"),
                    rs.getDouble("rate_to_mad"),
                    rs.getTimestamp("last_updated").toLocalDateTime()
            );
        }

        rs.close();
        stmt.close();
        conn.close();

        return rate;
    }

    public OperationResult updateCurrencyRate(String currencyCode, double newRate) throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();

        String query = "UPDATE currency_rates SET rate_to_mad = ?, last_updated = CURRENT_TIMESTAMP WHERE currency_code = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setDouble(1, newRate);
        stmt.setString(2, currencyCode.toUpperCase());

        int rowsAffected = stmt.executeUpdate();

        stmt.close();
        conn.close();

        if (rowsAffected > 0) {
            return OperationResult.success("Currency rate updated successfully: " + currencyCode);
        } else {
            return OperationResult.error("Currency not found: " + currencyCode);
        }
    }

    public double convertCurrency(double amount, String fromCurrency, String toCurrency) throws SQLException, ClassNotFoundException {
        // If same currency, return same amount
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return amount;
        }

        // Get both currency rates
        CurrencyRate fromRate = getCurrencyRate(fromCurrency);
        CurrencyRate toRate = getCurrencyRate(toCurrency);

        if (fromRate == null) {
            throw new IllegalArgumentException("Unknown source currency: " + fromCurrency);
        }

        if (toRate == null) {
            throw new IllegalArgumentException("Unknown target currency: " + toCurrency);
        }

        // Convert using MAD as base
        // Step 1: Convert from source currency to MAD
        double amountInMad = amount * fromRate.getRateToMad();

        // Step 2: Convert from MAD to target currency
        double convertedAmount = amountInMad / toRate.getRateToMad();

        return convertedAmount;
    }

    public Map<String, Double> convertToAllCurrencies(double amount, String fromCurrency) throws SQLException, ClassNotFoundException {
        Map<String, Double> conversions = new HashMap<>();

        List<CurrencyRate> allRates = getAllCurrencyRates();
        CurrencyRate fromRate = getCurrencyRate(fromCurrency);

        if (fromRate == null) {
            throw new IllegalArgumentException("Unknown source currency: " + fromCurrency);
        }

        // Convert amount to MAD first
        double amountInMad = amount * fromRate.getRateToMad();

        // Then convert to all other currencies
        for (CurrencyRate rate : allRates) {
            if (!rate.getCurrencyCode().equals(fromCurrency)) {
                double convertedAmount = amountInMad / rate.getRateToMad();
                conversions.put(rate.getCurrencyCode(), convertedAmount);
            }
        }

        return conversions;
    }

    public OperationResult addCurrency(String currencyCode, String currencyName, double rateToMad) throws SQLException, ClassNotFoundException  {
        Connection conn = DataBaseConnection.getConnection();

        String query = "INSERT INTO currency_rates (currency_code, currency_name, rate_to_mad) VALUES (?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, currencyCode.toUpperCase());
        stmt.setString(2, currencyName);
        stmt.setDouble(3, rateToMad);

        int rowsAffected = stmt.executeUpdate();

        stmt.close();
        conn.close();

        if (rowsAffected > 0) {
            return OperationResult.success("Currency added successfully: " + currencyCode);
        } else {
            return OperationResult.error("Failed to add currency: " + currencyCode);
        }
    }

    public OperationResult deleteCurrency(String currencyCode) throws SQLException, ClassNotFoundException {
        // Don't allow deletion of MAD (base currency)
        if (currencyCode.equalsIgnoreCase("MAD")) {
            return OperationResult.error("Cannot delete base currency (MAD)");
        }

        Connection conn = DataBaseConnection.getConnection();

        String query = "DELETE FROM currency_rates WHERE currency_code = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, currencyCode.toUpperCase());

        int rowsAffected = stmt.executeUpdate();

        stmt.close();
        conn.close();

        if (rowsAffected > 0) {
            return OperationResult.success("Currency deleted successfully: " + currencyCode);
        } else {
            return OperationResult.error("Currency not found: " + currencyCode);
        }
    }

    public double getExchangeRate(String fromCurrency, String toCurrency) throws SQLException, ClassNotFoundException {
        // If same currency, return 1.0
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return 1.0;
        }

        // Get both currency rates
        CurrencyRate fromRate = getCurrencyRate(fromCurrency);
        CurrencyRate toRate = getCurrencyRate(toCurrency);

        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Unknown currency code");
        }

        // Calculate direct exchange rate
        double exchangeRate = fromRate.getRateToMad() / toRate.getRateToMad();

        return exchangeRate;
    }
}