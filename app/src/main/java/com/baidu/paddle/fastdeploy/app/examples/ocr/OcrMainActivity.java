package com.baidu.paddle.fastdeploy.app.examples.ocr;

import static com.baidu.paddle.fastdeploy.app.ui.Utils.decodeBitmap; import static com.baidu.paddle.fastdeploy.app.ui.Utils.getRealPathFromURI;

import android.Manifest; import android.annotation.SuppressLint; import android.app.Activity; import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface; import android.content.Intent; import android.content.SharedPreferences; import android.content.pm.PackageManager; import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle; import android.os.SystemClock; import android.preference.PreferenceManager; import android.support.annotation.NonNull; import android.support.v4.app.ActivityCompat; import android.support.v4.content.ContextCompat; import android.view.View; import android.view.ViewGroup; import android.view.Window; import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton; import android.widget.ImageView; import android.widget.TextView;

import com.baidu.paddle.fastdeploy.RuntimeOption; import com.baidu.paddle.fastdeploy.app.examples.R; import com.baidu.paddle.fastdeploy.app.ui.view.CameraSurfaceView; import com.baidu.paddle.fastdeploy.app.ui.view.ResultListView; import com.baidu.paddle.fastdeploy.app.ui.Utils; import com.baidu.paddle.fastdeploy.app.ui.view.adapter.BaseResultAdapter;
import com.baidu.paddle.fastdeploy.app.ui.view.database.DatabaseHelper;
import com.baidu.paddle.fastdeploy.app.ui.view.database.DatabaseResultActivity;
import com.baidu.paddle.fastdeploy.app.ui.view.model.BaseResultModel; import com.baidu.paddle.fastdeploy.pipeline.PPOCRv3; import com.baidu.paddle.fastdeploy.vision.OCRResult; import com.baidu.paddle.fastdeploy.vision.Visualize; import com.baidu.paddle.fastdeploy.vision.ocr.Classifier; import com.baidu.paddle.fastdeploy.vision.ocr.DBDetector; import com.baidu.paddle.fastdeploy.vision.ocr.Recognizer;

import java.util.ArrayList; import java.util.List;

// Camera Imports
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.os.Environment;
import android.widget.Toast;

// Database Helper imports
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

public class OcrMainActivity extends Activity implements View.OnClickListener, CameraSurfaceView.OnTextureChangedListener { private static final String TAG = OcrMainActivity.class.getSimpleName();

    CameraSurfaceView svPreview;
    TextView tvStatus;
    ImageButton btnTorch;
    ImageButton btnShutter;
    ImageButton btnSettings;
    ImageView realtimeToggleButton;
    boolean isRealtimeStatusRunning = false;
    ImageView backInPreview;
    private ImageView albumSelectButton;
    private View cameraPageView;
    private ViewGroup resultPageView;
    private ImageView resultImage;
    private ImageView backInResult;
    private ResultListView resultView;
    private Bitmap picBitmap;
    private Bitmap shutterBitmap;
    private Bitmap originPicBitmap;
    private Bitmap originShutterBitmap;
    private boolean isShutterBitmapCopied = false;
    private FrameLayout openDatabase;

    private View captureArea;

    //  Flashlight variables
    private CameraManager cameraManager;
    private String cameraId;
    private ImageView flashlightToggle;
    private boolean isFlashlightOn;


    private final List<BaseResultModel> results = new ArrayList<>();
    private BaseResultAdapter adapter;
    private static final float CONFIDENCE_THRESHOLD = 0.1f; // 40% confidence threshold


    public static final int TYPE_UNKNOWN = -1;
    public static final int BTN_SHUTTER = 0;
    public static final int ALBUM_SELECT = 1;
    public static final int REALTIME_DETECT = 2;
    private static int TYPE = REALTIME_DETECT;

    private static final int REQUEST_PERMISSION_CODE_STORAGE = 101;
    private static final int INTENT_CODE_PICK_IMAGE = 100;
    private static final int TIME_SLEEP_INTERVAL = 50; // ms

    long timeElapsed = 0;
    long frameCounter = 0;

    // Call 'init' and 'release' manually later
    PPOCRv3 predictor = new PPOCRv3();

