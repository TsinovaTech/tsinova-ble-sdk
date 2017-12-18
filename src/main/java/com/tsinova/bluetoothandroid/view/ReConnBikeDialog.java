package com.tsinova.bluetoothandroid.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.tsinova.bluetoothandroid.R;
import com.tsinova.bluetoothandroid.pojo.SingletonBTInfo;


/**
 * 
 */
public class ReConnBikeDialog extends Dialog {

	public ReConnBikeDialog(Context context, int theme) {
		super(context, theme);
	}

	public ReConnBikeDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;
		private View contentView;

		private OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
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
		 * Set the positive button text and it"s listener
		 *
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(OnClickListener listener) {
			this.positiveButtonClickListener = listener;
			return this;
		}


		/**
		 * Set the negative button text and it"s listener
		 *
		 * @param listener
		 * @return
		 */
		public Builder setCloseButton(OnClickListener listener) {
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public ReConnBikeDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final ReConnBikeDialog dialog = new ReConnBikeDialog(context,R.style.CustomDialog);
			View layout = inflater.inflate(R.layout.custom_dialog2, null);


			View ll_hotline = layout.findViewById(R.id.ll_hotline);

			if (SingletonBTInfo.INSTANCE.isHasHotline()){
				ll_hotline.setVisibility(View.VISIBLE);
			}else {
				ll_hotline.setVisibility(View.INVISIBLE);
			}

			View btn_1 = layout.findViewById(R.id.btn_1);
			View ll_close = layout.findViewById(R.id.ll_close);

			layout.findViewById(R.id.tv_tel).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + "4008190660"));
					context.startActivity(intent);
				}
			});


			if (positiveButtonClickListener!=null){
				btn_1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						positiveButtonClickListener.onClick(dialog,DialogInterface.BUTTON_POSITIVE);
					}
				});
			}

			if (negativeButtonClickListener!=null){
				ll_close.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						negativeButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEGATIVE);
					}
				});
			}


			dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			dialog.setContentView(layout);
			return dialog;
		}

	}


}