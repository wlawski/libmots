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
import java.util.ArrayList;

import net.wiktorlawski.messageonthescreen.SharedElementService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.ServiceTestCase;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class SharedElementServiceTest
		extends ServiceTestCase<SharedElementService> {
	public static final String COMMAND_KEY = "COMMAND";
	public static final String NEW_MESSAGE_KEY = "NEW_MESSAGE";
	public static final String START_SHOWING_KEY = "START_SHOWING";

	private static final String DEBUG_MESSAGES_FIELD_NAME = "debugMessages";
	private static final String SHARED_ELEMENT_FIELD_NAME = "sharedElement";
	private static final String SHARED_ELEMENT_PARAMETERS_FIELD_NAME =
			"sharedElementParameters";
	private static final String SHOWING_FIELD_NAME = "showing";
	private static final String VISIBLE_FIELD_NAME = "visible";
	private static final String WINDOW_MANAGER_FIELD_NAME = "windowManager";

	private static final int SHARED_ELEMENT_HEIGHT = 100;
	private static final int SHARED_ELEMENT_WIDTH = 100;
	private static final int SHARED_ELEMENT_PADDING_X = 25;
	private static final int SHARED_ELEMENT_PADDING_Y = 25;

	public static final int COMMAND_SET_TEXT = 1;
	public static final int COMMAND_SHOW_SHARED_ELEMENT = 2;

	public SharedElementServiceTest() {
		super(SharedElementService.class);
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
		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);
		assertNotNull(debugMessages);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		/* sharedElement */
		Field sharedElementField =
				service.getClass().getDeclaredField(SHARED_ELEMENT_FIELD_NAME);
		sharedElementField.setAccessible(true);
		ImageView sharedElement = (ImageView) sharedElementField.get(service);
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
		assertEquals(Gravity.RIGHT | Gravity.TOP,
				sharedElementParameters.gravity);
		assertEquals(SHARED_ELEMENT_PADDING_X, sharedElementParameters.x);
		assertEquals(SHARED_ELEMENT_PADDING_Y, sharedElementParameters.y);

		/* showing */
		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		/* visible */
		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(false, visibleField.getBoolean(service));

		/* windowManager */
		Field WindowManagerField =
				service.getClass().getDeclaredField(WINDOW_MANAGER_FIELD_NAME);
		WindowManagerField.setAccessible(true);
		WindowManager windowManager =
				(WindowManager) WindowManagerField.get(service);
		assertNotNull(windowManager);
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(false, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(false, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(false, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(false, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(false, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(false, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(true, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(true, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(false, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(false, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(false, visibleField.getBoolean(service));
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
		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		if (debugMessages.isEmpty() == false) {
			fail("List of debug messages is not empty");
		}

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(false, visibleField.getBoolean(service));
	}

	/**
	 * When service receives SET_TEXT command with any new non-null text value,
	 * debugMessages should contain only this new text value and shared element
	 * should be visible.
	 */
	public void test_onStartCommand_SET_TEXT4() throws Exception {
		String expected = "text";

		sendSetText(expected);

		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
	}

	/**
	 * Service should accept empty strings as new texts provided for SET_TEXT
	 * command and shared element should be visible.
	 */
	public void test_onStartCommand_SET_TEXT5() throws Exception {
		String expected = new String();

		sendSetText(expected);

		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
	}

	/**
	 * Service should accept strings that contains multiple lines as new texts
	 * provided for SET_TEXT command. Shared element should be visible.
	 */
	public void test_onStartCommand_SET_TEXT6() throws Exception {
		String expected = "text\ntext";

		sendSetText(expected);

		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
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

		SharedElementService service = getService();

		Field debugMessagesField =
				service.getClass().getDeclaredField(DEBUG_MESSAGES_FIELD_NAME);
		debugMessagesField.setAccessible(true);

		@SuppressWarnings("unchecked")
		ArrayList<String> debugMessages =
				(ArrayList<String>) debugMessagesField.get(service);

		assertEquals(1, debugMessages.size());
		assertEquals(expected, debugMessages.get(0));

		Field showingField =
				service.getClass().getDeclaredField(SHOWING_FIELD_NAME);
		showingField.setAccessible(true);
		assertEquals(false, showingField.getBoolean(service));

		Field visibleField =
				service.getClass().getDeclaredField(VISIBLE_FIELD_NAME);
		visibleField.setAccessible(true);
		assertEquals(true, visibleField.getBoolean(service));
	}
}
