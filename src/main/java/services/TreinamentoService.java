package services;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_face.EigenFaceRecognizer;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer;

import java.io.File;
import java.nio.IntBuffer;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;


public class TreinamentoService {
    static String baseDir = "src/main/resources/banco_imagens";

    public static void main(String[] args) {
        execute();
    }

    public static void execute() {
        File imagesDir = new File(baseDir);
        File[] files = imagesDir.listFiles();
        MatVector photos = new MatVector(files.length);
        Mat labels = new Mat(files.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();

        int counter = 0;

        for (File image : files) {
            Mat photo = imread(image.getAbsolutePath(), IMREAD_GRAYSCALE);
            int id = Integer.parseInt(image.getName().split("_")[0]);
            photos.put(counter, photo);
            labelsBuf.put(counter, id);
            counter++;
        }

        FaceRecognizer eigenfaces = EigenFaceRecognizer.create();
        eigenfaces.train(photos, labels);
        eigenfaces.save("src/main/resources/classificadorEigenFaces.yml");


        FaceRecognizer fisherFaces = FisherFaceRecognizer.create();
        fisherFaces.train(photos, labels);
        fisherFaces.save("src/main/resources/classificadorFisherFaces.yml");

        FaceRecognizer lbph = FisherFaceRecognizer.create();
        lbph.train(photos, labels);
        lbph.save("src/main/resources/classificadorLBPH.yml");


    }
}
