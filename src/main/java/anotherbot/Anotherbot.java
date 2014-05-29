/* Update History: Build Name, Date
 *                 "Ok seems reasonable", May 20th, 2014. 7pm.
 *                 "Jesus christ what", May 27th, ~5pm
 * Current functionality:
 * -creates and reads settings from a config file
 * -connects to a server and channel
 * -retrieves and stores sentences and words in messages sent to the channel
 * -replies to messages with a random word previously learned
 * -arbitrarily writes processed words to dictionary
 * TODO:
 * -read and write to/from dictionary in a way that we can remember word frequency etc across different runs
 * -Markov chain (map relationships between words and build replies using that, building around a randomly chosen word in a message sent to the server)
 */

package anotherbot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

//From this class anotherbot will apply changes to himself as defined by the configuration file and perform actions that are to be called by anotherbotMain
public class Anotherbot extends PircBot {
    UserCfg settings;
    Dictionary dictionary;
    ArrayList<String> currentWords;
    Set<String> keys; // we are using this to store each individual word we
                      // know about, which we will use as a key pointing to a
                      // list of possible next words. sets cannot contain
                      // duplicate entries.
    Map<String, ArrayList<String>> wordsMap;

    // basic constructor that writes a new config file with default values
    public Anotherbot() {
        this.setVerbose(true); // what kind of info we get in the console.
        keys = new LinkedHashSet<String>();
        settings = new UserCfg("anotherbotForever", "irc.rizon.net",
                "#thesewingcircle");
        dictionary = new Dictionary("dictionary.txt");
        this.setName(loadName(settings));
    }

    // this is the constructor to be used when a valid cfg file already exists
    public Anotherbot(String server, String channel, String nick) {
        this.setVerbose(true);
        dictionary = new Dictionary("dictionary.txt");
        if (Util.fileExists(dictionary.getFilename())) {
            try {
                keys = new LinkedHashSet<String>(Util.getFileContents(dictionary.getFilename()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            keys = new LinkedHashSet<String>();
        }
        settings = new UserCfg(nick, server, channel);
        this.setName(loadName(settings)); // for the sake of consistency, load
                                          // this from the cfg file and dont
                                          // take it from nick
    }

    // constructor for using offline mode
    public Anotherbot(boolean offlineMode) {
        if (offlineMode) {
            keys = new LinkedHashSet<String>();
            String message;
            String replyMessage;
            dictionary = new Dictionary("dictionary.txt");
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Offline mode enabled. q to quit.");
            while (!(message = keyboard.nextLine()).equals("q")) {
                System.out.println("You: " + message);
                replyMessage = buildReply(message);
                System.out.println("anotherbot: "+replyMessage);
            }

        } else {
            return;
        }
    }

    // what to do when a message from somebody is sent to the channel
    @Override
    public void onMessage(String channel, String sender, String login,
            String hostname, String message) {
        String replyMessage = buildReply(message);
        if (replyMessage.equals(null)) return;
        sendMessage(channel, replyMessage);
    }

    private String buildReply(String message) {
        String replyMessage;
        String randomWord = null;
        processMessage(message);
        int numWords = keys.size();
        if (numWords==0) return null;
        int item = new Random().nextInt(numWords);
        int i = 0;
        for (String element : keys) {
            if (i == item) {
                randomWord = element;
            }
            i++;
        }
        return randomWord;

    }

    // what the main method should call to get the bot going
    public void beginServerConnection() {
        try {
            this.connect(settings.getField(1));
        } catch (IOException | IrcException e) {
            System.out.println(e);
        }
    }

    // what to do after successfully connecting to the server
    // currently, join the channel defined in the cfg file after connecting
    @Override
    public void onConnect() {
        try {
            this.joinChannel(settings.getField(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // the main reason for using loadName is to avoid having the constructor
    // handle an error not relevant to it (getField throws
    // FileNotFoundException) because it's making a new file
    private String loadName(UserCfg settings) {
        String name = null;
        try {
            name = settings.getField(0);
        } catch (IOException n) {
            System.out.println(n);
        }
        return name;
    }

    // when a message is sent to the channel, we have to pick out the garbage
    // and get the data we are looking for, which is words.
    private void processMessage(String message) {
        currentWords = new ArrayList<String>();
        String word;
        // get each word, or at least what we think is a word, in the line
        String[] splitWords = message.trim().split(" +");
        for (int size = splitWords.length, i = 0; i < size; i++) {
            if (splitWords[i].contains(" +")) continue;
            word = splitWords[i].trim().replaceAll("[^a-zA-Z]", "").toLowerCase();
            currentWords.add(word);
            keys.add(word);
            if (!currentWords.isEmpty()) {
            System.out.println("Words: " + currentWords.get(i)); // for testing
            }
        }
        for (String element : keys) {
            if (element.contains(" +")) { keys.remove(element); continue; }
            System.out.println("Keys: " + element); // for testing
        }
        dictionary.save(keys);
    }
}
