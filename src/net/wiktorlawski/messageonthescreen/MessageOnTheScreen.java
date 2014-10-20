/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Wiktor Lawski <wiktor.lawski@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.wiktorlawski.messageonthescreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * This class is core API for libmots library.
 */
public class MessageOnTheScreen {
    private static MessageOnTheScreen sInstance;

    private MessageOnTheScreen(Activity activity, boolean startShowing) {
        Context applicationContext = activity.getApplicationContext();
        applicationContext.startService(new Intent(applicationContext,
                SharedElementService.class));
    }

    /**
     * Returns reference to MessageOnTheScreen instance.
     * @param activity - caller Activity.
     * @param startShowing - if true, then showing of shared element will be
     * started immediately, even when there are no debug messages.
     * @return reference to MessageOnTheScreen instance.
     */
    static synchronized public MessageOnTheScreen getInstance(
            Activity activity, boolean startShowing) {
        if (sInstance == null) {
            sInstance = new MessageOnTheScreen(activity, startShowing);
        }

        show(activity, startShowing);

        return sInstance;
    }

    /**
     * Sets new text inside debug window (replacing current content).
     * @param activity - caller Activity.
     * @param newText - text that should replace current content of debug
     * window.
     */
    public void setText(Activity activity, String newText) {
    	Context applicationContext = activity.getApplicationContext();
    	Intent intent = new Intent(applicationContext,
    	        SharedElementService.class);
    	intent.putExtra(SharedElementService.COMMAND,
    			SharedElementService.SET_TEXT);
    	intent.putExtra(SharedElementService.NEW_MESSAGE, newText);
    	applicationContext.startService(intent);
    }

    private static void show(Activity activity, boolean startShowing) {
        Context applicationContext = activity.getApplicationContext();
        Intent intent = new Intent(applicationContext,
                SharedElementService.class);
        intent.putExtra(SharedElementService.COMMAND,
            SharedElementService.SHOW_SHARED_ELEMENT);
        intent.putExtra(SharedElementService.START_SHOWING, startShowing);
        applicationContext.startService(intent);
    }
}