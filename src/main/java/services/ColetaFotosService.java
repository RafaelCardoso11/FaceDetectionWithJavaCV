package services;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import repository.Pessoas;

import java.util.Scanner;

import static java.lang.Thread.sleep;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static utils.ResourceUtils.getResourcePath;

public class ColetaFotosService {

    static Frame frameCaptured = null;

    static Mat imageWithColor = new Mat();

    static int numbersOfPhotosPerPerson = 25;


    public static void main(String[] args) throws Exception {
        String classifierPath = getResourcePath("haarcascade-frontalface-alt.xml");

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        CascadeClassifier faceDetector = new CascadeClassifier(classifierPath);
        CanvasFrame canvasFrame = new CanvasFrame("Camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());

        FaceDetectionService faceDetectionService = new FaceDetectionService();


        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome da pessoa: ");
        String nameOfPerson = scanner.nextLine();

        int numberOfPhotos = 0;

        try {
        // Iniciando a captura de frames
            grabber.start();

            while ((frameCaptured = grabber.grab()) != null) {

                // Convertendo o frame capturado para uma imagem com cores
                imageWithColor = converter.convert(frameCaptured);

                RectVector faces = faceDetectionService.execute(imageWithColor, faceDetector);


                drawRectanglesInFaces(faces, imageWithColor);

                sleep(1000);
                if ((faces.size() > 0) && (numberOfPhotos < numbersOfPhotosPerPerson)) {

                    System.out.println(faces.size() + "_" + numberOfPhotos);
                    numberOfPhotos++;
                    Mat imageGray = new Mat();

                    // Convertendo a imagem com cores para uma imagem em escala de cinza
                    cvtColor(imageWithColor, imageGray, COLOR_BGRA2GRAY);

                    Mat faceCurrent = new Mat(imageGray, faces.get(0));

                    resize(faceCurrent, faceCurrent, new Size(160, 160));

                    int id = Pessoas.getIdByNome(nameOfPerson);
                    imwrite("src/main/resources/banco_imagens/" + id + "_" +nameOfPerson.toLowerCase() + "_" + numberOfPhotos + ".png", faceCurrent);
                }


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
    private static void drawRectanglesInFaces(RectVector faces, Mat imageWithColor) {
        for (int i = 0; i < faces.size(); i++) {
            Rect rect = faces.get(i);

            rectangle(imageWithColor, rect, new Scalar(0, 255, 0, 0));
        }
    }
}
