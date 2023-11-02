package edu.csun.desktopapp440.objects;

import java.sql.Date;

public class Reviews {

    private String Reviewer, quality, Review;

    private Date datePosted;

    public Reviews(String reviewer, String quality, String review, Date datePosted){
        Reviewer = reviewer;
        this.quality = quality;
        Review = review;
        this.datePosted = datePosted;
    }


    public String getReviewer() {
        return Reviewer;
    }

    public void setReviewer(String reviewer) {
        Reviewer = reviewer;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getReview() {
        return Review;
    }

    public void setReview(String review) {
        Review = review;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }


}
