package com.tecnocampus.autocarrent.Domain;

public class CarCategories {

    public enum Categories {
        ECONOMY, LUXURY, FAMILY, MINI;
    }
    public static int getDepositPrice(String category) {
        return switch (Categories.valueOf(category.toUpperCase())) {
            case ECONOMY -> 500;
            case LUXURY -> 1200;
            case FAMILY -> 700;
            case MINI -> 400;
        };
    }

    public static int getDailyPrice(String category) {
        return switch (Categories.valueOf(category.toUpperCase())) {
            case ECONOMY -> 30;
            case LUXURY -> 80;
            case FAMILY -> 50;
            case MINI -> 20;
        };
    }
}
