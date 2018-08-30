package sample;

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
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Rakesh Bhatt (rakeshbhatt10)
 */
public class WebCamManager {

	private Webcam webCam;
	private boolean stopCamera;
	private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
	private ImageView imgWebCamCapturedImage;

	public WebCamManager(final int webCamIndex, ImageView imgWebCamCapturedImage){
		initializeWebCam(webCamIndex);
		this.imgWebCamCapturedImage = imgWebCamCapturedImage;
	}

	protected void initializeWebCam(final int webCamIndex) {

		Task<Void> webCamTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

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

	protected void startWebCamStream() {

		stopCamera = false;

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				final AtomicReference<WritableImage> ref = new AtomicReference<>();
				BufferedImage img = null;

				while (!stopCamera) {
					try {
						if ((img = webCam.getImage()) != null) {

							ref.set(SwingFXUtils.toFXImage(img, ref.get()));
							img.flush();

							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									imageProperty.set(ref.get());
								}
							});
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

	protected void disposeWebCamCamera() {
		stopCamera = true;
		webCam.close();
	}

	protected void startWebCamCamera() {
		stopCamera = false;
		startWebCamStream();
	}

	protected void stopWebCamCamera() {
		stopCamera = true;
	}
}
