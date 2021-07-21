package io.github.devbobos.cameradic.view.camera;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.os.Trace;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Size;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.adapter.CameraHistoryAdapter;
import io.github.devbobos.cameradic.component.BorderedText;
import io.github.devbobos.cameradic.component.MultiBoxTracker;
import io.github.devbobos.cameradic.component.OverlayView;
import io.github.devbobos.cameradic.constant.Classifier;
import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.helper.ImageHelper;
import io.github.devbobos.cameradic.helper.NetworkChecker;
import io.github.devbobos.cameradic.helper.PreferenceHelper;
import io.github.devbobos.cameradic.helper.TextHelper;
import io.github.devbobos.cameradic.model.CameraHistory;
import io.github.devbobos.cameradic.model.History;
import io.github.devbobos.cameradic.component.TFLiteObjectDetectionAPIModel;
import io.github.devbobos.cameradic.model.glosbe.GlosbeResponse;
import io.github.devbobos.cameradic.model.glosbe.Meaning;
import io.github.devbobos.cameradic.model.glosbe.Tuc;
import io.github.devbobos.cameradic.model.naver.Item;
import io.github.devbobos.cameradic.model.naver.NaverDictionaryResponse;
import io.github.devbobos.cameradic.presenter.CameraPresenter;
import io.github.devbobos.cameradic.service.GlosbeService;
import io.github.devbobos.cameradic.service.NaverDictionaryService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by devbobos on 2018. 9. 25..
 */
