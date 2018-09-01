package Controladores;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Rakesh Bhatt (rakeshbhatt10)
 */
class WebCamManager {

	private Webcam webCam;
	private boolean stopCamera;
	private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
	private final ImageView imgWebCamCapturedImage;

	WebCamManager(final int webCamIndex, ImageView imgWebCamCapturedImage) {
		if (CONFIG.CAMERA)
			if (CONFIG.CAMERA_INDEX >= 0) {
				initializeWebCam(webCamIndex);
			}
		this.imgWebCamCapturedImage = imgWebCamCapturedImage;

	}

	static List<Webcam> getWebCams() {
		return Webcam.getWebcams();
	}

	private void initializeWebCam(final int webCamIndex) {

		Task<Void> webCamTask = new Task<Void>() {

			@Override
			protected Void call() {

				if (webCam != null) {
					disposeWebCamCamera();
				}

				webCam = Webcam.getWebcams().get(webCamIndex);
				webCam.open();
				startWebCamStream();
				startWebCamCamera();
				return null;
			}
		};

		Thread webCamThread = new Thread(webCamTask);
		webCamThread.setDaemon(true);
		webCamThread.start();
		}

	private void startWebCamStream() {

		stopCamera = false;

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() {

				final AtomicReference<WritableImage> ref = new AtomicReference<>();
				BufferedImage img;

				while (!stopCamera) {
					try {
						if ((img = webCam.getImage()) != null) {

							ref.set(SwingFXUtils.toFXImage(img, ref.get()));
							img.flush();

							Platform.runLater(() -> imageProperty.set(ref.get()));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		};

		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		imgWebCamCapturedImage.imageProperty().bind(imageProperty);

	}

	private void disposeWebCamCamera() {
		stopCamera = true;
		webCam.close();
	}

	private void startWebCamCamera() {
		stopCamera = false;
		startWebCamStream();
	}

}
