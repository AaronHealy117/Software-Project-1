package x14532757.softwareproject.Password;

import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 08/11/2017.
 *
 * Code Copied from:
 * Title: android-FingerprintDialog
 * Author: googleSamples
 * Date: 12/02/17
 * Availability: https://github.com/googlesamples/android-FingerprintDialog
 *
 */

public class FingerprintUIHelper extends FingerprintManager.AuthenticationCallback {


        private static final long ERROR_TIMEOUT_MILLIS = 1600;
        private static final long SUCCESS_DELAY_MILLIS = 1300;

        private final FingerprintManager mFingerprintManager;
        private final ImageView mIcon;
        private final TextView mErrorTextView;
        private final Callback mCallback;
        private CancellationSignal mCancellationSignal;

        private boolean mSelfCancelled;


        FingerprintUIHelper(FingerprintManager fingerprintManager,
                            ImageView icon, TextView errorTextView, Callback callback) {
        mFingerprintManager = fingerprintManager;
        mIcon = icon;
        mErrorTextView = errorTextView;
        mCallback = callback;
        }

        boolean isFingerprintAuthAvailable() {
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        return mFingerprintManager.isHardwareDetected()
        && mFingerprintManager.hasEnrolledFingerprints();
        }

        void startListening(FingerprintManager.CryptoObject cryptoObject) {
        if (!isFingerprintAuthAvailable()) {
        return;
        }
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        mFingerprintManager
        .authenticate(cryptoObject, mCancellationSignal, 0 /* flags */, this, null);
        mIcon.setImageResource(R.drawable.ic_fp_40px);
        }

        void stopListening() {
        if (mCancellationSignal != null) {
        mSelfCancelled = true;
        mCancellationSignal.cancel();
        mCancellationSignal = null;
        }
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
                if (!mSelfCancelled) {
                showError(errString);
                mIcon.postDelayed(new Runnable() {
        @Override
        public void run() {
                mCallback.onError();
                }
                }, ERROR_TIMEOUT_MILLIS);
                }
                }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                showError(helpString);
                }

        @Override
        public void onAuthenticationFailed() {
                showError(mIcon.getResources().getString(
                R.string.fingerprint_not_recognized));
                }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
                mIcon.setImageResource(R.drawable.ic_fp_40px);
                mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.darkred, null));
                mErrorTextView.setText(
                mErrorTextView.getResources().getString(R.string.fingerprint_success));
                mIcon.postDelayed(new Runnable() {
        @Override
        public void run() {
                mCallback.onAuthenticated();
                }
                }, SUCCESS_DELAY_MILLIS);
                }

        private void showError(CharSequence error) {
                mIcon.setImageResource(R.drawable.ic_fp_40px);
                mErrorTextView.setText(error);
                mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.buttonColor, null));
                mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
                mErrorTextView.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
                }

        private Runnable mResetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
                mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.buttonColor, null));
                mErrorTextView.setText(
                mErrorTextView.getResources().getString(R.string.fingerprint_hint));
                mIcon.setImageResource(R.drawable.ic_fp_40px);
                }
                };

        public interface Callback {

            void onAuthenticated();

            void onError();
        }
}
