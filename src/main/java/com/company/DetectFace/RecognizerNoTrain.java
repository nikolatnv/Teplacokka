package com.company.DetectFace;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_face;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.TreeMap;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

public class RecognizerNoTrain {

        public Map<Integer, String> ass = new TreeMap<>();
        public opencv_face.LBPHFaceRecognizer faceRecognizer = opencv_face.LBPHFaceRecognizer.create();

        public void runRecognizer() {
            File imageDir = new File("/media/basay/data/FindFace/src/main/java/com/company/DetectFace");
            FilenameFilter imgFilter = (file, s) -> s.toLowerCase().endsWith(".jpg");
            File[] imageFiles = imageDir.listFiles(imgFilter);
            opencv_core.MatVector matVector = new opencv_core.MatVector(imageFiles.length);
            opencv_core.Mat labels = new opencv_core.Mat(imageFiles.length, 1, CV_32SC1);
            IntBuffer labelsBuf = labels.createBuffer();
            int counter = 0;

            for (File image : imageFiles) {
                opencv_core.Mat baseImage = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
                int label = Integer.parseInt(image.getName().split("\\.")[0]);
                ass.put(label,image.getName());

                matVector.put(counter, baseImage);
                labelsBuf.put(counter,label);
                counter++;
            }

//        faceRecognizer.train(matVector,labels);
            faceRecognizer.read("/media/basay/data/FindFace/src/main/java/com/company/resultTrain.xml");
//        faceRecognizer.save("/media/basay/data/FindFace/src/main/java/com/company/resultTrain.xml");


        }

    }