    private String[] texts;
    private float[] recScores;
    private boolean initialized;

    // Record the start time
    long startTime;

    private boolean isTorchOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.ocr_activity_main);

        // Clear all setting items to avoid app crashing due to the incorrect settings
        initSettings();

        // Check and request CAMERA and WRITE_EXTERNAL_STORAGE permissions
        if (!checkAllPermissions()) {
            requestAllPermissions();
        }

        // Init the camera preview and UI components
        initView();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_torch:
                toggleFlashlight();
                break;
            case R.id.btn_shutter:
                TYPE = BTN_SHUTTER;
                shutterAndPauseCamera();
                results.clear();
                adapter.notifyDataSetChanged();
                break;
            case R.id.open_database:
                openOCRDatabase();
                break;
            case R.id.btn_settings:
                startActivity(new Intent(OcrMainActivity.this, OcrSettingsActivity.class));
                break;
            case R.id.realtime_toggle_btn:
                toggleRealtimeStyle();
                break;
            case R.id.back_in_preview:
                finish();
                break;
            case R.id.iv_select:
                TYPE = ALBUM_SELECT;
                // Judge whether authority has been granted.
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // If this permission was requested before the application but the user refused the request, this method will return true.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, INTENT_CODE_PICK_IMAGE);
                }
                resultView.setAdapter(null);
                break;
            case R.id.back_in_result:
                back();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        resultPageView.setVisibility(View.GONE);
        cameraPageView.setVisibility(View.VISIBLE);
        TYPE = REALTIME_DETECT;
        isShutterBitmapCopied = false;
        svPreview.onResume();
        results.clear();
        if (texts != null) {
            texts = null;
        }
        if (recScores != null) {
            recScores = null;
        }
    }

    //  Flashlight Functionality
    private void toggleFlashlight() {
        if (isFlashlightOn) {
            turnOffFlashlight();
        } else {
            turnOnFlashlight();
        }
    }

    private void turnOnFlashlight(){
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
            }
            isFlashlightOn = true;
            flashlightToggle.setImageResource(R.drawable.torch_on);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    };

    private void turnOffFlashlight(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
            }
            isFlashlightOn = false;
            flashlightToggle.setImageResource(R.drawable.torch_off);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    };

    //  Handles image Capture
    private void shutterAndPauseCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Sleep some times to ensure picture has been correctly shut.
                    Thread.sleep(TIME_SLEEP_INTERVAL * 10); // 500ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    public void run() {
                        // These code will run in main thread.
                        svPreview.onPause();
                        cameraPageView.setVisibility(View.GONE);
                        resultPageView.setVisibility(View.VISIBLE);
                        if (shutterBitmap != null && !shutterBitmap.isRecycled()) {
                            // Record the start time
                            startTime = System.currentTimeMillis();
                            resultImage.setImageBitmap(shutterBitmap);
                            cropAndProcessImage();

                        } else {
                            new AlertDialog.Builder(OcrMainActivity.this)
                                    .setTitle("Empty Result!")
                                    .setMessage("Current picture is empty, please try shutting it again!")
                                    .setCancelable(true)
                                    .show();
                        }
                    }
                });

            }
        }).start();
    }

    private void cropAndProcessImage() {

        // Get the coordinates of the capture area relative to the camera preview
        int[] previewLocation = new int[2];
        svPreview.getLocationInWindow(previewLocation);

        int[] captureLocation = new int[2];
        captureArea.getLocationInWindow(captureLocation);

        int left = captureLocation[0] - previewLocation[0];
        int top = captureLocation[1] - previewLocation[1];
        int width = captureArea.getWidth();
        int height = captureArea.getHeight();

        // Adjust coordinates based on the bitmap's dimensions vs the preview's dimensions
        float scaleX = (float) shutterBitmap.getWidth() / svPreview.getWidth();
        float scaleY = (float) shutterBitmap.getHeight() / svPreview.getHeight();

        left = (int) (left * scaleX);
        top = (int) (top * scaleY);
        width = (int) (width * scaleX);
        height = (int) (height * scaleY);

        // Ensure we're not exceeding the bitmap's boundaries
        left = Math.max(0, left);
        top = Math.max(0, top);
        width = Math.min(width, shutterBitmap.getWidth() - left);
        height = Math.min(height, shutterBitmap.getHeight() - top);

        // Crop the bitmap
        Bitmap croppedBitmap = Bitmap.createBitmap(shutterBitmap, left, top, width, height);

        // Process the cropped image
        processImage(croppedBitmap);

        // Clean up
        shutterBitmap.recycle();
        shutterBitmap = null;
    }

    private void copyBitmapFromCamera(Bitmap ARGB8888ImageBitmap) {
        if (isShutterBitmapCopied || ARGB8888ImageBitmap == null) {
            return;
        }
        if (!ARGB8888ImageBitmap.isRecycled()) {
            synchronized (this) {
                shutterBitmap = ARGB8888ImageBitmap.copy(Bitmap.Config.ARGB_8888, true);
                originShutterBitmap = ARGB8888ImageBitmap.copy(Bitmap.Config.ARGB_8888, true);
            }
            SystemClock.sleep(TIME_SLEEP_INTERVAL);
            isShutterBitmapCopied = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                cameraPageView.setVisibility(View.GONE);
                resultPageView.setVisibility(View.VISIBLE);
                Uri uri = data.getData();
                String path = getRealPathFromURI(this, uri);
                picBitmap = decodeBitmap(path, 720, 1280);
                originPicBitmap = picBitmap.copy(Bitmap.Config.ARGB_8888, true);
                resultImage.setImageBitmap(picBitmap);
                processImage(picBitmap);
            }
        }
    }

    private void toggleRealtimeStyle() {
        if (isRealtimeStatusRunning) {
            isRealtimeStatusRunning = false;
            realtimeToggleButton.setImageResource(R.drawable.realtime_stop_btn);
            svPreview.setOnTextureChangedListener(this);
            tvStatus.setVisibility(View.VISIBLE);
        }else {
            isRealtimeStatusRunning = true;
            realtimeToggleButton.setImageResource(R.drawable.realtime_start_btn);
            tvStatus.setVisibility(View.GONE);
            isShutterBitmapCopied = false;
            svPreview.setOnTextureChangedListener(new CameraSurfaceView.OnTextureChangedListener() {
                @Override
                public boolean onTextureChanged(Bitmap ARGB8888ImageBitmap) {
                    if (TYPE == BTN_SHUTTER) {
                        copyBitmapFromCamera(ARGB8888ImageBitmap);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onTextureChanged(Bitmap ARGB8888ImageBitmap) {
        if (TYPE == BTN_SHUTTER) {
            copyBitmapFromCamera(ARGB8888ImageBitmap);
            return false;
        }

        boolean modified = false;

        long tc = System.currentTimeMillis();
        OCRResult result = predictor.predict(ARGB8888ImageBitmap);
        timeElapsed += (System.currentTimeMillis() - tc);

        Visualize.visOcr(ARGB8888ImageBitmap, result);
        modified = result.initialized();

        frameCounter++;
        if (frameCounter >= 30) {
            final int fps = (int) (1000 / (timeElapsed / 30));
            runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                public void run() {
                    tvStatus.setText(fps + "fps");
                }
            });
            frameCounter = 0;
            timeElapsed = 0;
        }
        return modified;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload settings and re-initialize the predictor
        checkAndUpdateSettings();
        // Open camera until the permissions have been granted
        if (!checkAllPermissions()) {
            svPreview.disableCamera();
        } else {
            svPreview.enableCamera();
        }
        svPreview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        svPreview.onPause();
    }

    @Override
    protected void onDestroy() {
        if (predictor != null) {
            predictor.release();
        }
        super.onDestroy();
    }

    public void initView() {
        TYPE = REALTIME_DETECT;
        svPreview = findViewById(R.id.sv_preview);
        svPreview.setOnTextureChangedListener(this);
        tvStatus = findViewById(R.id.tv_status);
        btnTorch = findViewById(R.id.btn_torch);
        btnTorch.setOnClickListener(this);
        btnShutter = findViewById(R.id.btn_shutter);
        btnShutter.setOnClickListener(this);
        btnSettings = findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(this);
        realtimeToggleButton = findViewById(R.id.realtime_toggle_btn);
        realtimeToggleButton.setOnClickListener(this);
        realtimeToggleButton.setImageResource(R.drawable.realtime_stop);
        isRealtimeStatusRunning = false;
        backInPreview = findViewById(R.id.back_in_preview);
        backInPreview.setOnClickListener(this);
        albumSelectButton = findViewById(R.id.iv_select);
        albumSelectButton.setOnClickListener(this);
        cameraPageView = findViewById(R.id.camera_page);
        resultPageView = findViewById(R.id.result_page);
        resultImage = findViewById(R.id.result_image);
        backInResult = findViewById(R.id.back_in_result);
        backInResult.setOnClickListener(this);
        resultView = findViewById(R.id.result_list_view);
        openDatabase = findViewById(R.id.open_database);
        openDatabase.setOnClickListener(this);

        svPreview = findViewById(R.id.sv_preview);
        captureArea = findViewById(R.id.capture_area);

        // Initialize the adapter
        adapter = new BaseResultAdapter(getBaseContext(), R.layout.ocr_result_page_item, results);
        resultView.setAdapter(adapter);

    }

    // Main Processing Function
    private void processImage(Bitmap bitmap) {

        // Apply a short delay to ensure the image is fully processed
        SystemClock.sleep(TIME_SLEEP_INTERVAL * 10);

        // Perform OCR on the bitmap
        OCRResult result = predictor.predict(bitmap, true);

        texts = result.mText;
        recScores = result.mRecScores;

        adapter.notifyDataSetChanged();
        resultView.invalidate();

        // Update the result image
        resultImage.setImageBitmap(bitmap);

        // Copy the processed bitmap to the original bitmap
        if (TYPE == ALBUM_SELECT) {
            picBitmap = originPicBitmap.copy(Bitmap.Config.ARGB_8888, true);
        } else {
            shutterBitmap = originShutterBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }

        // Record the end time and calculate the elapsed time
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        // Display the elapsed time
        TextView elapsedTimeTextView = findViewById(R.id.elapsed_time);
        elapsedTimeTextView.setText("Time: " + elapsedTime + " ms");

        // Create a directory for this OCR result
        File resultDir = createOCRResultDirectory();

        // Save the cropped image
        String imagePath = saveCroppedImage(bitmap, resultDir);

        results.clear();
        initialized = result.initialized();
        if (initialized) {
            for (int i = 0; i < texts.length; i++) {
                if (recScores[i] > CONFIDENCE_THRESHOLD) {
                    String filteredText = filterText(texts[i]);

                    // A temporal class
                    BaseResultModel baseResult = new BaseResultModel(i + 1, texts[i], filteredText, recScores[i], elapsedTime, imagePath);
                    results.add(baseResult);
                }
            }
        }

        // Save the OCR results and processing time
        saveOCRResults(results, elapsedTime, resultDir, imagePath);

        // Optionally, you can show a toast message to inform the user
        Toast.makeText(this, "OCR results saved in " + resultDir.getName(), Toast.LENGTH_LONG).show();
    }

    // Create Directory
    private File createOCRResultDirectory() {

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File resultDir = new File(storageDir, "OCR_Results");

        // Create directory if it doesn't exist
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }
        return resultDir;
    }

    // Save Results
    private void saveOCRResults(List<BaseResultModel> results, long elapsedTime, File resultDir, String imagePath) {
        // Initialize SQLite database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (BaseResultModel result : results) {
            
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_TEXT, result.getName());
            values.put(DatabaseHelper.COLUMN_FILTERED_TEXT, result.getFilteredText());
            values.put(DatabaseHelper.COLUMN_CONFIDENCE, result.getConfidence());
            values.put(DatabaseHelper.COLUMN_IMAGE_PATH, imagePath);  // Store the image path
            values.put(DatabaseHelper.COLUMN_PROCESSING_TIME, elapsedTime);

            // Insert into the database
            db.insert(DatabaseHelper.TABLE_OCR_RESULTS, null, values);
        }

        db.close();
    }

    //  Save Cropped Bitmap
    private String saveCroppedImage(Bitmap croppedBitmap, File resultDir) {
        // Generate a unique image file name using the timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "cropped_image_" + timeStamp + ".jpg";
        File imageFile = new File(resultDir, imageFileName);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            boolean cropImage = croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // Path to image for reference
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Remove characters != "0-9" and "."
    private String filterText(String text) {
        StringBuilder filteredText = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c) || c == '.' || (c >= 'o' && c <= '9')) {
                filteredText.append(c);
            }
        }
        return filteredText.toString();
    }

    //  View Database Results/History
    private void openOCRDatabase() {
        Intent intent = new Intent(this, DatabaseResultActivity.class);
        startActivity(intent);
    }


    @SuppressLint("ApplySharedPref")
    public void initSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        OcrSettingsActivity.resetSettings();
    }

    public void checkAndUpdateSettings() {
        if (OcrSettingsActivity.checkAndUpdateSettings(this)) {
            String realModelDir = getCacheDir() + "/" + OcrSettingsActivity.modelDir;
            String detModelName = "ch_PP-OCRv3_det_infer_YOLO";
//            String clsModelName = "ch_ppocr_mobile_v2.0_cls_infer";
            String recModelName = "ch_PP-OCRv3_rec_infer";
            String realDetModelDir = realModelDir + "/" + detModelName;
//            String realClsModelDir = realModelDir + "/" + clsModelName;
            String realRecModelDir = realModelDir + "/" + recModelName;
            String srcDetModelDir = OcrSettingsActivity.modelDir + "/" + detModelName;
//            String srcClsModelDir = OcrSettingsActivity.modelDir + "/" + clsModelName;
            String srcRecModelDir = OcrSettingsActivity.modelDir + "/" + recModelName;
            Utils.copyDirectoryFromAssets(this, srcDetModelDir, realDetModelDir);
//            Utils.copyDirectoryFromAssets(this, srcClsModelDir, realClsModelDir);
            Utils.copyDirectoryFromAssets(this, srcRecModelDir, realRecModelDir);
            String realLabelPath = getCacheDir() + "/" + OcrSettingsActivity.labelPath;
            Utils.copyFileFromAssets(this, OcrSettingsActivity.labelPath, realLabelPath);

            String detModelFile = realDetModelDir + "/" + "inference.pdmodel";
            String detParamsFile = realDetModelDir + "/" + "inference.pdiparams";
//            String clsModelFile = realClsModelDir + "/" + "inference.pdmodel";
//            String clsParamsFile = realClsModelDir + "/" + "inference.pdiparams";
            String recModelFile = realRecModelDir + "/" + "inference.pdmodel";
            String recParamsFile = realRecModelDir + "/" + "inference.pdiparams";
            String recLabelFilePath = realLabelPath; // ppocr_keys_v1.txt
            RuntimeOption detOption = new RuntimeOption();
            RuntimeOption clsOption = new RuntimeOption();
            RuntimeOption recOption = new RuntimeOption();
            detOption.setCpuThreadNum(OcrSettingsActivity.cpuThreadNum);
            clsOption.setCpuThreadNum(OcrSettingsActivity.cpuThreadNum);
            recOption.setCpuThreadNum(OcrSettingsActivity.cpuThreadNum);
            detOption.setLitePowerMode(OcrSettingsActivity.cpuPowerMode);
            clsOption.setLitePowerMode(OcrSettingsActivity.cpuPowerMode);
            recOption.setLitePowerMode(OcrSettingsActivity.cpuPowerMode);
            if (Boolean.parseBoolean(OcrSettingsActivity.enableLiteFp16)) {
                detOption.enableLiteFp16();
                clsOption.enableLiteFp16();
                recOption.enableLiteFp16();
            }
            DBDetector detModel = new DBDetector(detModelFile, detParamsFile, detOption);
//            Classifier clsModel = new Classifier(clsModelFile, clsParamsFile, clsOption);
            Recognizer recModel = new Recognizer(recModelFile, recParamsFile, recLabelFilePath, recOption);
            predictor.init(detModel, recModel);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(OcrMainActivity.this)
                    .setTitle("Permission denied")
                    .setMessage("Click to force quit the app, then open Settings->Apps & notifications->Target " +
                            "App->Permissions to grant all of the permissions.")
                    .setCancelable(false)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OcrMainActivity.this.finish();
                        }
                    }).show();
        }
    }

    private void requestAllPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA}, 0);
    }

    private boolean checkAllPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}