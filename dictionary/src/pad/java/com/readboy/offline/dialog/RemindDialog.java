/**
 * 寮瑰嚭寮忓璇濇
 */

package com.readboy.offline.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;

import com.readboy.Dictionary.R;

public class RemindDialog {
	private static final String TAG = "RemindDialog";
	
	private static final int DIALOG_DISMISS = 1;
	private static final int DIALOG_DISMISS_TIMER = 5000;
	private Dialog		mDialog;
	private TextView	mDialogTitle;
	private TextView	mDialogMessage;
	private ImageButton		mDialogConfirm, mDialogCancel;
	private boolean	mAutoDismiss = true;
	private Context mContext;
	
	public RemindDialog(Context context)
    {
		this(context, "", "", R.style.dialog);
	}

	public RemindDialog(Context context, String title, String message, int style) {
		mContext = context;
		mDialog = new Dialog(mContext, style);
		View v = View.inflate(context, R.layout.remind_dialog, null);
		mDialogTitle = (TextView) v.findViewById(R.id.remind_title_id);
		mDialogMessage = (TextView) v.findViewById(R.id.remind_message_id);
		mDialogConfirm = (ImageButton) v.findViewById(R.id.remind_confirm_id);
		mDialogConfirm.setOnClickListener(mConfirmOnClickListener);
		mDialogCancel =  (ImageButton) v.findViewById(R.id.remind_cancel_id);
		mDialogCancel.setOnClickListener(mCancelOnClickListener);
		LayoutParams layoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mDialog.setContentView(v, layoutParam);
		
//		mDialogTitle.setText(title);
		mDialogMessage.setText(message);
		
		this.setCanceledOnTouchOutside(false);
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == DIALOG_DISMISS) {
                if(mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
			}
		}
	};
	
	private OnClickListener mConfirmOnClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
            if(mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
		}
	};
	
	private OnClickListener mCancelOnClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
            if(mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
		}
	};
	
	public void setConfirmOnClickListener(OnClickListener l) {
		if(l == null) {
			mDialogConfirm.setOnClickListener(mConfirmOnClickListener);
		} else {
			mDialogConfirm.setOnClickListener(l);
		}
	}
	
	public void setCancelOnClickListener(OnClickListener l) {
		mDialogCancel.setOnClickListener(l);
	}
	
	public void setDismissListener(OnDismissListener listener) {
		mDialog.setOnDismissListener(listener);
	}
	
	public void setCanceledOnTouchOutside(boolean cancel) {
		mDialog.setCanceledOnTouchOutside(cancel);
	}
	
	public void setAutoDismiss(boolean enable) {
		mAutoDismiss = enable;
	}
	
	public void setCancelEnable(boolean enable) {
		if(enable) {
			mDialogCancel.setVisibility(View.VISIBLE);
		} else {
			mDialogCancel.setVisibility(View.GONE);
		}
	}
	
	public void remindShow() {
		mDialog.show();
		mHandler.removeMessages(DIALOG_DISMISS);
		if(mAutoDismiss) {
			mHandler.sendEmptyMessageDelayed(DIALOG_DISMISS, DIALOG_DISMISS_TIMER);
		}
	}

	public void remindShow(int idTitle, int idMessage) {
		String title = mContext.getResources().getString(idTitle);
		String message = mContext.getResources().getString(idMessage);
		setRemind(title, message);
		mDialog.show();
		mHandler.removeMessages(DIALOG_DISMISS);
		if(mAutoDismiss) {
			mHandler.sendEmptyMessageDelayed(DIALOG_DISMISS, DIALOG_DISMISS_TIMER);
		}
	}
	
	public void remindShow(String title, String message) {
		setRemind(title, message);
		mDialog.show();
		mHandler.removeMessages(DIALOG_DISMISS);
		if(mAutoDismiss) {
			mHandler.sendEmptyMessageDelayed(DIALOG_DISMISS, DIALOG_DISMISS_TIMER);
		}
	}
	
	public void remindDismiss() {
		mHandler.removeMessages(DIALOG_DISMISS);
        if(mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
	}

    public void stopAutoDimiss() {
        mHandler.removeMessages(DIALOG_DISMISS);
    }

    public void restartAutoDimiss() {
        if (mDialog != null && mDialog.isShowing() && mHandler != null) {
            mHandler.sendEmptyMessage(DIALOG_DISMISS);
        }
    }

	public boolean remindIsShowing() {
		return mDialog.isShowing();
	}
	
	public void setRemindKeyEvent(OnKeyListener onKeyListener) {
		mDialog.setOnKeyListener(onKeyListener);
	}
	
	public void setRemind(String title, String message) {
//		mDialogTitle.setText(title);
		mDialogMessage.setText(message);
	}
	
	public String getRemindMessage() {
		return mDialogMessage.getText().toString();
	}

    public void onDistroyed() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

    }
}