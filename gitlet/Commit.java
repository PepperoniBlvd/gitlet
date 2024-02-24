package gitlet;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class
import java.io.Serializable;
import java.io.File;
import java.util.Map;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable{
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

     /** The message of this Commit. */
    private String message;
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The commit directory. */
    public static final File COMMIT_DIR = Utils.join(CWD, ".gitlet", "commits");
    /** The SHA1 of parent. */
    private String parent = "";
    /** The SHA1 of parent2. */
    private String parent2 = "";
    /** The TimeStamp of this Commit. */
    private Date timeStamp = new Date();
    /** The commit map. Key:filename Value:SHA1 */
    Map<String, String> commitMap = new java.util.HashMap<>();
    /* TODO: fill in the rest of this class. */

    /** Constructor */
    public Commit(String message, String parent, String parent2, Map<String, String> commitMap) {
        this.message = message;
        this.parent = parent;
        this.parent2 = parent2;
        this.commitMap = commitMap;
        this.timeStamp.getTime();
    }

    public Commit(){
    }

    /** Init first commit */
    public void initCommit(){
        this.message = "initial commit";
        this.parent = "";
        this.parent2 = "";
        this.timeStamp.setTime(0);
    }

    /** Return the parent1 */
    public String getParent() {
        return parent;
    }

    /** Return the Map<String, String> */
    public Map<String, String> getBlobs() {
        return commitMap;
    }

    /** Write this Commit and return the filename(SHA1) */
    public String write() {
        if (!COMMIT_DIR.exists()) {
            COMMIT_DIR.mkdir();
        }
        String sha1 = Utils.sha1(Utils.serialize(this));
        String sp = File.separator;
        File commitFile = new File(".gitlet" + sp + "commits" + sp + sha1);
        if (!commitFile.exists()) {
            try {
                commitFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.writeObject(commitFile, this);
        return sha1;
    }

    /** Read commit. */
    public static Commit read(String sha1) {
        if (sha1.length() == 8) {
            for (String filename : Utils.plainFilenamesIn(COMMIT_DIR)) {
                if (filename.startsWith(sha1)) {
                    sha1 = filename;
                    break;
                }
            }
        }
        String sp = File.separator;
        File commitFile = new File(".gitlet" + sp + "commits" + sp + sha1);
        if (!commitFile.exists()) {
            return null;
        }
        return Utils.readObject(commitFile, Commit.class);
    }

    /** Return the commit log. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("===\n");
        // SHA1
        sb.append("commit " + Utils.sha1(Utils.serialize(this))).append("\n");
        // Merge
        if (!parent2.equals("")) {
            sb.append("Merge: ").append(parent.substring(0, 7)).append(" ");
            sb.append(parent2.substring(0, 7)).append("\n");
        }
        // TimeStamp
        sb.append("Date: ");
        SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");
        sb.append(format.format(timeStamp)).append("\n");
        // Message
        sb.append(message).append("\n");
        return sb.toString();
    }
    
    /** Return the commit ID which has the given message. */
    public static List<String> findCommit(String message) {
        List<String> commitID = new ArrayList<>();
        List<String> commits = Utils.plainFilenamesIn(COMMIT_DIR);
        for (String commit : commits) {
            Commit c = read(commit);
            if (cmt.message.equals(message)) {
                commitID.add(Utils.sha1(Utils.serialize(cmt)));
            }
        }
        return commitID;
    }

    /** Create new Blobs */
    public static Map<String, String> mergeBlobs(Stage stage, Commit oldCmt) {
        Map<String, String> map = oldCmt.getBlobs();
        for (String rm : stage.getRemoved()) {
            map.remove(rm);
        }
        // Traverse Map
        for (Map.Entry<String, String> entry : stage.getStaged().entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    /** Return all commit log */
    public static List<String> getCommitLog() {
        List<String> commits = Utils.plainFilenamesIn(COMMIT_DIR);
        List<String> commitLog = new ArrayList<>();
        for (String commit : commits) {
            Commit cmt = read(commit);
            commitLog.add(cmt.toString());
        }
        return commitLog;
    }
}
