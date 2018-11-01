package com.example.harjacober.obouncechat.data;

public class GroupInfo {
    private String groupName;
    private String grouPurpose;
    private String groupProfileUrl;
    private String groupThumnail;
    private long createdAt;
    private String groupId;

    public GroupInfo() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGrouPurpose() {
        return grouPurpose;
    }

    public void setGrouPurpose(String grouPurpose) {
        this.grouPurpose = grouPurpose;
    }

    public String getGroupProfileUrl() {
        return groupProfileUrl;
    }

    public void setGroupProfileUrl(String groupProfileUrl) {
        this.groupProfileUrl = groupProfileUrl;
    }

    public String getGroupThumnail() {
        return groupThumnail;
    }

    public void setGroupThumnail(String groupThumnail) {
        this.groupThumnail = groupThumnail;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
