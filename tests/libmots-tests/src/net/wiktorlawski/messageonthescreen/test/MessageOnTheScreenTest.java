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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import net.wiktorlawski.messageonthescreen.MessageOnTheScreen;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.InstrumentationTestCase;

public class MessageOnTheScreenTest extends InstrumentationTestCase {
	private static final String MOTS_INSTANCE_FIELD_NAME = "sInstance";

	private Activity activity;
	private Instrumentation instrumentation;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		instrumentation = getInstrumentation();
		Intent intent =
			new Intent(getInstrumentation().getContext(),
				DummyActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity = instrumentation.startActivitySync(intent);
	}

	@Override
	protected void tearDown() {
		activity.finish();
	}

	/**
	 * By default no MessageOnTheScreen should exist.
	 */
	public void test_defaultNoInstance() throws Exception {
		Field mots =
			MessageOnTheScreen.class
			.getDeclaredField(MOTS_INSTANCE_FIELD_NAME);
		mots.setAccessible(true);
		MessageOnTheScreen actual = (MessageOnTheScreen) mots.get(null);

		MessageOnTheScreen expected = null;

		assertEquals(expected, actual);
	}

	/**
	 * If MessageOnTheScreen object already exists, then
	 * MessageOnTheScreen.getInstance() method should return reference to
	 * this instance.
	 */
	public void test_getInstance() throws Exception {
		Field mots =
			MessageOnTheScreen.class
			.getDeclaredField(MOTS_INSTANCE_FIELD_NAME);
		mots.setAccessible(true);
		Constructor<MessageOnTheScreen> motsConstructor =
			MessageOnTheScreen.class.getDeclaredConstructor(Activity.class,
					boolean.class);
		motsConstructor.setAccessible(true);
		MessageOnTheScreen expected = motsConstructor.newInstance(activity,
				false);
		mots.set(null, expected);

		MessageOnTheScreen actual = MessageOnTheScreen.getInstance(activity,
				false);

		assertEquals(expected, actual);
	}

	/**
	 * Multiple MessageOnTheScreen.getInstance() method calls should
	 * return reference to the same object.
	 */
	public void test_getInstance2() {
		MessageOnTheScreen expected = MessageOnTheScreen.getInstance(activity,
				false);
		MessageOnTheScreen actual = MessageOnTheScreen.getInstance(activity,
				false);

		assertEquals(expected, actual);
	}

	/**
	 * Multiple MessageOnTheScreen.getInstance() method calls should
	 * return reference to the same object even when showing parameter is
	 * different.
	 */
	public void test_getInstance3() {
		MessageOnTheScreen expected = MessageOnTheScreen.getInstance(activity,
				false);
		MessageOnTheScreen actual = MessageOnTheScreen.getInstance(activity,
				true);

		assertEquals(expected, actual);
	}

	/**
	 * If MessageOnTheScreen object already exists, then
	 * MessageOnTheScreen.getInstance() method should return reference to
	 * this instance even when requested visibility is different.
	 */
	public void test_getInstance4() throws Exception {
		Field mots =
			MessageOnTheScreen.class
			.getDeclaredField(MOTS_INSTANCE_FIELD_NAME);
		mots.setAccessible(true);
		Constructor<MessageOnTheScreen> motsConstructor =
			MessageOnTheScreen.class.getDeclaredConstructor(Activity.class,
					boolean.class);
		motsConstructor.setAccessible(true);
		MessageOnTheScreen expected = motsConstructor.newInstance(activity,
				false);
		mots.set(null, expected);

		MessageOnTheScreen actual = MessageOnTheScreen.getInstance(activity,
				true);

		assertEquals(expected, actual);
	}

	/**
	 * Multiple MessageOnTheScreen.getInstance() method calls should
	 * return reference to the same object even for requested visibility set to
	 * true.
	 */
	public void test_getInstance5() {
		MessageOnTheScreen expected = MessageOnTheScreen.getInstance(activity,
				true);
		MessageOnTheScreen actual = MessageOnTheScreen.getInstance(activity,
				true);

		assertEquals(expected, actual);
	}

	/**
	 * Setting null as the new text should not result in any type of exception.
	 */
	public void test_setText() {
		MessageOnTheScreen mots = MessageOnTheScreen.getInstance(activity,
				false);
		mots.setText(activity, null);
	}

	/**
	 * Setting new text as empty String should not result in any type of
	 * exception.
	 */
	public void test_setText2() {
		MessageOnTheScreen mots = MessageOnTheScreen.getInstance(activity,
				false);
		mots.setText(activity, new String());
	}

	/**
	 * Setting new text as single line text should not result in any type of
	 * exception.
	 */
	public void test_setText3() {
		MessageOnTheScreen mots = MessageOnTheScreen.getInstance(activity,
				false);
		mots.setText(activity, "text");
	}

	/**
	 * Setting new text as multiple line text should not result in any type of
	 * exception.
	 */
	public void test_setText4() {
		MessageOnTheScreen mots = MessageOnTheScreen.getInstance(activity,
				false);
		mots.setText(activity, "text\ntext");
	}

	/**
	 * Setting very long new text should not result in any type of exception.
	 */
	public void test_setText5() {
		MessageOnTheScreen mots = MessageOnTheScreen.getInstance(activity,
				false);
		String text = "text";
		StringBuilder newText = new StringBuilder(10000 * text.length());

		for (int i = 0; i < 10000; i++) {
			newText.append("text");
		}

		mots.setText(activity, newText.toString());
	}

	/**
	 * Setting null as the new text should not result in any type of exception,
	 * even when requested visibility should be set to true.
	 */
	public void test_setText6() {
		MessageOnTheScreen mots = MessageOnTheScreen.getInstance(activity,
				true);
		mots.setText(activity, null);
	}

	/**
	 * Setting new text as empty String should not result in any type of
	 * exception, even when requested visibility should be set to true.
	 */
	public void test_setText7() {
		MessageOnTheScreen mots = MessageOnTheScreen.getInstance(activity,
				true);
		mots.setText(activity, new String());
	}

	/**
	 * Setting new text as single line text should not result in any type of
	 * exception, even when requested visibility should be set to true.
	 */
	public void test_setText8() {
		MessageOnTheScreen mots = MessageOnTheScreen.getInstance(activity,
				true);
		mots.setText(activity, "text");
	}

	/**
	 * Setting new text as multiple line text should not result in any type of
	 * exception, even when requested visibility should be set to true.
	 */
	public void test_setText9() {
		MessageOnTheScreen mots = MessageOnTheScreen.getInstance(activity,
				true);
		mots.setText(activity, "text\ntext");
	}

	/**
	 * Setting very long new text should not result in any type of exception,
	 * even when requested visibility should be set to true.
	 */
	public void test_setText10() {
		MessageOnTheScreen mots = MessageOnTheScreen.getInstance(activity,
				true);
		String text = "text";
		StringBuilder newText = new StringBuilder(10000 * text.length());

		for (int i = 0; i < 10000; i++) {
			newText.append("text");
		}

		mots.setText(activity, newText.toString());
	}
}
