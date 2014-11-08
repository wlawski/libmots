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

package net.wiktorlawski.messageonthescreen.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.wiktorlawski.messageonthescreen.SharedElementService;
import net.wiktorlawski.messageonthescreen.test.mocks.SharedElementServiceMockContext;
import net.wiktorlawski.messageonthescreen.test.mocks.WindowManagerMock;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.test.ServiceTestCase;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class SharedElementServiceTest
		extends ServiceTestCase<SharedElementService> {
	public static final String COMMAND_KEY = "COMMAND";
	public static final String NEW_MESSAGE_KEY = "NEW_MESSAGE";
	public static final String START_SHOWING_KEY = "START_SHOWING";

	private static final int SHARED_ELEMENT_HEIGHT = 100;
	private static final int SHARED_ELEMENT_WIDTH = 100;
	@SuppressWarnings("unused")
	private static final int SHARED_ELEMENT_PADDING_X = 25;
	private static final int SHARED_ELEMENT_PADDING_Y = 25;

	/* x = 800 - (SHARED_ELEMENT_WIDTH / 2) - SHARED_ELEMENT_PADDING_X */
	/* y = SHARED_ELEMENT_PADDING_Y + (SHARED_ELEMENT_HEIGHT / 2) */
	private static final Position DEFAULT_LANDSCAPE_POSITION =
			new Position(675, SHARED_ELEMENT_PADDING_Y);
	/* x = 480 - (SHARED_ELEMENT_WIDTH / 2) - SHARED_ELEMENT_PADDING_X */
	/* y = SHARED_ELEMENT_PADDING_Y + (SHARED_ELEMENT_HEIGHT / 2) */
	private static final Position DEFAULT_PORTRAIT_CENTER_POSITION =
			new Position(405, 75);
	/* 480 - SHARED_ELEMENT_WIDTH - SHARED_ELEMENT_PADDING_X */
	private static final Position DEFAULT_PORTRAIT_POSITION =
			new Position(355, SHARED_ELEMENT_PADDING_Y);
	private static final Position DEFAULT_POSITION =
			new Position(-1, -1);
	/*
	 * Rescaled DEFAULT_PORTRAIT_POSITION for changed orientation
	 * (portrait -> landscape)
	 */
	private static final Position DEFAULT_RESCALED_LANDSCAPE_POSITION =
			new Position(591, 15);
	/*
	 * Rescaled DEFAULT_LANDSCAPE_POSITION for changed orientation
	 * (landscape -> portrait)
	 */
	private static final Position DEFAULT_RESCALED_PORTRAIT_POSITION =
			new Position(405, 41);

	private static final String DEBUG_MESSAGES_FIELD_NAME = "debugMessages";
	private static final String INITIAL_TOUCH_X_FIELD_NAME = "initialTouchX";
	private static final String INITIAL_TOUCH_Y_FIELD_NAME = "initialTouchY";
	private static final String LAST_TOUCH_X_FIELD_NAME = "lastTouchX";
	private static final String LAST_TOUCH_Y_FIELD_NAME = "lastTouchY";
	private static final String MOVED_FIELD_NAME = "moved";
	private static final String ON_TOUCH_EVENT_METHOD_NAME = "onTouchEvent";
	private static final String ORIENTATION_LISTENER_FIELD_NAME =
			"orientationListener";
	private static final String PREVIOUSLY_PORTRAIT_FIELD_NAME =
			"previouslyPortrait";
	private static final String SHARED_ELEMENT_PARAMETERS_FIELD_NAME =
			"sharedElementParameters";
	private static final String SHARED_ELEMENT_VIEW_FIELD_NAME =
			"sharedElement";
	private static final String SHOWING_FIELD_NAME = "showing";
	private static final String VISIBLE_FIELD_NAME = "visible";
	private static final String WINDOW_MANAGER_FIELD_NAME = "windowManager";
	private static final String CLEAR_DEBUG_MESSAGES_METHOD_NAME =
			"clearDebugMessages";

	public static final int COMMAND_SET_TEXT = 1;
	public static final int COMMAND_SHOW_SHARED_ELEMENT = 2;
	public static final int COMMAND_ADD_MESSAGE = 3;

	public SharedElementServiceTest() {
		super(SharedElementService.class);
	}

	@Override
	protected void setUp() throws Exception {
		setContext(new SharedElementServiceMockContext());
		WindowManagerMock.getInstance()
		.setNewDisplay(WindowManagerMock.DEFAULT_DISPLAY_WIDTH,
				WindowManagerMock.DEFAULT_DISPLAY_HEIGHT);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> getDebugMessages() throws Exception {
		SharedElementService service = getService();
		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		return (ArrayList<String>) debugMessagesField.get(service);
	}

	private float getInitialTouchX() throws Exception {
		Object sharedElementView = getSharedElementView();
		Field initialTouchXField =
				sharedElementView.getClass()
				.getDeclaredField(INITIAL_TOUCH_X_FIELD_NAME);
		initialTouchXField.setAccessible(true);

		return initialTouchXField.getFloat(sharedElementView);
	}

	private float getInitialTouchY() throws Exception {
		Object sharedElementView = getSharedElementView();
		Field initialTouchYField =
				sharedElementView.getClass()
				.getDeclaredField(INITIAL_TOUCH_Y_FIELD_NAME);
		initialTouchYField.setAccessible(true);

		return initialTouchYField.getFloat(sharedElementView);
	}

	private float getLastTouchX() throws Exception {
		Object sharedElementView = getSharedElementView();
		Field lastTouchXField =
				sharedElementView.getClass()
				.getDeclaredField(LAST_TOUCH_X_FIELD_NAME);
		lastTouchXField.setAccessible(true);

		return lastTouchXField.getFloat(sharedElementView);
	}

	private float getLastTouchY() throws Exception {
		Object sharedElementView = getSharedElementView();
		Field lastTouchYField =
				sharedElementView.getClass()
				.getDeclaredField(LAST_TOUCH_Y_FIELD_NAME);
		lastTouchYField.setAccessible(true);

		return lastTouchYField.getFloat(sharedElementView);
	}

	private OrientationEventListener getOrientationListener() throws Exception {
		SharedElementService service = getService();
		Field orientationListenerField =
				service.getClass()
				.getDeclaredField(ORIENTATION_LISTENER_FIELD_NAME);
		orientationListenerField.setAccessible(true);

		return (OrientationEventListener) orientationListenerField.get(service);
	}

	private boolean getPreviouslyPortrait() throws Exception {
		Object orientationListener = getOrientationListener();
		Field previouslyPortraitField =
				orientationListener.getClass()
				.getDeclaredField(PREVIOUSLY_PORTRAIT_FIELD_NAME);
		previouslyPortraitField.setAccessible(true);

		return previouslyPortraitField.getBoolean(orientationListener);
	}

	private Object getSharedElementView() throws Exception {
		SharedElementService service = getService();
		Field sharedElementViewField =
				service.getClass()
				.getDeclaredField(SHARED_ELEMENT_VIEW_FIELD_NAME);
		sharedElementViewField.setAccessible(true);

		return sharedElementViewField.get(service);
	}

	private WindowManagerMock getWindowManagerMock() throws Exception {
		SharedElementService service = getService();
		Field WindowManagerMockField =
				service.getClass().getDeclaredField(WINDOW_MANAGER_FIELD_NAME);
		WindowManagerMockField.setAccessible(true);

		return (WindowManagerMock) WindowManagerMockField.get(service);
	}

	private boolean isMoved() throws Exception {
		Object sharedElementView = getSharedElementView();
		Field movedField =
				sharedElementView.getClass()
				.getDeclaredField(MOVED_FIELD_NAME);
		movedField.setAccessible(true);

		return movedField.getBoolean(sharedElementView);
	}

	private boolean isShowing() throws Exception {
		SharedElementService service = getService();
		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);

		return showingField.getBoolean(service);
	}

	private boolean isVisible() throws Exception {
		SharedElementService service = getService();
		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);

		return visibleField.getBoolean(service);
	}

	private void sendAddMessage(String message) {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_ADD_MESSAGE);
		intent.putExtra(NEW_MESSAGE_KEY, message);
		startService(intent);
	}

	private void sendMotionEvent(float x, float y, int action)
			throws Exception {
		Object sharedElementView = getSharedElementView();
		Method onTouchEventMethod = Class.forName("android.widget.ImageView")
				.getMethod(ON_TOUCH_EVENT_METHOD_NAME, MotionEvent.class);
		MotionEvent motionEvent =
				MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), action, x, y, 0);
		onTouchEventMethod.invoke(sharedElementView, motionEvent);
	}

	private void sendStartShowing(boolean startShowing) {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SHOW_SHARED_ELEMENT);
		intent.putExtra(START_SHOWING_KEY, startShowing);
		startService(intent);
	}

	private void sendSetText(String newText) {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SET_TEXT);
		intent.putExtra(NEW_MESSAGE_KEY, newText);
		startService(intent);
	}

	/**
	 * This test checks default values.
	 */
	public void test_onCreate() throws Exception {
		Context context = getContext();

		Intent intent = new Intent();
		intent.setClass(context, SharedElementService.class);
		startService(intent);
		SharedElementService service = getService();

		/* debugMessages */
		ArrayList<String> debugMessages = getDebugMessages();
		assertNotNull(debugMessages);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		/* orientationListener */
		OrientationEventListener orientationListener = getOrientationListener();
		assertNotNull(orientationListener);
		Field mEnabledOrientationListenerField =
				OrientationEventListener.class.getDeclaredField("mEnabled");
		mEnabledOrientationListenerField.setAccessible(true);
		boolean mEnabledOrientationListener =
				(Boolean) mEnabledOrientationListenerField
				.get(orientationListener);
		assertTrue(mEnabledOrientationListener);

		/* sharedElement */
		ImageView sharedElement = (ImageView) getSharedElementView();
		assertNotNull(sharedElement);

		/* sharedElementParameters */
		Field sharedElementParametersField = service.getClass()
				.getDeclaredField(SHARED_ELEMENT_PARAMETERS_FIELD_NAME);
		sharedElementParametersField.setAccessible(true);
		LayoutParams sharedElementParameters =
				(LayoutParams) sharedElementParametersField.get(service);
		assertNotNull(sharedElementParameters);
		assertEquals(SHARED_ELEMENT_HEIGHT, sharedElementParameters.height);
		assertEquals(SHARED_ELEMENT_WIDTH, sharedElementParameters.width);
		assertEquals(LayoutParams.TYPE_PHONE, sharedElementParameters.type);
		assertEquals(LayoutParams.FLAG_NOT_FOCUSABLE,
				sharedElementParameters.flags);
		assertEquals(Gravity.LEFT | Gravity.TOP,
				sharedElementParameters.gravity);
		assertEquals((int) DEFAULT_PORTRAIT_POSITION.x,
				sharedElementParameters.x);
		assertEquals((int) DEFAULT_PORTRAIT_POSITION.y,
				sharedElementParameters.y);

		/* showing */
		assertEquals(false, isShowing());

		/* visible */
		assertEquals(false, isVisible());

		/* windowManager */
		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertNotNull(windowManagerMock);
		assertEquals(WindowManagerMock.getInstance(), windowManagerMock);
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should be started even when incoming intent does not come with
	 * any Bundle.
	 */
	public void test_onStartCommand() {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		startService(intent);
		SharedElementService service = getService();

		assertNotNull(service);
	}

	/**
	 * Service should ignore intent with empty extras (by default debugMessages
	 * list should be empty and shared element should not be visible).
	 */
	public void test_onStartCommand2() throws Exception {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtras(new Bundle());
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should ignore intent with empty extras and keep already gathered
	 * debugMessages list. Shared element should be visible because there is
	 * debug message.
	 */
	public void test_onStartCommand3() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtras(new Bundle());
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should ignore intent without "COMMAND" key (by default
	 * debugMessages list should be empty and shared element should not be
	 * visible).
	 */
	public void test_onStartCommand4() throws Exception {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra("DummyKey", 0);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should ignore intent without "COMMAND" key and keep already
	 * gathered debugMessages list. Shared element should be visible because
	 * there is debug message.
	 */
	public void test_onStartCommand5() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra("DummyKey", 0);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should ignore intent with value '0' for "COMMAND" key (by
	 * default debugMessages list should be empty and shared element should not
	 * be visible).
	 */
	public void test_onStartCommand6() throws Exception {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, 0);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should ignore intent with value '0' for "COMMAND" key and keep
	 * already gathered debugMessages list. Shared element should be visible
	 * because there is debug message.
	 */
	public void test_onStartCommand7() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, 0);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should ignore intent with negative integer value for "COMMAND"
	 * key (by default debugMessages list should be empty and shared element
	 * should not be visible).
	 */
	public void test_onStartCommand8() throws Exception {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, -1);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should ignore intent with negative integer value for "COMMAND"
	 * key and keep already gathered debugMessages list. Shared element should
	 * be visible because there is debug message.
	 */
	public void test_onStartCommand9() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, -1);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should ignore intent with unknown positive integer value for
	 * "COMMAND" key (by default debugMessages list should be empty and shared
	 * element should not be visible).
	 */
	public void test_onStartCommand10() throws Exception {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, 1000);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should ignore intent with unknown positive integer value for
	 * "COMMAND" key and keep already gathered debugMessages list. Shared
	 * element should be visible because there is debug message.
	 */
	public void test_onStartCommand11() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, 1000);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should prepare empty debugMessages list for ADD_MESSAGE command
	 * when intent does not deliver any text value and service has not existed
	 * before (by default shared element should not be visible).
	 */
	public void test_onStartCommand_ADD_MESSAGE() throws Exception {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_ADD_MESSAGE);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should keep gathered debugMessages list for ADD_MESSAGE command
	 * when intent does not deliver new message value and service already
	 * contains at least one debug message.
	 */
	public void test_onStartCommand_ADD_MESSAGE2() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_ADD_MESSAGE);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should keep gathered debugMessages list for ADD_MESSAGE when
	 * intent delivers null as new text value (by default shared element should
	 * not be visible for empty debugMessages list).
	 */
	public void test_onStartCommand_ADD_MESSAGE3() throws Exception {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_ADD_MESSAGE);
		intent.putExtra(NEW_MESSAGE_KEY, (String) null);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should keep gathered debugMessages list for ADD_MESSAGE when
	 * intent delivers null as new text value (shared element should be visible
	 * for non-empty debugMessages list).
	 */
	public void test_onStartCommand_ADD_MESSAGE4() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_ADD_MESSAGE);
		intent.putExtra(NEW_MESSAGE_KEY, (String) null);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * When debugMessages list is empty and service receives ADD_MESSAGE command
	 * with any new non-null message value, debugMessages should contain only
	 * this new message value and shared element should be visible.
	 */
	public void test_onStartCommand_ADD_MESSAGE5() throws Exception {
		String expected = "text";

		sendAddMessage(expected);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * When debugMessages list is not empty and service receives ADD_MESSAGE
	 * command with any new non-null text value, debugMessages should keep
	 * already gathered messages and add this new message as last. Shared
	 * element should be visible.
	 */
	public void test_onStartCommand_ADD_MESSAGE6() throws Exception {
		String expected1 = "text1";
		String expected2 = "text2";

		sendSetText(expected1);
		sendAddMessage(expected2);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(2, debugMessages.size());
		assertEquals(expected1, debugMessages.get(0));
		assertEquals(expected2, debugMessages.get(1));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should accept empty strings as new messages provided for
	 * ADD_MESSAGE command and shared element should be visible.
	 */
	public void test_onStartCommand_ADD_MESSAGE7() throws Exception {
		String expected = new String();

		sendAddMessage(expected);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should accept strings that contains multiple lines as new
	 * messages provided for ADD_MESSAGE command. Shared element should be
	 * visible after adding such new message.
	 */
	public void test_onStartCommand_ADD_MESSAGE8() throws Exception {
		String expected = "text\ntext";

		sendAddMessage(expected);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should accept very long strings as new messages provided for
	 * ADD_MESSAGE command. Shared element should be visible after adding such
	 * message.
	 */
	public void test_onStartCommand_ADD_MESSAGE9() throws Exception {
		String text = "text";
		StringBuilder newText = new StringBuilder(10000 * text.length());

		for (int i = 0; i < 10000; i++) {
			newText.append("text");
		}

		String expected = newText.toString();

		sendAddMessage(expected);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should prepare empty debugMessages list and not show shared
	 * element for SHOW_SHARED_ELEMENT command when intent does not come with
	 * START_SHOWING key and service has not existed before.
	 */
	public void test_onStartCommand_SHOW_SHARED_ELEMENT() throws Exception {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SHOW_SHARED_ELEMENT);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should keep debugMessages list and shared element visible for
	 * SHOW_SHARED_ELEMENT command when intent does not come with START_SHOWING
	 * key and service already exists when there is debug message.
	 */
	public void test_onStartCommand_SHOW_SHARED_ELEMENT2() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SHOW_SHARED_ELEMENT);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should keep debugMessages list and visible shared element for
	 * SHOW_SHARED_ELEMENT command when intent does not come with value true for
	 * START_SHOWING key but there is debug message.
	 */
	public void test_onStartCommand_SHOW_SHARED_ELEMENT3() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SHOW_SHARED_ELEMENT);
		intent.putExtra(START_SHOWING_KEY, false);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should keep debugMessages list and keep showing shared element
	 * for SHOW_SHARED_ELEMENT command when intent comes with value true for
	 * START_SHOWING key and there is debug message.
	 */
	public void test_onStartCommand_SHOW_SHARED_ELEMENT4() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SHOW_SHARED_ELEMENT);
		intent.putExtra(START_SHOWING_KEY, true);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(true, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should keep empty debugMessages list (empty by default) and
	 * showing shared element for SHOW_SHARED_ELEMENT command when shared
	 * element was already showing and intent comes with value true for
	 * START_SHOWING key.
	 */
	public void test_onStartCommand_SHOW_SHARED_ELEMENT5() throws Exception {
		sendStartShowing(true);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SHOW_SHARED_ELEMENT);
		intent.putExtra(START_SHOWING_KEY, true);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(true, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should keep empty debugMessages list (empty by default) and
	 * stop showing shared element for SHOW_SHARED_ELEMENT command when shared
	 * element was already showing and intent comes with value false for
	 * START_SHOWING key.
	 */
	public void test_onStartCommand_SHOW_SHARED_ELEMENT6() throws Exception {
		sendStartShowing(true);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SHOW_SHARED_ELEMENT);
		intent.putExtra(START_SHOWING_KEY, false);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should prepare empty debugMessages list for SET_TEXT command when
	 * intent does not deliver any text value and service has not existed
	 * before (by default shared element should not be visible).
	 */
	public void test_onStartCommand_SET_TEXT() throws Exception {
		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SET_TEXT);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should clear debugMessages list for SET_TEXT command when intent
	 * does not deliver new text value and service already exists (by default
	 * shared element should not be visible).
	 */
	public void test_onStartCommand_SET_TEXT2() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SET_TEXT);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should clear debugMessages list for SET_TEXT command when intent
	 * delivers null as new text value (by default shared element should not be
	 * visible).
	 */
	public void test_onStartCommand_SET_TEXT3() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SET_TEXT);
		intent.putExtra(NEW_MESSAGE_KEY, (String) null);
		startService(intent);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * When service receives SET_TEXT command with any new non-null text value,
	 * debugMessages should contain only this new text value and shared element
	 * should be visible.
	 */
	public void test_onStartCommand_SET_TEXT4() throws Exception {
		String expected = "text";

		sendSetText(expected);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should accept empty strings as new texts provided for SET_TEXT
	 * command and shared element should be visible.
	 */
	public void test_onStartCommand_SET_TEXT5() throws Exception {
		String expected = new String();

		sendSetText(expected);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should accept strings that contains multiple lines as new texts
	 * provided for SET_TEXT command. Shared element should be visible.
	 */
	public void test_onStartCommand_SET_TEXT6() throws Exception {
		String expected = "text\ntext";

		sendSetText(expected);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Service should accept very long strings as new texts provided for
	 * SET_TEXT command. Shared element should be visible.
	 */
	public void test_onStartCommand_SET_TEXT7() throws Exception {
		String text = "text";
		StringBuilder newText = new StringBuilder(10000 * text.length());

		for (int i = 0; i < 10000; i++) {
			newText.append("text");
		}

		String expected = newText.toString();

		sendSetText(expected);

		ArrayList<String> debugMessages = getDebugMessages();
		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		assertEquals(false, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * After clearing empty list, it should be still empty (by default shared
	 * element should not be visible).
	 */
	public void test_clearDebugMessages() throws Exception {
		Context context = getContext();
		Intent intent = new Intent();
		intent.setClass(context, SharedElementService.class);
		startService(intent);
		SharedElementService service = getService();

		Method clearDebugMessagesMethod =
				service.getClass()
				.getDeclaredMethod(CLEAR_DEBUG_MESSAGES_METHOD_NAME);
		clearDebugMessagesMethod.setAccessible(true);
		clearDebugMessagesMethod.invoke(service);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * After clearing empty list, it should be still empty. Shared element
	 * should be visible when requested.
	 */
	public void test_clearDebugMessages2() throws Exception {
		sendStartShowing(true);

		SharedElementService service = getService();

		Method clearDebugMessagesMethod =
				service.getClass()
				.getDeclaredMethod(CLEAR_DEBUG_MESSAGES_METHOD_NAME);
		clearDebugMessagesMethod.setAccessible(true);
		clearDebugMessagesMethod.invoke(service);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(true, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * After clearing list that contains one debug message, it should be empty
	 * (by default shared element should hide itself).
	 */
	public void test_clearDebugMessages3() throws Exception {
		sendSetText("text");

		SharedElementService service = getService();

		Method clearDebugMessagesMethod =
				service.getClass()
				.getDeclaredMethod(CLEAR_DEBUG_MESSAGES_METHOD_NAME);
		clearDebugMessagesMethod.setAccessible(true);
		clearDebugMessagesMethod.invoke(service);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * After clearing list that contains one debug message, it should be empty.
	 * Shared element should not hide itself when requested to be visible.
	 */
	public void test_clearDebugMessages4() throws Exception {
		sendStartShowing(true);
		sendSetText("text");

		SharedElementService service = getService();

		Method clearDebugMessagesMethod =
				service.getClass()
				.getDeclaredMethod(CLEAR_DEBUG_MESSAGES_METHOD_NAME);
		clearDebugMessagesMethod.setAccessible(true);
		clearDebugMessagesMethod.invoke(service);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(true, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * After clearing list that contains multiple debug messages, it should be
	 * empty (by default shared element should hide itself).
	 */
	public void test_clearDebugMessages5() throws Exception {
		sendSetText("text");
		sendAddMessage("text");

		SharedElementService service = getService();

		Method clearDebugMessagesMethod =
				service.getClass()
				.getDeclaredMethod(CLEAR_DEBUG_MESSAGES_METHOD_NAME);
		clearDebugMessagesMethod.setAccessible(true);
		clearDebugMessagesMethod.invoke(service);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(false, isShowing());
		assertEquals(false, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_POSITION.x, windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_POSITION.y, windowManagerMock.getViewLocation().y);
	}

	/**
	 * After clearing list that contains multiple debug messages, it should be
	 * empty. Shared element should not hide itself when requested to be
	 * visible.
	 */
	public void test_clearDebugMessages6() throws Exception {
		sendStartShowing(true);
		sendSetText("text");
		sendAddMessage("text");

		SharedElementService service = getService();

		Method clearDebugMessagesMethod =
				service.getClass()
				.getDeclaredMethod(CLEAR_DEBUG_MESSAGES_METHOD_NAME);
		clearDebugMessagesMethod.setAccessible(true);
		clearDebugMessagesMethod.invoke(service);

		ArrayList<String> debugMessages = getDebugMessages();

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		assertEquals(true, isShowing());
		assertEquals(true, isVisible());

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * OrientationEventListener should know screen orientation when created
	 * (default orientation is portrait).
	 */
	public void test_onOrientationChanged() throws Exception {
		WindowManagerMock windowManagerMock = WindowManagerMock.getInstance();
		sendStartShowing(true);

		assertEquals(true, getPreviouslyPortrait());
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * OrientationEventListener should know screen orientation when created
	 * for device in landscape mode.
	 */
	public void test_onOrientationChanged2() throws Exception {
		WindowManagerMock windowManagerMock = WindowManagerMock.getInstance();
		windowManagerMock
		.setNewDisplay(WindowManagerMock.DEFAULT_DISPLAY_HEIGHT,
				WindowManagerMock.DEFAULT_DISPLAY_WIDTH);
		sendStartShowing(true);

		assertEquals(false, getPreviouslyPortrait());
		assertEquals(DEFAULT_LANDSCAPE_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_LANDSCAPE_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * OrientationEventListener should ignore "orientation change" when new
	 * orientation value equals ORIENTATION_UNKNOWN (default orientation is
	 * portrait).
	 */
	public void test_onOrientationChanged3() throws Exception {
		WindowManagerMock windowManagerMock = WindowManagerMock.getInstance();
		sendStartShowing(true);

		OrientationEventListener orientationListener = getOrientationListener();
		orientationListener.onOrientationChanged(
				OrientationEventListener.ORIENTATION_UNKNOWN);

		assertEquals(true, getPreviouslyPortrait());
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * OrientationEventListener should move shared element after orientation
	 * change from portrait to landscape.
	 */
	public void test_onOrientationChanged4() throws Exception {
		WindowManagerMock windowManagerMock = WindowManagerMock.getInstance();
		sendStartShowing(true);

		windowManagerMock
		.setNewDisplay(WindowManagerMock.DEFAULT_DISPLAY_HEIGHT,
				WindowManagerMock.DEFAULT_DISPLAY_WIDTH);
		OrientationEventListener orientationListener = getOrientationListener();
		orientationListener.onOrientationChanged(100);

		assertEquals(false, getPreviouslyPortrait());
		assertEquals(DEFAULT_RESCALED_LANDSCAPE_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_RESCALED_LANDSCAPE_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * OrientationEventListener should move shared element after orientation
	 * change from landscape to portrait.
	 */
	public void test_onOrientationChanged5() throws Exception {
		WindowManagerMock windowManagerMock = WindowManagerMock.getInstance();
		windowManagerMock
		.setNewDisplay(WindowManagerMock.DEFAULT_DISPLAY_HEIGHT,
				WindowManagerMock.DEFAULT_DISPLAY_WIDTH);
		sendStartShowing(true);

		windowManagerMock
		.setNewDisplay(WindowManagerMock.DEFAULT_DISPLAY_WIDTH,
				WindowManagerMock.DEFAULT_DISPLAY_HEIGHT);
		OrientationEventListener orientationListener = getOrientationListener();
		orientationListener.onOrientationChanged(10);

		assertEquals(true, getPreviouslyPortrait());
		assertEquals(DEFAULT_RESCALED_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_RESCALED_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
	}

	/**
	 * Initial touch on shared element should not move it but initialize its
	 * fields to current values.
	 */
	public void test_onTouchEvent() throws Exception {
		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getLastTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * Release of shared element without previous touch should not move shared
	 * element but initialize its fields to current values.
	 */
	public void test_onTouchEvent2() throws Exception {
		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_UP);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getLastTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * MotionEvent.ACTION_CANCEL for shared element without previous touch
	 * should neither move shared element nor affect its fields values.
	 */
	public void test_onTouchEvent3() throws Exception {
		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_CANCEL);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
		assertEquals(0.0f, getInitialTouchX());
		assertEquals(0.0f, getInitialTouchY());
		assertEquals(DEFAULT_POSITION.x, getLastTouchX());
		assertEquals(DEFAULT_POSITION.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * Move event for shared element should not cause its position change
	 * without previous touch.
	 */
	public void test_onTouchEvent4() throws Exception {
		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_MOVE);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
		assertEquals(0.0f, getInitialTouchX());
		assertEquals(0.0f, getInitialTouchY());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getLastTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * Sequence of MotionEvent.ACTION_DOWN events on shared element should not
	 * move it but update its fields to current values.
	 */
	public void test_onTouchEvent5() throws Exception {
		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getLastTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * MotionEvent.ACTION_CANCEL for shared element after
	 * MotionEvent.ACTION_DOWN event should not affect its fields values.
	 */
	public void test_onTouchEvent6() throws Exception {
		int deltaX = 5;
		int deltaY = 5;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newTouchPosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newTouchPosition.x, newTouchPosition.y,
				MotionEvent.ACTION_CANCEL);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getLastTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * MotionEvent.ACTION_MOVE on already touched shared element should update
	 * its position.
	 */
	public void test_onTouchEvent7() throws Exception {
		int deltaX = 5;
		int deltaY = 5;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newTouchPosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newTouchPosition.x, newTouchPosition.y,
				MotionEvent.ACTION_MOVE);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
				getLastTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY,
				getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * MotionEvent.ACTION_DOWN after releasing shared element should trigger
	 * update of its fields but do not move it.
	 */
	public void test_onTouchEvent8() throws Exception {
		int deltaX = 5;
		int deltaY = 5;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_UP);
		Position newTouchPosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newTouchPosition.x, newTouchPosition.y,
				MotionEvent.ACTION_DOWN);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
				getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY,
				getInitialTouchY());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
				getLastTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY,
				getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * MotionEvent.ACTION_CANCEL for shared element after MotionEvent.ACTION_UP
	 * event should not affect its fields values.
	 */
	public void test_onTouchEvent9() throws Exception {
		int deltaX = 5;
		int deltaY = 5;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_UP);
		Position newTouchPosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newTouchPosition.x, newTouchPosition.y,
				MotionEvent.ACTION_CANCEL);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getLastTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * MotionEvent.ACTION_MOVE on already touched shared element should update
	 * its position, even if previous event was MotionEvent.ACTION_UP.
	 */
	public void test_onTouchEvent10() throws Exception {
		int deltaX = 5;
		int deltaY = 5;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_UP);
		Position newTouchPosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newTouchPosition.x, newTouchPosition.y,
				MotionEvent.ACTION_MOVE);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(newTouchPosition.x, getLastTouchX());
		assertEquals(newTouchPosition.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * MotionEvent.ACTION_DOWN after MotionEvent.ACTION_MOVE should trigger
	 * update of shared element fields but do not move it.
	 */
	public void test_onTouchEvent11() throws Exception {
		int deltaX = 5;
		int deltaY = 5;
		int deltaX2 = 10;
		int deltaY2 = 10;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newMovePosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newMovePosition.x, newMovePosition.y,
				MotionEvent.ACTION_MOVE);
		Position newTouchPosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x - deltaX2,
						DEFAULT_PORTRAIT_CENTER_POSITION.y - deltaY2);
		sendMotionEvent(newTouchPosition.x, newTouchPosition.y,
				MotionEvent.ACTION_DOWN);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x - deltaX2,
				getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y - deltaY2,
				getInitialTouchY());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x - deltaX2,
				getLastTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y - deltaY2,
				getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * MotionEvent.ACTION_UP after MotionEvent.ACTION_MOVE should trigger
	 * update of shared element fields but do not move it further.
	 */
	public void test_onTouchEvent12() throws Exception {
		int deltaX = 5;
		int deltaY = 5;
		int deltaX2 = 15;
		int deltaY2 = 15;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newMovePosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newMovePosition.x, newMovePosition.y,
				MotionEvent.ACTION_MOVE);
		Position newTouchPosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x - deltaX2,
						DEFAULT_PORTRAIT_CENTER_POSITION.y - deltaY2);
		sendMotionEvent(newTouchPosition.x, newTouchPosition.y,
				MotionEvent.ACTION_UP);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(newTouchPosition.x, getInitialTouchX());
		assertEquals(newTouchPosition.y, getInitialTouchY());
		assertEquals(newTouchPosition.x, getLastTouchX());
		assertEquals(newTouchPosition.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * Moving shared element a bit to the left should not mean for the final
	 * user that it is "moved".
	 */
	public void test_onTouchEvent13() throws Exception {
		int deltaX = -7;
		int deltaY = 0;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newMovePosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newMovePosition.x, newMovePosition.y,
				MotionEvent.ACTION_MOVE);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(newMovePosition.x, getLastTouchX());
		assertEquals(newMovePosition.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * Moving shared element a bit to the right should not mean for the final
	 * user that it is "moved".
	 */
	public void test_onTouchEvent14() throws Exception {
		int deltaX = 7;
		int deltaY = 0;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newMovePosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newMovePosition.x, newMovePosition.y,
				MotionEvent.ACTION_MOVE);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(newMovePosition.x, getLastTouchX());
		assertEquals(newMovePosition.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * Moving shared element a bit to the top should not mean for the final
	 * user that it is "moved".
	 */
	public void test_onTouchEvent15() throws Exception {
		int deltaX = 0;
		int deltaY = -7;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newMovePosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newMovePosition.x, newMovePosition.y,
				MotionEvent.ACTION_MOVE);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(newMovePosition.x, getLastTouchX());
		assertEquals(newMovePosition.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * Moving shared element a bit to the bottom should not mean for the final
	 * user that it is "moved".
	 */
	public void test_onTouchEvent16() throws Exception {
		int deltaX = 0;
		int deltaY = 7;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newMovePosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newMovePosition.x, newMovePosition.y,
				MotionEvent.ACTION_MOVE);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(newMovePosition.x, getLastTouchX());
		assertEquals(newMovePosition.y, getLastTouchY());
		assertEquals(false, isMoved());
	}

	/**
	 * Changing shared element position significantly (left) means for the final
	 * user that it is "moved".
	 */
	public void test_onTouchEvent17() throws Exception {
		int deltaX = -8;
		int deltaY = 0;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newMovePosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newMovePosition.x, newMovePosition.y,
				MotionEvent.ACTION_MOVE);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(newMovePosition.x, getLastTouchX());
		assertEquals(newMovePosition.y, getLastTouchY());
		assertEquals(true, isMoved());
	}

	/**
	 * Changing shared element position significantly (right) means for
	 * the final user that it is "moved".
	 */
	public void test_onTouchEvent18() throws Exception {
		int deltaX = 8;
		int deltaY = 0;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newMovePosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newMovePosition.x, newMovePosition.y,
				MotionEvent.ACTION_MOVE);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(newMovePosition.x, getLastTouchX());
		assertEquals(newMovePosition.y, getLastTouchY());
		assertEquals(true, isMoved());
	}

	/**
	 * Changing shared element position significantly (top) means for the final
	 * user that it is "moved".
	 */
	public void test_onTouchEvent19() throws Exception {
		int deltaX = 0;
		int deltaY = -8;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newMovePosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newMovePosition.x, newMovePosition.y,
				MotionEvent.ACTION_MOVE);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(newMovePosition.x, getLastTouchX());
		assertEquals(newMovePosition.y, getLastTouchY());
		assertEquals(true, isMoved());
	}

	/**
	 * Changing shared element position significantly (bottom) means for
	 * the final user that it is "moved".
	 */
	public void test_onTouchEvent20() throws Exception {
		int deltaX = 0;
		int deltaY = 8;

		sendStartShowing(true);
		sendMotionEvent(DEFAULT_PORTRAIT_CENTER_POSITION.x,
				DEFAULT_PORTRAIT_CENTER_POSITION.y, MotionEvent.ACTION_DOWN);
		Position newMovePosition =
				new Position(DEFAULT_PORTRAIT_CENTER_POSITION.x + deltaX,
						DEFAULT_PORTRAIT_CENTER_POSITION.y + deltaY);
		sendMotionEvent(newMovePosition.x, newMovePosition.y,
				MotionEvent.ACTION_MOVE);

		WindowManagerMock windowManagerMock = getWindowManagerMock();
		assertEquals(DEFAULT_PORTRAIT_POSITION.x + deltaX,
				windowManagerMock.getViewLocation().x);
		assertEquals(DEFAULT_PORTRAIT_POSITION.y + deltaY,
				windowManagerMock.getViewLocation().y);
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.x, getInitialTouchX());
		assertEquals(DEFAULT_PORTRAIT_CENTER_POSITION.y, getInitialTouchY());
		assertEquals(newMovePosition.x, getLastTouchX());
		assertEquals(newMovePosition.y, getLastTouchY());
		assertEquals(true, isMoved());
	}
}
