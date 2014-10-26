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

package net.wiktorlawski.messageonthescreen.test.mocks;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class WindowManagerMock implements WindowManager {
    private static final int APP_WIDTH = 480;

    private static WindowManagerMock instance;

    private Display displayInstance;
    private View sharedElementView;

    private WindowManagerMock() throws Exception {
        /* displayInstance */
        Class<?> displayManagerGlobal =
            Class.forName("android.hardware.display.DisplayManagerGlobal");
        Method getInstance = displayManagerGlobal.getMethod("getInstance");
        Class<?> displayInfo = Class.forName("android.view.DisplayInfo");
        Object displayInfoInstance =
            displayInfo.cast(displayInfo.newInstance());
        Field displayInfoAppWidthField = displayInfo.getField("appWidth");
        displayInfoAppWidthField.set(displayInfoInstance, APP_WIDTH);
        Class<?> displayAdjustments =
            Class.forName("android.view.DisplayAdjustments");
        Class<?> display = Class.forName("android.view.Display");
        Constructor<?> displayConstructor =
            display.getDeclaredConstructor(displayManagerGlobal, int.class,
                displayInfo, displayAdjustments);
        displayInstance =
            (Display) displayConstructor.newInstance(
                displayManagerGlobal.cast(getInstance.invoke(null)),
                1 /* TYPE_BUILT_IN */,
                displayInfoInstance,
                displayAdjustments.newInstance());
    }

    public static WindowManagerMock getInstance() throws Exception {
        if (instance == null) {
            instance = new WindowManagerMock();
        }

        return instance;
    }

    @Override
    public void addView(View view, android.view.ViewGroup.LayoutParams params) {
        if (sharedElementView == null) {
            sharedElementView = view;
        } else {
            /* Force NullPointerException - this view is already added! */
            sharedElementView = null;
            sharedElementView.equals(view);
        }
    }

    @Override
    public void removeView(View view) {
        if (sharedElementView.equals(view)) {
            sharedElementView = null;
        }
    }

    @Override
    public void updateViewLayout(View view,
            android.view.ViewGroup.LayoutParams params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Display getDefaultDisplay() {
        return displayInstance;
    }

    @Override
    public void removeViewImmediate(View view) {
        throw new UnsupportedOperationException();
    }
}
