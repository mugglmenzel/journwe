package models.helpers;

import java.io.Serializable;

/**
 * Created by mugglmenzel on 17.02.14.
 */
public class SocialAutocompleteFriend implements Serializable {

    private String id;

    private String displayName;

    private String concatinatedNameIdentifiers;

    public SocialAutocompleteFriend(String id, String displayName, String concatinatedNameIdentifiers) {
        this.id = id;
        this.displayName = displayName;
        this.concatinatedNameIdentifiers = concatinatedNameIdentifiers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getConcatinatedNameIdentifiers() {
        return concatinatedNameIdentifiers;
    }

    public void setConcatinatedNameIdentifiers(String concatinatedNameIdentifiers) {
        this.concatinatedNameIdentifiers = concatinatedNameIdentifiers;
    }
}
