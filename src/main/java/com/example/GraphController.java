package com.example;

import javafx.fxml.FXML;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.File;
import java.io.IOException;

public class GraphController {
    @FXML
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a .wav File");

        // filter to only allow .wav files
        ExtensionFilter wavFilter = new ExtensionFilter("WAV Files", "*.wav");
        fileChooser.getExtensionFilters().add(wavFilter);

        // show file selection dialog
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            System.out.println("Selected .wav file: " + file.getAbsolutePath());

        } else {
            System.out.println("No file selected.");
        }
    }
}
