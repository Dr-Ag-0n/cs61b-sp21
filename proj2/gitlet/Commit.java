package gitlet;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author DrAgOn
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The time of this Commit. */
    private String timeStamp;
    /** The parent Commit. */
    private String parentCommit;
    /** The tracts of blobs */
    private HashMap<String, String> tracts;

    public Commit(String m) {
        message = m;
        Date initialTime = new Date(0);
        String curTimeStamp = String.format(
                Locale.US,
                "%ta %tb %te %tT %tY %tz",
                initialTime, initialTime, initialTime, initialTime, initialTime, initialTime
        );
        timeStamp = curTimeStamp;
        parentCommit = null;
        tracts = new HashMap<>();
    }

    public Commit(Commit c) {
        message = c.message;
        timeStamp = c.timeStamp;
        parentCommit = null;
        tracts = new HashMap<>(c.tracts);
    }

    public void storeTrack(String fileName, String blobHash) {
        tracts.put(fileName, blobHash);
    }

    public void setParentCommit(String commitHash) {
        parentCommit = commitHash;
    }

    public void setMessage(String m) {
        message = m;
    }

    public void setCurTime() {
        Date curTime = new Date(System.currentTimeMillis());
        String curTimeStamp = String.format(
                Locale.US,
                "%ta %tb %te %tT %tY %tz",
                curTime, curTime, curTime, curTime, curTime, curTime
        );
        timeStamp = curTimeStamp;
    }

    public String returnTrack(String fileName) {
        return tracts.get(fileName);
    }

    public void rmTrack(String fileName) {
        tracts.remove(fileName);
    }

    public String returnParent() {
        return parentCommit;
    }

    public String returnTime() {
        return timeStamp;
    }

    public String returnMes() {
        return message;
    }
}
