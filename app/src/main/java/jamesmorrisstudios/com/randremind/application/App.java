/*
 * Copyright (c) 2015. James Morris Studios <james.morris.studios@gmail.com>
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of James Morris Studios.
 * The intellectual and technical concepts contained
 * herein are proprietary to James Morris Studios are protected
 * by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from James Morris Studios.
 */

package jamesmorrisstudios.com.randremind.application;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Top level application class
 * <p/>
 * Created by James on 7/7/2014.
 */
public final class App extends Application {
    private static Context context;

    /**
     * Gets the Application level Context.
     * NEVER hold a reference to this as that can cause a memory leak
     *
     * @return Application Context
     */
    @NonNull
    public static Context getContext() {
        return context;
    }

    /**
     * Initial create for the application.
     * Sets application level context
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}
