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

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

/**
 * Service ensures that interactive element exists at the top of other Activity.
 */
public class SharedElementService extends Service {
    public static final String COMMAND = "COMMAND";
    public static final String NEW_MESSAGE = "NEW_MESSAGE";
    public static final String START_SHOWING = "START_SHOWING";

    public static final int INCORRECT_COMMAND = 0;
    public static final int SET_TEXT = 1;
    public static final int SHOW_SHARED_ELEMENT = 2;
    public static final int ADD_MESSAGE = 3;

    private static final int SHARED_ELEMENT_HEIGHT = 100;
    private static final int SHARED_ELEMENT_WIDTH = 100;
    private static final int SHARED_ELEMENT_PADDING_X = 25;
    private static final int SHARED_ELEMENT_PADDING_Y = 25;

    private ArrayList<String> debugMessages = new ArrayList<String>();
    private OrientationListener orientationListener;
    private SharedElementView sharedElement;
    private LayoutParams sharedElementParameters;

    /* If true, shared element should be always visible */
    private boolean showing;
    private boolean visible; /* Current visibility of shared element */
    private WindowManager windowManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        sharedElement = new SharedElementView(this);
        sharedElement.setImageResource(R.drawable.icon);
        sharedElementParameters = new LayoutParams(SHARED_ELEMENT_WIDTH,
                SHARED_ELEMENT_HEIGHT, LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        sharedElementParameters.gravity = Gravity.LEFT | Gravity.TOP;
        sharedElementParameters.x =
                getScreenWidth() - SHARED_ELEMENT_WIDTH -
                SHARED_ELEMENT_PADDING_X;
        sharedElementParameters.y = SHARED_ELEMENT_PADDING_Y;

        orientationListener = new OrientationListener();
        orientationListener.enable();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if ((sharedElement != null) && visible) {
            windowManager.removeView(sharedElement);
        }
    }

    /*
     * This is the old onStart method that will be called on the pre-2.0
     * platform.  On 2.0 or later we override onStartCommand() so this
     * method will not be called.
     *
     * Reference:
     * http://developer.android.com/reference/android/app/Service.html
     */
    @Override
    public void onStart(Intent intent, int startId) {
    	handleCommand(intent);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	handleCommand(intent);

    	/* Continue running until explicitly stopped. */
    	return START_STICKY;
    }

    private void handleCommand(Intent intent) {
    	if (intent == null) {
            return;
    	}

    	Bundle bundle = intent.getExtras();

    	if (bundle == null) {
            return;
    	}

    	int command = bundle.getInt(COMMAND, INCORRECT_COMMAND);
    	String newMessage = bundle.getString(NEW_MESSAGE);
    	boolean startShowing = bundle.getBoolean(START_SHOWING, false);

    	switch (command) {
    	case SET_TEXT:
    	    debugMessages.clear();
    	    /* No break */

    	case ADD_MESSAGE:
    	    if (newMessage != null) {
    	        debugMessages.add(newMessage);
    	        showSharedElement(showing);
    	    } else {
    	    	hideSharedElement(showing);
    	    }

    	    break;

    	case SHOW_SHARED_ELEMENT:
    	    if (startShowing) {
    	    	showSharedElement(startShowing);
    	    } else {
    	    	hideSharedElement(startShowing);
    	    }

    	    break;
    	}
    }

    /* package */ void clearDebugMessages() {
    	debugMessages.clear();

    	if (!showing) {
    		hideSharedElement(showing);
    	}
    }

    @SuppressWarnings("deprecation")
    private int getScreenHeight() {
        return windowManager.getDefaultDisplay().getHeight();
    }

    @SuppressWarnings("deprecation")
    private int getScreenWidth() {
        return windowManager.getDefaultDisplay().getWidth();
    }

    private void hideSharedElement(boolean newShowing) {
    	showing = newShowing;

    	if ((debugMessages.isEmpty() == false) || !visible) {
    		return;
    	}

    	windowManager.removeView(sharedElement);

    	visible = false;
    }

    private void showSharedElement(boolean newShowing) {
    	showing = newShowing;

    	if (visible) {
    		return;
    	}

    	windowManager.addView(sharedElement, sharedElementParameters);

    	visible = true;
    }

    private class OrientationListener extends OrientationEventListener {
        private boolean previouslyPortrait;

        public OrientationListener() {
            super(getApplicationContext());

            previouslyPortrait = isPortrait();
        }

        private void adaptToOrientationChange() {
            float previousWidth = getScreenHeight();
            float previousHeight = getScreenWidth();
            float width = getScreenWidth();
            float height = getScreenHeight();
            float scaleWidth = width / previousWidth;
            float scaleHeight = height / previousHeight;

            sharedElementParameters.x *= scaleWidth;
            sharedElementParameters.y *= scaleHeight;
            windowManager.updateViewLayout(sharedElement,
                    sharedElementParameters);
        }

        /* package */ boolean isPortrait() {
            if (getScreenWidth() < getScreenHeight()) {
                return true;
            }

            return false;
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN) {
                return;
            }

