package com.test.videocolorfilter;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.widget.SeekBar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mVideoView = null;

    private ColorFilterMatrixUtil mColorFilterMatrixUtil = new ColorFilterMatrixUtil();
    private RectShape mReactShape = null;
    private MediaPlayer mMediaPlayer = null;
    private Surface mSurface = null;
    private SurfaceTexture mSurfaceTexture = null;

    private boolean mFrameAvailable = false;

    private float mHueValue = 0;
    private float mSaturationValue = 1;
    private float mLightnessValue = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVideoView = findViewById(R.id.video_view);
        initData();
        setVideoView();
    }

    private void initData() {
        //设置版本2
        mVideoView.setEGLContextClientVersion(2);
        //设置渲染器
        mVideoView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                mReactShape = new RectShape();
                int textureId = GLUtil.generateOESTexture();
                mSurfaceTexture = new SurfaceTexture(textureId);
                mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        mFrameAvailable = true;
                    }
                });
                mSurface = new Surface(mSurfaceTexture);
                mReactShape.setTextureId(textureId);
                setupPlayer();
            }

            @Override
            public void onSurfaceChanged(GL10 gl10, int i, int i1) {

            }

            @Override
            public void onDrawFrame(GL10 gl10) {
                if (mFrameAvailable) {
                    mSurfaceTexture.updateTexImage();
                    mFrameAvailable = false;
                }

                float[] colorFilter = mColorFilterMatrixUtil.getColorFilterArray16();
                mReactShape.setColorFilterArray(colorFilter);
                mReactShape.draw();
            }
        });
    }

    private void setupPlayer() {
        try {
            mMediaPlayer = MediaPlayer.create(this, R.raw.testfile);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVideoView() {
        mHueValue = 90;        //-180~180
        mSaturationValue = 1;    //-1~1
        mLightnessValue = 1;     //-1~1
        mColorFilterMatrixUtil.setHue(mHueValue);
        mColorFilterMatrixUtil.setSaturation(mSaturationValue);
        mColorFilterMatrixUtil.setLightness(mLightnessValue);
    }

}
