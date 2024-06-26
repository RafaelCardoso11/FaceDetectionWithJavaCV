package services;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

public class FaceDetectionService {

    public RectVector execute(Mat imageWithColor, CascadeClassifier faceDetector){
        RectVector faces = new RectVector();
        Mat imageGray = new Mat();

        // Convertendo a imagem com cores para uma imagem em escala de cinza
        cvtColor(imageWithColor, imageGray, COLOR_BGRA2GRAY);

        // Detectando as faces na imagem em escala de cinza e armazenando em um vetor
        faceDetector.detectMultiScale(imageGray, faces, 5, 1, 0,  new Size(150, 150), new Size(500, 500));


        return faces;

    }
}
