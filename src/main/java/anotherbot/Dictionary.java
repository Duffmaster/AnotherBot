package anotherbot;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.lang.ArrayIndexOutOfBoundsException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Dictionary {
    // TODO
    private final String SEPARATOR = ":";
    private final Charset CHAR_SET = Charset.forName("utf-8");

    private String filename;
    private ArrayList<String> queue = new ArrayList<String>();

    public Dictionary(String filename) throws FileNotFoundException, IOException {
        this.filename = filename;
        if (!Util.fileExists(filename)) {
            Util.makeFile(this.filename, CHAR_SET);
        }
        queue=Util.getFileContents(filename);
    }

    // this should mean overwrite the current contents. of course it probably
    // wont end up doing that.
    public void save(Collection<String> lines) throws FileNotFoundException,
            IOException { // a collection can be a set, a
        // list, etc
        ArrayList<String> linesToWrite = new ArrayList<String>(lines);
        Iterator<String> it = linesToWrite.iterator();
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(filename, false)))) {// false=don't append
            while (it.hasNext()) {
                out.println(it.next());
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    // saveOld is just appending whatever text is sent to it to the end of the
    // file. for testing.
    public void saveOld(Collection<String> lines) throws FileNotFoundException,
            IOException { // a collection can be a set, a
        // list, etc
        ArrayList<String> linesToWrite = new ArrayList<String>(lines);
        Iterator<String> it = linesToWrite.iterator();
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(filename, true)))) { // true=append
            while (it.hasNext()) {
                out.println(it.next() + SEPARATOR); // this will append anything
                                                    // to the end
                // of the file, in other words it wont
                // overwrite the file
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    public ArrayList<String> loadKeys() throws FileNotFoundException, IOException {
    	 ArrayList<String> dictionaryContents = new ArrayList<String>(queue);
    	 ArrayList<String> keys=new ArrayList<String>();
    	 String[] splitElement;
    	 for (String element : dictionaryContents) {
    		 splitElement=element.trim().split(SEPARATOR);
    		 keys.add(splitElement[0]);
    	 }
		System.out.println("Left side loaded: "+keys.toString());
		return keys;
    }
    
    //finds all the possible words related to a given key
    public String loadLinks(String key) throws FileNotFoundException, IOException{
    	ArrayList<String> dictionaryContents = queue;
   	 	String links="";
   	 	String[] splitElement;
   	 	for (String element : dictionaryContents) {
   	 		splitElement=element.trim().split(SEPARATOR);
   	 		if(splitElement[0].equals(key)){
   	 			try{
   	 				links=splitElement[1];
   	 			}catch(ArrayIndexOutOfBoundsException exception){
   	 				links="";
   	 			}
   	 		}
   	 	}
		return links;
    }
    
    public void addKey(String word) throws FileNotFoundException, IOException {
    	if (queue.contains(word+SEPARATOR)==false) {
    		queue.add(word+SEPARATOR);
    	}
    }

    public void addLink(String leftSide, String wordRight)
            throws FileNotFoundException, IOException {
        ArrayList<String> elements= new ArrayList<String>(queue);
        for (String element : elements) {
        	if (element.trim().contains(leftSide+SEPARATOR)) {
        		queue.add(element+" "+wordRight);
        		queue.remove(elements.indexOf(element+SEPARATOR)+1);
        	}
        }
        // apparently to avoid the
        // nightmarish hell that is
        // RandomAccessFile we have to
        // look for the left side word
        // in the arraylist, modify it
        // there, then rewrite the
        // entire file with the new
        // arraylist
        // System.out.println(dictionaryContents.toString());
    }
    
    public void clearQueue() throws FileNotFoundException, IOException
    {
    	save(queue);
    	queue.clear();
    	queue=Util.getFileContents(this.filename);
    }

    public String getFilename() {
        return this.filename;
    }

}
