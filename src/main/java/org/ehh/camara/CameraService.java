package org.ehh.camara;

import com.github.sarxos.webcam.Webcam;

import java.awt.image.BufferedImage;
import java.util.List;

public class CameraService {

    public List<Webcam> listCameras() {
        return Webcam.getWebcams();
    }

    public BufferedImage capture(Webcam cam) {
        cam.open();
        BufferedImage img = cam.getImage();
        cam.close();
        return img;
    }
}