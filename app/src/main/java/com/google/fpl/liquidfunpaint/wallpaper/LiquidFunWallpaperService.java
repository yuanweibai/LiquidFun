package com.google.fpl.liquidfunpaint.wallpaper;

import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.google.fpl.liquidfun.R;
import com.google.fpl.liquidfunpaint.Controller;
import com.google.fpl.liquidfunpaint.Renderer;
import com.google.fpl.liquidfunpaint.tool.Tool;

public class LiquidFunWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new LiquidFunEngine();
    }

    private class LiquidFunEngine extends WallpaperService.Engine {

        private LiquidFunGlSurfaceView mGlSurfaceView;
        private Controller mController;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            initView();
        }

        private void initView() {
            mGlSurfaceView = new LiquidFunGlSurfaceView(LiquidFunWallpaperService.this);
            mGlSurfaceView.setEGLContextClientVersion(2);
            mGlSurfaceView.setPreserveEGLContextOnPause(true);

            Renderer renderer = Renderer.getInstance();
            Renderer.getInstance().init(LiquidFunWallpaperService.this);
            mController = new Controller(LiquidFunWallpaperService.this);
            mGlSurfaceView.setRenderer(renderer);
            renderer.startSimulation();

            Tool.getTool(Tool.ToolType.WATER).setColor(getColor(getString(R.string.default_water_color), "color"));
            mController.setTool(Tool.ToolType.WATER);

            mController.init();
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

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                mController.onResume();
                mGlSurfaceView.onResume();
                Renderer.getInstance().totalFrames = -10000;
            } else {
                mController.onPause();
                mGlSurfaceView.onPause();
            }
        }

        @Override public void onDestroy() {
            super.onDestroy();
            mController.onPause();
            mGlSurfaceView.onPause();
        }

        private class LiquidFunGlSurfaceView extends GLSurfaceView {

            public LiquidFunGlSurfaceView(LiquidFunWallpaperService context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return LiquidFunEngine.this.getSurfaceHolder();
            }
        }
    }
}
