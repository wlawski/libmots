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
import android.view.WindowManager;
import android.widget.ImageView;

public class SharedElementServiceTest
		extends ServiceTestCase<SharedElementService> {
	public static final String COMMAND_KEY = "COMMAND";
	public static final String NEW_MESSAGE_KEY = "NEW_MESSAGE";

	private static final String DEBUG_MESSAGES_FIELD_NAME = "debugMessages";
	private static final String SHARED_ELEMENT_FIELD_NAME = "sharedElement";
	private static final String WINDOW_MANAGER_FIELD_NAME = "windowManager";

	public static final int COMMAND_SET_TEXT = 1;

	public SharedElementServiceTest() {
		super(SharedElementService.class);
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
	 * list should be empty).
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
	}

	/**
	 * Service should ignore intent with empty extras and keep already gathered
	 * debugMessages list.
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
	}

	/**
	 * Service should ignore intent without "COMMAND" key (by default
	 * debugMessages list should be empty).
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
	}

	/**
	 * Service should ignore intent without "COMMAND" key and keep already
	 * gathered debugMessages list.
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
	}

	/**
	 * Service should ignore intent with value '0' for "COMMAND" key (by
	 * default debugMessages list should be empty).
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
	}

	/**
	 * Service should ignore intent with value '0' for "COMMAND" key and keep
	 * already gathered debugMessages list.
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
	}

	/**
	 * Service should ignore intent with negative integer value for "COMMAND"
	 * key (by default debugMessages list should be empty).
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
	}

	/**
	 * Service should ignore intent with negative integer value for "COMMAND"
	 * key and keep already gathered debugMessages list.
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
	}

	/**
	 * Service should ignore intent with unknown positive integer value for
	 * "COMMAND" key (by default debugMessages list should be empty).
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
	}

	/**
	 * Service should ignore intent with unknown positive integer value for
	 * "COMMAND" key and keep already gathered debugMessages list.
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
	}

	/**
	 * Service should prepare empty debugMessages list for SET_TEXT command when
	 * intent does not deliver any text value and service has not existed
	 * before.
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
	}

	/**
	 * Service should clear debugMessages list for SET_TEXT command when intent
	 * does not deliver new text value and service already exists.
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
	}

	/**
	 * Service should clear debugMessages list for SET_TEXT command when intent
	 * delivers null as new text value.
	 */
	public void test_onStartCommand_SET_TEXT3() throws Exception {
		String expected = "text";

		sendSetText(expected);

		Intent intent = new Intent();
		intent.setClass(getContext(), SharedElementService.class);
		intent.putExtra(COMMAND_KEY, COMMAND_SET_TEXT);
		intent.putExtra(SharedElementService.NEW_MESSAGE, (String) null);
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
	}

	/**
	 * When service receives SET_TEXT command with any new non-null text value,
	 * debugMessages should contain only this new text value.
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
	}

	/**
	 * Service should accept empty strings as new texts provided for SET_TEXT
	 * command.
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
	}

	/**
	 * Service should accept strings that contains multiple lines as new texts
	 * provided for SET_TEXT command.
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
	}

	/**
	 * Service should accept very long strings as new texts provided for
	 * SET_TEXT command.
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
	}
}
