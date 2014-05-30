package anotherbot;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Dictionary {
    // TODO
    private final Charset CHAR_SET = Charset.forName("utf-8");
    private String filename;

    public Dictionary(String filename) {
        this.filename = filename;
        if (!Util.fileExists(filename)) {
            Util.makeFile(this.filename, CHAR_SET);
        }
    }

    public void save(Collection<String> lines) throws FileNotFoundException,
            IOException { // a collection can be a set, a
        // list, etc
        ArrayList<String> contents = new ArrayList<String>(
                Util.getFileContents(this.filename));
        ArrayList<String> linesToWrite = new ArrayList<String>(lines);
        Iterator<String> it = linesToWrite.iterator();
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(filename, true)))) {
            while (it.hasNext()) {

                out.println(it.next()); // this will append anything to the end
                                        // of the file, in other words it wont
                                        // overwrite the file
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public String getFilename() {
        return this.filename;
    }

    public void saveTest(String word, String nextWord)
            throws FileNotFoundException, IOException {
        String[] leftSide =Util.getFileContents(this.filename).toString().split("\n");
        int i=0;
        String[] split;
        for (String element : leftSide) {
            split=element.split(":");
            leftSide[i]=split[0];
            i++;
        }
        System.out.println(leftSide[0]);
        int findWord;
        // ArrayList<String> linesToWrite = new ArrayList<String>(lines);
     //   Iterator<String> it = contents.iterator();
        findWord = leftSide[0].lastIndexOf(word);
        if (findWord == -1) {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(filename, true)))) {
                out.println(word+":"+nextWord);
                //out.print(": ");
       //         while (!it.next().equals(word)) {
        //            out.println(it.next()); // this will append anything to the
                                            // end
                    // of the file, in other words it wont
                    // overwrite the file
       //         }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

}
