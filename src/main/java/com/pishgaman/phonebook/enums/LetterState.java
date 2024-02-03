package com.pishgaman.phonebook.enums;

public enum LetterState {
    DRAFT("draft", "پیش‌نویس"),
    DELIVERED("delivered", "تحویل شده");

    private final String state;
    private final String persianCaption;

    LetterState(String state, String persianCaption) {
        this.state = state;
        this.persianCaption = persianCaption;
    }

    public String getState() {
        return state;
    }

    public String getPersianCaption() {
        return persianCaption;
    }
}

