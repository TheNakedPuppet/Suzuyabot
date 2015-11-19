import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Timer;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

public class SuzuyaBot
extends PircBot {
    boolean canRespond = true;
    boolean canButt = true;
    final Pattern SymbolPattern = Pattern.compile("[_,$^]");

    ArrayList<String> ModList;
    static String Channel;
    static Properties properties;
    static ArrayList<Series> SeriesArray;
    static int linksNumberOfLines;
    static Map<String, String> nicknames;
    static HashMap<String,String> levels;
    static SimpleDateFormat sdf;
    static SimpleDateFormat fullDateFormat;
    static Channel[] channels;
    static int channelsCount;
    static String nicknamesPath;
    static String linksFile;
    static String catgirlLinksFile;
    static String waifusFile;
    static String mainChannel;
    static String botName;
    static String oauth;
    static boolean canHealthy;
    static boolean isAnnoying;
    static boolean isHealthy;
    static boolean isWaifu;
    static boolean shouldLog;
    static int healthyCooldown;
    static int waifuCooldown;
    static Timer healthyTimer;
    static Timer waifuTimer;
    static String ReSubString;
    static String SubString;
    static Timer timer;
    

    static {
        properties = new Properties();
        SeriesArray = new ArrayList();
        linksNumberOfLines = 0;
        nicknames = new HashMap<String, String>();
        sdf = new SimpleDateFormat("<HH:mm>");
        fullDateFormat = new SimpleDateFormat("MM-dd-yyyy <HH:mm>");
        channelsCount = 1;
        nicknamesPath = "Nicknames.ser";
        linksFile = "Links.txt";
        catgirlLinksFile = "Links.txt";
        waifusFile = "WaifusV2.txt";
        mainChannel = "#ayitzchance";
        canHealthy = true;
        isAnnoying = true;
        isHealthy = true;
        isWaifu = true;
        shouldLog = false;
        healthyCooldown = 3000;
        waifuCooldown = 3000;
    }

    public static void main(String[] args) {
        if (!new File("config.properties").exists()) {
            SuzuyaBot.setPropertyValue("linksFile", "Links.txt");
            SuzuyaBot.setPropertyValue("mainChannel", "#thenakedpuppet");
            SuzuyaBot.setPropertyValue("botName", "Enter Bot Name");
            SuzuyaBot.setPropertyValue("oauth", "Enter Bot's oauth (From http://waa.ai/Iq6)");
            SuzuyaBot.setPropertyValue("shouldLog", "0");
            SuzuyaBot.setPropertyValue("isAnnoying", "true");
            SuzuyaBot.setPropertyValue("isHealthy", "true");
            SuzuyaBot.setPropertyValue("isWaifu", "true");
            SuzuyaBot.setPropertyValue("healthyCooldown", "30");
            SuzuyaBot.setPropertyValue("waifuCooldown", "30");
            SuzuyaBot.setPropertyValue("resubString", "%name% thanks for staying around for %months% months PogChamp");
            SuzuyaBot.setPropertyValue("subString", "WELCOME TO THE TOMATO PATCH %name%");
        }
        linksFile = SuzuyaBot.getPropertyValue("linksFile");
        mainChannel = SuzuyaBot.getPropertyValue("mainChannel");
        botName = SuzuyaBot.getPropertyValue("botName");
        oauth = SuzuyaBot.getPropertyValue("oauth");
        isAnnoying = Boolean.parseBoolean(SuzuyaBot.getPropertyValue("isAnnoying"));
        isHealthy = Boolean.parseBoolean(SuzuyaBot.getPropertyValue("isHealthy"));
        isWaifu = Boolean.parseBoolean(SuzuyaBot.getPropertyValue("isWaifu"));
        healthyCooldown = Integer.parseInt(SuzuyaBot.getPropertyValue("healthyCooldown")) * 1000;
        waifuCooldown = Integer.parseInt(SuzuyaBot.getPropertyValue("waifuCooldown")) * 1000;
        ReSubString = SuzuyaBot.getPropertyValue("ReSubString");
        SubString = SuzuyaBot.getPropertyValue("SubString");
        healthyTimer = new Timer(healthyCooldown, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SuzuyaBot.canHealthy = true;
            }
        });
        waifuTimer = new Timer(waifuCooldown, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SuzuyaBot.canHealthy = true;
            }
        });
        if (Integer.parseInt(SuzuyaBot.getPropertyValue("shouldLog")) != 0) {
            shouldLog = true;
        }
        channels = new Channel[args.length + 1];
        SuzuyaBot bot = new SuzuyaBot(botName);
        SuzuyaBot.channels[0] = new Channel(bot, mainChannel);
        if (args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                SuzuyaBot.channels[i + 1] = new Channel(bot, args[i]);
            }
        }
        
    }

    public SuzuyaBot(String botName) {
        this.timer = new Timer(500, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                SuzuyaBot.this.canRespond = true;
            }
        });
        timer.setRepeats(false);
        timer.start();
        this.ModList = new ArrayList();
        if (botName == "Enter Bot Name") {
            System.out.println("No bot name selected");
        }
        this.setName(botName);
        if (!new File(nicknamesPath).exists()) {
            SuzuyaBot.serializeObject(nicknames, nicknamesPath);
            System.out.println("New nicknames file saved in " + nicknamesPath);
        }
        try {
            nicknames = (HashMap)SuzuyaBot.deserializeObject(nicknamesPath);
            nicknames.put("thenakedpuppet", "TNP");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading nicknames file");
        }
        try {
            if (botName == "Enter Bot Name") {
                System.out.println("No bot name selected");
            }
            this.connect("irc.twitch.tv", 6667, oauth);
        }
        catch (IOException | IrcException e) {
            e.printStackTrace();
        }
        try {
            linksNumberOfLines = SuzuyaBot.countLines(linksFile);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (new File(nicknamesPath).exists()) {
        	levels = loadLevels();
        }
        SuzuyaBot.loadWaifusFromTxt("WaifusV2.txt");
    }
    @Override
    protected void onAction(String sender,String login,String hostname,String target,String action){
    	System.out.println("Announced Update"); 
    	String[] params = action.split("> "); 
    	String user;
    	int level;
    	params = params[1].split(" ",2); 
    	user = params[0].toLowerCase(); 
    	params = params[1].split("Level "); 
    	level = Integer.parseInt(params[1].replace("!", "")); 
    	System.out.println("Updating level " + user + " to " + level);
    	updateLevel(user,level);
    }
    @Override
    protected void onUserMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
        String modName;
        super.onUserMode(channel, sourceNick, sourceLogin, sourceHostname, recipient);
        if (recipient.contains((CharSequence)"+o")) {
            channel = recipient.substring(0, recipient.lastIndexOf(" +"));
            modName = recipient.substring(recipient.lastIndexOf(" ") + 1, recipient.length());
            this.addMod(channel, modName);
        }
        if (recipient.contains((CharSequence)"-o")) {
            channel = recipient.substring(0, recipient.lastIndexOf(" -"));
            modName = recipient.substring(recipient.lastIndexOf(" ") + 1, recipient.length());
            this.removeMod(channel, modName);
        }
    }

    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        int cd;
        String[] params;
        String realName = sender;
        String nickname = this.getNickname(sender);
        long yourmilliseconds = System.currentTimeMillis();
        boolean isMod = this.isMod(channel, realName);
        int userLevel;
        if(levels.containsKey(sender.toLowerCase())){
        	userLevel = Integer.parseInt(levels.get(sender.toLowerCase()));
        }else{
        	userLevel = 0;
        	levels.put(sender.toLowerCase(), "0");
        }
        System.out.println(realName + " Lv " + userLevel);
        Date resultdate = new Date(yourmilliseconds);
        Channel currentChannel = channels[0];
        for (int i = 0; i < channelsCount; ++i) {
            if (!SuzuyaBot.channels[i].Name.equals(channel)) continue;
            currentChannel = channels[i];
        }
        if(canRespond){
        	timer.restart();
        	canRespond = false;
        }
        //////SUBSCRIBER STUFF/////////////////////////////////////////////////////////////
        if (sender.equalsIgnoreCase("twitchnotify") && message.contains("subscribed")){
        	System.out.println("triggered");
            if(message.contains("months")) {
            	System.out.println("triggered resub");
                sendMessage(channel, getResubString(message));
            }else{
                System.out.println("triggered sub");
                sendMessage(channel, getSubString(message));
            }
        }
        
        //////LEVELS STUFF////////////////////////////////////////////////////////////////////
        
        if(sender.equalsIgnoreCase("mikuia")&&(message.contains("Lv")||message.contains("Level"))){
        	String user = null;
        	int level = 0;
        	//System.out.println(message);
        	if(message.matches(".+ Lv [0-9]+.+")){
        		//System.out.println("Manual Update");
        		params = message.split(": ");
        		user = params[0].toLowerCase();
        		params = params[1].split("Lv ");
        		params = params[1].split(" ", 2);
        		level = Integer.parseInt(params[0]);
        		//System.out.println("Updating level " + user + " to " + level);
        	}
        	updateLevel(user,level);
        }
        
        
        
        
        
        
        
        
        
        //////COMMANDS STUFF//////////////////////////////////////////////////////////////////
        if (message.toLowerCase().matches("!suzuya dummy .+") && (channel.contains((CharSequence)sender) || sender.equalsIgnoreCase("thenakedpuppet"))) {
            String[] params2 = this.getParameters(message, "!suzuya dummy ", 2);
            currentChannel.commands.add(new Command(params2[1], 1, params2[0], 1));
            this.sendMessage(channel, "Added command " + params2[1].toLowerCase() + " MrDestructoid");
            SuzuyaBot.serializeObject(currentChannel.commands, currentChannel.commandsFile);
            return;
        }
        if (message.toLowerCase().matches("!suzuya addcom .+ \\| .+ \\| .+") && (channel.contains((CharSequence)sender) || sender.equalsIgnoreCase("thenakedpuppet"))) {
            String[] params3;
            for (String s : params3 = message.replace((CharSequence)"!suzuya addcom ", (CharSequence)"").split(" \\| ")) {
                System.out.println(s);
            }
            params3[1] = SuzuyaBot.replaceConstants(params3[1].trim());
            for (int i2 = 0; i2 < currentChannel.commands.size(); ++i2) {
                if (!currentChannel.commands.get((int)i2).trigger.equalsIgnoreCase(params3[0])) continue;
                currentChannel.commands.get((int)i2).response = params3[2];
                SuzuyaBot.serializeObject(currentChannel.commands, currentChannel.commandsFile);
                this.sendMessage(channel, "Command " + params3[1] + " edited I think MrDestructoid ");
                return;
            }
            if (message.toLowerCase().matches("!suzuya addcom .+ \\| .+ \\| .+ \\| .+ \\| \\d") && (channel.contains((CharSequence)sender) || sender.equalsIgnoreCase("thenakedpuppet"))) {
                params3[3] = SuzuyaBot.replaceConstants(params3[3].trim());
                currentChannel.commands.add(new Command(params3[0], Integer.parseInt(params3[1].trim()), params3[2], Integer.parseInt(params3[3].trim()), Integer.parseInt(params3[4].trim())*1000));
                this.sendMessage(channel, "Added command " + params3[0].toLowerCase() + " MrDestructoid");
                SuzuyaBot.serializeObject(currentChannel.commands, currentChannel.commandsFile);
                return;
            }
            if (message.toLowerCase().matches("!suzuya addcom .+ \\| .+ \\| .+ \\| .+") && (channel.contains((CharSequence)sender) || sender.equalsIgnoreCase("thenakedpuppet"))) {
                params3[3] = SuzuyaBot.replaceConstants(params3[3].trim());
                currentChannel.commands.add(new Command(params3[0], Integer.parseInt(params3[1].trim()), params3[2], Integer.parseInt(params3[3].trim())));
                this.sendMessage(channel, "Added command " + params3[0].toLowerCase() + " MrDestructoid");
                SuzuyaBot.serializeObject(currentChannel.commands, currentChannel.commandsFile);
                return;
            }
            currentChannel.commands.add(new Command(params3[0], Integer.parseInt(params3[1].trim()), params3[2]));
            this.sendMessage(channel, "Added command " + params3[0].toLowerCase() + " MrDestructoid");
            SuzuyaBot.serializeObject(currentChannel.commands, currentChannel.commandsFile);
            return;
        }
        if (message.toLowerCase().matches("!suzuya remove .+") && (isMod || sender.equalsIgnoreCase("thenakedpuppet"))) {
            params = this.getParameters(message, "!suzuya remove ", 1);
            boolean flag = false;
            for (int i2 = 0; i2 < currentChannel.commands.size(); ++i2) {
                if (!currentChannel.commands.get((int)i2).trigger.equalsIgnoreCase(params[0])) continue;
                currentChannel.commands.remove(i2);
                flag = true;
            }
            if (flag) {
                SuzuyaBot.serializeObject(currentChannel.commands, currentChannel.commandsFile);
                this.sendMessage(channel, "Command " + params[0] + " removed I think MrDestructoid ");
            }
        }
        if (message.toLowerCase().matches("!suzuya edit .+") && (isMod || sender.equalsIgnoreCase("thenakedpuppet"))) {
            params = this.getParameters(message, "!suzuya edit ", 2);
            for (int i3 = 0; i3 < currentChannel.commands.size(); ++i3) {
                if (!currentChannel.commands.get((int)i3).trigger.equalsIgnoreCase(params[1])) continue;
                currentChannel.commands.get((int)i3).response = params[0];
                SuzuyaBot.serializeObject(currentChannel.commands, currentChannel.commandsFile);
                this.sendMessage(channel, "Command " + params[1] + " edited I think MrDestructoid ");
                return;
            }
        }
        for (int i4 = 0; i4 < currentChannel.commands.size(); ++i4) {
            long startTime = System.currentTimeMillis();
            String str1 = currentChannel.commands.get(i4).getResponse(message, userLevel);
            if (str1.equals("")) continue;
            try {
                Thread.sleep(1);
                this.sendMessage(channel, str1.replace((CharSequence)"%sender%", (CharSequence)realName).replace((CharSequence)"%nick%", (CharSequence)nickname));
                long endTime = System.currentTimeMillis();
                System.out.println("Took " + (endTime - startTime) + " ns");
            }
            catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return;
        }
        if (message.toLowerCase().matches("!suzuya nickname .+")) {
            String[] params4 = this.getParameters(message, "!suzuya nickname", 2);
            System.out.println("name = " + params4[1] + " nick = " + params4[0]);
            if (sender.equalsIgnoreCase(params4[1]) || sender.equalsIgnoreCase("thenakedpuppet")) {
                SuzuyaBot.setNickname(this, currentChannel, params4);
            }
            System.out.println("Error adding nick");
        }
        if (message.toLowerCase().matches("!suzuya setnickname .+")) {
            String[] params5 = this.getParameters(message, "!suzuya setnickname", 2);
            System.out.println("name = " + params5[1] + " nick = " + params5[0]);
            if (sender.equalsIgnoreCase(params5[1]) || sender.equalsIgnoreCase("thenakedpuppet")) {
                SuzuyaBot.setNickname(this, currentChannel, params5);
            }
            System.out.println("Error adding nick");
        }
        if (message.toLowerCase().matches("!suzuya setnick .+")) {
            String[] params6 = this.getParameters(message, "!suzuya setnick", 2);
            System.out.println("name = " + params6[1] + " nick = " + params6[0]);
            if (sender.equalsIgnoreCase(params6[1]) || sender.equalsIgnoreCase("thenakedpuppet")) {
                SuzuyaBot.setNickname(this, currentChannel, params6);
            }
            System.out.println("Error adding nick");
        }
        if (message.toLowerCase().matches("!suzuya removenickname .+")) {
            String[] params7 = this.getParameters(message, "!suzuya removenickname", 1);
            if (sender.equalsIgnoreCase(sender) || isMod || sender.equalsIgnoreCase("thenakedpuppet")) {
                nicknames.remove(params7[0].toLowerCase());
                this.sendMessage(channel, "Nickname probably removed MrDestructoid ");
                SuzuyaBot.serializeObject(nicknames, nicknamesPath);
                return;
            }
            System.out.println("Error removing nick");
        }
        sender = this.getNickname(sender);
       /* if (message.equalsIgnoreCase("!np") || message.equalsIgnoreCase("!song") || message.equalsIgnoreCase("!map")) {
            String content = null;
            Throwable startTime = null;
			Object i2 = null;
			try {
			    Scanner scanner = new Scanner(new File("np.txt")).useDelimiter("\\Z");
			    try {
			        content = ".me - " + scanner.next() + " MrDestructoid ";
			        scanner.close();
			    }
			    finally {
			        if (scanner != null) {
			            scanner.close();
			        }
			    }
			}
			catch (Throwable i3) {
			    if (startTime == null) {
			        startTime = i3;
			    } else if (startTime != i3) {
			        startTime.addSuppressed(i3);
			    }
			    try {
					throw startTime;
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
            this.canRespond = false;
            timer.restart();
            this.sendMessage(channel, content);
        }*/
        if (message.equalsIgnoreCase("!toggle annoying")) {
            isAnnoying = !isAnnoying;
            SuzuyaBot.setPropertyValue("isAnnoying", isAnnoying);
        }
        if (message.equalsIgnoreCase("!toggle healthy")) {
            isHealthy = !isHealthy;
            SuzuyaBot.setPropertyValue("isHealthy", isHealthy);
        }
        if (message.equalsIgnoreCase("!toggle waifu")) {
            isWaifu = !isWaifu;
            SuzuyaBot.setPropertyValue("isWaifu", isWaifu);
        }
        if (message.equalsIgnoreCase("!toggle waifus")) {
            isWaifu = !isWaifu;
            SuzuyaBot.setPropertyValue("isWaifu", isWaifu);
        }
        if (message.toLowerCase().matches("!suzuya healthycooldown .+") && (channel.contains((CharSequence)realName) || realName.equalsIgnoreCase("kittehcommando") || realName.equalsIgnoreCase("tomatera9") || realName.equalsIgnoreCase("thenakedpuppet"))) {
            String[] params8 = this.getParameters(message, "!suzuya healthycooldown ", 1);
            cd = Integer.parseInt(params8[0]);
            SuzuyaBot.setPropertyValue("healthyCooldown", params8[0]);
            healthyTimer.setDelay(cd * 100);
            healthyTimer.setInitialDelay(cd * 1000);
            healthyTimer.restart();
            this.sendMessage(channel, "Cooldown changed to " + params8[0] + " seconds");
        }
        if (message.toLowerCase().matches("!suzuya waifucooldown .+") && (channel.contains((CharSequence)realName) || realName.equalsIgnoreCase("kittehcommando") || realName.equalsIgnoreCase("tomatera9") || realName.equalsIgnoreCase("thenakedpuppet"))) {
            String[] params9 = this.getParameters(message, "!suzuya waifucooldown ", 1);
            cd = Integer.parseInt(params9[0]);
            SuzuyaBot.setPropertyValue("waifuCooldown", params9[0]);
            waifuTimer.setDelay(cd * 100);
            waifuTimer.setInitialDelay(cd * 1000);
            waifuTimer.restart();
            this.sendMessage(channel, "Cooldown changed to " + params9[0] + " seconds");
        }
        if (channel.contains((CharSequence)realName) || realName.equalsIgnoreCase("kittehcommando") || realName.equalsIgnoreCase("thenakedpuppet")) {
            if (message.toLowerCase().matches("!suzuya resub .+")) {
                String[] params10 = this.getParameters(message, "!suzuya resub ", 1);
                SuzuyaBot.setPropertyValue("resubString", params10[0]);
                ReSubString = params10[0];
                this.sendMessage(channel, "Resub string altered tomaZ");
            }
            if (message.toLowerCase().matches("!suzuya sub .+")) {
                String[] params11 = this.getParameters(message, "!suzuya sub ", 1);
                SuzuyaBot.setPropertyValue("subString", params11[0]);
                SubString = params11[0];
                this.sendMessage(channel, "Sub string altered tomaZ");
            }
        }
        if (message.toLowerCase().matches("!suzuya google .+")) {
            String[] params12 = this.getParameters(message, "!suzuya google ", 1);
            this.sendMessage(channel, "http://lmgtfy.com/?q=" + params12[0].trim().replace((CharSequence)" ", (CharSequence)"+"));
        }
        if (isHealthy && canHealthy) {
            if (message.toLowerCase().matches("!healthy .+")) {
                String[] params13 = this.getParams(message, 1, false);
                this.sendMessage(channel, this.getRandomLine(new File(linksFile), linksNumberOfLines, Integer.parseInt(params13[0])));
                canHealthy = false;
                healthyTimer.restart();
            }
            if (message.toLowerCase().equalsIgnoreCase("!healthy")) {
                String line = this.getRandomLine(new File(linksFile), linksNumberOfLines);
                this.sendMessage(channel, line);
                canHealthy = false;
                healthyTimer.restart();
            }
        }
        if (isWaifu) {
            if (message.toLowerCase().equalsIgnoreCase("!waifu")) {
                String line = this.getRandomWaifu();
                System.out.println(line);
                this.sendMessage(channel, line);
                this.canRespond = false;
                timer.restart();
                return;
            }
            if (message.toLowerCase().matches("!waifu .+")) {
                ArrayList<Waifu> waifus = new ArrayList<Waifu>();
                String[] params14 = this.getParameters(message, "!waifu ", 1);
                for (int i5 = 0; i5 < SeriesArray.size(); ++i5) {
                    for (int x = 0; x < SeriesArray.get(i5).getNames().size(); ++x) {
                        if (!SuzuyaBot.containsIgnoreCase(SeriesArray.get(i5).getNames(), params14[0])) continue;
                        for (Waifu w : SeriesArray.get(i5).getWaifus()) {
                            waifus.add(w);
                        }
                    }
                }
                if (waifus.size() == 0) {
                    System.out.println("Error getting waifus");
                } else {
                    int rand = (int)(Math.random() * (double)waifus.size());
                    Waifu randWaifu = (Waifu)waifus.get(rand);
                    Object line = ".me Your waifu is " + randWaifu.name + " from " + randWaifu.series.getNames().get(0);
                    System.out.println((String)line);
                    this.sendMessage(channel, (String)line);
                }
                this.canRespond = false;
                timer.restart();
            }
            if (message.toLowerCase().matches("!suzuya addwaifu .+")) {
                String[] params15 = message.replace((CharSequence)"!suzuya addwaifu ", (CharSequence)"").split(" \\| ");
                System.out.println("Series = " + params15[0] + " Name = " + params15[1]);
                for (Series s : SeriesArray) {
                    for (Waifu w : s.getWaifus()) {
                        if (!w.name.equalsIgnoreCase(params15[1])) continue;
                        return;
                    }
                }
                try {
                    SuzuyaBot.addWaifu(params15[0], params15[1]);
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (message.equalsIgnoreCase("!suzuya")) {
            this.sendMessage(channel, "Hi! I'm a bot made by TheNakedPuppet/TNP. If you have any questions or need any help, feel free to message TNP :)");
        }
        if ((message.equalsIgnoreCase("!dc") || message.equalsIgnoreCase("!off")) && (realName.equalsIgnoreCase("thenakedpuppet") || channel.contains((CharSequence)realName))) {
            this.sendMessage(channel, "Goodbye!");
            SuzuyaBot.closeBot(SeriesArray);
        }
        if (isAnnoying) {
            if (message.toLowerCase().equalsIgnoreCase("!hug") || message.toLowerCase().contains((CharSequence)" sad ") || message.toLowerCase().contains((CharSequence)" sadder ") || message.toLowerCase().contains((CharSequence)" saddest ") || message.toLowerCase().contains((CharSequence)" cri ") || message.toLowerCase().contains((CharSequence)" cry ") || message.toLowerCase().contains((CharSequence)" depressed ") || message.toLowerCase().matches(";.+;")) {
                sender = String.valueOf(Character.toUpperCase(sender.charAt(0))) + sender.substring(1);
                String response = ".me hugs " + sender;
                this.sendMessage(channel, response);
            }
            if (message.toLowerCase().matches("!hug .+")) {
                String[] params16 = this.getParameters(message, "!hug ", 1);
                sender = String.valueOf(Character.toUpperCase(sender.charAt(0))) + sender.substring(1);
                this.sendMessage(channel, ".me and " + sender + " hug " + params16[0]);
            }
            if (message.toLowerCase().equalsIgnoreCase("!pet")) {
                this.sendMessage(channel, ".me pets " + sender);
            }
            if (message.toLowerCase().matches("!pet .+")) {
                sender = String.valueOf(Character.toUpperCase(sender.charAt(0))) + sender.substring(1);
                String[] params17 = this.getParameters(message, "!pet ", 1);
                this.sendMessage(channel, ".me and " + sender + " pet " + params17[0]);
            }
            if (message.toLowerCase().equalsIgnoreCase("!lick")) {
                this.sendMessage(channel, ".me licks " + sender);
            }
            if (message.toLowerCase().matches("!lick .+")) {
                sender = String.valueOf(Character.toUpperCase(sender.charAt(0))) + sender.substring(1);
                String[] params18 = this.getParameters(message, "!lick ", 1);
                this.sendMessage(channel, ".me and " + sender + " lick " + params18[0]);
            }
            if (message.toLowerCase().equalsIgnoreCase("!kiss")) {
                this.sendMessage(channel, ".me kiss " + sender);
            }
            if (message.toLowerCase().matches("!kiss .+")) {
                sender = String.valueOf(Character.toUpperCase(sender.charAt(0))) + sender.substring(1);
                String[] params19 = this.getParameters(message, "!kiss ", 1);
                this.sendMessage(channel, ".me and " + sender + " kiss " + params19[0]);
            }
        }
    }

    public void addMod(String channel, String modName) {
        if (this.ModList.contains(modName)) {
            System.out.println(String.valueOf(modName) + " is already a mod");
        } else {
            this.ModList.add(modName);
            System.out.println("Added mod " + modName);
        }
    }

    public void removeMod(String channel, String modName) {
        if (this.ModList.contains(modName)) {
            this.ModList.remove(modName);
            System.out.println("Removed mod " + modName);
        }
    }

    public boolean isMod(String channel, String name) {
        return this.ModList.contains(name);
    }

    public static String replaceConstants(String message) {
        String str = message;
        str = str.replace((CharSequence)"starts with", (CharSequence)"1");
        str = str.replace((CharSequence)"starts", (CharSequence)"1");
        str = str.replace((CharSequence)"starts with", (CharSequence)"1");
        str = str.replace((CharSequence)"with", (CharSequence)"1");
        str = str.replace((CharSequence)"begins with", (CharSequence)"1");
        str = str.replace((CharSequence)"contains", (CharSequence)"2");
        str = str.replace((CharSequence)"has", (CharSequence)"2");
        str = str.replace((CharSequence)"equals", (CharSequence)"3");
        str = str.replace((CharSequence)"is", (CharSequence)"3");
        str = str.replace((CharSequence)"exact", (CharSequence)"3");
        str = str.replace((CharSequence)"reg", (CharSequence)"2");
        str = str.replace((CharSequence)"regular", (CharSequence)"2");
        str = str.replace((CharSequence)"special", (CharSequence)"2");
        str = str.replace((CharSequence)"mod", (CharSequence)"3");
        str = str.replace((CharSequence)"mod only", (CharSequence)"3");
        str = str.replace((CharSequence)"mods", (CharSequence)"3");
        str = str.replace((CharSequence)"all", (CharSequence)"1");
        str = str.replace((CharSequence)"everyone", (CharSequence)"1");
        str = str.replace((CharSequence)"any", (CharSequence)"1");
        str = str.replace((CharSequence)"anyone", (CharSequence)"1");
        return str;
    }

    public static void serializeObject(Object object, String file) {
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in " + file);
        }
        catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static Object deserializeObject(String file) {
        Object e = null;
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            e = in.readObject();
            in.close();
            fileIn.close();
            return e;
        }
        catch (IOException i) {
            i.printStackTrace();
            return null;
        }
        catch (ClassNotFoundException c) {
            System.out.println("Object was not found(?!?!?!?!)");
            c.printStackTrace();
            return null;
        }
    }

    public String[] getParams(String message, int numberOfParameters, boolean isAddcom) {
        message = message.trim();
        String[] array = new String[numberOfParameters];
        System.out.println("MESSAGE =" + message);
        if (isAddcom) {
            String result;
            message = message.substring(message.lastIndexOf("addcom ") + 7, message.length());
            numberOfParameters = 2;
            array[0] = result = message.substring(0, message.indexOf(" "));
            array[1] = result = message.substring(message.indexOf(" ") + 1);
            return array;
        }
        for (int i = numberOfParameters; i != 0; --i) {
            System.out.println("LAST INDEX OF SPACE = " + message.lastIndexOf(" "));
            String result = message.substring(message.lastIndexOf(" ") + 1);
            System.out.println(String.valueOf(i) + "  RESULT = " + result);
            message = message.substring(0, message.lastIndexOf(" "));
            System.out.println("MESSAGE SUBSTRING = " + message);
            array[numberOfParameters - i] = result;
        }
        System.out.println(array);
        return array;
    }

    public String[] getParameters(String message, String command, int numberOfParameters) {
        String[] array = new String[numberOfParameters];
        message = message.replace((CharSequence)command, (CharSequence)"");
        message = message.trim();
        for (int i = numberOfParameters - 1; i >= 0; --i) {
            if (i == 0) {
                System.out.println("MESSAGE @ i = " + i + " = " + message);
                array[0] = message.trim();
                continue;
            }
            String result = message.substring(0, message.indexOf(" "));
            System.out.println("RESULT @ i = " + i + " = " + message);
            message = message.substring(message.indexOf(" "));
            System.out.println("MESSAGE @ i = " + i + " = " + message);
            array[i] = result;
        }
        System.out.print("Params = ");
        for (String s : array) {
            System.out.println(s);
        }
        return array;
    }

    public String getNickname(String name) {
        if (nicknames.containsKey(name)) {
            return nicknames.get(name);
        }
        return name;
    }

    public static void setNickname(SuzuyaBot bot, Channel currentChannel, String[] params) {
        nicknames.put(params[1].toLowerCase(), params[0]);
        bot.sendMessage(currentChannel.Name, "MrDestructoid " + params[1] + " is now known as " + params[0]);
        SuzuyaBot.serializeObject(nicknames, nicknamesPath);
    }

    public String getRandomLine(File file, int numberOfLines, int seed) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            for (int i = 0; i < numberOfLines; ++i) {
                if (i == seed - 1) {
                    String result = "#" + (i + 1) + " " + scanner.nextLine();
                    scanner.close();
                    return result;
                }
                scanner.nextLine();
            }
            scanner.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
        return "#1 licdn.awwni.me/npmp.jpg";
    }

    public String getRandomLine(File file, int numberOfLines) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            System.out.println("number of lines: " + numberOfLines);
            int seed = (int)(Math.random() * (double)numberOfLines + 1.0);
            System.out.println("seed: " + seed);
            for (int i = 0; i < numberOfLines; ++i) {
                if (i == seed - 1) {
                    String result = "#" + (i + 1) + " " + scanner.nextLine();
                    scanner.close();
                    return result;
                }
                scanner.nextLine();
            }
            scanner.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
        return "#1 licdn.awwni.me/npmp.jpg";
    }

    public String getRandomWaifu() {
        int randSeries = (int)(Math.random() * (double)SeriesArray.size());
        Series targetSeries = SeriesArray.get(randSeries);
        int randWaifu = (int)(Math.random() * (double)targetSeries.getWaifus().size());
        return targetSeries.getWaifus().get(randWaifu).giveWaifu();
    }

    public String getRandomWaifu(ArrayList<Series> SeriesArray) {
        int randSeries = (int)(Math.random() * (double)SeriesArray.size());
        Series targetSeries = SeriesArray.get(randSeries);
        int randWaifu = (int)(Math.random() * (double)targetSeries.getWaifus().size());
        return targetSeries.getWaifus().get(randWaifu).giveWaifu();
    }

    public static boolean addWaifu(String seriesName, String waifuName) throws IOException {
        for (int i = 0; i < SeriesArray.size(); ++i) {
            if (!SuzuyaBot.containsIgnoreCase(SeriesArray.get(i).getNames(), seriesName)) continue;
            System.out.println("Series exists!");
            for (int x = 0; x < SeriesArray.get(i).getWaifus().size(); ++x) {
                if (!waifuName.equalsIgnoreCase(SeriesArray.get(i).getWaifus().get(x).getName())) continue;
                System.out.println("Waifu already exists in this series!");
                return false;
            }
            SeriesArray.get(i).addWaifu(new Waifu(waifuName, SeriesArray.get(i)));
            return true;
        }
        Series newSeries = new Series(seriesName);
        newSeries.addWaifu(new Waifu(waifuName, newSeries));
        SeriesArray.add(newSeries);
        SuzuyaBot.serializeObject(SeriesArray, "WaifusV2.ser");
        SuzuyaBot.saveWaifus(SeriesArray);
        return true;
    }

    public static void loadWaifusFromTxt(String filedir) {
        String[] seriesNames;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filedir));
            System.out.println("START LOAD");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = scanner.nextLine();
        String[] arrstring = seriesNames = line.replaceAll("[\\[\\]]", "").split(" \\| ");
        int n = arrstring.length;
        for (int i = 0; i < n; ++i) {
            String s = arrstring[i];
           // System.out.print(s);
        }
        Series nSeries = new Series(seriesNames);
       // System.out.print("\n");
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line == "" || line == null) continue;
            if (line.startsWith("[")) {
                SeriesArray.add(nSeries);
                seriesNames = line.replaceAll("[\\[\\]]", "").split(" \\| ");
                nSeries = new Series(seriesNames);
                for (String s : seriesNames) {
                    //System.out.print(s);
                }
                //System.out.print("\n");
                continue;
            }
            nSeries.addWaifu(new Waifu(line, nSeries));
           // System.out.println("-" + line);
        }
        scanner.close();
    }

    public static void saveWaifus(ArrayList<Series> SeriesArray) throws IOException {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter("WaifusV2.txt", false));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < SeriesArray.size() - 1; ++i) {
            System.out.println(SeriesArray.get(i).getNamesAsString());
            out.write(String.valueOf(SeriesArray.get(i).getNamesAsString()) + "\n");
            for (int x = 0; x < SeriesArray.get(i).getWaifus().size(); ++x) {
                System.out.println("  " + SeriesArray.get(i).getWaifus().get(x).getName());
                out.write(String.valueOf(SeriesArray.get(i).getWaifus().get(x).getName()) + "\n");
            }
        }
        out.close();
    }

    public static int countLines(String filename) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] != 10) continue;
                    ++count;
                }
            }
            int n = count == 0 && !empty ? 1 : count;
            return n;
        }
        finally {
            is.close();
        }
    }

    public static void setPropertyValue(String property, String value) {
        block12 : {
            FileOutputStream output = null;
            try {
                try {
                    output = new FileOutputStream("config.properties");
                    properties.setProperty(property, value);
                    properties.store(output, null);
                    output.close();
                }
                catch (IOException io) {
                    io.printStackTrace();
                    if (output == null) break block12;
                    try {
                        output.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            finally {
                if (output != null) {
                    try {
                        output.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void setPropertyValue(String property, boolean bool) {
        block12 : {
            FileOutputStream output = null;
            String value = bool ? "true" : "false";
            try {
                try {
                    output = new FileOutputStream("config.properties");
                    properties.setProperty(property, value);
                    properties.store(output, null);
                    output.close();
                }
                catch (IOException io) {
                    io.printStackTrace();
                    if (output == null) break block12;
                    try {
                        output.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            finally {
                if (output != null) {
                    try {
                        output.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String getPropertyValue(String property) {
        try {
            FileInputStream inputStream = new FileInputStream("config.properties");
            properties.load(inputStream);
            inputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String result = properties.getProperty(property);
        return result;
    }

    public static void openWebpage(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        }
        catch (Exception var1_1) {
            // empty catch block
        }
    }

    public static void readImageFromURL(String url, String d) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        }
        catch (Exception var2_2) {
            // empty catch block
        }
    }

    public static Image loadImage(String url) throws IOException {
        BufferedImage image = null;
        image = ImageIO.read(new File(url));
        return image;
    }

    public static void writeImage(String url, String target) {
        BufferedImage bi = null;
        try {
            bi = (BufferedImage)SuzuyaBot.loadImage(url);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Image Loaded");
        File outputfile = new File(target);
        try {
            ImageIO.write((RenderedImage)bi, "png", outputfile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Image Written");
    }

    public static synchronized void playSound(String url) {
        File file = new File(url);
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
                    clip.open(inputStream);
                    inputStream.close();
                    clip.start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    public String getResubString(String message) {
        String[] parts = message.split(" ", 2);
        String name = parts[0];
        String months = parts[1].replace((CharSequence)"subscribed for ", (CharSequence)"").replace((CharSequence)" months in a row!", (CharSequence)"");
        return ReSubString.replace((CharSequence)"%name%", (CharSequence)name).replace((CharSequence)"%months%", (CharSequence)months).replace((CharSequence)"%capsname%", (CharSequence)name.toUpperCase());
    }

    public String getSubString(String message) {
        String[] parts = message.split(" ", 2);
        String name = parts[0];
        return SubString.replace((CharSequence)"%name%", (CharSequence)name).replace((CharSequence)"%capsname%", (CharSequence)name.toUpperCase());
    }

    public static boolean containsIgnoreCase(ArrayList<String> AL, String str) {
        for (String s : AL) {
            if (!s.equalsIgnoreCase(str)) continue;
            return true;
        }
        return false;
    }
    public static void updateLevel(String user,int lv){
    		levels.put(user, String.valueOf(lv));
    		saveLevels(levels);
    }
    public static void saveLevels(Map<String,String> lvls){
    	 PrintWriter out = null;
         try {
             out = new PrintWriter(new FileWriter("Levels.txt", false));
         }
         catch (IOException e) {
             e.printStackTrace();
         }
         for (Entry<String, String> entry : lvls.entrySet()) {
 		    String key = entry.getKey();
 		    String value = entry.getValue();
 		    out.write(key + " " + value + "\n");
 		}
         out.close();
         //System.out.println("Levels Saved");
    }
    public static HashMap<String,String> loadLevels(){
    	HashMap<String,String> map = new HashMap<String,String>();

    	Scanner scanner = null;
		try {
			scanner = new Scanner(new File("Levels.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
    	String[] line;
    	scanner.nextLine();
    	while(scanner.hasNextLine()){
    		line = scanner.nextLine().split(" ");
    		//System.out.println(line[0] + " " + line[1]);
    		map.put(line[0], line[1]);
    	}
    	return map;
    }
    public static void closeBot(ArrayList<Series> seriesarray) {
        try {
            SuzuyaBot.saveWaifus(seriesarray);
            System.exit(0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}