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

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

/**
 * Service ensures that interactive element exists at the top of other Activity.
 */
public class SharedElementService extends Service {
    private static final int SHARED_ELEMENT_HEIGHT = 100;
    private static final int SHARED_ELEMENT_WIDTH = 100;
    private static final int SHARED_ELEMENT_PADDING_X = 25;
    private static final int SHARED_ELEMENT_PADDING_Y = 25;

    private ImageView sharedElement;
    private WindowManager windowManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        sharedElement = new ImageView(this);
        sharedElement.setImageResource(R.drawable.shared_element);
        LayoutParams parameters = new LayoutParams(SHARED_ELEMENT_WIDTH,
                SHARED_ELEMENT_HEIGHT, LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.OPAQUE);
        parameters.gravity = Gravity.RIGHT | Gravity.TOP;
        parameters.x = SHARED_ELEMENT_PADDING_X;
        parameters.y = SHARED_ELEMENT_PADDING_Y;

        windowManager.addView(sharedElement, parameters);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (sharedElement != null) {
            windowManager.removeView(sharedElement);
        }
    }
}
