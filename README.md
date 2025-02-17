# PP-OCR

PP-OCR is an Android application that leverages PaddleOCR for real-time Optical Character Recognition (OCR) processing. This application provides functionalities to capture images, process OCR in real-time or in batch mode, and adjust settings to optimize OCR performance.

## Features

### OcrMainActivity
The main activity that handles:
- **Camera Preview**: Displays the camera feed with live OCR processing.
- **Image Capture and Processing**: Allows users to capture images for OCR and processes them using PaddleOCR.
- **Realtime Toggle**: Enables or disables real-time OCR detection on the camera feed.
- **Flashlight**: Provides flashlight control for enhanced image capture in low-light conditions.
- **View and Manage Results**: Displays OCR results for review and allows users to switch between results and the camera page.

### OcrSettingsActivity
A settings activity that allows users to configure the app:
- **Model Selection**: Choose between pre-installed OCR models or provide custom model files.
- **Scoring Threshold Settings**: Adjust the confidence threshold for OCR results (default is 40%).
- **Performance Settings**: Customize the number of CPU threads and power modes for optimal performance.
- **Lite FP16 Support**: Enable or disable Lite FP16 model inference for enhanced performance on supported devices.

## Key Functionalities

### Camera and Image Preprocessing
- **CameraOrientation Handling**: Automatically adjusts the camera orientation based on device position.
- **Bitmap Conversion and Processing**: Captures frames from the camera, processes them into bitmaps, and applies OCR algorithms for text recognition.
- **Image Cropping and Text Highlighting**: Offers options to crop and highlight text from captured images for enhanced OCR accuracy.

### OCR Processing
- **PaddleOCR Integration**: Uses the pre-trained PPOCRv3 model for text detection and recognition.
- **Confidence Filtering**: Filters recognized text based on the configured confidence threshold.
- **Realtime and Batch Modes**:
  - **Realtime Detection**: Continuous text detection while viewing the camera feed.
  - **Batch Processing**: Processes captured images or selected pictures from the gallery.

### Application Settings
- **Model Configuration**: Customize the directory paths for models and label data.
- **Threshold Adjustment**: Control the sensitivity of OCR detection by configuring the score threshold.
- **Performance Optimization**: Adjust CPU threading and power consumption levels to balance speed and energy efficiency.

## File Structure

### Core Files
- **OcrMainActivity.java**: Handles the main user interface interactions, camera preview, image capture, and OCR processing using the PaddleOCR model.
- **OcrSettingsActivity.java**: Provides a preference activity for configurable app settings such as model paths and OCR parameters.

### Utility Classes
- **Utils.java**: A utility class for I/O operations, camera handling, file operations, and OpenGL shader programs. Key functionalities:
  - Copying files and directories.
  - Reading text files.
  - Handling camera and image dimensions.

## Getting Started

### Prerequisites
- Android Studio
- A device with Android 5.0 (API level 21) or higher.
- Access to pre-trained OCR models (compatible with PaddleOCR).

### Installation
1. Clone the repository to your local machine.
2. Import the project into Android Studio.
3. Sync the Gradle build files to download dependencies.
4. Run the application on a connected Android device or emulator.

### Usage
1. Launch the app to access the camera page.
2. Capture an image or enable real-time detection for live OCR processing.
3. Use the settings menu to adjust parameters like models, threads, and thresholds.
4. Review OCR results in the result list view.

## Configuration Options
The **OcrSettingsActivity** provides the following options for customizing the app:
- **Model Directory**: Set the directory for custom or pre-installed OCR models.
- **Label Path**: Specify the path to a label file for recognized text categories.
- **CPU Settings**:
  - Number of threads.
  - Power mode (e.g., high performance or energy-saving).
- **Confidence Threshold**: Adjust the minimum score for OCR recognition.
- **Lite FP16 Inference**: Enable/disable support for FP16 inference to enhance performance on supported hardware.

## Development and Contribution

### Code Overview
- **OcrMainActivity.java** and **OcrSettingsActivity.java**: Core activities for OCR processing and app configuration.
- **Utils.java**: Assists with file management, camera setup, and other utilities.

### Contributing
Contributions are welcome! Please follow these steps:
1. Fork the repository.
2. Create a new branch for your feature.
3. Commit your changes and push them to your fork.
4. Open a pull request describing your changes.

## Future Enhancements
The following features are planned in future releases:
- Multi-language OCR support.
- Improved error handling and UI feedback.
- Dark mode support for the app interface.

## License
This project is licensed under the MIT License. See the LICENSE file for more details.
