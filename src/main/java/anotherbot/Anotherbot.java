/* Update History: Build Name, Date
 * "Ok seems reasonable", May 20th, 2014. 7pm.
 * "Jesus christ what", May 27th, ~5pm
 * "Toothpick Skyscraper", May 29th ~5pm
 * "Barbedwire Tumbleweed", June 4th ~6pm
 * "Comedy Transfusion", June 4th 7:47pm 
 *
 * CURRENT FUNCTIONALITY:
 * 
 * -creates and reads settings from a config file
 * -connects to a server and channel
 * -retrieves and stores  words in messages sent to the channel
 * -replies to messages by picking a random word from the user's message
 * -then builds a reply starting from that word using words that are known to have come after it in previous messages
 * -the chance of adding another word to the sentence is based on a random number and whether or not we know a word that can come after the last word in the sentence
 * -writes words with known possible next words to dictionary
 * -read and write to/from dictionary in a way that we can remember word frequency etc across different runs
 * 
 * IMPLEMENTED BUT NEEDS IMPROVEMENT (TODO):
 * 
 * -Markov chain (map relationships between words and build replies using that, building around a randomly chosen word in a message sent to the server)
 * -Should add functionality to build sentences from either side of a word (unneccesary but nice)
 *
 * THINGS THAT WORK MOSTLY FINE AND DON'T NEED IMPROVEMENT (AT THE CURRENT MOMENT):
 * 
 * Util.java
 * UserCfg.java
 */

package anotherbot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

//From this class anotherbot will apply changes to himself as defined by the configuration file and perform actions that are to be called by anotherbotMain
public class Anotherbot extends PircBot {
    private ArrayList<String> currentWords;
    private Dictionary dictionary;
    // private ArrayList<String> associatedWords;
    private Set<String> keys; // we are using this to store each individual word
    // we
    // know about, which we will use as a key pointing to a
    // list of possible next words. sets cannot contain
    // duplicate entries.
    private UserCfg settings;

    private Map<String, ArrayList<String>> wordsMap = new LinkedHashMap<String, ArrayList<String>>();

    // basic constructor that writes a new config file with default values
    public Anotherbot() {
        this.setVerbose(true); // what kind of info we get in the console.
        keys = new LinkedHashSet<String>();
        settings = new UserCfg("anotherbotForever", "irc.rizon.net",
                "#thesewingcircle");
        dictionary = new Dictionary("dictionary.txt");
        this.setName(loadName(settings));
    }

    // constructor for using offline mode
    public Anotherbot(boolean offlineMode) {
        this.setVerbose(true);
        dictionary = new Dictionary("dictionary.txt");
        if (offlineMode) {
            loadExistingKeys();
            loadKeyValues();
            String message;
            String replyMessage;
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Offline mode enabled. q to quit.");
            while (!(message = keyboard.nextLine()).equals("q")) {
                System.out.println("You: " + message);
                if ((replyMessage = buildReply(message)) != null) {
                    System.out.println("anotherbot: " + replyMessage);
                }
            }
            keyboard.close();

        } else {
            return;
        }
    }

    // this is the constructor to be used when a valid cfg file already exists
    public Anotherbot(String server, String channel, String nick) {
        this.setVerbose(true);
        dictionary = new Dictionary("dictionary.txt");
        settings = new UserCfg(nick, server, channel);
        this.setName(loadName(settings)); // for the sake of consistency, load
        // this from the cfg file and dont
        // take it from nick
    }

    // what the main method should call to get the bot going
    public void beginServerConnection() {
        try {
            this.connect(settings.getField(1));
        } catch (IOException | IrcException e) {
            System.out.println(e);
        }
    }

    private String buildReply(String message) {
        String replyMessage = null;
        String randomWord = null;
        String endOfSentence = "";
        ArrayList<String> nextWords;
        boolean addMoreWords;
        processMessage(message);
        int numWords = currentWords.size();
        if (numWords == 0)
            return null;
        int item = new Random().nextInt(numWords);
        int i = 0;
        // needs to be adjusted
        for (String word : currentWords) {
            if (i == item) {
                if (keys.contains(word)) {
                    randomWord = word;
                    replyMessage = randomWord;
                    endOfSentence = replyMessage;
                    break;
                } else
                    return null;
            }
            i++;
        }
        // build sentence based off the randomly chosen word
        System.out
                .println("Anotherbot.buildReply(str): building reply from this word in the message: "
                        + replyMessage);
        addMoreWords = true;
        i = 0;
        nextWords = getPossibleNextWords(endOfSentence);
        if (nextWords == null) {
            return null;
        }
        while (addMoreWords && !(getPossibleNextWords(endOfSentence) == null)) {
            nextWords = getPossibleNextWords(endOfSentence);
            item = new Random().nextInt(nextWords.size());
            for (String element : nextWords) {
                System.out
                        .println("Anotherbot.buildReply(str): element in nextWords="
                                + element);
                if (i == item) {
                    replyMessage = replyMessage + " " + element;
                    endOfSentence = element;
                    break;
                }
                i++;
            }
            if(nextWords.size()==0||new Random().nextInt(10)==1)
            	addMoreWords = false;
        }

        return replyMessage + ".".trim();
    }

