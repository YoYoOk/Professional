package com.yj.professional.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityController {
	public static List<Activity> activities = new ArrayList<Activity>();

    //���һ���»
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    //�Ƴ�һ���
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    //�ر����л
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
