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

import net.wiktorlawski.messageonthescreen.test.Position;
import net.wiktorlawski.messageonthescreen.test.SharedElementServiceTest;

import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class WindowManagerMock implements WindowManager {
    public static final int DEFAULT_DISPLAY_HEIGHT = 800;
    public static final int DEFAULT_DISPLAY_WIDTH = 480;
    public static final int DEFAULT_NAVIGATION_BAR_HEIGHT_LANDSCAPE = 0;
    public static final int DEFAULT_NAVIGATION_BAR_HEIGHT_PORTRAIT = 0;

    private static WindowManagerMock instance;

    private View binView;
    private Position binViewPosition;
    private Display displayInstance;
    private View sharedElementView;
    private Position viewPosition;

    private WindowManagerMock() throws Exception {
        createNewDisplay(DEFAULT_DISPLAY_WIDTH, DEFAULT_DISPLAY_HEIGHT);
    }

    private void createNewDisplay(int displayWidth, int displayHeight)
            throws Exception {
        Class<?> displayManagerGlobal =
            Class.forName("android.hardware.display.DisplayManagerGlobal");
        Method getInstance = displayManagerGlobal.getMethod("getInstance");
        Class<?> displayInfo = Class.forName("android.view.DisplayInfo");
        Object displayInfoInstance =
            displayInfo.cast(displayInfo.newInstance());
        Field displayInfoAppWidthField = displayInfo.getField("appWidth");
        displayInfoAppWidthField.set(displayInfoInstance, displayWidth);
        Field displayInfoAppHeightField = displayInfo.getField("appHeight");
        displayInfoAppHeightField.set(displayInfoInstance, displayHeight);
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

    @SuppressWarnings("deprecation")
    @Override
    public void addView(View view, android.view.ViewGroup.LayoutParams params) {
        if (view.getClass().getName().equals(
                "net.wiktorlawski.messageonthescreen" +
                ".SharedElementService$SharedElementView")) {
            if (sharedElementView == null) {
                sharedElementView = view;
                android.view.WindowManager.LayoutParams layoutParams =
                        (android.view.WindowManager.LayoutParams) params;
                viewPosition = new Position(layoutParams.x, layoutParams.y);
            } else {
                /* Force NullPointerException - this view is already added! */
                sharedElementView = null;
                sharedElementView.equals(view);
            }
        } else if (view.getClass().getName()
                .equals("android.widget.ImageView")) {
            if (binView == null) {
                binView = view;
                binViewPosition =
                        new Position((getDefaultDisplay().getWidth() -
                                SharedElementServiceTest.BIN_WIDTH) / 2,
                                getDefaultDisplay().getHeight() -
                                SharedElementServiceTest.BIN_HEIGHT);
            } else {
                /* Force NullPointerException - this view is already added! */
                binView = null;
                binView.equals(view);
            }
        }
    }

    @Override
    public void removeView(View view) {
        if (sharedElementView.equals(view)) {
            sharedElementView = null;
            viewPosition = null;
        } else if (binView.equals(view)) {
            binView = null;
            binViewPosition = null;
        }
    }

    @Override
    public void updateViewLayout(View view,
            android.view.ViewGroup.LayoutParams params) {
        if (sharedElementView.equals(view)) {
            android.view.WindowManager.LayoutParams layoutParams =
                    (android.view.WindowManager.LayoutParams) params;
            viewPosition = new Position(layoutParams.x, layoutParams.y);
        }
    }

    public void clean() {
        binView = null;
        binViewPosition = null;
        sharedElementView = null;
        viewPosition = null;
    }

    public Position getBinViewLocation() {
        if (binView == null) {
            return new Position(-1, -1); /* Incorrect screen position */
        }

        return binViewPosition;
    }

    @Override
    public Display getDefaultDisplay() {
        return displayInstance;
    }

    public Position getViewLocation() {
        if (sharedElementView == null) {
            return new Position(-1, -1); /* Incorrect screen position */
        }

        return viewPosition;
    }

    @Override
    public void removeViewImmediate(View view) {
        throw new UnsupportedOperationException();
    }

    public void setNewDisplay(int newDisplayWidth, int newDisplayHeight)
            throws Exception {
        createNewDisplay(newDisplayWidth, newDisplayHeight);
    }
}
