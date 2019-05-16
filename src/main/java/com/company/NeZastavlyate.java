package com.company;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class NeZastavlyate implements Runnable {

    @Override
    public void run() {

        try {
            String voice = "/media/basay/data/voice/ne_zastavlyate.mp3";
            InputStream musicFile = new FileInputStream(voice);
            AudioDevice audioDevice = new JavaSoundAudioDevice();
            AdvancedPlayer advancedPlayer = new AdvancedPlayer(musicFile, audioDevice);
            advancedPlayer.play();
        } catch (FileNotFoundException e) {
            System.out.println("Музыкальный файл не найден.");
        } catch (JavaLayerException e) {
            System.out.println("Не могу воспроизвести файл");
        }
    }
}