package com.example.liengua;

import java.util.List;
import java.util.Objects;

public class FeedbackItem {
    private int id;               // Unique identifier for each feedback item
    private boolean isFeedbackProvided;
    private boolean statusChange;
    private String originalPhrase; // The original phrase
    private String feedback;      // The feedback provided by the user
    private boolean suggestDelete; // Whether the user suggests deletion

    // Constructor
    public FeedbackItem(String originalPhrase) {
        this.id = 0;
        this.isFeedbackProvided = false;
        this.originalPhrase = originalPhrase;
        this.feedback = "";
        this.suggestDelete = false;
        this.statusChange = false;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIsFeedbackProvided(boolean set) {
        this.isFeedbackProvided = set;
    }

    public Boolean getFeedbackProvided() {
        return isFeedbackProvided;
    }

    public String getOriginalPhrase() {
        return originalPhrase;
    }

    public void setOriginalPhrase(String originalPhrase) {
        this.originalPhrase = originalPhrase;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public boolean isSuggestDelete() {
        return suggestDelete;
    }

    public void setSuggestDelete(boolean suggestDelete) {
        this.suggestDelete = suggestDelete;
    }

    public boolean getStatusChange() {return this.statusChange;}
    public void setStatusChange(Boolean suggestDelete, String feedback) {
        if(suggestDelete != null && (suggestDelete != this.suggestDelete)) {
            this.statusChange = true;
        } else if (feedback!=null && !feedback.equals(this.feedback)) {
            if(feedback.isEmpty()) {
                this.statusChange = true;
            } else this.statusChange = this.feedback.isEmpty();
            this.setFeedback(feedback);
        } else {
            this.statusChange = false;
        }
    }
}