            if (isPortrait()) {
                if (!previouslyPortrait) {
                    adaptToOrientationChange();
                    previouslyPortrait = true;
                }
            } else {
                if (previouslyPortrait) {
                    adaptToOrientationChange();
                    previouslyPortrait = false;
                }
            }
        }
    }

    private class SharedElementView extends ImageView {
        private static final int BIN_HEIGHT = 100;
        private static final int BIN_WIDTH = 100;
        private static final float TOUCH_ERROR_MARGIN = 7.5f;

        private ImageView binView;
        private float initialTouchX;
        private float initialTouchY;
        private float lastTouchX = -1;
        private float lastTouchY = -1;
        private boolean longPressed;
        private Handler longPressHandler = new Handler();
        private Runnable longPressRunnable = new LongPressRunnable();
        private boolean moved;
        private boolean readyToRemove;

        public SharedElementView(Context context) {
            super(context);
        }

        private void cancelLongPressBehavior() {
            longPressed = false;
            sharedElement.setImageResource(R.drawable.icon);
            longPressHandler.removeCallbacks(longPressRunnable);

            if (binView != null) {
                windowManager.removeView(binView);
                binView = null;
            }
        }

        private boolean isOverlappingBin() {
            final int showNavigationBarId = getResources()
                    .getIdentifier("config_showNavigationBar", "bool",
                        "android");

            final boolean hasNavigationBar = (showNavigationBarId > 0) ?
                    getResources().getBoolean(showNavigationBarId)
                    : false;

            final int navigationBarHeightPortrait =
                    hasNavigationBar ?
                        getResources().getDimensionPixelSize(
                                getResources()
                                .getIdentifier(
                                        "navigation_bar_height",
                                        "dimen", "android"))
                        : 0;

            final int navigationBarHeight =
                    orientationListener.isPortrait() ?
                        navigationBarHeightPortrait
                        /*
                         * Navigation bar located on the left or right in
                         * landscape mode
                         */
                        : 0;

            final int binX0 = (getScreenWidth() - BIN_WIDTH) / 2;
            final int binX1 = (getScreenWidth() + BIN_WIDTH) / 2;
            final int binY0 =
                    getScreenHeight() - BIN_HEIGHT - navigationBarHeight;

            if ((((sharedElementParameters.x >= binX0) &&
                    (sharedElementParameters.x <= binX1)) ||
                    ((sharedElementParameters.x + SHARED_ELEMENT_WIDTH >=
                            binX0) &&
                    (sharedElementParameters.x + SHARED_ELEMENT_WIDTH <=
                            binX1))) &&
                    (sharedElementParameters.y + SHARED_ELEMENT_HEIGHT >=
                            binY0)) {
                return true;
            }

            return false;
        }

        private boolean isSignificantDifference(float rawX, float rawY) {
            if ((initialTouchX >= rawX - TOUCH_ERROR_MARGIN) &&
                    (initialTouchX <= rawX + TOUCH_ERROR_MARGIN) &&
                    (initialTouchY >= rawY - TOUCH_ERROR_MARGIN) &&
                    (initialTouchY <= rawY + TOUCH_ERROR_MARGIN)) {
                return false;
            }

            return true;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            final float x = ev.getRawX();
            final float y = ev.getRawY();

            switch (ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
                cancelLongPressBehavior();
                readyToRemove = false;

                break;

            case MotionEvent.ACTION_DOWN:
                initialTouchX = lastTouchX = ev.getRawX();
                initialTouchY = lastTouchY = ev.getRawY();
                moved = false;

                cancelLongPressBehavior();
                readyToRemove = false;
                longPressHandler.postDelayed(longPressRunnable,
                        ViewConfiguration.getLongPressTimeout());

                break;

            case MotionEvent.ACTION_MOVE:
                /*
                 * Do not move shared element without previous ACTION_DOWN or
                 * ACTION_UP.
                 */
                if ((lastTouchX != -1) && (lastTouchY != -1)) {
                    sharedElementParameters.x += (int) (x - lastTouchX);
                    sharedElementParameters.y += (int) (y - lastTouchY);
                    windowManager.updateViewLayout(sharedElement,
                        sharedElementParameters);

                    if (isSignificantDifference(x, y)) {
                    	moved = true;

                        if (!longPressed) {
                            cancelLongPressBehavior();
                        }
                    }

                    if (longPressed) {
                        if (this.isOverlappingBin()) {
                            if (!readyToRemove) {
                                sharedElement
                                .setImageResource(R.drawable.icon_stroke_red);
                                readyToRemove = true;
                            }
                        } else if (readyToRemove) {
                            sharedElement
                            .setImageResource(R.drawable.icon_stroke);
                            readyToRemove = false;
                        }
                    }
                }

                lastTouchX = x;
                lastTouchY = y;

                break;

            case MotionEvent.ACTION_UP:
                if (!moved && (isSignificantDifference(x, y) == false) &&
                        !longPressed) {
                    new DebugWindow(getApplicationContext(), debugMessages,
                        SharedElementService.this);
                }

                initialTouchX = lastTouchX = x;
                initialTouchY = lastTouchY = y;
                moved = false;

                cancelLongPressBehavior();

                if (readyToRemove) {
                    SharedElementService.this.stopSelf();
                }

                break;
            }

            return true;
        }

        private class LongPressRunnable implements Runnable {
            private static final long VIBRATE_DURATION_MS = 50;

            @Override
            public void run() {
                longPressed = true;
                sharedElement.setImageResource(R.drawable.icon_stroke);

                Vibrator vibrator =
                        (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(VIBRATE_DURATION_MS);

                createBinView();
            }

            private void createBinView() {
                binView = new ImageView(SharedElementService.this);
                binView.setImageResource(R.drawable.bin);
                LayoutParams binViewParameters = new LayoutParams(BIN_WIDTH,
                        BIN_HEIGHT, LayoutParams.TYPE_PHONE,
                        LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSPARENT);
                binViewParameters.gravity = Gravity.CENTER | Gravity.BOTTOM;

                windowManager.addView(binView, binViewParameters);
            }
        }
    }
}
