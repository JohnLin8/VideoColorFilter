package com.test.videocolorfilter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.test.videocolorfilter.camera.CameraHelper;
import com.test.videocolorfilter.camera.CameraListener;

public class MainActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "MainActivity----";
//    private GLSurfaceView mVideoView = null;
    private TextureView previewView = null;

    private ColorFilterMatrixUtil mColorFilterMatrixUtil = new ColorFilterMatrixUtil();
    private RectShape mReactShape = null;
    private MediaPlayer mMediaPlayer = null;
    private Surface mSurface = null;
    private SurfaceTexture mSurfaceTexture = null;

    private boolean mFrameAvailable = false;

    private float mHueValue = 0;
    private float mSaturationValue = 1;
    private float mLightnessValue = 1;
    private Camera mCamera;
    private CameraHelper cameraHelper;
    private Camera.Size previewSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mVideoView = findViewById(R.id.video_view);
        previewView = findViewById(R.id.textureView);
//        previewView.bringToFront();

        //在布局结束后才做初始化操作
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
//        init();
    }

//    private void initData() {
//        //设置版本2
//        mVideoView.setEGLContextClientVersion(2);
//        //设置渲染器
//        mVideoView.setRenderer(new GLSurfaceView.Renderer() {
//            @Override
//            public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
//                mReactShape = new RectShape();
//                int textureId = GLUtil.generateOESTexture();
//                mSurfaceTexture = new SurfaceTexture(textureId);
//                mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
//                    @Override
//                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//                        mFrameAvailable = true;
//                    }
//                });
//                mSurface = new Surface(mSurfaceTexture);
//                mReactShape.setTextureId(textureId);
////                initCamera();
////                setupPlayer();
//            }
//
//            @Override
//            public void onSurfaceChanged(GL10 gl10, int i, int i1) {
//
//            }
//
//            @Override
//            public void onDrawFrame(GL10 gl10) {
//                if (mFrameAvailable) {
//                    mSurfaceTexture.updateTexImage();
//                    mFrameAvailable = false;
//                }
//
//                float[] colorFilter = mColorFilterMatrixUtil.getColorFilterArray16();
//                mReactShape.setColorFilterArray(colorFilter);
//                mReactShape.draw();
//            }
//        });
//    }
//
//    private void setupPlayer() {
//        try {
//            mMediaPlayer = MediaPlayer.create(this, R.raw.testfile);
//            mMediaPlayer.setSurface(mSurface);
//            mMediaPlayer.setLooping(true);
//            mMediaPlayer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setVideoView() {
//        mHueValue = -15;        //-180~180
//        mSaturationValue = 1;    //-1~1
//        mLightnessValue = 1;     //-1~1
//        mColorFilterMatrixUtil.setHue(mHueValue);
//        mColorFilterMatrixUtil.setSaturation(mSaturationValue);
//        mColorFilterMatrixUtil.setLightness(mLightnessValue);
//    }

    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Camera.Size lastPreviewSize = previewSize;
                previewSize = camera.getParameters().getPreviewSize();

            }

            @Override
            public void onPreview(final byte[] nv21, Camera camera) {

            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {

                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };
        //TODO cameraHelper
        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(), previewView.getMeasuredHeight()))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .additionalRotation(90)
                .specificCameraId(1)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
        cameraHelper.start();
    }

    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA
//            ,Manifest.permission.READ_PHONE_STATE
    };

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;


    public void init() {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initCamera();
//            initData();
//            setVideoView();
        }
    }

    /**
     * 权限检查
     *
     * @param neededPermissions 需要的权限
     * @return 是否全部被允许
     */
    protected boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;
        for (int grantResult : grantResults) {
            isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
        }
        afterRequestPermission(requestCode, isAllGranted);
    }

    void afterRequestPermission(int requestCode, boolean isAllGranted) {
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            if (isAllGranted) {
                initCamera();
//                initData();
//                setVideoView();
            } else {
                Toast.makeText(this,"no permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onGlobalLayout() {
        previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        init();
    }
}
