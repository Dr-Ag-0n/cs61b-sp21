package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author DrAgOn
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
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

    /* TODO: fill in the rest of this class. */
    public Commit(String m, String t) {
        message = m;
        timeStamp = t;
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

    public String returnBlobHash(String fileName) {
        return tracts.get(fileName);
    }

    public void setParentCommit(String commitHash) {
        parentCommit = commitHash;
    }

    public void setMessage(String m) {
        message = m;
    }

    public void setCurTime() {
        Date curTime = new Date(System.currentTimeMillis());
        timeStamp = curTime.toString();
    }

    public String returnTrack(String fileName) {
        return tracts.get(fileName);
    }

    public void rmTrack(String fileName) {
        tracts.remove(fileName);
    }
}
