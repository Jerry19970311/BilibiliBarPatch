import java.util.List;

public class SimpleFloor {
    private String floor;
    private String author;
    private String text;
    private String time;
    private List<String> images;

    public String getAuthor() {
        return author;
    }

    public String getFloor() {
        return floor;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public List<String> getImages() {
        return images;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
