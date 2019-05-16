package com.company;

import org.bytedeco.javacpp.opencv_core.*;

import static org.bytedeco.javacpp.opencv_core.*;

public class CutFaceFromStream {

    public IplImage getSubImageFromIpl(IplImage iplImage, int x, int y, int wight, int height){

        IplImage resizeIplImg = IplImage.create(wight,height,iplImage.depth(),iplImage.nChannels());
        cvSetImageROI(iplImage,cvRect(x,y,wight,height));
        cvCopy(iplImage, resizeIplImg);
        cvResetImageROI(iplImage);
        return resizeIplImg;
    }
}
