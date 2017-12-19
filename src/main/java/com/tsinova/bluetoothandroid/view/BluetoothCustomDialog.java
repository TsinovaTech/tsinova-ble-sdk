package com.tsinova.bluetoothandroid.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.tsinova.bluetoothandroid.R;

/**
 * 
 */
public class BluetoothCustomDialog extends Dialog {

	public BluetoothCustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public BluetoothCustomDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;
		private String title;
		private String title_2;
		private String message;
		private int positiveButtonBak;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;

		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Set the Dialog message from String
		 *
		 * @param title
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 *
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 *
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}
		public Builder setTitle_2(int title) {
			this.title_2 = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 *
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}
		public Builder setTitle_2(String title) {
			this.title_2 = title;
			return this;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 *
		 * @param v
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it"s listener
		 *
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and it"s listener
		 *
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}
		/**
		 * Set the positive button text and it"s listener
		 *
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int backgroundResource, String positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonBak = backgroundResource;
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it"s listener
		 *
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText,DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it"s listener
		 *
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public BluetoothCustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final BluetoothCustomDialog dialog = new BluetoothCustomDialog(context,R.style.SDK_CustomDialog);
			View layout = inflater.inflate(R.layout.custom_dialog, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			TextView title2 = ((TextView) layout.findViewById(R.id.title_2));
			if (TextUtils.isEmpty(title_2)) {
				title2.setVisibility(View.GONE);
			} else {
				title2.setText(title_2);
				title2.setVisibility(View.VISIBLE);
			}

			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.btn_positive)).setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.btn_positive)).setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.btn_positive).setVisibility(View.GONE);
			}
			
			if(positiveButtonBak != 0){
				((Button) layout.findViewById(R.id.btn_positive)).setBackgroundResource(positiveButtonBak);
			}
			
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.btn_negative)).setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.btn_negative)).setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.btn_negative).setVisibility(View.GONE);
			}
			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} 
//			else if (contentView != null) {
//				// if no message set
//				// add the contentView to the dialog body
//				((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
//				((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
//			}
			dialog.setContentView(layout);
			return dialog;
		}

	}

}