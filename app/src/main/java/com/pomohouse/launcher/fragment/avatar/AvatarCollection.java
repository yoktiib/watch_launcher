package com.pomohouse.launcher.fragment.avatar;


import com.pomohouse.launcher.R;

/**
 * Created by Admin on 11/17/2016 AD.
 */

public class AvatarCollection {

    private static AvatarCollection instance = null;
    private AvatarLinkedMap<String, Integer> avatarMap;

    public static AvatarCollection getInstance() {
        if (instance == null) {
            instance = new AvatarCollection();
        }
        return instance;
    }

    public AvatarLinkedMap<String, Integer> getAvatarMap() {
        return avatarMap;
    }

    private AvatarCollection() {
        avatarMap = new AvatarLinkedMap<>();
        avatarMap.put("0", R.drawable.avatar_1);
        avatarMap.put("1", R.drawable.avatar_2);
        avatarMap.put("2", R.drawable.avatar_3);
        avatarMap.put("3", R.drawable.avatar_4);
        avatarMap.put("4", R.drawable.avatar_5);
        avatarMap.put("5", R.drawable.avatar_6);
        avatarMap.put("6", R.drawable.avatar_7);
        avatarMap.put("7", R.drawable.avatar_8);
        /*avatarMap.put("8", R.drawable.avatar_9);
        avatarMap.put("9", R.drawable.avatar_other02);
        avatarMap.put("10", R.drawable.avatar_other03);
        avatarMap.put("11", R.drawable.avatar_other04);
        avatarMap.put("12", R.drawable.avatar_other05);
        avatarMap.put("13", R.drawable.avatar_other06);*/
    }

}
