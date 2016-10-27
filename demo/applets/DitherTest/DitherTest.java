/*
 * @(#)DitherTest.java	1.15 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)DitherTest.java	1.15 04/07/26
 */

import java.applet.Applet;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

public class DitherTest extends Applet implements Runnable {
    final private static int NOOP = 0;
    final private static int RED = 1;
    final private static int GREEN = 2;
    final private static int BLUE = 3;
    final private static int ALPHA = 4;
    final private static int SATURATION = 5;

    private Thread runner;

    private DitherControls XControls;
    private DitherControls YControls;
    private DitherCanvas canvas;

    public static void main(String args[]) {
        Frame f = new Frame("DitherTest");
        DitherTest ditherTest = new DitherTest();
        ditherTest.init();
        f.add("Center", ditherTest);
        f.pack();
        f.setVisible(true);
        ditherTest.start();
    }

    public void init() {
        String xspec = null, yspec = null;
        int xvals[] = new int[2];
        int yvals[] = new int[2];
        
        try {
            xspec = getParameter("xaxis");
            yspec = getParameter("yaxis");
        } catch (NullPointerException npe) {
            //only occurs if run as application
        }
        
        if (xspec == null) {
            xspec = "red";
        }
        if (yspec == null) {
            yspec = "blue";
        }
        int xmethod = colormethod(xspec, xvals);
        int ymethod = colormethod(yspec, yvals);

        setLayout(new BorderLayout());
        XControls = new DitherControls(this, xvals[0], xvals[1],
                                       xmethod, false);
        YControls = new DitherControls(this, yvals[0], yvals[1],
                                       ymethod, true);
        YControls.addRenderButton();
        add("North", XControls);
        add("South", YControls);
        add("Center", canvas = new DitherCanvas());
    }

    private int colormethod(String s, int vals[]) {
        int method = NOOP;
        if (s == null) {
            s = "";
        }
        String lower = s.toLowerCase();
        int len = 0;
        if (lower.startsWith("red")) {
            method = RED;
            lower = lower.substring(3);
        } else if (lower.startsWith("green")) {
            method = GREEN;
            lower = lower.substring(5);
        } else if (lower.startsWith("blue")) {
            method = BLUE;
            lower = lower.substring(4);
        } else if (lower.startsWith("alpha")) {
            method = ALPHA;
            lower = lower.substring(5);
        } else if (lower.startsWith("saturation")) {
            method = SATURATION;
            lower = lower.substring(10);
        }
        if (method == NOOP) {
            vals[0] = 0;
            vals[1] = 0;
            return method;
        }
        int begval = 0;
        int endval = 255;
        try {
            int dash = lower.indexOf('-');
            if (dash < 0) {
                endval = Integer.parseInt(lower);
            } else {
                begval = Integer.parseInt(lower.substring(0, dash));
                endval = Integer.parseInt(lower.substring(dash + 1));
            }
        } catch (NumberFormatException nfe) {
        }

        if (begval < 0) {
            begval = 0;
        } else if (begval > 255) {
            begval = 255;
        }
        
        if (endval < 0) {
            endval = 0;
        } else if (endval > 255) {
            endval = 255;
        }

        vals[0] = begval;
        vals[1] = endval;
        return method;
    }

    /**
     * Calculates and returns the image.  Halts the calculation and returns
     * null if the Applet is stopped during the calculation.
     */
    private Image calculateImage() {
        Thread me = Thread.currentThread();
        
        int width = canvas.getSize().width;
        int height = canvas.getSize().height;
        int xvals[] = new int[2];
        int yvals[] = new int[2];
        int xmethod = XControls.getParams(xvals);
        int ymethod = YControls.getParams(yvals);
        int pixels[] = new int[width * height];
        int c[] = new int[4];   //temporarily holds R,G,B,A information
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                c[0] = c[1] = c[2] = 0;
                c[3] = 255;
                if (xmethod < ymethod) {
                    applymethod(c, xmethod, i, width, xvals);
                    applymethod(c, ymethod, j, height, yvals);
                } else {
                    applymethod(c, ymethod, j, height, yvals);
                    applymethod(c, xmethod, i, width, xvals);
                }
                pixels[index++] = ((c[3] << 24) |
                                   (c[0] << 16) |
                                   (c[1] << 8) |
                                   c[2]);
            }

            // Poll once per row to see if we've been told to stop.
            if (runner != me) {
                return null;
            }
        }
        return createImage(new MemoryImageSource(width, height,
                            ColorModel.getRGBdefault(), pixels, 0, width));
    }

    private void applymethod(int c[], int method, int step, 
                             int total, int vals[]) {
        if (method == NOOP) {
            return;
        }
        int val = ((total < 2)
                   ? vals[0]
                   : vals[0] + ((vals[1] - vals[0]) * step / (total - 1)));
        switch (method) {
        case RED:
            c[0] = val;
            break;
        case GREEN:
            c[1] = val;
            break;
        case BLUE:
            c[2] = val;
            break;
        case ALPHA:
            c[3] = val;
            break;
        case SATURATION:
            int max = Math.max(Math.max(c[0], c[1]), c[2]);
            int min = max * (255 - val) / 255;
            if (c[0] == 0) {
                c[0] = min;
            }
            if (c[1] == 0) {
                c[1] = min;
            }
            if (c[2] == 0) {
                c[2] = min;
            }
            break;
        }
    }

    public void start() {
        runner = new Thread(this);
        runner.start();
    }

    public void run() {
        canvas.setImage(null);  // Wipe previous image
        Image img = calculateImage();
        if (img != null && runner == Thread.currentThread()) {
            canvas.setImage(img);
        }
    }

    public void stop() {
        runner = null;
    }

    public void destroy() {
        remove(XControls);
        remove(YControls);
        remove(canvas);
    }

    public String getAppletInfo() {
        return "An interactive demonstration of dithering.";
    }

    public String[][] getParameterInfo() {
        String[][] info = {
            {"xaxis", "{RED, GREEN, BLUE, ALPHA, SATURATION}",
             "The color of the Y axis.  Default is RED."},
            {"yaxis", "{RED, GREEN, BLUE, ALPHA, SATURATION}", 
             "The color of the X axis.  Default is BLUE."}
        };
        return info;
    }
}

