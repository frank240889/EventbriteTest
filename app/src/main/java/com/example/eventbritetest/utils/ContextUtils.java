package com.example.eventbritetest.utils;

import android.app.Application;

import com.example.eventbritetest.R;

import java.util.HashMap;

public class ContextUtils {
    private static ContextUtils INSTANCE;
    private static Application mApplication;
    private static HashMap<String, String> mCategories;

    private ContextUtils(Application application) {
        mApplication = application;
        initCategories();
    }

    static {
        mCategories = new HashMap<>();
    }

    private static void initCategories() {
        String[] categoriesId = mApplication.getResources().getStringArray(R.array.id_categories);
        String[] categoriesName = mApplication.getResources().getStringArray(R.array.name_categories);
        for(int i = 0 ; i < categoriesId.length ; i++) {
            mCategories.put(categoriesId[i], categoriesName[i]);
        }
    }

    public static ContextUtils init(Application application) {
        if(INSTANCE == null) {
            INSTANCE = new ContextUtils(application);
        }
        return INSTANCE;
    }

    public float dpToPx(int dp) {
        return dp * mApplication.getResources().getDisplayMetrics().density;
    }

    public String toCommonEventCategory(String idCategory) {
        if(idCategory.equals(""))
            return "";
        return mCategories.get(idCategory);
    }
}
