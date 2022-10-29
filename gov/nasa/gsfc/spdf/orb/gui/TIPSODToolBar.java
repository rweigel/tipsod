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
 * $Id: TIPSODToolBar.java,v 1.31 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on March 12, 2002, 1:10 PM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.content.behaviors.Animation;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The TIPSODToolBar class implements the toolBar (animation Bar) for the
 * application.
 *
 * @author rchimiak
 * @version $Revision: 1.31 $
 */
public class TIPSODToolBar extends JToolBar {

    private JFormattedTextField time = null;
    private final JSpinner speedSpinner;

    /**
     * Creates new TIPSODToolBar.
     */
    public TIPSODToolBar() {

        super();
        this.setRollover(true);
        setFloatable(true);
        //------------------------------- play -----------------------------

        final Icon playIcon = new ImageIcon(TIPSODToolBar.class.getResource("/images/Play24.gif"));
        final Icon pauseIcon = new ImageIcon(TIPSODToolBar.class.getResource("/images/Pause24.gif"));

        final JButton playButton = new JButton(playIcon);

        playButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (ContentBranch.getSatBranch() != null) {

                    Animation animation = ContentBranch.getSatBranch().getAnimation();
                    if (animation != null) {

                        if (animation.isPaused() || animation.isStopped()) {
                            playButton.setIcon(pauseIcon);
                            animation.play();
                        } else if (animation.isPlayed()) {
                            playButton.setIcon(playIcon);
                            animation.pause();
                        }
                    }

                }
            }
        });
        add(playButton);

        //------------------------------- pause -----------------------------
        //------------------------------- stop -----------------------------
        JButton stopButton = new JButton(
                new ImageIcon(TIPSODToolBar.class.getResource("/images/Stop24.gif")));
        stopButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (ContentBranch.getSatBranch() != null
                        && ContentBranch.getSatBranch().getAnimation() != null) {
                    playButton.setIcon(playIcon);
                    ContentBranch.getSatBranch().getAnimation().stop();
                }
            }
        });
        add(stopButton);

        addSeparator();
        add(OrbitViewer.getSlider());

        addSeparator();

        add(new JLabel("time: "));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        df.setTimeZone(Util.UTC_TIME_ZONE);
        time = new JFormattedTextField(df);
        time.setBorder(BorderFactory.createEtchedBorder(Color.darkGray, Color.white));
        time.setColumns(12);
        time.setMaximumSize(new Dimension(time.getPreferredSize().width, 20));

        time.setEditable(false);
        time.setFocusable(false);
        time.setBackground(new Color(255, 255, 255));
        add(time);
        addSeparator();

        add(new JLabel("speed: "));
        SpinnerModel model = new SpinnerNumberModel(10, 1, 40, 1);
        speedSpinner = new JSpinner(model);

        speedSpinner.setMaximumSize(new Dimension(15, 20));
        add(speedSpinner);
        addSeparator();

        speedSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                if (ContentBranch.getSatBranch() != null
                        && ContentBranch.getSatBranch().getAnimation() != null) {

                    ContentBranch.getSatBranch().getAnimation().setAlpha();
                }
            }
        });
    }

    /**
     * Returns the time field displaying the time corresponding to the
     * spacecraft location on the orbit plot
     *
     * @return the time field
     */
    public JFormattedTextField getTime() {

        return time;
    }

    /**
     * Sets the time formattedText field to an empty string
     */
    public void resetTime() {
        time.setText("");
    }

    /**
     * Returns the Spinner displaying the speed at which the animation
     * performs(an integer between 1 and 10).
     *
     * @return the speed spinner widget
     */
    public JSpinner getSpeedSpinner() {
        return speedSpinner;
    }

    /**
     * Implements the help text being displayed when selecting one of the
     * toolbar icon
     */
    class JToolTipButton extends JButton {

        public JToolTipButton(Icon icon, String toolTipText) {

            super(icon);
            setToolTipText(toolTipText);
        }

        @Override
        public Point getToolTipLocation(MouseEvent e) {

            Dimension size = getSize();
            return new Point(size.width / 2, size.height / 2);
        }
    }
}
