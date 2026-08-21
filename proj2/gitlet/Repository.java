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

    /* TODO: fill in the rest of this class. */
    public static void initialize() {
        /* Create .gitlet directory and subdirectory in it*/
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            COMMITS_DIR.mkdir();
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        /* Create initial commit */
        Commit initCommit = new Commit("initial commit", "00:00:00 UTC, Thursday, 1 January 1970", null);
        File commit0 = join(COMMITS_DIR, "Commit0");
        writeObject(commit0, initCommit);
    }
}
