package anotherbot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;

//UserCfg is how we are going to allow the user to define settings for anotherbot. this class should only be called by anotherbot
public class UserCfg {
    private static final String filename = "anotherbot.cfg";
    private static final Charset CFG_ENCODE_TYPE = Charset.forName("utf-8");

    private static final String[] FIELDS = { "nick = ", "server = ",
            "channel = " };

    private static final String COMMENT_DENOTER = "#";

    private static final String DEFAULT_NICK = "anotherbotForever";
    private static final String DEFAULT_SERVER = "irc.rizon.net";
    private static final String DEFAULT_CHANNEL = "#thesewingcircle";

    private String nick;
    private String server;
    private String channel;

    public UserCfg() {
        if (Util.fileExists(filename)) {
            try {
                this.nick = getField(0);
                this.server = getField(1);
                this.channel = getField(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.nick = DEFAULT_NICK;
            this.server = DEFAULT_SERVER;
            this.channel = DEFAULT_CHANNEL;
            addDefaultSettings();
        }
    }

    public UserCfg(String nick, String server, String channel) {
        if (Util.fileExists(filename)) { // for now, the user defined settings
                                         // will always take priority
            try {
                this.nick = getField(0);
                this.server = getField(1);
                this.channel = getField(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.nick = nick;
            this.server = server;
            this.channel = channel;
            String[] toWrite = { FIELDS[0] + this.nick,
                    FIELDS[1] + this.server, FIELDS[2] + this.channel };
            Util.makeFile(filename, toWrite, CFG_ENCODE_TYPE);
        }
    }

    public String getField(int fieldIndex) throws IOException,
            FileNotFoundException {
        // instead of saying "look for this line number", we're looking for the
        // string of the setting field we want; this means less errors on our
        // side, less headache on user's side
        // why am i using linenumberreader
        StringBuilder value = new StringBuilder();
        try (LineNumberReader reader = new LineNumberReader(new FileReader(
                filename))) {
            String line = null;
            while ((line = reader.readLine().trim()) != null) {
                if (line.startsWith(COMMENT_DENOTER)
                        || !line.startsWith(FIELDS[fieldIndex])) {
                    continue;
                } // to remind myself not to waste my time again: THIS IS WHERE
                  // WE DECIDED JUST TO IGNORE ANY GARBAGE LINES/LINES NOT
                  // RELEVANT TO OUR SEARCH QUEREY
                value.append(line.split("= ")[1]);
                break;
            }
        }
        return new String(value.toString());
    }

    public void addDefaultSettings() {
        String[] toWrite = { FIELDS[0] + DEFAULT_NICK,
                FIELDS[1] + DEFAULT_SERVER, FIELDS[2] + DEFAULT_CHANNEL };
        Util.makeFile(filename, toWrite, CFG_ENCODE_TYPE);
    }
}
