package org.example.model;

public enum ProductCategory {
    APPETIZER("Aperitive"),
    MAIN_COURSE("Fel Principal"),
    DESSERT("Desert"),
    ALCOHOLIC_DRINK("Băuturi Alcoolice"),
    NON_ALCOHOLIC_DRINK("Băuturi Răcoritoare"),
    OTHER("Altele");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}