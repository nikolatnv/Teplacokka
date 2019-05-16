package com.company;
import com.company.DetectFace.RecognizerNoTrain;
import org.bytedeco.javacv.*;

import java.io.*;
import java.util.concurrent.*;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;



public class MainStream implements Runnable {

    private CanvasFrame canvasFrame;
    private ResizeIplImg resizeIplImg = new ResizeIplImg();
    private CutFaceFromStream cutFaceFromStream = new CutFaceFromStream();
    private TrainDetector trainDetector = new TrainDetector();
    private RecognizerNoTrain recognizerNoTrain = new RecognizerNoTrain();
    private int labelOrange = 0;
    private int labelNagaina = 0;
    private Semaphore semaphore = new Semaphore(1, true);
    private int labelDetect;
    private int count;


    @Override
    public void run() {

        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
//      FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("/media/basay/data/RecordVideoFromCam/video.avi");
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        String filename = "/media/basay/data/RecordVideoFromCam/video.avi";
        double quality = 1;

        try {
            grabber.setAudioStream(0);
            grabber.start();

            Frame frame = grabber.grab();

            canvasFrame = new CanvasFrame("Распознаватель ебала!");
            canvasFrame.setCanvasSize(frame.imageWidth, frame.imageHeight);

            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(filename, frame.imageWidth, frame.imageHeight);
            recorder.setFrameRate(25);
            recorder.setFormat("avi");
            recorder.setVideoCodec(13);
            recorder.setVideoBitrate((int) quality * 1024 * 1024);

            trainOrRecognizer();

            recorder.start();

            while (canvasFrame.isVisible() && (frame = grabber.grab()) != null) {

                //iplImage = converter.convert(frame); - цветной конвертр

                IplImage iplImage = toGray(converter.convert(frame));
                findObj(iplImage);
                canvasFrame.showImage(converter.convert(iplImage));
                recorder.record(converter.convert(iplImage));

            }

            recorder.stop();
            canvasFrame.dispose();

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }

// перевод изображения в серый цвет

    private IplImage toGray(IplImage img) {
        IplImage currentFrame = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(img, currentFrame, CV_RGB2GRAY);
        return currentFrame;
    }

//метод поиска обьекта в потоке

    private void findObj(IplImage currentImg) throws InterruptedException {

        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad("/media/basay/data/FindFace/src/main/java/com/company/cascades/cascades.xml"));
        CvMemStorage cvMemStorage = CvMemStorage.create();
        CvSeq faces = cvHaarDetectObjects(currentImg, cascade, cvMemStorage, 1.2, 5, CV_HAAR_DO_CANNY_PRUNING);

        int total = faces.total();
        int face_w = 120;
        int face_h = 150;


        if (total > 0) {
//            System.out.println(total + " faces");
            for (int i = 0; i < total; i++) {

                CvRect rect = new CvRect(cvGetSeqElem(faces, i));
                int x = rect.x(), y = rect.y(), width = rect.width(), height = rect.height();
                IplImage face = cutFaceFromStream.getSubImageFromIpl(currentImg, x, y, width, height);
                face = resizeIplImg.resizeIplImg(face, face_w, face_h);
                int label = recognizerNoTrain.faceRecognizer.predict_label(cvarrToMat(face));
                IplImage recFace = cvLoadImage("/media/basay/data/FindFace/src/main/java/com/company/DetectFace/" + recognizerNoTrain.ass.get(label),
                        CV_LOAD_IMAGE_COLOR);

                /* Запускать при обучении

                int label = trainDetector.faceRecognizer.predict_label(cvarrToMat(face));
                IplImage recFace = cvLoadImage("/media/basay/data/FindFace/src/main/java/com/company/DetectFace/" + trainDetector.ass.get(label),
                        CV_LOAD_IMAGE_COLOR);
                */

                recFace = resizeIplImg.resizeIplImg(recFace, face.width(), face.height());
                if ((x + recFace.width() < canvasFrame.getWidth()) && (y + recFace.height() < canvasFrame.getHeight())) {

//                    отрисовка квадрата на фрейме

//                    cvSetImageROI(currentImg, cvRect(x,y, recFace.width(), recFace.height()));
//                    cvCopy(recFace, currentImg);

                    System.out.println(label);

                    if (label > 291 && label < 487) {
                        labelOrange++;
                        if (labelOrange == 60) {

                            new Thread(new FindObjOnFrame(semaphore,"Orange")).start();
                            System.out.println("Рыжий");
                        }
                        if (labelOrange == 350) {
                            System.out.println("Сброс");
                            labelOrange = 0;
                        }
                    }

                    if (label > 1 && label < 290) {

                        labelNagaina++;
                        if (labelNagaina == 30) {
                            new Thread(new FindObjOnFrame(semaphore,"Nagaina")).start();
                            System.out.println("Нагайна");
                        }
                        if (labelNagaina == 250) {
                            System.out.println("Reset");
                            labelNagaina = 0;
                        }
                    }


                }

/*              Если требуется добавить фото нового человека в базу данных

                cvSaveImage("/media/basay/data/FindFace/src/main/java/com/company/DetectFace/" + count +"new.jpg", face);
                count++;
                rectangle(cvarrToMat(currentImg), new Rect(x,y,width,height), new Scalar(0,255,0,0),2,0,0);
*/
            }

        }

    }
//    метод выбора запуска либо обучение и тренировка распознавателя, либо распознавание потока.
    private void trainOrRecognizer() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader( System.in));
        String request = "";

            while (!request.equals("e")) {

                    System.out.println("Выберете распознавание или обучение? \n распознавание нажмите - r\n обучение нажмите - t\n");
                    request = reader.readLine();

                    if (request.equals("r")) {
                        System.out.println("Wait...");
                        recognizerNoTrain.runRecognizer();
                        break;
                    }
                    if (request.equals("t")) {
                        System.out.println("Wait...");
                        trainDetector.runTrain();
                        break;
                    }
                    System.out.println("Некорректный ввод! Повторите!");
            }
    }

}

