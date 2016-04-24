package edu.rit.se.beepbrake.Analysis;

import android.content.Context;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.JavaCameraView;

/**
 * Created by richykapadia on 9/20/15.
 */
public class LoaderCallback extends BaseLoaderCallback {

    private JavaCameraView view;

    public LoaderCallback(Context AppContext, JavaCameraView view) {
        super(AppContext);
        this.view = view;
    }

    @Override
    public void onManagerConnected(int status) {
        super.onManagerConnected(status);
        switch (status) {
            case LoaderCallback.SUCCESS: {
                Log.d("LoaderCallback", "Sucessfully Connected!");
                view.enableView();
                break;
            }
            default: {
                super.onManagerConnected(status);
                break;
            }
        }

    }

    @Override
    public void onPackageInstall(int operation, InstallCallbackInterface callback) {
        super.onPackageInstall(operation, callback);
    }
}