package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author DrAgOn
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The directory to store commits */
    public static final File COMMITS_DIR = join(GITLET_DIR, "Commits");
    /** The directory of staging area */
    public static final File STAGING_DIR = join(GITLET_DIR, "Staging_Area");
    public static final File STAGED_FOR_ADDITION = join(STAGING_DIR, "Staged_for_addition");
    public static final File STAGED_FOR_REMOVAL = join(STAGING_DIR, "Staged_for_removal");
    /** The directory to store blobs */
    public static final File BLOBS_DIR = join(GITLET_DIR, "Blobs");
    /** Count of blobs */
    private static int blobsCnt = 0;
    /** Count of addition tracks */
    private static int addCnt = 0;
    /** Head pointer and Master branch */
    public static Commit HEAD;
    public static Commit Master;

    /* TODO: fill in the rest of this class. */
    public static void initialize() {
        /* Create .gitlet directory and subdirectory init */
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            COMMITS_DIR.mkdir();
            STAGING_DIR.mkdir();
            STAGED_FOR_ADDITION.mkdir();
            STAGED_FOR_REMOVAL.mkdir();
            BLOBS_DIR.mkdir();
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        /* Create initial commit */
        Commit initCommit = new Commit("initial commit", "00:00:00 UTC, Thursday, 1 January 1970", null);
        Commit HEAD = initCommit;
        Commit Master = initCommit;
        File commit0 = join(COMMITS_DIR, "Commit0");
        writeObject(commit0, initCommit);
    }

    public static void add(String fileName) {
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        /* Write file into blob */
        File blob = join(BLOBS_DIR, "blob" + blobsCnt);
        writeObject(addFile, blob);
        /* Compare file's hash code with current commit's file */
        Object curBlob = HEAD.returnTrack(fileName);
        if (curBlob != null) {
            if (sha1(blob).equals(sha1(curBlob))) {
                return;
            }
        }
        Map<String, Object> track = null;
        track.put(fileName, blob);
        /* Staged for addition */
        File addition = join(STAGING_DIR, "addition" + addCnt);
        writeObject(addition, (Serializable) track);
    }
}
