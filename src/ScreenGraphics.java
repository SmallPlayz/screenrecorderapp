import com.teamdev.jxcapture.Codec;
import com.teamdev.jxcapture.EncodingParameters;
import com.teamdev.jxcapture.InterpolationMode;
import com.teamdev.jxcapture.VideoCapture;
import com.teamdev.jxcapture.video.Desktop;
import com.teamdev.jxcapture.video.win.AVICapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class ScreenGraphics {
    private JFrame frame;
    public static JLabel label;
    private JButton startOrPauseRecording;
    private JButton stopRecording;
    private boolean paused = false;
    ScreenGraphics() {
        frame = new JFrame("Screen Recorder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Frame now exits on close.
        frame.setSize(1280, 633); // Frame height and width is now set to variables 'width' and 'height' declared and initialized on line 5 and 6.
        frame.setResizable(false); // Frame can no longer be manually resized by user.
        frame.setLayout(null); // Frame layout set to null.

        label = new JLabel();
        label.setBounds(15, 15, 1000, 562);
        label.setOpaque(true);
        label.setBackground(Color.black);
        frame.add(label);

        startOrPauseRecording = new JButton("Start");
        startOrPauseRecording.setBounds(1030, 30, 1200-1030, 40);
        frame.add(startOrPauseRecording);

        stopRecording = new JButton("Stop");
        stopRecording.setBounds(1030, 85, 1200-1030, 40);
        frame.add(stopRecording);

        //***************************

        VideoCapture videoCapture = new AVICapture();
        videoCapture.setVideoSource(new Desktop());

        List<Codec> codecs = videoCapture.getVideoCodecs();
        if (codecs.isEmpty()) {
            throw new IllegalStateException("There are no suitable codecs available");
        }

        System.out.println("Available video codecs:");
        for (Codec codec : codecs) {
            System.out.println(codec);
        }
        Codec preferredCodec = codecs.get(0);
        System.out.println("preferredCodec = " + preferredCodec);

        //***********************

        startOrPauseRecording.addActionListener(e -> {
            if(!paused){
                paused = true;
                startOrPauseRecording.setText("Start");
                videoCapture.pause();
            } else {

                EncodingParameters encodingParameters = new EncodingParameters(new File("Video"+(int)(Math.random() * 10000) + 1+".avi"));
                encodingParameters.setBitrate(10000000);
                encodingParameters.setSize(new Dimension(1600, 900));
                encodingParameters.setFramerate(30);
                encodingParameters.setInterpolationMode(InterpolationMode.HighQuality);
                encodingParameters.setCodec(preferredCodec);
                System.out.println("encodingParameters = " + encodingParameters);

                paused = false;
                startOrPauseRecording.setText("Pause");
                videoCapture.start(encodingParameters);
            }
        });

        stopRecording.addActionListener(e -> {
            if(paused)
                startOrPauseRecording.setText("Start");

            videoCapture.stop();
        });

        Video thread = new Video();
        thread.start();

        frame.setVisible(true); // Frame is now visible when code run.
    }
    public void run() {
        Robot bot = null;
        try {
            bot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        BufferedImage img = bot.createScreenCapture(new Rectangle(0, 0, 1600, 900));
        label.setIcon(new ImageIcon(img));
    }
}
class Video extends Thread {
    public void run() {
        Robot bot = null;
        try {
            bot = new Robot();

            while(true) {
                BufferedImage img = bot.createScreenCapture(new Rectangle(0, 0, 1600, 900));
                Image newImage = img.getScaledInstance(ScreenGraphics.label.getWidth(), ScreenGraphics.label.getHeight(), Image.SCALE_DEFAULT);
                ScreenGraphics.label.setIcon(new ImageIcon(newImage));
                //Thread.sleep((long) 16.67);
            }

        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
}