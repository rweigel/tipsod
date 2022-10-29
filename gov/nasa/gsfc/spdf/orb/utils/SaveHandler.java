/*
 * NOSA HEADER START
 *
 * The contents of this file are subject to the terms of the NASA Open
 * Source Agreement (NOSA), Version 1.3 only (the "Agreement").  You may
 * not use this file except in compliance with the Agreement.
 *
 * You can obtain a copy of the agreement at
 *   docs/NASA_Open_Source_Agreement_1.3.txt
 * or
 *   http://sscweb.gsfc.nasa.gov/tipsod/NASA_Open_Source_Agreement_1.3.txt.
 *
 * See the Agreement for the specific language governing permissions
 * and limitations under the Agreement.
 *
 * When distributing Covered Code, include this NOSA HEADER in each
 * file and include the Agreement file at
 * docs/NASA_Open_Source_Agreement_1.3.txt.  If applicable, add the
 * following below this NOSA HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 *
 * NOSA HEADER END
 *
 * Copyright (c) 2003-2006 United States Government as represented by the
 * National Aeronautics and Space Administration. No copyright is claimed
 * in the United States under Title 17, U.S.Code. All Other Rights Reserved.
 *
 * $Id: SaveHandler.java,v 1.23 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on May 24, 2002, 1:11 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.content.behaviors.Animation;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.j3d.Canvas3D;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

/**
 * The SaveHandler class implements the response to a save action. The image is
 * saved as a JPEG to disk
 *
 * @author rchimiak
 * @version $Revision: 1.23 $
 */
public class SaveHandler extends ImageHandler {

    private static String CURRENT_DIRECTORY = new String();

    private final static String ANIMATED_GIF = "Animated GIF (*.gif)";
    private final static String JPEG = "Single frame JPEG (*.jpg)";
    private final static String PNG = "Single frame PNG (*.png)";
    private final static String GIF = "Single frame GIF (*.gif)";
    private final static String[] filters = {ANIMATED_GIF,
        JPEG,
        PNG,
        GIF};

    public interface Exts {

        int JPG = 0,
                PNG = 1,
                GIF = 2,
                VIDEO = 3,
                PRINT = 4;
    }

    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String png = "png";

    private static final long[] savingTime = {0l, 0l};

    private JFileChooser chooser;

    /**
     * Creates new SaveHandler
     */
    public SaveHandler(OrbitViewer ov, Canvas3D canvas) {

        super(ov, canvas);
    }

