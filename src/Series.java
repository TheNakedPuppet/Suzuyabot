import java.io.Serializable;
import java.util.ArrayList;

public class Series
implements Serializable {
    private ArrayList<String> names = new ArrayList();
    private ArrayList<Waifu> waifus = new ArrayList();

    public Series(String name) {
        this.names.add(name);
    }

    public Series(String[] name) {
        for (String s : name) {
            this.names.add(s);
        }
    }

    public Series(String name, Waifu initalWaifu) {
        this.names.add(name);
        this.waifus.add(initalWaifu);
    }

    public Series(String name, Waifu[] initalWaifuArray) {
        this.names.add(name);
        for (int i = 0; i < initalWaifuArray.length; ++i) {
            this.waifus.add(initalWaifuArray[i]);
        }
    }

    public Series(String name, ArrayList<Waifu> initalWaifuArray) {
        this.names.add(name);
        for (int i = 0; i < initalWaifuArray.size(); ++i) {
            this.waifus.add(initalWaifuArray.get(i));
        }
    }

    public Series(String[] namesArray, ArrayList<Waifu> initalWaifuArray) {
        for (String s : namesArray) {
            this.names.add(s);
        }
        for (Waifu w : initalWaifuArray) {
            this.waifus.add(w);
        }
    }

    public void addWaifu(Waifu waifu) {
        this.waifus.add(waifu);
    }

    public boolean removeWaifu(String waifuName) {
        for (int i = 0; i < this.waifus.size(); ++i) {
            if (!this.waifus.get(i).getName().equalsIgnoreCase(waifuName)) continue;
            this.waifus.remove(i);
            return true;
        }
        return false;
    }

    public void addAlternateName(String name) {
        this.names.add(name);
    }

    public void removeAlternateName(String name) {
        this.names.remove(name);
    }

    public ArrayList<String> getNames() {
        return this.names;
    }

    public String getNamesAsString() {
        String s = null;
        for (int i = 0; i < this.names.size(); ++i) {
            s = String.valueOf(s) + this.names.get(i);
            if (i == this.names.size() - 1) continue;
            s = String.valueOf(s) + " | ";
        }
        return s;
    }

    public ArrayList<Waifu> getWaifus() {
        return this.waifus;
    }
}
