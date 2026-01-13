package org.example.util;

public final class PriceCalculator {
    private static double tvaRate = 0.0;
    public static void setTvaRate(double newRate) {
        tvaRate = newRate;
    }
    public static double getTvaRate() {
        return tvaRate;
    }

    public static double calculatePriceWithTVA(double basePrice) {
        return basePrice * (1 + tvaRate);
    }
}