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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

/**
 * This class prepares dialog that presents gathered debug messages.
 */
/* package */ class DebugWindow {
	SharedElementService service;

	/* package */ DebugWindow(Context context,
			ArrayList<String> debugMessages, SharedElementService service) {
		this.service = service;

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(context, R.layout.debug_message);

		for (String s : debugMessages) {
			adapter.add(s);
		}

		builder.setAdapter(adapter, new DebugMessageClickListener());
		builder.setNegativeButton(R.string.cancel,
				new DebugWindowCancelListener());
		builder.setNeutralButton(R.string.clear,
				new DebugWindowClearListener());

		AlertDialog dialog = builder.create();
		dialog.getWindow()
			.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

		dialog.show();
	}

	private class DebugWindowCancelListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}	

	private class DebugWindowClearListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			service.clearDebugMessages();
		}
	}

	private class DebugMessageClickListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			/* Nothing */
		}		
	}
}
