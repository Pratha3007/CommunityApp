package src.model;

public class Community {
    private int communityId;

    private String name;
    String description;
    private User createdByUser;

    public Community(int communityId, String name, String description, User createdByUser) {
        this.communityId = communityId;
        this.name = name;
        this.description = description;
        this.createdByUser = createdByUser;
    }
    public Community(String name, String description, User createdByUser) {
        this.name = name;
        this.description = description;
        this.createdByUser = createdByUser;
    }

    public int getCommunityId() {
        return communityId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
