package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author DrAgOn
 */
public class Repository implements Serializable {
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
        String commitHash = sha1(serialize(initCommit));
        File commit = join(COMMITS_DIR, commitHash);
        writeObject(commit, initCommit);
        /* Save HEAD and MASTER information */
        File head = join(GITLET_DIR, "HEAD");
        writeContents(head, commitHash);
        File master = join(GITLET_DIR, "MASTER");
        writeObject(master, commitHash);
    }

    public static void add(String fileName) {
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        /* Write file into blob */
        byte[] blobContents = readContents(addFile);
        String blobHash = sha1(blobContents);
        File blob = join(BLOBS_DIR, blobHash);
        writeObject(blob, sha1(addFile));
        /* Staged for addition */
        File addition = join(STAGED_FOR_ADDITION, fileName);
        /*
         * If the addition track already exists,
         * delete the previous saved blob.
         */
        if (addition.exists()) {
            String removalBlobHash = readContentsAsString(addition);
            File removalBlob = join(BLOBS_DIR, removalBlobHash);
            restrictedDelete(removalBlob);
        }
        writeContents(addition, blobHash);
        /*
         * Compare file's hash code with current commit's file,
         * remove the addition if current blob is the same as the current commit's.
         */
        File head = join(GITLET_DIR, "HEAD");
        String curCommitHash = readContentsAsString(head);
        File cur = join(COMMITS_DIR, curCommitHash);
        Commit curCommit = readObject(cur, Commit.class);
        String curBlob = curCommit.returnBlob(sha1(fileName));
        if (curBlob != null) {
            if (curBlob.equals(blobHash)) {

            }
        }
    }
}
