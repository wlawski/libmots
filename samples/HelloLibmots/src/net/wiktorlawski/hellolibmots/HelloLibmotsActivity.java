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

package net.wiktorlawski.hellolibmots;

import net.wiktorlawski.messageonthescreen.MessageOnTheScreen;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * This class presents basic use of libmots library. Each available
 * functionality for this activity represents one of this library options.
 */
public class HelloLibmotsActivity extends Activity {
	private MessageOnTheScreen mots;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hello_libmots_layout);
	}

	public void getInstance(View view) {
		boolean startShowing =
			((CheckBox) findViewById(R.id.start_showing))
				.isChecked();
		mots = MessageOnTheScreen.getInstance(this, startShowing);

		toggleMotsControls(true);
	}

	public void addMessage(View view) {
		mots.addMessage(this,
			((EditText) findViewById(R.id.new_message)).getText()
				.toString());
	}

	public void setText(View view) {
		mots.setText(this,
			((EditText) findViewById(R.id.new_message)).getText()
				.toString());
	}

	private void toggleMotsControls(boolean enable) {
		((Button) findViewById(R.id.add_message)).setEnabled(enable);
		((Button) findViewById(R.id.set_text)).setEnabled(enable);
	}
}
