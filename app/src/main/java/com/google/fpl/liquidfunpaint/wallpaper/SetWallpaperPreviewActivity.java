package com.google.fpl.liquidfunpaint.wallpaper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
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

        Renderer renderer = Renderer.getInstance();
        Renderer.getInstance().init(this);
        mController = new Controller(this);
        mGlSurfaceView.setRenderer(renderer);
        renderer.startSimulation();

        Tool.getTool(Tool.ToolType.WATER).setColor(getColor(getString(R.string.default_water_color), "color"));
        mController.setTool(Tool.ToolType.WATER);

        findViewById(R.id.set_wallpaper_btn).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startSetLiveWallpaper();
            }
        });
    }

    private void startSetLiveWallpaper() {
        Intent intent = new Intent();
        intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(getApplicationContext(), LiquidFunWallpaperService.class));
        startActivityForResult(intent, 1);
    }

    private int getColor(String name, String defType) {
        Resources r = getResources();
        int id = r.getIdentifier(name, defType, getPackageName());
        int color = r.getColor(id);
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
        Renderer.getInstance().totalFrames = -10000;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mController.onPause();
        mGlSurfaceView.onPause();
    }
}
