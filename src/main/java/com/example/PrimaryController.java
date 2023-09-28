package com.example;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PrimaryController {
    @FXML
    private LineChart<Number, Number> left_chart;
    @FXML
    private LineChart<Number, Number> right_chart;

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
            parseFile(file);
        } else {
            System.out.println("No file selected.");
        }
    }

    private void parseFile(File file) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            int channels = format.getChannels();

            // ensure it's a stereo audio file
            if (channels != 2) {
                System.out.println("Please input a stereo audio file");
                return;
            }

            // read the audio data into a byte array
            byte[] audioData = new byte[(int) audioInputStream.getFrameLength() * (format.getSampleSizeInBits() / 8) * channels];
            audioInputStream.read(audioData);

            // extract left and right channels
            short[] leftChannel = new short[audioData.length / 4];
            short[] rightChannel = new short[audioData.length / 4];

            for (int i = 0; i < audioData.length; i += 4) {
                // shift the MSB left to merge it with the LSB
                leftChannel[i / 4] = (short) (audioData[i] | (audioData[i + 1] << 8));
                rightChannel[i / 4] = (short) (audioData[i + 2] | (audioData[i + 3] << 8));
            }

            plotData(leftChannel, rightChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void plotData(short[] leftChannel, short[] rightChannel) {
        new Thread(() -> {
            left_chart.setCreateSymbols(false);
            right_chart.setCreateSymbols(false);
            left_chart.setLegendVisible(false);
            right_chart.setLegendVisible(false);
            XYChart.Series<Number, Number> leftSeries = new XYChart.Series<>();
            XYChart.Series<Number, Number> rightSeries = new XYChart.Series<>();

            // spawn threads to handle plotting the data
            Thread leftThread = new Thread(() -> {
                List<XYChart.Data<Number, Number>> dataPoints = new ArrayList<>();
                for (int i = 0; i < leftChannel.length; i++) {
                    if (i % 1000 == 0) {
                        System.out.println("left: " + i + " / " + leftChannel.length);
                    }
                    dataPoints.add(new XYChart.Data<>(i, leftChannel[i]));
                }
                leftSeries.getData().addAll(dataPoints);
            });

            Thread rightThread = new Thread(() -> {
                List<XYChart.Data<Number, Number>> dataPoints = new ArrayList<>();
                for (int i = 0; i < rightChannel.length; i++) {
                    if (i % 1000 == 0) {
                        System.out.println("right: " + i + " / " + rightChannel.length);
                    }
                    dataPoints.add(new XYChart.Data<>(i, rightChannel[i]));

                }
                rightSeries.getData().addAll(dataPoints);
            });

            leftThread.start();
            rightThread.start();

            try {
                // wait here until both threads have finished
                leftThread.join();
                rightThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Platform.runLater(() -> {
                // clear existing data and add the new series to the chart
                left_chart.getData().clear();
                right_chart.getData().clear();
                left_chart.getData().add(leftSeries);
                right_chart.getData().add(rightSeries);
                leftSeries.getNode().setStyle("-fx-stroke-width: 1px;");
                rightSeries.getNode().setStyle("-fx-stroke-width: 1px;");
                System.out.println("done plotting");
            });
        }).start();
    }
}