package models.adventure.time;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 07.12.13
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
public class TimeOptionRating {

    private String timeOptionId;

    private Double rating;


    public TimeOptionRating(String timeOptionId, Double rating) {
        this.timeOptionId = timeOptionId;
        this.rating = rating;
    }

    public String getTimeOptionId() {
        return timeOptionId;
    }

    public void setTimeOptionId(String timeOptionId) {
        this.timeOptionId = timeOptionId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "TimeOptionRating{" +
                "timeOptionId='" + timeOptionId + '\'' +
                ", rating=" + rating +
                '}';
    }
}
