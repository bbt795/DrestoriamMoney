package com.drestoriam.drestoriammoney.classes;

public class Job {

    private String category;
    private String title;
    private int amount;

    public Job(String category, String title, int amount){

        this.category = category;
        this.title = title;
        this.amount = amount;

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
