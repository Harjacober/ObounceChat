package com.example.harjacober.obouncechat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.harjacober.obouncechat.data.Group;
import com.example.harjacober.obouncechat.data.GroupInfo;
import com.example.harjacober.obouncechat.data.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SharedPreferenceUtils {

    static SharedPreferences preferences;
    static SharedPreferences.Editor editor;
    public static final String DEFAULT_PREFERENCE = "com.example.harjacober.obouncechat-default-preference";
    public static final String PROFILE_URL = "com.example.harjacober.obouncechat-profile-uri-key";
    public static final String USERNAME = "com.example.harjacober.obouncechat-username-key";
    public static final String FULL_NAME = "com.example.harjacober.obouncechat-fullNAme-key";
    public static final String SELF_DESC = "com.example.harjacober.obouncechat-selfDesc-key";
    public static final String THUMBNAIL = "com.example.harjacober.obouncechat-thumbnail-key";
    public static final String EMAIL_KEY = "com.example.harjacober.obouncechat-email-key";
    public static final String PHONE_NO = "com.example.harjacober.obouncechat-phoneNO-key";
    public static final String CURR_FRND_ID = "com.example.harjacober.obouncechat-current-friendid-key";
    public static final String REG_PROGRESS = "com.example.harjacober.obouncechat-current-reg-progress-key";
    public static final String CURR_GROUP_INFO = "com.example.harjacober.obouncechat-current-rgroupinfo-key";
    public static final String CURR_GROUP = "com.example.harjacober.obouncechat-current-group-key";
    public static final String ALL_FRNDS_ID = "com.example.harjacober.obouncechat-all-frnds_id-key";
    public static final String CURR_FRND_USER_OBJ = "com.example.harjacober.obouncechat-current-frnd-user-obj-key";

    public static void saveUserProfilePicUrl(Context context, Uri profileUri){
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(PROFILE_URL, String.valueOf(profileUri));
        editor.commit();
    }
    public static void saveUsername(Context context, String profileUri){
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(USERNAME, String.valueOf(profileUri));
        editor.commit();
    }
    public static String retrieveProfileUri(Context context){
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(PROFILE_URL, "");
    }
    public static String retrieveUsername(Context context){
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(USERNAME, "");
    }

    public static void saveUserFullName(Context context, String fullName) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(FULL_NAME, fullName);
        editor.commit();
    }

    public static void saveUserSelfDesc(Context context, String selfDesc) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(SELF_DESC, selfDesc);
        editor.commit();
    }
    public static void savethumbnail(Context context, Uri thumbnail) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(THUMBNAIL, String.valueOf(thumbnail));
        editor.commit();
    }
    public static void saveEmail(Context context, String email) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(EMAIL_KEY, String.valueOf(email));
        editor.commit();
    }
    public static void savePhoneNo(Context context, String phoneNO) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(PHONE_NO, phoneNO);
        editor.commit();
    }
    public static void saveCurrentFriendId(Context context, String friendID) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(CURR_FRND_ID, friendID);
        editor.commit();
    }
    public static void saveCurrentGroupInfo(Context context, GroupInfo groupInfo) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(groupInfo); // myObject - instance of MyObject
        editor.putString(CURR_GROUP_INFO, json);
        editor.commit();
    }
    public static GroupInfo retrieveCurrGroupInfo(Context context){
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(CURR_GROUP_INFO, "");
        return gson.fromJson(json, GroupInfo.class);
    }
    public static void saveRegistrationProgress(Context context, String progress) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(REG_PROGRESS, progress);
        editor.commit();
    }
    public static String retrieveRegProgress(Context context){
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(REG_PROGRESS, "");
    }
    public static String retrieveFullName(Context context){
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(FULL_NAME, "");
    }
    public static String retrieveSelfDesc(Context context){
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(SELF_DESC, "");
    }

    public static String retrieveThumbnail(Context context) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(THUMBNAIL, "");
    }

    public static String retrievePhoneNo(Context context) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(PHONE_NO, "Not Provided");
    }

    public static String retrieveEmail(Context context) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(EMAIL_KEY, "Not Provided");
    }

    public static String retrieveCurrentFriendId(Context context) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(CURR_FRND_ID, "");
    }

    public static void saveCurrentGroup(Context context, Group group) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(group); // myObject - instance of MyObject
        editor.putString(CURR_GROUP, json);
        editor.commit();
    }
    public static Group retrieveCurrGroup(Context context){
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(CURR_GROUP, "");
        return gson.fromJson(json, Group.class);
    }

    public static void saveCurrentFriendUserObject(Context context, User user) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user); // myObject - instance of MyObject
        editor.putString(CURR_FRND_USER_OBJ, json);
        editor.commit();
    }
    public static User retrieveFrndUserObject(Context context){
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(CURR_FRND_USER_OBJ, "");
        return gson.fromJson(json, User.class);
    }

    public static void saveAllFriendsId(Context context, List<String> friendsIdList) {
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(friendsIdList); // myObject - instance of MyObject
        editor.putString(ALL_FRNDS_ID, json);
        editor.commit();
    }
    public static List retrieveAllFriendId(Context context){
        preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(ALL_FRNDS_ID, "");
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}