class DitherCanvas extends Canvas {
    private Image img;
    private static String calcString = "Calculating...";

    public void paint(Graphics g) {
        int w = getSize().width;
        int h = getSize().height;
        if (img == null) {
            super.paint(g);
            g.setColor(Color.black);
            FontMetrics fm = g.getFontMetrics();
            int x = (w - fm.stringWidth(calcString)) / 2;
            int y = h / 2;
            g.drawString(calcString, x, y);
        } else {
            g.drawImage(img, 0, 0, w, h, this);
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    public Dimension getMinimumSize() {
        return new Dimension(20, 20);
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }

    public Image getImage() {
        return img;
    }

    public void setImage(Image img) {
        this.img = img;
        repaint();
    }
}

class DitherControls extends Panel implements ActionListener {
    private CardinalTextField start;
    private CardinalTextField end;
    private Button button;
    private Choice choice;
    private DitherTest applet;

    private static LayoutManager dcLayout = new FlowLayout(FlowLayout.CENTER,
                                                           10, 5);

    public DitherControls(DitherTest app, int s, int e, int type,
                          boolean vertical) {
        applet = app;
        setLayout(dcLayout);
        add(new Label(vertical ? "Vertical" : "Horizontal"));
        add(choice = new Choice());
        choice.addItem("Noop");
        choice.addItem("Red");
        choice.addItem("Green");
        choice.addItem("Blue");
        choice.addItem("Alpha");
        choice.addItem("Saturation");
        choice.select(type);
        add(start = new CardinalTextField(Integer.toString(s), 4));
        add(end = new CardinalTextField(Integer.toString(e), 4));
    }

    /* puts on the button */
    public void addRenderButton() {
        add(button = new Button("New Image"));
        button.addActionListener(this);
    }

    /* retrieves data from the user input fields */
    public int getParams(int vals[]) {
        try {
            vals[0] = scale(Integer.parseInt(start.getText()));
        } catch (NumberFormatException nfe) {
            vals[0] = 0;
        }
        try {
            vals[1] = scale(Integer.parseInt(end.getText()));
        } catch (NumberFormatException nfe) {
            vals[1] = 255;
        }
        return choice.getSelectedIndex();
    }
    
    /* fits the number between 0 and 255 inclusive */
    private int scale(int number) {
        if (number < 0) {
            number = 0;
        } else if (number > 255) {
            number = 255;
        }
        return number;
    }

    /* called when user clicks the button */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            applet.start();
        }
    }
}

class CardinalTextField extends TextField {

    String oldText = null;

    public CardinalTextField(String text, int columns) { 
        super(text, columns);
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.TEXT_EVENT_MASK);
        oldText = getText();
    }

    // Consume non-digit KeyTyped events
    // Note that processTextEvent kind of eliminates the need for this 
    // function, but this is neater, since ideally, it would prevent 
    // the text from appearing at all.  Sigh.  See bugid 4100317/4114565. 
    //
    protected void processEvent(AWTEvent evt) {
        int id = evt.getID();
        if (id != KeyEvent.KEY_TYPED) {
            super.processEvent(evt);
            return;
        }

        KeyEvent kevt = (KeyEvent) evt;
        char c = kevt.getKeyChar();

        // Digits, backspace, and delete are okay 
        // Note that the minus sign is not allowed (neither is decimal)
        if (Character.isDigit(c) || (c == '\b') || (c == '\u007f')) {
            super.processEvent(evt);
            return;
        }

        Toolkit.getDefaultToolkit().beep();
        kevt.consume();
    }

    // Should consume TextEvents for non-integer Strings
    // Store away the text in the tf for every TextEvent
    // so we can revert to it on a TextEvent (paste, or 
    // legal key in the wrong location) with bad text
    //
    // Note: it would be easy to extend this to an eight-bit 
    // TextField (range 0-255), but I'll leave it as-is.  
    //
    protected void processTextEvent(TextEvent te) { 
        // The empty string is okay, too 
        String newText = getText();
        if (newText.equals("") || textIsCardinal(newText)) {
            oldText = newText;
            super.processTextEvent(te);
            return;
        }

        Toolkit.getDefaultToolkit().beep();
        setText(oldText);
    }

    // Returns true for Cardinal (non-negative) numbers
    // Note that the empty string is not allowed
    private boolean textIsCardinal(String textToCheck) {
        int value = -1;

        try { 
            value = Integer.parseInt(textToCheck, 10); 
            return (value >= 0);
        } catch (NumberFormatException nfe) { 
            return false; 
        }
    }
}

