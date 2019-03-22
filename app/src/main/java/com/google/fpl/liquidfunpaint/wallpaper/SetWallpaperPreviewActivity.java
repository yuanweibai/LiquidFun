package com.google.fpl.liquidfunpaint.wallpaper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.google.fpl.liquidfun.R;
import com.google.fpl.liquidfunpaint.Controller;
import com.google.fpl.liquidfunpaint.Renderer;
import com.google.fpl.liquidfunpaint.tool.Tool;

public class SetWallpaperPreviewActivity extends Activity implements View.OnTouchListener {

    private GLSurfaceView mGlSurfaceView;
    private Controller mController;
    private Renderer mRenderer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaper_preview_layout);

        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mGlSurfaceView = findViewById(R.id.gl_surface_view);
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setOnTouchListener(this);
        mGlSurfaceView.setPreserveEGLContextOnPause(true);

        mRenderer = new Renderer();
        mRenderer.init(this);
        mController = new Controller(this, mRenderer);
        mGlSurfaceView.setRenderer(mRenderer);
        mRenderer.startSimulation();

        Tool.getTool(Tool.ToolType.WATER).setColor(getToolColor(R.color.red));
        mController.setTool(Tool.ToolType.WATER, mRenderer);

        findViewById(R.id.set_wallpaper_btn).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startSetLiveWallpaper();
            }
        });
        mController.init();
    }

    private void startSetLiveWallpaper() {
        Intent intent = new Intent();
        intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(getApplicationContext(), LiquidFunWallpaperService.class));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            finish();
        }
    }

    private int getToolColor(@ColorRes int colorId) {
        int color = getResources().getColor(colorId);
        // ARGB to ABGR
        int red = (color >> 16) & 0xFF;
        int blue = (color << 16) & 0xFF0000;
        return (color & 0xFF00FF00) | red | blue;
    }

    /**
     * OnTouch event handler.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mController.onTouch(v, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mController.onResume();
        mGlSurfaceView.onResume();
        mRenderer.totalFrames = -10000;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mController.onPause();
        mGlSurfaceView.onPause();
    }
}
