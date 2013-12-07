package models.adventure.place;

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 07.12.13
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
public class PlaceOptionRating {

    private String placeOptionId;

    private Double rating;


    public PlaceOptionRating(String placeOptionId, Double rating) {
        this.placeOptionId = placeOptionId;
        this.rating = rating;
    }

    public String getPlaceOptionId() {
        return placeOptionId;
    }

    public void setPlaceOptionId(String placeOptionId) {
        this.placeOptionId = placeOptionId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "PlaceOptionRating{" +
                "placeOptionId='" + placeOptionId + '\'' +
                ", rating=" + rating +
                '}';
    }
}
