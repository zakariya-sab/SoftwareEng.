package models;

import java.time.LocalDateTime;

public class CurrencyRate {

    private String currencyCode;
    private String currencyName;
    private double rateToMad;  // 1  currency = X MAD
    private LocalDateTime lastUpdated;
    //constructer
    public CurrencyRate(String currencyCode, String currencyName, double rateToMad, LocalDateTime lastUpdated) {
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.rateToMad = rateToMad;  // This means: 1 [currency] = rateToMad MAD
        this.lastUpdated = lastUpdated;
    }

    // Convert FROM this currency TO MAD
    // Example: 100 USD → MAD: 100 × 10.0 = 1000 MAD
    public double convertToMad(double amount) {
        return amount * rateToMad;
    }

    // Convert FROM MAD TO this currency
    // Example: 1000 MAD → USD: 1000 ÷ 10.0 = 100 USD
    public double convertFromMad(double amount) {
        return amount / rateToMad;
    }

    // Convert between any two currencies
    public static double convertCurrency(double amount, CurrencyRate fromCurrency, CurrencyRate toCurrency) {
        // Step 1: Convert to MAD first
        double amountInMad = fromCurrency.convertToMad(amount);
        // Step 2: Convert from MAD to target currency
        return toCurrency.convertFromMad(amountInMad);
    }

    // Getters and Setters
    public String getCurrencyCode() { return currencyCode; }
    public String getCurrencyName() { return currencyName; }
    public double getRateToMad() { return rateToMad; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    public void setRateToMad(double rateToMad) { this.rateToMad = rateToMad; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

}