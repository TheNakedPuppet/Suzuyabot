import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Channel {
    public String Name;
    public String commandsFile;
    public String logFile;
    public ArrayList<Command> commands = new ArrayList();
    public File log;

    public Channel(SuzuyaBot bot, String channelName) {
        this.Name = channelName;
        this.commandsFile = String.valueOf(channelName) + "Commands.ser";
        this.logFile = "chat/" + channelName + ".txt";
        if (!new File(this.commandsFile).exists()) {
            SuzuyaBot.serializeObject(this.commands, this.commandsFile);
            System.out.println("New commands file for " + channelName + " saved in " + this.commandsFile);
        }
        if (!new File(this.logFile).exists()) {
            try {
                PrintWriter out = new PrintWriter(new FileWriter(new File(this.logFile), true));
                java.sql.Date resultdate = new java.sql.Date(System.currentTimeMillis());
                out.write("Log started at " + SuzuyaBot.fullDateFormat.format(resultdate));
                out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.commands = (ArrayList)SuzuyaBot.deserializeObject(this.commandsFile);
        }
        catch (Exception e) {
            System.out.println("Error loading commands file");
        }
        bot.joinChannel(channelName);
        long yourmilliseconds = System.currentTimeMillis();
        java.sql.Date resultdate = new java.sql.Date(yourmilliseconds);
    }

    public void writeToLog(String message) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(this.logFile, true));
            out.write("\n" + message);
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}