package hackit.dto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("geoDataString")
    @Expose
    private String geoDataString;
    @SerializedName("link")
    @Expose
    private String link;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getGeoDataString() {
        return geoDataString;
    }

    public void setGeoDataString(String geoDataString) {
        this.geoDataString = geoDataString;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}