    @Override
    public void handleCommand() {

        try {
            if (chooser == null) {
                chooser = new JFileChooser(new File(CURRENT_DIRECTORY));

                for (int i = 0; i < filters.length; i++) {

                    chooser.addChoosableFileFilter(new ImageFilter(filters[i]));

                }
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(chooser.getChoosableFileFilters()[0]);
                chooser.setFileView(new ImageFileView());

            }

            chooser.setSelectedFile(new File(""));

            int returnVal = chooser.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                String name = chooser.getSelectedFile().getName();
                int i = name.lastIndexOf('.');
                name = i > 0 && i < name.length() - 1 ? name.substring(0, i) : name;

                FileOutputStream fileOut = null;

                CURRENT_DIRECTORY = chooser.getCurrentDirectory().getPath();

                switch (((ImageFilter) chooser.getFileFilter()).getExtension()) {

                    case Exts.GIF:
                        saveImage(Exts.GIF);
                        fileOut = new FileOutputStream(
                                CURRENT_DIRECTORY + "/" + name + ".gif");
                        AnimatedGifEncoder e = new AnimatedGifEncoder();
                        e.start(fileOut);
                        e.addFrame(bImage);
                        e.finish();
                        removeImage();
                        break;
                    case Exts.JPG:
                        saveImage(Exts.JPG);
                        fileOut = new FileOutputStream(
                                CURRENT_DIRECTORY + "/" + name + ".jpg");
                        ImageIO.write(bImage, "jpg", fileOut);
                        removeImage();
                        break;
                    case Exts.PNG:
                        saveImage(Exts.PNG);
                        fileOut = new FileOutputStream(
                                CURRENT_DIRECTORY + "/" + name + ".png");
                        ImageIO.write(bImage, "png", fileOut);
                        removeImage();
                        break;
                    case Exts.VIDEO:
                        Animation anim;
                        if (ContentBranch.getSatBranch() != null) {

                            anim = ContentBranch.getSatBranch().getAnimation();
                            int size;
                            size = OrbitViewer.getSlider().getMaximum();
                            OptionPane option = new OptionPane(size);

                            if (option.getSampling() != 0 || option.getRate() != 0) {

                                anim.startRecording(
                                        CURRENT_DIRECTORY + "/" + name + ".gif", option.getRate(), option.getRepeat());
                                long startTime = System.currentTimeMillis();

                                for (int j = 0; j < size; j++) {

                                    if (j % option.getSampling() == 0 || j == size - 1) {

                                        OrbitViewer.getSlider().setValue((j));
                                        anim.processRecording();
                                    }
                                }
                                anim.stopRecording();

                                long estimatedTime = System.currentTimeMillis() - startTime;
                                int numberOfFrames = size / option.getSampling();
                                long estimatedTimePerFrame = estimatedTime / numberOfFrames;

                                savingTime[0] += 1;
                                savingTime[1] = savingTime[1] == 0 ? estimatedTimePerFrame
                                        : ((savingTime[1] * savingTime[0]) + estimatedTimePerFrame) / (savingTime[0] + 1);
                            }
                        }
                        System.gc();
                        break;
                    default:
                        break;
                }
                if (fileOut != null) {

                    fileOut.flush();
                    fileOut.close();
                    System.gc();
                }
            }
        } catch (HeadlessException e) {
            removeImage();
            System.err.println("Failed to save image: " + e);

        } catch (IOException e) {
            removeImage();
            System.err.println("Failed to save image: " + e);
        }
    }

    public class ImageFilter extends FileFilter {

        String description = null;

        public ImageFilter(String description) {

            super();
            setDescription(description);
        }

        @Override
        public boolean accept(File file) {

            if (file.isDirectory()) {

                return true;
            }

            String name = file.getName().toLowerCase();

            int i = description.lastIndexOf("*.") + 2;

            String ext = description.substring(i, i + 3);

            return name.endsWith(ext);
        }

        public int getExtension() {

            if (description.equalsIgnoreCase(JPEG)) {
                return Exts.JPG;
            }

            if (description.equalsIgnoreCase(PNG)) {
                return Exts.PNG;
            }

            if (description.equalsIgnoreCase(GIF)) {
                return Exts.GIF;
            }

            if (description.equalsIgnoreCase(ANIMATED_GIF)) {
                return Exts.VIDEO;
            } else {
                return Exts.PRINT;
            }
        }

        //The description of this filter
        @Override
        public String getDescription() {

            return description;
        }

        public void setDescription(String description) {

            this.description = description;
        }
    }

    public class OptionPane extends JOptionPane {

        final int SAMPLING = 0;
        final int RATE = 1;
        final int REPEAT = 2;
        private int sampling = 30;
        private int rate = 2;
        private int repeat = 10;
        private JTextField savingText;
        private JTextField playbackText;
        private final int size;

        public OptionPane(int size) {
            this.size = size;

            ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
            toolTipManager.setInitialDelay(100);
            toolTipManager.setDismissDelay(5000);

            final JSlider slider1 = getSlider(SAMPLING);
            slider1.setBorder(BorderFactory.createTitledBorder("Select a Time Sampling Rate:"));
            slider1.setToolTipText("<HTML><BODY>Number of time steps along orbit skipped between GIF frames captured.<BR>"
                    + "Lower rate gives smoother motion but will take longer to save.</BODY></HTML>");

            slider1.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent event) {
                    if (!slider1.getModel().getValueIsAdjusting()) {

                        JSlider source = (JSlider) event.getSource();

                        setSampling(source.getValue());
                        playbackText.setText(calculatePlayback());
                        savingText.setText(calculateSaving());
                    }
                }
            });

            final JSlider slider2 = getSlider(RATE);
            slider2.setBorder(BorderFactory.createTitledBorder(
                    "Select Playback Rate of Animated GIF:"));
            slider2.setToolTipText("<HTML><BODY>Sets ... frames/second <BR>"
                    + "Higher rate gives a shorter total playback time.</BODY></HTML>");
            slider2.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent event) {
                    if (!slider2.getModel().getValueIsAdjusting()) {

                        JSlider source = (JSlider) event.getSource();

                        setRate(source.getValue());
                        playbackText.setText(calculatePlayback());
                    }
                }
            });

            JSlider slider3 = getSlider(REPEAT);
            slider3.setBorder(
                    BorderFactory.createTitledBorder("Number of loop repetitions:"));
            slider3.setToolTipText("<HTML><BODY>Number of times the set of GIF frames should be played.<br>0 means play indefinitely</BODY></HTML>");

            JPanel pane = new JPanel();
            pane.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;

            JLabel savingLabel = new JLabel("Approximate Total Saving Time is: ");
            savingLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(5, 5, 0, 0);

            pane.add(savingLabel, c);

            savingText = new JTextField(15);
            savingText.setHorizontalAlignment(JTextField.CENTER);
            savingText.setEditable(false);
            savingText.setHorizontalAlignment(JTextField.CENTER);
            savingText.setText(calculateSaving());
            savingText.setFont(new Font("Arial", Font.PLAIN, 11));
            c.gridx = 1;
            c.gridy = 0;
            c.insets = new Insets(5, 5, 5, 5);
            pane.add(savingText, c);

            JLabel playbackLabel = new JLabel(
                    "Approximate Playback Time Per Loop is: ");
            playbackLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
            c.gridx = 0;
            c.gridy = 1;
            c.insets = new Insets(5, 5, 0, 5);
            pane.add(playbackLabel, c);

            playbackText = new JTextField(15);
            playbackText.setEditable(false);
            playbackText.setHorizontalAlignment(JTextField.CENTER);
            playbackText.setText(calculatePlayback());
            playbackText.setFont(new Font("Dialog", Font.PLAIN, 11));
            c.gridx = 1;
            c.gridy = 1;
            c.insets = new Insets(5, 5, 5, 5);
            pane.add(playbackText, c);

            pane.setBorder(BorderFactory.createTitledBorder("Estimations:"));

            Object[] msg = {slider1, slider2, slider3, pane};
            setMessage(msg);

            setOptionType(
                    JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = createDialog(
                    this, "Select Values");
            dialog.setVisible(true);

            Object save = getValue();

            int i = save == null ? JOptionPane.CANCEL_OPTION
                    : ((Integer) save);

            if (i == JOptionPane.OK_OPTION) {

                setSampling(slider1.getValue());

                setRate(slider2.getValue());

                setRepeat(slider3.getValue());
            } else {

                setSampling(0);
                setRate(0);
                setRepeat(0);
            }
        }

        public String calculateSaving() {

            if (getSampling() != 0) {

                long totalSaving = savingTime[1] * (size / getSampling());
                double milliToSecs = Math.pow(10, -3);
                int totalSavingInSecs = (int) (totalSaving * milliToSecs);

                return totalSavingInSecs != 0 ? String.valueOf(totalSavingInSecs) + " seconds"
                        : "not available";
            } else {
                return "not valid";
            }
        }

        public String calculatePlayback() {

            if (getSampling() != 0 && getRate() != 0) {

                double framesCount = size / getSampling();

                double calculatedSeconds = framesCount / getRate();
                double doubleActualSeconds = calculatedSeconds + 1;
                int seconds = (int) Math.floor(doubleActualSeconds + 0.5d);

                return String.valueOf(seconds) + " seconds";
            } else {
                return "not valid";
            }
        }

        public JSlider getSlider(
                int value) {

            JSlider slider = new JSlider();
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setMinimum(0);

            switch (value) {

                case SAMPLING:
                    slider.setMajorTickSpacing(10);
                    slider.setMinorTickSpacing(5);
                    slider.setMaximum(50);
                    slider.setValue(sampling);
                    break;
                case RATE:
                    slider.setMajorTickSpacing(4);
                    slider.setMinorTickSpacing(2);
                    slider.setMaximum(20);
                    slider.setValue(rate);
                    break;
                case REPEAT:
                    slider.setMajorTickSpacing(4);
                    slider.setMinorTickSpacing(2);
                    slider.setMaximum(20);
                    slider.setValue(repeat);
                    break;
                default:
                    break;
            }
            return slider;
        }

        public void setSampling(int i) {

            sampling = i;
        }

        public int getSampling() {

            return sampling;
        }

        public void setRate(int i) {

            rate = i;
        }

        public int getRate() {

            return rate;
        }

        public void setRepeat(int i) {

            repeat = i;
        }

        public int getRepeat() {

            return repeat;
        }
    }

    private class ImageFileView extends FileView {

        ImageIcon jpgIcon = new ImageIcon(ImageFileView.class.getResource("/images/jpgIcon.gif"));
        ImageIcon gifIcon = new ImageIcon(ImageFileView.class.getResource("/images/gifIcon.gif"));
        ImageIcon pngIcon = new ImageIcon(ImageFileView.class.getResource("/images/pngIcon.png"));

        @Override
        public String getName(File f) {
            return null; //let the L&F FileView figure this out
        }

        @Override
        public String getDescription(File f) {
            return null; //let the L&F FileView figure this out
        }

        @Override
        public Boolean isTraversable(File f) {
            return null; //let the L&F FileView figure this out
        }

        public String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
        }

        @Override
        public String getTypeDescription(File f) {

            String extension = getExtension(f);
            String type = null;

            if (extension != null) {
                if (extension.equals(jpeg)
                        || extension.equals(jpg)) {
                    type = "JPEG Image";
                } else if (extension.equals(gif)) {
                    type = "GIF Image";
                } else if (extension.equals(png)) {
                    type = "PNG Image";
                }
            }
            return type;
        }

        @Override
        public Icon getIcon(File f) {
            String extension = getExtension(f);
            Icon icon = null;

            if (extension != null) {
                if (extension.equals(jpeg)
                        || extension.equals(jpg)) {
                    icon = jpgIcon;
                } else if (extension.equals(gif)) {
                    icon = gifIcon;
                } else if (extension.equals(png)) {
                    icon = pngIcon;
                }
            }
            return icon;

        }
    }

}
