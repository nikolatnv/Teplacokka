package com.company;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;


public class FindObjOnFrame implements Runnable {

    private AudioDevice audioDevice = new JavaSoundAudioDevice();
    private String labelDetect;
    private Semaphore semaphore;

    FindObjOnFrame(Semaphore semaphore, String labelDetect) {
        this.semaphore = semaphore;
        this.labelDetect = labelDetect;
    }


/*
     *  Поставить счётчик на колличество срабатываний метода если он больше 1 и меньше 2-х часов проспускать выполнение
     *  иначе сбрасывать счётчик и запускать снова
     * */

    @Override
    public void run() {

        try {

            semaphore.acquire();

            if(labelDetect.equals("Orange")){
                soundOrange();
            }
            if(labelDetect.equals("Nagaina")){
                soundNagaina();
            }

            semaphore.release();

        } catch (FileNotFoundException e) {
            System.out.println("Музыкальный файл не найден.");
        } catch (JavaLayerException e) {
            System.out.println("Не могу воспроизвести файл");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Не уснул!");
        }
    }

    private void soundOrange() throws IOException, JavaLayerException, InterruptedException {
        String voice = "/media/basay/data/voice/orange_hello.mp3";
        InputStream musicFile = new FileInputStream(voice);
        AudioDevice audioDevice = new JavaSoundAudioDevice();
        AdvancedPlayer advancedPlayer = new AdvancedPlayer(musicFile, audioDevice);
        advancedPlayer.play();
    }
    private void soundNagaina() throws IOException, JavaLayerException, InterruptedException {
        String voice = "/media/basay/data/voice/nagaina_hello.mp3";
        InputStream musicFile = new FileInputStream(voice);
        AudioDevice audioDevice = new JavaSoundAudioDevice();
        AdvancedPlayer advancedPlayer = new AdvancedPlayer(musicFile, audioDevice);
        advancedPlayer.play();
    }

}



// private static volatile FindObjOnFrame findObjOnFrame; - если нужен сингтон
/*  если нужен синглтон

    static FindObjOnFrame getFindObjOnFrame(){
        FindObjOnFrame loclFindObj = findObjOnFrame;
            if(loclFindObj == null){
                synchronized (FindObjOnFrame.class){
                    loclFindObj = findObjOnFrame;
                        if(loclFindObj == null){
                            loclFindObj = findObjOnFrame = new FindObjOnFrame();
                        }
                }
            }
            return loclFindObj;
    }
*/

