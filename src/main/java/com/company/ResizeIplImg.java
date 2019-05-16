package com.company;

import org.bytedeco.javacpp.opencv_core.*;

import static org.bytedeco.javacpp.opencv_imgproc.cvResize;

public class ResizeIplImg {
    public IplImage resizeIplImg(IplImage img, int weight, int height){

        IplImage resizeImage = IplImage.create(weight,height,img.depth(),img.nChannels());
        cvResize(img,resizeImage);
        return resizeImage;

    }
}
