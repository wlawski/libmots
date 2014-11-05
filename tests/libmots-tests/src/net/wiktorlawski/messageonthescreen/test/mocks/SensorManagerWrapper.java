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

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Looper;

public class SensorManagerWrapper {
    private static SensorManagerWrapper instance;

    private Object systemSensorManagerInstance;
    private SharedElementServiceMockContext context;

    private SensorManagerWrapper(SharedElementServiceMockContext context)
            throws Exception {
        this.context = context;

        Class<?> systemSensorManager =
            Class.forName("android.hardware.SystemSensorManager");
        Constructor<?> systemSensorManagerConstructor =
            systemSensorManager.getDeclaredConstructor(Context.class,
                    Looper.class);
        systemSensorManagerInstance =
            systemSensorManagerConstructor.newInstance(this.context,
                    Looper.getMainLooper());
    }

    public static SensorManagerWrapper getInstance(
            SharedElementServiceMockContext context) throws Exception {
        if (instance == null) {
            instance = new SensorManagerWrapper(context);
        }

        return instance;
    }

    public SensorManager getSensorManager() {
        return (SensorManager) systemSensorManagerInstance;
    }
}
