import java.io.Serializable;

public class Waifu
implements Serializable {
    public String name;
    public String fileType;
    public Series series;

    public Waifu(String name, Series series) {
        this.name = name;
        this.series = series;
        this.fileType = "png";
    }

    public Waifu(String name) {
        this.name = name;
        this.fileType = "png";
    }

    public Waifu(String name, String series, String fileType) {
        this.name = name;
        this.series = new Series(series);
        this.fileType = fileType;
    }

    public Waifu() {
        this.name = "Chitoge Kirisaki";
        this.series = new Series("Nisekoi");
        this.fileType = "png";
    }

    public String giveWaifu(String i) {
        return ".me Your waifu is " + this.name + " from " + i;
    }

    public String giveWaifu() {
        return ".me Your waifu is " + this.name + " from " + this.series.getNames().get(0);
    }

    public String getName() {
        return this.name;
    }
}