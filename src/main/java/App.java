import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import services.FaceDetectionService;

import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static utils.ResourceUtils.getResourcePath;

public class App {
    public static void main(String[] args) throws Exception {
        String classifierPath = getResourcePath("haarcascade-frontalface-alt.xml");

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        CascadeClassifier faceDetector = new CascadeClassifier(classifierPath);
        CanvasFrame canvasFrame = new CanvasFrame("Camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        FaceDetectionService faceDetectionService = new FaceDetectionService();


        Frame frameCaptured = null;

        Mat imageWithColor = new Mat();

        // Iniciando a captura de frames

        try {
            grabber.start();

            while ((frameCaptured = grabber.grab()) != null) {

                // Convertendo o frame capturado para uma imagem com cores
                imageWithColor = converter.convert(frameCaptured);

                RectVector faces = faceDetectionService.execute(imageWithColor, faceDetector);

                System.out.println(faces.size());

                drawRectanglesInFaces(faces, imageWithColor);

                showImage(canvasFrame, frameCaptured);

                if(faces.size() == 1){
                    System.out.println("Face detectada");
                    stop(grabber, canvasFrame);
                }

            }

        } catch (FrameGrabber.Exception e) {
            System.out.println("Erro ao capturar frames: " + e.getMessage());
            stop(grabber, canvasFrame);
        }

    }

    /**
     * Exibe a imagem na janela.
     *
     * @param canvasFrame    Janela onde a imagem é exibida.
     * @param frameCaptured  Frame capturado.
     */
    private static void showImage(CanvasFrame canvasFrame, Frame frameCaptured) {
        if (canvasFrame.isVisible()) {
            canvasFrame.showImage(frameCaptured);
        }
    }


    /**
     * Para a captura de frames e fecha a janela.
     *
     * @param grabber     Objeto responsável pela captura de frames.
     * @param canvasFrame Janela onde a imagem é exibida.
     * @throws FrameGrabber.Exception Se ocorrer um erro ao parar a captura de frames.
     */
    private static void stop(OpenCVFrameGrabber grabber, CanvasFrame canvasFrame) throws FrameGrabber.Exception {
        canvasFrame.dispose();
        grabber.stop();
    }

    /**
     * Desenha um retângulo em volta de cada face detectada.
     *
     * @param faces          Vetor de faces detectadas.
     * @param imageWithColor Imagem com as faces detectadas.
     */
    private static void drawRectanglesInFaces(RectVector faces, Mat imageWithColor) {
        for (int i = 0; i < faces.size(); i++) {
            Rect rect = faces.get(i);

            rectangle(imageWithColor, rect, new Scalar(0, 255, 0, 0));
        }
    }
}
