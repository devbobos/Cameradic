package io.github.devbobos.cameradic.view.camera;

/*
 * Copyright 2017 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Fragment;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.component.AutoFitTextureView;
import io.github.devbobos.cameradic.helper.ImageHelper;
import io.github.devbobos.cameradic.view.HistoryActivity;
import io.github.devbobos.cameradic.view.SettingActivity;

public class LegacyCameraConnectionFragment extends Fragment
{
   @BindView(R.id.camera_textureview) AutoFitTextureView textureView;
   @BindView(R.id.camera_imageview_setting) AppCompatImageView imageViewSetting;
   @BindView(R.id.camera_imageview_flash) AppCompatImageView imageViewFlash;
   @BindView(R.id.camera_imageview_history) AppCompatImageView imageViewHistory;
  @BindColor(R.color.colorAvalonWhite) int colorAvalonWhite;
  @BindColor(R.color.colorAvalonYellow) int colorAvalonYellow;
  @BindColor(R.color.colorAvalonDeepYellow) int colorAvalonDeepYellow;
  private boolean isFlashSupported;
  private boolean isFlashOn;
  private Camera camera;
  private Camera.PreviewCallback imageListener;
  private Size desiredSize;

  /**
   * The layout identifier to inflate for this Fragment.
   */
  private int layout;

  public LegacyCameraConnectionFragment(
          final Camera.PreviewCallback imageListener, final int layout, final Size desiredSize) {
    this.imageListener = imageListener;
    this.layout = layout;
    this.desiredSize = desiredSize;
  }

  /**
   * Conversion from screen rotation to JPEG orientation.
   */
  private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

  static {
    ORIENTATIONS.append(Surface.ROTATION_0, 90);
    ORIENTATIONS.append(Surface.ROTATION_90, 0);
    ORIENTATIONS.append(Surface.ROTATION_180, 270);
    ORIENTATIONS.append(Surface.ROTATION_270, 180);
  }

  /**
   * {@link android.view.TextureView.SurfaceTextureListener} handles several lifecycle events on a
   * {@link TextureView}.
   */
  private final TextureView.SurfaceTextureListener surfaceTextureListenerWithFlashOn =
      new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(
                final SurfaceTexture texture, final int width, final int height) {

          int index = getCameraId();
          camera = Camera.open(index);

          try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes != null
                && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
              parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            List<Camera.Size> cameraSizes = parameters.getSupportedPreviewSizes();
            Size[] sizes = new Size[cameraSizes.size()];
            int i = 0;
            for (Camera.Size size : cameraSizes) {
              sizes[i++] = new Size(size.width, size.height);
            }
            Size previewSize =
                CameraConnectionFragment.chooseOptimalSize(
                    sizes, desiredSize.getWidth(), desiredSize.getHeight());
            parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
            camera.setDisplayOrientation(90);
            camera.setParameters(parameters);
            camera.setPreviewTexture(texture);
          } catch (IOException exception) {
            camera.release();
          }

          camera.setPreviewCallbackWithBuffer(imageListener);
          Camera.Size s = camera.getParameters().getPreviewSize();
          camera.addCallbackBuffer(new byte[ImageHelper.getInstance().getYUVByteSize(s.height, s.width)]);

          textureView.setAspectRatio(s.height, s.width);

          camera.startPreview();
        }

        @Override
        public void onSurfaceTextureSizeChanged(
                final SurfaceTexture texture, final int width, final int height) {}

        @Override
        public boolean onSurfaceTextureDestroyed(final SurfaceTexture texture) {
          return true;
        }

        @Override
        public void onSurfaceTextureUpdated(final SurfaceTexture texture) {}
      };
  private final TextureView.SurfaceTextureListener surfaceTextureListenerWithFlashOff =
          new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(
                    final SurfaceTexture texture, final int width, final int height) {

              int index = getCameraId();
              camera = Camera.open(index);

              try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes != null
                        && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                  parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                List<Camera.Size> cameraSizes = parameters.getSupportedPreviewSizes();
                Size[] sizes = new Size[cameraSizes.size()];
                int i = 0;
                for (Camera.Size size : cameraSizes) {
                  sizes[i++] = new Size(size.width, size.height);
                }
                Size previewSize =
                        CameraConnectionFragment.chooseOptimalSize(
                                sizes, desiredSize.getWidth(), desiredSize.getHeight());
                parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
                camera.setDisplayOrientation(90);
                camera.setParameters(parameters);
                camera.setPreviewTexture(texture);
              } catch (IOException exception) {
                camera.release();
              }

              camera.setPreviewCallbackWithBuffer(imageListener);
              Camera.Size s = camera.getParameters().getPreviewSize();
              camera.addCallbackBuffer(new byte[ImageHelper.getInstance().getYUVByteSize(s.height, s.width)]);

              textureView.setAspectRatio(s.height, s.width);

              camera.startPreview();
            }

            @Override
            public void onSurfaceTextureSizeChanged(
                    final SurfaceTexture texture, final int width, final int height) {}

            @Override
            public boolean onSurfaceTextureDestroyed(final SurfaceTexture texture) {
              return true;
            }

            @Override
            public void onSurfaceTextureUpdated(final SurfaceTexture texture) {}
          };

  /**
   * An {@link AutoFitTextureView} for camera preview.
   */

  /**
   * An additional thread for running tasks that shouldn't block the UI.
   */
  private HandlerThread backgroundThread;

  @Override
  public View onCreateView(
          final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    View view = inflater.inflate(layout, container, false);
    ButterKnife.bind(this, view);

    return view;
  }

  @Override
  public void onViewCreated(final View view, final Bundle savedInstanceState) {
    textureView = (AutoFitTextureView) view.findViewById(R.id.camera_textureview);
  }

  @Override
  public void onActivityCreated(final Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onResume() {
    super.onResume();
    startBackgroundThread();
    // When the screen is turned off and turned back on, the SurfaceTexture is already
    // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
    // a camera and start preview from here (otherwise, we wait until the surface is ready in
    // the SurfaceTextureListener).

    if (textureView.isAvailable()) {
      camera.startPreview();
    } else {
      textureView.setSurfaceTextureListener(surfaceTextureListenerWithFlashOff);
    }
    isFlashOn = false;
    imageViewFlash.setImageResource(R.drawable.ic_flash_off_black_24dp);
    imageViewFlash.setColorFilter(colorAvalonWhite);
  }

  @Override
  public void onPause() {
    stopCamera();
    stopBackgroundThread();
    super.onPause();
  }

  /**
   * Starts a background thread and its {@link Handler}.
   */
  private void startBackgroundThread() {
    backgroundThread = new HandlerThread("CameraBackground");
    backgroundThread.start();
  }

  /**
   * Stops the background thread and its {@link Handler}.
   */
  private void stopBackgroundThread() {
    backgroundThread.quitSafely();
    try {
      backgroundThread.join();
      backgroundThread = null;
    } catch (final InterruptedException e) {
      Logger.e(e, "Exception!");
    }
  }

  protected void stopCamera() {
    if (camera != null) {
      camera.stopPreview();
      camera.setPreviewCallback(null);
      camera.release();
      camera = null;
    }
  }

  private int getCameraId() {
    CameraInfo ci = new CameraInfo();
    for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
      Camera.getCameraInfo(i, ci);
      if (ci.facing == CameraInfo.CAMERA_FACING_BACK)
        return i;
    }
    return -1; // No camera found
  }
  @OnClick({R.id.camera_imageview_setting, R.id.camera_imageview_history}) void onMenuIconClicked(View view)
  {
    Intent intent;
    switch (view.getId())
    {
      case R.id.camera_imageview_setting:
        intent = new Intent(getActivity(), SettingActivity.class);
        getActivity().startActivity(intent);
        break;
      case R.id.camera_imageview_history:
        intent = new Intent(getActivity(), HistoryActivity.class);
        getActivity().startActivity(intent);
        break;
    }
  }
  @RequiresApi(api = Build.VERSION_CODES.M)
  @OnClick(R.id.camera_imageview_flash) void onFlashClicked()
  {
    if(isFlashSupported)
    {
      if(isFlashOn)
      {
        isFlashOn = false;
        imageViewFlash.setImageResource(R.drawable.ic_flash_off_black_24dp);
        imageViewFlash.setColorFilter(colorAvalonWhite);
        setFlashOff();
      }
      else
      {
        isFlashOn = true;
        imageViewFlash.setImageResource(R.drawable.ic_flash_on_black_24dp);
        imageViewFlash.setColorFilter(colorAvalonDeepYellow);
        setFlashOn();
      }
    }
  }
  @OnTouch({R.id.camera_imageview_setting, R.id.camera_imageview_history, R.id.camera_imageview_flash})
  boolean onTouched(View view, MotionEvent event)
  {
    switch (event.getAction())
    {
      case MotionEvent.ACTION_DOWN:
        switch (view.getId())
        {
          case R.id.camera_imageview_setting:
            imageViewSetting.setColorFilter(colorAvalonDeepYellow);
            break;
          case R.id.camera_imageview_history:
            imageViewHistory.setColorFilter(colorAvalonDeepYellow);
            break;
          case R.id.camera_imageview_flash:
            imageViewFlash.setColorFilter(colorAvalonDeepYellow);
            break;
        }
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        switch (view.getId())
        {
          case R.id.camera_imageview_setting:
            imageViewSetting.setColorFilter(colorAvalonWhite);
            break;
          case R.id.camera_imageview_history:
            imageViewHistory.setColorFilter(colorAvalonWhite);
            break;
          case R.id.camera_imageview_flash:
            if(isFlashOn)
            {
              imageViewFlash.setColorFilter(colorAvalonWhite);
            }
            else
            {
              imageViewFlash.setColorFilter(colorAvalonDeepYellow);
            }
            break;
        }
        break;
    }
    return false;
  }

  private void setFlashOff() {
    textureView.setSurfaceTextureListener(surfaceTextureListenerWithFlashOff);
  }
  private void setFlashOn() {
    textureView.setSurfaceTextureListener(surfaceTextureListenerWithFlashOn);
  }
}