public class CameraActivity extends AppCompatActivity implements ImageReader.OnImageAvailableListener, Camera.PreviewCallback
{
    @BindView(R.id.camera_progressbar) ProgressBar progressBar;
    @BindView(R.id.camera_relativelayout_progress) RelativeLayout relativeLayoutProgress;
    @BindView(R.id.camera_textview_progress_title) TextView textViewProgressTitle;
    @BindView(R.id.camera_textview_progress_description) TextView textViewProgressDescription;
    @BindView(R.id.camera_relativelayout_information) RelativeLayout relativeLayoutInformation;
    @BindView(R.id.camera_textview_information_title) TextView textViewInformationTitle;
    @BindView(R.id.camera_textview_information_description) TextView textViewInformationDescription;
    @BindView(R.id.camera_recyclerview) RecyclerView recyclerView;
    @BindColor(R.color.colorAvalonWhite) int colorAvalonWhite;
    @BindColor(R.color.colorAvalonYellow) int colorAvalonYellow;
    @BindColor(R.color.colorAvalonDeepYellow) int colorAvalonDeepYellow;
    @BindString(R.string.common_network_unavailable) String stringNetworkUnavailable;
    private Handler progressDescriptionHandler;
    private Timer progressDescriptionTimer;
    private ProgressDescriptionTimerTask progressDescriptionTimerTask;
    private class ProgressDescriptionTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            progressDescriptionHandler.post(new Runnable() {
                @Override
                public void run()
                {
                    progressDescriptionPosition = presenter.getProgressDescriptionPosition(progressDescriptionPosition);
                    Animation bottomToTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_top);
                    textViewProgressDescription.startAnimation(bottomToTop);
                    textViewProgressDescription.setText(presenter.getProgressDescriptionText(progressDescriptionPosition));
                }
            });
        }
    }
    private boolean isSearching;
    private int progressDescriptionPosition;
    private CameraHistoryAdapter cameraHistoryAdapter;
    private CameraPresenter presenter;
    private String recognitionTitleBuffer;
    private int recognitionDictionaryTypeBuffer = -1;
    private static final int PERMISSIONS_REQUEST = 1;

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private boolean debug = false;

    private Handler handler;
    private HandlerThread handlerThread;
    private boolean useCamera2API;
    private boolean isProcessingFrame = false;
    private byte[][] yuvBytes = new byte[3][];
    private int[] rgbBytes = null;
    private int yRowStride;

    protected int previewWidth = 0;
    protected int previewHeight = 0;

    private Runnable postInferenceCallback;
    private Runnable imageConverter;

    // Configuration values for the prepackaged SSD model.
    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco_labels_list.txt";
    private static final boolean TF_OD_API_IS_QUANTIZED = true;

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum DetectorMode {
        TF_OD_API;
    }

    private static final DetectorMode MODE = DetectorMode.TF_OD_API;

    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.6f;

    private static final boolean MAINTAIN_ASPECT = false;

    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);

    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;

    private Integer sensorOrientation;

    private Classifier detector;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;

    private byte[] luminanceCopy;

    private BorderedText borderedText;
    OverlayView trackingOverlay;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Logger.d("onCreate " + this);
        super.onCreate(null);
        presenter = new CameraPresenter(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }

        setContentView(R.layout.camera_activity);
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        if (hasPermission())
        {
            setFragment();
            relativeLayoutProgress.setVisibility(View.VISIBLE);

            if(NetworkChecker.getInstance().isNetworkOnline(getApplicationContext()))
            {
                isSearching = true;
                progressBar.setVisibility(View.VISIBLE);
                relativeLayoutProgress.setVisibility(View.VISIBLE);
                relativeLayoutInformation.setVisibility(View.GONE);
                progressDescriptionHandler = new Handler();
                progressDescriptionTimerTask = new ProgressDescriptionTimerTask();
                progressDescriptionTimer = new Timer();
                progressDescriptionTimer.schedule(progressDescriptionTimerTask, 0, 2000);
                recyclerView.setVisibility(View.GONE);
            }
            else
            {
                progressBar.setVisibility(View.GONE);
                relativeLayoutProgress.setVisibility(View.GONE);
                relativeLayoutInformation.setVisibility(View.VISIBLE);
                textViewInformationDescription.setText(stringNetworkUnavailable);
                recyclerView.setVisibility(View.GONE);
            }
        }
        else
        {
            requestPermission();
        }

    }


    protected int[] getRgbBytes() {
        imageConverter.run();
        return rgbBytes;
    }

    protected int getLuminanceStride() {
        return yRowStride;
    }

    protected byte[] getLuminance() {
        return yuvBytes[0];
    }

    /**
     * Callback for android.hardware.Camera API
     */
    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera) {
        if (isProcessingFrame) {
            Logger.w("Dropping frame!");
            return;
        }

        try {
            // Initialize the storage bitmaps once when the resolution is known.
            if (rgbBytes == null) {
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                previewHeight = previewSize.height;
                previewWidth = previewSize.width;
                rgbBytes = new int[previewWidth * previewHeight];
                onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
            }
        } catch (final Exception e) {
            Logger.e(e, "Exception!");
            return;
        }

        isProcessingFrame = true;
        yuvBytes[0] = bytes;
        yRowStride = previewWidth;

        imageConverter =
                new Runnable() {
                    @Override
                    public void run() {
                        ImageHelper.getInstance().convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
                    }
                };

        postInferenceCallback =
                new Runnable() {
                    @Override
                    public void run() {
                        camera.addCallbackBuffer(bytes);
                        isProcessingFrame = false;
                    }
                };
        processImage();
    }

    /**
     * Callback for Camera2 API
     */
    @Override
    public void onImageAvailable(final ImageReader reader) {
        //We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return;
        }
        if (rgbBytes == null) {
            rgbBytes = new int[previewWidth * previewHeight];
        }
        try {
            final Image image = reader.acquireLatestImage();

            if (image == null) {
                return;
            }

            if (isProcessingFrame) {
                image.close();
                return;
            }
            isProcessingFrame = true;
            Trace.beginSection("imageAvailable");
            final Image.Plane[] planes = image.getPlanes();
            fillBytes(planes, yuvBytes);
            yRowStride = planes[0].getRowStride();
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();

            imageConverter =
                    new Runnable() {
                        @Override
                        public void run() {
                            ImageHelper.getInstance().convertYUV420ToARGB8888(
                                    yuvBytes[0],
                                    yuvBytes[1],
                                    yuvBytes[2],
                                    previewWidth,
                                    previewHeight,
                                    yRowStride,
                                    uvRowStride,
                                    uvPixelStride,
                                    rgbBytes);
                        }
                    };

            postInferenceCallback =
                    new Runnable() {
                        @Override
                        public void run() {
                            image.close();
                            isProcessingFrame = false;
                        }
                    };

            processImage();
        } catch (final Exception e) {
            Logger.e(e, "Exception!");
            Trace.endSection();
            return;
        }
        Trace.endSection();
    }

    @Override
    public synchronized void onStart() {
        Logger.d("onStart " + this);
        super.onStart();
    }

    @Override
    public synchronized void onResume() {
        Logger.d("onResume " + this);
        super.onResume();

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        if(cameraHistoryAdapter!=null)
        {
            cameraHistoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public synchronized void onPause() {
        Logger.d("onPause " + this);

//        if (!isFinishing()) {
//            Logger.d("Requesting finish");
//            finish();
//        }

        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException e) {
            Logger.e(e, "Exception!");
        }

        super.onPause();
    }

    @Override
    public synchronized void onStop() {
        Logger.d("onStop " + this);
        super.onStop();
    }

    @Override
    public synchronized void onDestroy() {
        Logger.d("onDestroy " + this);
        super.onDestroy();
        if(isSearching)
        {
            progressDescriptionTimer.cancel();
            progressDescriptionTimerTask.cancel();
        }
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, final String[] permissions, final int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setFragment();
            } else {
                requestPermission();
            }
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) ||
                    shouldShowRequestPermissionRationale(PERMISSION_STORAGE)) {
                Toast.makeText(CameraActivity.this,
                        "Camera AND storage permission are required for this demo", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[] {PERMISSION_CAMERA, PERMISSION_STORAGE}, PERMISSIONS_REQUEST);
        }
    }

    // Returns true if the device supports the required hardware level, or better.
    private boolean isHardwareLevelSupported(
            CameraCharacteristics characteristics, int requiredLevel) {
        int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            return requiredLevel == deviceLevel;
        }
        // deviceLevel is not LEGACY, can use numerical sort
        return requiredLevel <= deviceLevel;
    }

    private String chooseCamera() {
        final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (final String cameraId : manager.getCameraIdList()) {
                final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                final StreamConfigurationMap map =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                if (map == null) {
                    continue;
                }

                // Fallback to camera1 API for internal cameras that don't have full support.
                // This should help with legacy situations where using the camera2 API causes
                // distorted or otherwise broken previews.
                useCamera2API = (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                        || isHardwareLevelSupported(characteristics,
                        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
                Logger.i("Camera API lv2?: %s", useCamera2API);

                return cameraId;
            }
        } catch (CameraAccessException e) {
            Logger.e(e, "Not allowed to access camera");
        }

        return null;
    }

    protected void setFragment() {
        String cameraId = chooseCamera();

        Fragment fragment;
        if (useCamera2API) {
            CameraConnectionFragment camera2Fragment =
                    CameraConnectionFragment.newInstance(
                            new CameraConnectionFragment.ConnectionCallback() {
                                @Override
                                public void onPreviewSizeChosen(final Size size, final int rotation) {
                                    previewHeight = size.getHeight();
                                    previewWidth = size.getWidth();
                                    CameraActivity.this.onPreviewSizeChosen(size, rotation);
                                }
                            },
                            this,
                            getLayoutId(),
                            getDesiredPreviewFrameSize());

            camera2Fragment.setCamera(cameraId);
            fragment = camera2Fragment;
        } else {
            fragment = new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
//            Toast.makeText(this, "LegacyCameraConnection!", Toast.LENGTH_SHORT).show();
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_framelayout_container, fragment)
                .commit();
    }

    protected void fillBytes(final Image.Plane[] planes, final byte[][] yuvBytes) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (int i = 0; i < planes.length; ++i) {
            final ByteBuffer buffer = planes[i].getBuffer();
            if (yuvBytes[i] == null) {
                Logger.d("Initializing buffer %d at size %d", i, buffer.capacity());
                yuvBytes[i] = new byte[buffer.capacity()];
            }
            buffer.get(yuvBytes[i]);
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public void requestRender() {
        final OverlayView overlay = (OverlayView) findViewById(R.id.camera_overlayview_debug);
        if (overlay != null) {
            overlay.postInvalidate();
        }
    }

    public void addCallback(final OverlayView.DrawCallback callback) {
        final OverlayView overlay = (OverlayView) findViewById(R.id.camera_overlayview_debug);
        if (overlay != null) {
            overlay.addCallback(callback);
        }
    }

    public void onSetDebug(final boolean debug) {}

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            debug = !debug;
//            requestRender();
//            onSetDebug(debug);
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    protected void readyForNextImage() {
        if (postInferenceCallback != null) {
            postInferenceCallback.run();
        }
    }

    protected int getScreenOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(this);

        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            Logger.e("Exception initializing classifier!", e);
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }


        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        Logger.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        Logger.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);

        frameToCropTransform =
                ImageHelper.getInstance().getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = (OverlayView) findViewById(R.id.camera_overlayview_tracking);
        trackingOverlay.addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                        if (isDebug()) {
                            tracker.drawDebug(canvas);
                        }
                    }
                });

        addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        if (!isDebug()) {
                            return;
                        }
                        final Bitmap copy = cropCopyBitmap;
                        if (copy == null) {
                            return;
                        }

                        final int backgroundColor = Color.argb(100, 0, 0, 0);
                        canvas.drawColor(backgroundColor);

                        final Matrix matrix = new Matrix();
                        final float scaleFactor = 2;
                        matrix.postScale(scaleFactor, scaleFactor);
                        matrix.postTranslate(
                                canvas.getWidth() - copy.getWidth() * scaleFactor,
                                canvas.getHeight() - copy.getHeight() * scaleFactor);
                        canvas.drawBitmap(copy, matrix, new Paint());

                        final Vector<String> lines = new Vector<String>();
                        if (detector != null) {
                            final String statString = detector.getStatString();
                            final String[] statLines = statString.split("\n");
                            for (final String line : statLines) {
                                lines.add(line);
                            }
                        }
                        lines.add("");

                        lines.add("Frame: " + previewWidth + "x" + previewHeight);
                        lines.add("Crop: " + copy.getWidth() + "x" + copy.getHeight());
                        lines.add("View: " + canvas.getWidth() + "x" + canvas.getHeight());
                        lines.add("Rotation: " + sensorOrientation);
                        lines.add("Inference time: " + lastProcessingTimeMs + "ms");

                        borderedText.drawLines(canvas, 10, canvas.getHeight() - 10, lines);
                    }
                });
    }
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        byte[] originalLuminance = getLuminance();
        tracker.onFrame(
                previewWidth,
                previewHeight,
                getLuminanceStride(),
                sensorOrientation,
                originalLuminance,
                timestamp);
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
//        Logger.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        if (luminanceCopy == null) {
            luminanceCopy = new byte[originalLuminance.length];
        }
        System.arraycopy(originalLuminance, 0, luminanceCopy, 0, originalLuminance.length);
        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageHelper.getInstance().saveBitmap(croppedBitmap);
        }

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
//                        Logger.i("Running detection on image " + currTimestamp);
                        final long startTime = SystemClock.uptimeMillis();
                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap);
                        final Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2.0f);

                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                        switch (MODE) {
                            case TF_OD_API:
                                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                break;
                        }

                        final List<Classifier.Recognition> mappedRecognitions = new LinkedList<Classifier.Recognition>();

                        for (final Classifier.Recognition result : results) {
                            final RectF location = result.getLocation();
                            if (location != null) {
                                if(NetworkChecker.getInstance().isNetworkOnline(getApplicationContext()))
                                {
                                    if(result.getConfidence() >= ((float) PreferenceHelper.getInstance().getRecognitionMinValue(getApplicationContext()))*0.01)
                                    {
                                        canvas.drawRect(location, paint);
                                        cropToFrameTransform.mapRect(location);
                                        result.setLocation(location);
                                        mappedRecognitions.add(result);
                                        if(result.getTitle()!=null && result.getTitle().trim().length()>0)
                                        {
                                            if(recognitionTitleBuffer == null || recognitionDictionaryTypeBuffer == -1)
                                            {
                                                recognitionTitleBuffer = result.getTitle();
                                                recognitionDictionaryTypeBuffer = PreferenceHelper.getInstance().getDictionaryType(getApplicationContext());
                                                setCameraHistoryList(result.getTitle(), result.getConfidence());
                                            }
                                            else
                                            {
                                                if(!recognitionTitleBuffer.equals(result.getTitle()) ||
                                                        recognitionDictionaryTypeBuffer != PreferenceHelper.getInstance().getDictionaryType(getApplicationContext()))
                                                {
                                                    recognitionTitleBuffer = result.getTitle();
                                                    recognitionDictionaryTypeBuffer = PreferenceHelper.getInstance().getDictionaryType(getApplicationContext());
                                                    setCameraHistoryList(result.getTitle(), result.getConfidence());
                                                }
                                                else
                                                {
                                                    if(cameraHistoryAdapter!=null &&
                                                            PreferenceHelper.getInstance().getShowRecognitionValue(getApplicationContext()) == SystemConstant.SHOW_RECOGNITION_VALUE_ENABLED)
                                                    {
                                                        List<CameraHistory> list = cameraHistoryAdapter.getList();
                                                        for(int i = 0; i < list.size(); i++)
                                                        {
                                                            list.get(i).setRecognitionValue(result.getConfidence()*100);
                                                        }
                                                        cameraHistoryAdapter.setList(list);
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run()
                                                            {
                                                                cameraHistoryAdapter.notifyDataSetChanged();
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run()
                                        {
                                            progressBar.setVisibility(View.GONE);
                                            relativeLayoutProgress.setVisibility(View.GONE);
                                            relativeLayoutInformation.setVisibility(View.VISIBLE);
                                            recyclerView.setVisibility(View.INVISIBLE);
                                            textViewInformationDescription.setText(stringNetworkUnavailable);
                                        }
                                    });
                                }
                            }
                        }

                        tracker.trackResults(mappedRecognitions, luminanceCopy, currTimestamp);
                        trackingOverlay.postInvalidate();

                        requestRender();
                        computingDetection = false;
                    }
                });
    }
    protected int getLayoutId() {
        return R.layout.camera_fragment;
    }
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }
    private void setCameraHistoryList(String title, float recognitionValue)
    {
        String titleKorean = TextHelper.getInstance().getKoreanFromResultTitle(title);
        Call<GlosbeResponse> glosbeResponseCall;
        switch (PreferenceHelper.getInstance().getDictionaryType(this))
        {
            case SystemConstant.LANGUAGE_TYPE_KOREAN:
                Call<NaverDictionaryResponse> naverDictionaryResponseCall = NaverDictionaryService.getInstance().getNaverDictionaryResult(titleKorean);
                naverDictionaryResponseCall.enqueue(new Callback<NaverDictionaryResponse>() {
                    @Override
                    public void onResponse(Call<NaverDictionaryResponse> call, Response<NaverDictionaryResponse> response)
                    {
                        List<CameraHistory> list = new ArrayList<>();
                        if(response.body()!=null)
                        {
                            Logger.d("request naver dictionary success : "+response.body().toString());
                            NaverDictionaryResponse naverDictionaryResponse = response.body();
                            for(Item index : naverDictionaryResponse.getItems())
                            {
                                if(index.getDescription().length()>0)
                                {
                                    list.add(new CameraHistory(
                                            recognitionValue * 100,
                                            TextHelper.getInstance().removeHtmlEntityOnResponse(index.getTitle()),
                                            TextHelper.getInstance().removeHtmlEntityOnResponse(index.getDescription()),
                                            index.getLink(),
                                            index.getThumbnail(),
                                            SystemConstant.LANGUAGE_TYPE_KOREAN));
                                }
                            }
                        }
                        if(list.size() > 0)
                        {
                            setRecognitionResult(list);
                        }
                        else
                        {
                            onFailure(call, new Throwable("korean result is 0"));
                        }
                    }

                    @Override
                    public void onFailure(Call<NaverDictionaryResponse> call, Throwable t)
                    {
                        Logger.d("request naver dictionary failed : "+t.getMessage());
                        Call<GlosbeResponse> retryGlosbeResponseCall =
                                GlosbeService.getInstance().getGlosbeResponse(titleKorean, GlosbeService.getInstance().LANGUAGE_KOREAN, GlosbeService.getInstance().LANGUAGE_KOREAN);
                        retryGlosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                            @Override
                            public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                            {
                                List<CameraHistory> list = new ArrayList<>();
                                GlosbeResponse glosbeResponse = response.body();
                                if(glosbeResponse!=null)
                                {
                                    Logger.d("request glosbe dictionary success : " + response.body().toString()+"\n"+call.request().url());
                                    for(Tuc tuc : glosbeResponse.getTuc())
                                    {
                                        for(Meaning meaning : tuc.getMeanings())
                                        {
                                            if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                            {
                                                StringBuilder titleBuilder = new StringBuilder();
                                                titleBuilder.append(tuc.getPhrase().getText());
                                                titleBuilder.append(" (");
                                                titleBuilder.append(titleKorean);
                                                titleBuilder.append(")");
                                                list.add(new CameraHistory(
                                                        recognitionValue * 100,
                                                        titleBuilder.toString(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_KO_KO+title,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_KOREAN));
                                            }
                                            else
                                            {
                                                list.add(new CameraHistory(
                                                        recognitionValue * 100,
                                                        glosbeResponse.getPhrase(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_KO_KO+title,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_KOREAN));
                                            }
                                        }
                                    }
                                }
                                if(list.size() > 0)
                                {
                                    setRecognitionResult(list);
                                }
                                else
                                {
                                    Call<GlosbeResponse> retryGlosbeResponseCall =
                                            GlosbeService.getInstance().getGlosbeResponse(title, GlosbeService.getInstance().LANGUAGE_ENGLISH, GlosbeService.getInstance().LANGUAGE_KOREAN);
                                    retryGlosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                                        @Override
                                        public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                                        {
                                            List<CameraHistory> list = new ArrayList<>();
                                            GlosbeResponse glosbeResponse = response.body();
                                            if(glosbeResponse!=null)
                                            {
                                                Logger.d("request glosbe dictionary success : " + response.body().toString()+"\n"+call.request().url());
                                                for(Tuc tuc : glosbeResponse.getTuc())
                                                {
                                                    for(Meaning meaning : tuc.getMeanings())
                                                    {
                                                        if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                                        {
                                                            StringBuilder titleBuilder = new StringBuilder();
                                                            titleBuilder.append(tuc.getPhrase().getText());
                                                            titleBuilder.append(" (");
                                                            titleBuilder.append(titleKorean);
                                                            titleBuilder.append(")");
                                                            list.add(new CameraHistory(
                                                                    recognitionValue * 100,
                                                                    titleBuilder.toString(),
                                                                    TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                                    SystemConstant.URL_GLOSBE_EN_KO+title,
                                                                    null,
                                                                    SystemConstant.LANGUAGE_TYPE_KOREAN));
                                                        }
                                                        else
                                                        {
                                                            list.add(new CameraHistory(
                                                                    recognitionValue * 100,
                                                                    glosbeResponse.getPhrase(),
                                                                    TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                                    SystemConstant.URL_GLOSBE_EN_KO+title,
                                                                    null,
                                                                    SystemConstant.LANGUAGE_TYPE_KOREAN));
                                                        }
                                                    }
                                                }
                                            }
                                            setRecognitionResult(list);
                                        }

                                        @Override
                                        public void onFailure(Call<GlosbeResponse> call, Throwable t)
                                        {
                                            Logger.d("request glosbe dictionary failed : "+t.getMessage());
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<GlosbeResponse> call, Throwable t)
                            {
                                Logger.d("request glosbe dictionary failed : "+t.getMessage());
                            }
                        });
                    }
                });
                break;
            case SystemConstant.LANGUAGE_TYPE_ENGLISH:
                glosbeResponseCall =
                        GlosbeService.getInstance().getGlosbeResponse(titleKorean, GlosbeService.getInstance().LANGUAGE_KOREAN, GlosbeService.getInstance().LANGUAGE_ENGLISH);
                glosbeResponseCall.enqueue(new Callback<GlosbeResponse>()
                {
                    @Override
                    public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                    {
                        List<CameraHistory> list = new ArrayList<>();
                        GlosbeResponse glosbeResponse = response.body();
                        if (glosbeResponse != null)
                        {
                            Logger.d("request glosbe dictionary success : " + response.body().toString()+"\n"+call.request().url());
                            for (Tuc tuc : glosbeResponse.getTuc())
                            {
                                for (Meaning meaning : tuc.getMeanings())
                                {
                                    if (tuc.getPhrase() != null && tuc.getPhrase().getText() != null)
                                    {
                                        StringBuilder titleBuilder = new StringBuilder();
                                        titleBuilder.append(tuc.getPhrase().getText());
                                        titleBuilder.append(" (");
                                        titleBuilder.append(titleKorean);
                                        titleBuilder.append(")");
                                        list.add(new CameraHistory(
                                                recognitionValue * 100,
                                                titleBuilder.toString(),
                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                SystemConstant.URL_GLOSBE_KO_EN + title,
                                                null,
                                                SystemConstant.LANGUAGE_TYPE_ENGLISH
                                        ));
                                    } else
                                    {
                                        list.add(new CameraHistory(
                                                recognitionValue * 100,
                                                glosbeResponse.getPhrase(),
                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                SystemConstant.URL_GLOSBE_KO_EN + title,
                                                null,
                                                SystemConstant.LANGUAGE_TYPE_ENGLISH
                                        ));
                                    }
                                }
                            }
                        }
                        if(list.size() > 0)
                        {
                            setRecognitionResult(list);
                        }
                        else
                        {
                            Call<GlosbeResponse> retryGlosbeResponseCall =
                                    GlosbeService.getInstance().getGlosbeResponse(title, GlosbeService.getInstance().LANGUAGE_ENGLISH, GlosbeService.getInstance().LANGUAGE_ENGLISH);
                            retryGlosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                                @Override
                                public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                                {
                                    List<CameraHistory> list = new ArrayList<>();
                                    GlosbeResponse glosbeResponse = response.body();
                                    if (glosbeResponse != null)
                                    {
                                        Logger.d("request glosbe dictionary success : " + response.body().toString()+"\n"+call.request().url());
                                        for (Tuc tuc : glosbeResponse.getTuc())
                                        {
                                            for (Meaning meaning : tuc.getMeanings())
                                            {
                                                if (tuc.getPhrase() != null && tuc.getPhrase().getText() != null)
                                                {
                                                    StringBuilder titleBuilder = new StringBuilder();
                                                    titleBuilder.append(tuc.getPhrase().getText());
                                                    titleBuilder.append(" (");
                                                    titleBuilder.append(titleKorean);
                                                    titleBuilder.append(")");
                                                    list.add(new CameraHistory(
                                                            recognitionValue * 100,
                                                            titleBuilder.toString(),
                                                            TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                            SystemConstant.URL_GLOSBE_EN_EN + title,
                                                            null,
                                                            SystemConstant.LANGUAGE_TYPE_ENGLISH
                                                    ));
                                                } else
                                                {
                                                    list.add(new CameraHistory(
                                                            recognitionValue * 100,
                                                            glosbeResponse.getPhrase(),
                                                            TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                            SystemConstant.URL_GLOSBE_EN_EN + title,
                                                            null,
                                                            SystemConstant.LANGUAGE_TYPE_ENGLISH
                                                    ));
                                                }
                                            }
                                        }
                                    }
                                    setRecognitionResult(list);
                                }

                                @Override
                                public void onFailure(Call<GlosbeResponse> call, Throwable t)
                                {
                                    Logger.d("request glosbe dictionary failed : " + t.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<GlosbeResponse> call, Throwable t)
                    {
                        Logger.d("request glosbe dictionary failed : " + t.getMessage());
                    }
                });
                break;
            case SystemConstant.LANGUAGE_TYPE_JAPANESE:
                glosbeResponseCall =
                        GlosbeService.getInstance().getGlosbeResponse(titleKorean, GlosbeService.getInstance().LANGUAGE_KOREAN, GlosbeService.getInstance().LANGUAGE_JAPANESE);
                glosbeResponseCall.enqueue(new Callback<GlosbeResponse>()
                {
                    @Override
                    public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                    {
                        List<CameraHistory> list = new ArrayList<>();
                        GlosbeResponse glosbeResponse = response.body();
                        if (glosbeResponse != null)
                        {
                            Logger.d("request glosbe dictionary success : " + response.body().toString()+"\n"+call.request().url());
                            for (Tuc tuc : glosbeResponse.getTuc())
                            {
                                for (Meaning meaning : tuc.getMeanings())
                                {
                                    if (tuc.getPhrase() != null && tuc.getPhrase().getText() != null)
                                    {
                                        StringBuilder titleBuilder = new StringBuilder();
                                        titleBuilder.append(tuc.getPhrase().getText());
                                        titleBuilder.append(" (");
                                        titleBuilder.append(titleKorean);
                                        titleBuilder.append(")");
                                        list.add(new CameraHistory(
                                                recognitionValue * 100,
                                                titleBuilder.toString(),
                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                SystemConstant.URL_GLOSBE_KO_JP + title,
                                                null,
                                                SystemConstant.LANGUAGE_TYPE_JAPANESE
                                        ));
                                    } else
                                    {
                                        list.add(new CameraHistory(
                                                recognitionValue * 100,
                                                glosbeResponse.getPhrase(),
                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                SystemConstant.URL_GLOSBE_KO_JP + title,
                                                null,
                                                SystemConstant.LANGUAGE_TYPE_JAPANESE
                                        ));
                                    }
                                }
                            }
                        }
                        if(list.size() > 0)
                        {
                            setRecognitionResult(list);
                        }
                        else
                        {
                            Call<GlosbeResponse> retryGlosbeResponseCall =
                                    GlosbeService.getInstance().getGlosbeResponse(title, GlosbeService.getInstance().LANGUAGE_ENGLISH, GlosbeService.getInstance().LANGUAGE_JAPANESE);
                            retryGlosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                                @Override
                                public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                                {
                                    List<CameraHistory> list = new ArrayList<>();
                                    GlosbeResponse glosbeResponse = response.body();
                                    if (glosbeResponse != null)
                                    {
                                        Logger.d("request glosbe dictionary success : " + response.body().toString()+"\n"+call.request().url());
                                        for (Tuc tuc : glosbeResponse.getTuc())
                                        {
                                            for (Meaning meaning : tuc.getMeanings())
                                            {
                                                if (tuc.getPhrase() != null && tuc.getPhrase().getText() != null)
                                                {
                                                    StringBuilder titleBuilder = new StringBuilder();
                                                    titleBuilder.append(tuc.getPhrase().getText());
                                                    titleBuilder.append(" (");
                                                    titleBuilder.append(titleKorean);
                                                    titleBuilder.append(")");
                                                    list.add(new CameraHistory(
                                                            recognitionValue * 100,
                                                            titleBuilder.toString(),
                                                            TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                            SystemConstant.URL_GLOSBE_EN_JP + title,
                                                            null,
                                                            SystemConstant.LANGUAGE_TYPE_JAPANESE
                                                    ));
                                                } else
                                                {
                                                    list.add(new CameraHistory(
                                                            recognitionValue * 100,
                                                            glosbeResponse.getPhrase(),
                                                            TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                            SystemConstant.URL_GLOSBE_EN_JP + title,
                                                            null,
                                                            SystemConstant.LANGUAGE_TYPE_JAPANESE
                                                    ));
                                                }
                                            }
                                        }
                                    }
                                    setRecognitionResult(list);
                                }

                                @Override
                                public void onFailure(Call<GlosbeResponse> call, Throwable t)
                                {
                                    Logger.d("request glosbe dictionary failed : " + t.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<GlosbeResponse> call, Throwable t)
                    {
                        Logger.d("request glosbe dictionary failed : " + t.getMessage());
                    }
                });
                break;
            case SystemConstant.LANGUAGE_TYPE_CHINESE:
                glosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(titleKorean, GlosbeService.getInstance().LANGUAGE_KOREAN, GlosbeService.getInstance().LANGUAGE_CHINESE);
                glosbeResponseCall.enqueue(new Callback<GlosbeResponse>()
                {
                    @Override
                    public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                    {
                        List<CameraHistory> list = new ArrayList<>();
                        GlosbeResponse glosbeResponse = response.body();
                        if (glosbeResponse != null)
                        {
                            Logger.d("request glosbe dictionary success : " + response.body().toString()+"\n"+call.request().url());
                            for (Tuc tuc : glosbeResponse.getTuc())
                            {
                                for (Meaning meaning : tuc.getMeanings())
                                {
                                    if (tuc.getPhrase() != null && tuc.getPhrase().getText() != null)
                                    {
                                        StringBuilder titleBuilder = new StringBuilder();
                                        titleBuilder.append(tuc.getPhrase().getText());
                                        titleBuilder.append(" (");
                                        titleBuilder.append(titleKorean);
                                        titleBuilder.append(")");
                                        list.add(new CameraHistory(
                                                recognitionValue * 100,
                                                titleBuilder.toString(),
                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                SystemConstant.URL_GLOSBE_KO_CN + title,
                                                null,
                                                SystemConstant.LANGUAGE_TYPE_CHINESE
                                        ));
                                    } else
                                    {
                                        list.add(new CameraHistory(
                                                recognitionValue * 100,
                                                glosbeResponse.getPhrase(),
                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                SystemConstant.URL_GLOSBE_KO_CN + title,
                                                null,
                                                SystemConstant.LANGUAGE_TYPE_CHINESE
                                        ));
                                    }
                                }
                            }
                        }
                        if(list.size() > 0)
                        {
                            setRecognitionResult(list);
                        }
                        else
                        {
                            Call<GlosbeResponse> retryGlosbeResponseCall =
                                    GlosbeService.getInstance().getGlosbeResponse(title, GlosbeService.getInstance().LANGUAGE_ENGLISH, GlosbeService.getInstance().LANGUAGE_CHINESE);
                            retryGlosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                                @Override
                                public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                                {
                                    List<CameraHistory> list = new ArrayList<>();
                                    GlosbeResponse glosbeResponse = response.body();
                                    if (glosbeResponse != null)
                                    {
                                        Logger.d("request glosbe dictionary success : " + response.body().toString()+"\n"+call.request().url());
                                        for (Tuc tuc : glosbeResponse.getTuc())
                                        {
                                            for (Meaning meaning : tuc.getMeanings())
                                            {
                                                if (tuc.getPhrase() != null && tuc.getPhrase().getText() != null)
                                                {
                                                    StringBuilder titleBuilder = new StringBuilder();
                                                    titleBuilder.append(tuc.getPhrase().getText());
                                                    titleBuilder.append(" (");
                                                    titleBuilder.append(titleKorean);
                                                    titleBuilder.append(")");
                                                    list.add(new CameraHistory(
                                                            recognitionValue * 100,
                                                            titleBuilder.toString(),
                                                            TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                            SystemConstant.URL_GLOSBE_EN_CN + title,
                                                            null,
                                                            SystemConstant.LANGUAGE_TYPE_CHINESE
                                                    ));
                                                } else
                                                {
                                                    list.add(new CameraHistory(
                                                            recognitionValue * 100,
                                                            glosbeResponse.getPhrase(),
                                                            TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                            SystemConstant.URL_GLOSBE_EN_CN + title,
                                                            null,
                                                            SystemConstant.LANGUAGE_TYPE_CHINESE
                                                    ));
                                                }
                                            }
                                        }
                                    }
                                    setRecognitionResult(list);
                                }

                                @Override
                                public void onFailure(Call<GlosbeResponse> call, Throwable t)
                                {
                                    Logger.d("request glosbe dictionary failed : " + t.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<GlosbeResponse> call, Throwable t)
                    {
                        Logger.d("request glosbe dictionary failed : " + t.getMessage());
                    }
                });
                break;
            case SystemConstant.LANGUAGE_TYPE_RUSSIAN:
                glosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(titleKorean, GlosbeService.getInstance().LANGUAGE_KOREAN, GlosbeService.getInstance().LANGUAGE_RUSSIAN);
                glosbeResponseCall.enqueue(new Callback<GlosbeResponse>()
                {
                    @Override
                    public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                    {
                        List<CameraHistory> list = new ArrayList<>();
                        GlosbeResponse glosbeResponse = response.body();
                        if (glosbeResponse != null)
                        {
                            Logger.d("request glosbe dictionary success : " + response.body().toString()+"\n"+call.request().url());
                            for (Tuc tuc : glosbeResponse.getTuc())
                            {
                                for (Meaning meaning : tuc.getMeanings())
                                {
                                    if (tuc.getPhrase() != null && tuc.getPhrase().getText() != null)
                                    {
                                        StringBuilder titleBuilder = new StringBuilder();
                                        titleBuilder.append(tuc.getPhrase().getText());
                                        titleBuilder.append(" (");
                                        titleBuilder.append(titleKorean);
                                        titleBuilder.append(")");
                                        list.add(new CameraHistory(
                                                recognitionValue * 100,
                                                titleBuilder.toString(),
                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                SystemConstant.URL_GLOSBE_KO_RN + title,
                                                null,
                                                SystemConstant.LANGUAGE_TYPE_RUSSIAN
                                        ));
                                    } else
                                    {
                                        list.add(new CameraHistory(
                                                recognitionValue * 100,
                                                glosbeResponse.getPhrase(),
                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                SystemConstant.URL_GLOSBE_KO_RN + title,
                                                null,
                                                SystemConstant.LANGUAGE_TYPE_RUSSIAN
                                        ));
                                    }
                                }
                            }
                        }
                        if(list.size() > 0)
                        {
                            setRecognitionResult(list);
                        }
                        else
                        {
                            Call<GlosbeResponse> retryGlosbeResponseCall =
                                    GlosbeService.getInstance().getGlosbeResponse(title, GlosbeService.getInstance().LANGUAGE_ENGLISH, GlosbeService.getInstance().LANGUAGE_RUSSIAN);
                            retryGlosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                                @Override
                                public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                                {
                                    List<CameraHistory> list = new ArrayList<>();
                                    GlosbeResponse glosbeResponse = response.body();
                                    if (glosbeResponse != null)
                                    {
                                        Logger.d("request glosbe dictionary success : " + response.body().toString()+"\n"+call.request().url());
                                        for (Tuc tuc : glosbeResponse.getTuc())
                                        {
                                            for (Meaning meaning : tuc.getMeanings())
                                            {
                                                if (tuc.getPhrase() != null && tuc.getPhrase().getText() != null)
                                                {
                                                    StringBuilder titleBuilder = new StringBuilder();
                                                    titleBuilder.append(tuc.getPhrase().getText());
                                                    titleBuilder.append(" (");
                                                    titleBuilder.append(titleKorean);
                                                    titleBuilder.append(")");
                                                    list.add(new CameraHistory(
                                                            recognitionValue * 100,
                                                            titleBuilder.toString(),
                                                            TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                            SystemConstant.URL_GLOSBE_EN_RN + title,
                                                            null,
                                                            SystemConstant.LANGUAGE_TYPE_RUSSIAN
                                                    ));
                                                } else
                                                {
                                                    list.add(new CameraHistory(
                                                            recognitionValue * 100,
                                                            glosbeResponse.getPhrase(),
                                                            TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                            SystemConstant.URL_GLOSBE_EN_RN + title,
                                                            null,
                                                            SystemConstant.LANGUAGE_TYPE_RUSSIAN
                                                    ));
                                                }
                                            }
                                        }
                                    }
                                    setRecognitionResult(list);
                                }

                                @Override
                                public void onFailure(Call<GlosbeResponse> call, Throwable t)
                                {
                                    Logger.d("request glosbe dictionary failed : " + t.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<GlosbeResponse> call, Throwable t)
                    {
                        Logger.d("request glosbe dictionary failed : " + t.getMessage());
                    }
                });
                break;
        }
    }
    public void setRecognitionResult(List<CameraHistory> list)
    {
        Logger.d("setRecognitionResult "+list.toString());
        isSearching = false;
        progressDescriptionTimerTask.cancel();
        progressDescriptionTimer.cancel();
        progressBar.setVisibility(View.GONE);
        relativeLayoutProgress.setVisibility(View.GONE);
        if(list.size()>0)
        {
            relativeLayoutInformation.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            cameraHistoryAdapter = new CameraHistoryAdapter(this);
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    cameraHistoryAdapter.setList(list);
                    recyclerView.setAdapter(cameraHistoryAdapter);
                }
            });
        }
        else
        {
            relativeLayoutInformation.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            textViewInformationDescription.setText(getResources().getString(R.string.search_information_nothing));
        }
    }
}
