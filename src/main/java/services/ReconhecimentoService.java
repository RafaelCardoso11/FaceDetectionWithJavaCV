package services;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import repository.Pessoas;

import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static utils.ResourceUtils.getResourcePath;

public class ReconhecimentoService {

    public static void main(String[] args) throws Exception {
        String classifierPath = getResourcePath("haarcascade-frontalface-alt.xml");

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);

        CascadeClassifier faceDetector = new CascadeClassifier(classifierPath);
        FaceRecognizer faceRecognizer = FisherFaceRecognizer.create();

        faceRecognizer.read(getResourcePath("classificadorFisherFaces.yml"));


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

                drawRectanglesInFaces(faces, imageWithColor, faceRecognizer);

                showImage(canvasFrame, frameCaptured);


            }

        } catch (FrameGrabber.Exception e) {
            System.out.println("Erro ao capturar frames: " + e.getMessage());
            stop(grabber, canvasFrame);
        }

    }

    /**
     * Exibe a imagem na janela.
     *
     * @param canvasFrame   Janela onde a imagem é exibida.
     * @param frameCaptured Frame capturado.
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
    private static void drawRectanglesInFaces(RectVector faces, Mat imageWithColor, FaceRecognizer faceRecognizer) {
        for (int i = 0; i < faces.size(); i++) {
            Rect rect = faces.get(i);

            rectangle(imageWithColor, rect, new Scalar(0, 255, 0, 0));

            Mat face = new Mat(imageWithColor, rect);
            cvtColor(face, face, COLOR_BGRA2GRAY);

            resize(face, face, new Size(160, 160));

            IntPointer label = new IntPointer(1);
            DoublePointer confidence = new DoublePointer(1);
            faceRecognizer.predict(face, label, confidence);

            int predicted = label.get(0);


            String nome = "";

            if(predicted  == -1){
                nome = "Desconhecido";
            }else {
                nome = Pessoas.getNomeById(predicted) +  " - " + confidence.get(0);
            }

            int x  = Math.max(rect.tl().x() - 10, 0);
            int y = Math.max(rect.tl().y() - 10, 0);

            putText(imageWithColor, nome, new Point(x, y), FONT_HERSHEY_PLAIN, 1.4, new Scalar(0, 255, 0, 0));

        }
    }
}
