package com.example.harjacober.obouncechat.data;

public class Group {
    private Message message;
    private User members;
    private GroupInfo details;

    public Group() {
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getUser() {
        return members;
    }

    public void setUser(User user) {
        this.members = user;
    }

    public GroupInfo getDetails() {
        return details;
    }

    public void setDetails(GroupInfo details) {
        this.details = details;
    }
}