    // futile attempt to clean the code up. checks for existing dictionary and
    // sets keys equal to the keys in the dictionary
    private void loadExistingKeys() {
        if (Util.fileExists(dictionary.getFilename())) {
            try {
                keys = new LinkedHashSet<String>(dictionary.loadLeftSide());
                System.out
                        .println("Anotherbot.loadExistingKeys(): Keys loaded from existing dictionary: "
                                + keys.toString()); // seems to work
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            keys = new LinkedHashSet<String>();
        }
    }

    private void loadKeyValues() {
        if (Util.fileExists(dictionary.getFilename())) {
            try {
                ArrayList<String> associatedWords;
                for (String key : keys) {
                    System.out.println(keys.toString());
                    associatedWords = new ArrayList<String>(
                            dictionary.loadRightSide(key));
                    System.out
                            .println("Anotherbot.loadKeyValues(): associated words found: "
                                    + associatedWords.toString());
                    for (String word : associatedWords) {
                        addToList(key, word);

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToFile() {
        ArrayList<String> toWrite = new ArrayList<String>();
        String value;
        String newLine;
        for (String key : keys) {
            if (wordsMap.get(key) == null)
                continue;
            else
                value = wordsMap.get(key).toString().trim()
                        .replaceAll("[^a-zA-Z]", " ");
            System.out.println("Anotherbot.saveToFile(): key=" + key);
            newLine = (key + ": " + value).replaceAll("\\s+", " ");
            toWrite.add(newLine);
        }
        try {
            dictionary.save(toWrite);
        } catch (IOException e) {
            // TODO Auto-generated catch block
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

    // what to do when a message from somebody is sent to the channel
    @Override
    public void onMessage(String channel, String sender, String login,
            String hostname, String message) {
        String replyMessage = buildReply(message);
        if (replyMessage.equals(null))
            return;
        sendMessage(channel, replyMessage);
    }

    // synchronized????? yes. it's a multithreading thing.
    // http://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html
    // is it necessary? maybe not. but it doesn't seem to hurt us. perhaps it
    // will noticeably hurt or help us when handling a large number of values.
    // this actually seems to do exactly what it should do, which is map the
    // word (mapKey) to the arraylist of values that are able to come after it
    // so, as this appears to work, that means we have WORKING WORD ASSOCIATION
    private synchronized void addToList(String mapKey, String newValue) {
        List<String> valuesList = wordsMap.get(mapKey);
        if (valuesList == null) {
            valuesList = new ArrayList<String>();
            valuesList.add(newValue);
            wordsMap.put(mapKey, (ArrayList<String>) valuesList);
        } else {
            if (!valuesList.contains(newValue)) {
                valuesList.add(newValue);
            }
        }
        System.out.println("Anotherbot.addToList(str, str): key=" + mapKey
                + "; newly added value: " + newValue
                + "; words associated to mapKey(" + mapKey + "): "
                + wordsMap.get(mapKey).toString());
    }

    private ArrayList<String> getPossibleNextWords(String mapKey) {
        return wordsMap.get(mapKey);
    }

    // when a message is sent to the channel, we have to pick out the garbage
    // and get the data we are looking for, which is words.
    private void processMessage(String message) {
        currentWords = new ArrayList<String>();
        // Map<String, String> associatedWord = new HashMap<String, String>();
        // ArrayList<String> keyValue = new ArrayList<String>();
        String word;
        String wordBefore = "";
        // get each word, or at least what we think is a word, in the line
        String[] getWords = message.trim().split("\\s+");
        for (int size = getWords.length, i = 0; i < size; i++) {
            // if it's not a letter ignore it
            if (getWords[i].equals("") || getWords[i].contains("[^a-zA-Z]")) {
                continue;
            }
            // getting the word
            word = getWords[i].trim().replaceAll("[^a-zA-Z]", "").toLowerCase();
            // when we do replaceAll("[^a-zA-Z]", "") and it's just garbage text
            // like a semicolon it will still be added as a blank character, and
            // we dont want that
            if (word.equals("")) {
                continue;
            }
            currentWords.add(word);
            keys.add(word);
            // dictionary.saveLine(key+":"+word, false);
            if (i > 0) {
                System.out
                        .println("Anotherbot.processMessage(str): wordBefore="
                                + wordBefore + " word=" + word);
                addToList(wordBefore, word);

                System.out
                        .println("Anotherbot.getPossibleNextWords(str): possible next words for "
                                + wordBefore
                                + " are: "
                                + getPossibleNextWords(wordBefore).toString());
            }

            if (!currentWords.isEmpty()) {
                System.out
                        .println("Anotherbot.processMessage(str): word detected in message: "
                                + currentWords.get(i));
            }
            wordBefore = word;
            saveToFile();
        }
    }
}
