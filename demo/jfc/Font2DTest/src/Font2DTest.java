/*
 * @(#)Font2DTest.java	1.25 04/07/26
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
 * @(#)Font2DTest.java	1.25 04/07/26
 */

import java.awt.BorderLayout;
import java.awt.CheckboxGroup;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.StringTokenizer;

import javax.swing.*;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

/**
 * Font2DTest.java
 *
 * @version @(#)Font2DTest.java	1.2 00/08/22
 * @author Shinsuke Fukuda
 * @author Ankit Patel [Conversion to Swing - 01/07/30] 
 */

/// Main Font2DTest Class

public final class Font2DTest extends JPanel implements ActionListener, ItemListener {

    /// JFrame that will contain Font2DTest
    private final JFrame parent;
    /// FontPanel class that will contain all graphical output
    private final FontPanel fp;
    /// RangeMenu class that contains info about the unicode ranges
    private final RangeMenu rm;

    /// Other menus to set parameters for text drawing
    private final ChoiceV2 fontMenu;
    private final JTextField sizeField;
    private final ChoiceV2 styleMenu;
    private final ChoiceV2 textMenu;
    private int currentTextChoice = 0;
    private final ChoiceV2 transformMenu;
    private final ChoiceV2 transformMenuG2;    
    private final ChoiceV2 methodsMenu;
    private final JCheckBox useAntialiasCB;
    private final JCheckBox useFractionalCB;

    /// CheckboxMenuItems
    private CheckboxMenuItemV2 displayGridCBMI;
    private CheckboxMenuItemV2 force16ColsCBMI;
    private CheckboxMenuItemV2 showFontInfoCBMI;

    /// JDialog boxes
    private JDialog userTextDialog;
    private JTextArea userTextArea;
    private JDialog printDialog;
    private JDialog fontInfoDialog;
    private LabelV2 fontInfos[] = new LabelV2[2];
    private JFileChooser filePromptDialog = null;

    private ButtonGroup printCBGroup;
    private JRadioButton printModeCBs[] = new JRadioButton[3];

    /// Status bar
    private final LabelV2 statusBar;

    /// Universal font used for labels
    private final Font labelFont = new Font( "dialog", Font.BOLD, 12 );

  /// Text filename
  private String tFileName;

    /// Initialize GUI variables and its layouts
    public Font2DTest( JFrame f, boolean isApplet ) {
        parent = f;
        
        rm = new RangeMenu( this, parent );
        fp = new FontPanel( this, parent );
        statusBar = new LabelV2("");

        fontMenu = new ChoiceV2( this );
        sizeField = new JTextField( "12", 3 );
        styleMenu = new ChoiceV2( this );
        textMenu = new ChoiceV2( ); // listener added later
        transformMenu = new ChoiceV2( this );
        transformMenuG2 = new ChoiceV2( this );        
        methodsMenu = new ChoiceV2( this );

        useAntialiasCB = new JCheckBox( "Antialiasing", false );
        useFractionalCB = new JCheckBox(" Fractional Metrics", false );
        useAntialiasCB.setFont( labelFont );
        useFractionalCB.setFont( labelFont );
        sizeField.addActionListener( this );
        useAntialiasCB.addItemListener( this );
        useFractionalCB.addItemListener( this );

        setupPanel();
        setupMenu( isApplet );
        setupDialog( isApplet );
    }

    /// Set up the main interface panel
    private void setupPanel() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = new Insets( 2, 0, 2, 2 );
        this.setLayout( gbl );

        addLabeledComponentToGBL( "Font:", fontMenu, gbl, gbc, this );
        addLabeledComponentToGBL( "Size:", sizeField, gbl, gbc, this );
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        addLabeledComponentToGBL( "Transform:", transformMenu, gbl, gbc, this );
        gbc.gridwidth = 1;

        addLabeledComponentToGBL( "Range:", rm, gbl, gbc, this );
        addLabeledComponentToGBL( "Style:", styleMenu, gbl, gbc, this );
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        addLabeledComponentToGBL( "Text to use:", textMenu, gbl, gbc, this );
        gbc.gridwidth = 1;

	/// ABP
        addLabeledComponentToGBL( "Method:", methodsMenu, gbl, gbc, this );
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        addLabeledComponentToGBL( "Graphics2D Transform:", transformMenuG2, gbl, gbc, this );        
        gbc.gridwidth = 1;
        
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbl.setConstraints( useAntialiasCB, gbc );
        this.add( useAntialiasCB );
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints( useFractionalCB, gbc );
        this.add( useFractionalCB );        
        
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets( 2, 0, 0, 2 );
        gbc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints( fp, gbc );
        this.add( fp );

        gbc.weighty = 0;
        gbc.insets = new Insets( 0, 2, 0, 0 );
        gbl.setConstraints( statusBar, gbc );
        this.add( statusBar );
    }

    /// Adds a component to a container with a label to its left in GridBagLayout
    private void addLabeledComponentToGBL( String name,
                                           JComponent c,
                                           GridBagLayout gbl,
                                           GridBagConstraints gbc,
                                           Container target ) {
        LabelV2 l = new LabelV2( name );
        GridBagConstraints gbcLabel = (GridBagConstraints) gbc.clone();
        gbcLabel.insets = new Insets( 2, 2, 2, 0 );
        gbcLabel.gridwidth = 1;
        gbcLabel.weightx = 0;

        if ( c == null )
          c = new JLabel( "" );

        gbl.setConstraints( l, gbcLabel );
        target.add( l );
        gbl.setConstraints( c, gbc );
        target.add( c );
    }

    /// Sets up menu entries
    private void setupMenu( boolean isApplet ) {
        JMenu fileMenu = new JMenu( "File" );
        JMenu optionMenu = new JMenu( "Option" );

        fileMenu.add( new MenuItemV2( "Save Selected Options...", this ));
        fileMenu.add( new MenuItemV2( "Load Options...", this ));
        fileMenu.addSeparator();
        fileMenu.add( new MenuItemV2( "Save as JPEG...", this ));
        fileMenu.add( new MenuItemV2( "Load JPEG File to Compare...", this ));
        fileMenu.add( new MenuItemV2( "Page Setup...", this ));
        fileMenu.add( new MenuItemV2( "Print...", this ));
        fileMenu.addSeparator();
        if ( !isApplet )
          fileMenu.add( new MenuItemV2( "Exit", this ));
        else
          fileMenu.add( new MenuItemV2( "Close", this ));

        displayGridCBMI = new CheckboxMenuItemV2( "Display Grid", true, this );
        force16ColsCBMI = new CheckboxMenuItemV2( "Force 16 Columns", false, this );
        showFontInfoCBMI = new CheckboxMenuItemV2( "Display Font Info", false, this );
        optionMenu.add( displayGridCBMI );
        optionMenu.add( force16ColsCBMI );
        optionMenu.add( showFontInfoCBMI );

        JMenuBar mb = parent.getJMenuBar();
        if ( mb == null )
          mb = new JMenuBar();
        mb.add( fileMenu );
        mb.add( optionMenu );

        parent.setJMenuBar( mb );

        String fontList[] =
          GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for ( int i = 0; i < fontList.length; i++ )
          fontMenu.addItem( fontList[i] );
        fontMenu.setSelectedItem( "Dialog" );

        styleMenu.addItem( "Plain" );
        styleMenu.addItem( "Bold" );
        styleMenu.addItem( "Italic" );
        styleMenu.addItem( "Bold Italic" );

        transformMenu.addItem( "None" );
        transformMenu.addItem( "Scale" );
        transformMenu.addItem( "Shear" );
        transformMenu.addItem( "Rotate" );

        transformMenuG2.addItem( "None" );
        transformMenuG2.addItem( "Scale" );
        transformMenuG2.addItem( "Shear" );
        transformMenuG2.addItem( "Rotate" );

        methodsMenu.addItem( "drawString" );
        methodsMenu.addItem( "drawChars" );
        methodsMenu.addItem( "drawBytes" );
        methodsMenu.addItem( "drawGlyphVector" );
        methodsMenu.addItem( "TextLayout.draw" );
        methodsMenu.addItem( "GlyphVector.getOutline + draw" );
        methodsMenu.addItem( "TextLayout.getOutline + draw" );

        textMenu.addItem( "Unicode Range" );
        textMenu.addItem( "All Glyphs" );
        textMenu.addItem( "User Text" );
        textMenu.addItem( "Text File" );
        textMenu.addActionListener ( this ); // listener added later so unneeded events not thrown
    }

    /// Sets up the all dialogs used in Font2DTest...
    private void setupDialog( boolean isApplet ) {
        if (!isApplet) 
        	filePromptDialog = new JFileChooser( );
        else
        	filePromptDialog = null;

        /// Prepare user text dialog...
        userTextDialog = new JDialog( parent, "User Text", false );
        JPanel dialogTopPanel = new JPanel();
        JPanel dialogBottomPanel = new JPanel();
        LabelV2 message1 = new LabelV2( "Enter text below and then press update" );
        LabelV2 message2 = new LabelV2( "(Unicode char can be denoted by \\uXXXX)" );
        LabelV2 message3 = new LabelV2( "(Supplementary chars can be denoted by \\UXXXXXX)" );
        userTextArea = new JTextArea( "Java2D!" );
        ButtonV2 bUpdate = new ButtonV2( "Update", this );
        userTextArea.setFont( new Font( "dialog", Font.PLAIN, 12 ));
        dialogTopPanel.setLayout( new GridLayout( 3, 1 ));
        dialogTopPanel.add( message1 );
        dialogTopPanel.add( message2 );
        dialogTopPanel.add( message3 );
        dialogBottomPanel.add( bUpdate );
        //ABP
        JScrollPane userTextAreaSP = new JScrollPane(userTextArea);
        userTextAreaSP.setPreferredSize(new Dimension(300, 100));

        userTextDialog.getContentPane().setLayout( new BorderLayout() );
        userTextDialog.getContentPane().add( "North", dialogTopPanel );
        userTextDialog.getContentPane().add( "Center", userTextAreaSP );
        userTextDialog.getContentPane().add( "South", dialogBottomPanel );
        userTextDialog.pack();
	userTextDialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                userTextDialog.hide();
            }
        });

        /// Prepare printing dialog...
        printCBGroup = new ButtonGroup();
        printModeCBs[ fp.ONE_PAGE ] =
          new JRadioButton( "Print one page from currently displayed character/line",
                         true );
        printModeCBs[ fp.CUR_RANGE ] =
          new JRadioButton( "Print all characters in currently selected range",
                         false );
        printModeCBs[ fp.ALL_TEXT ] =
          new JRadioButton( "Print all lines of text",
                         false );
        LabelV2 l =
          new LabelV2( "Note: Page range in native \"Print\" dialog will not affect the result" );
        JPanel buttonPanel = new JPanel();
        printModeCBs[ fp.ALL_TEXT ].setEnabled( false );
        buttonPanel.add( new ButtonV2( "Print", this ));
        buttonPanel.add( new ButtonV2( "Cancel", this ));

        printDialog = new JDialog( parent, "Print...", true );
        printDialog.setResizable( false );
	printDialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                printDialog.hide();
            }
        });
        printDialog.getContentPane().setLayout( new GridLayout( printModeCBs.length + 2, 1 ));
        printDialog.getContentPane().add( l );
        for ( int i = 0; i < printModeCBs.length; i++ ) {
            printModeCBs[i].setFont( labelFont );
            printCBGroup.add( printModeCBs[i] );
            printDialog.getContentPane().add( printModeCBs[i] );
        }
        printDialog.getContentPane().add( buttonPanel );
        printDialog.pack();

        /// Prepare font information dialog...
        fontInfoDialog = new JDialog( parent, "Font info", false );
        fontInfoDialog.setResizable( false );
        fontInfoDialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                fontInfoDialog.hide();
                showFontInfoCBMI.setState( false );
            }
        });
        JPanel fontInfoPanel = new JPanel();
        fontInfoPanel.setLayout( new GridLayout( fontInfos.length, 1 ));
        for ( int i = 0; i < fontInfos.length; i++ ) {
            fontInfos[i] = new LabelV2("");
            fontInfoPanel.add( fontInfos[i] );
        }
        fontInfoDialog.getContentPane().add( fontInfoPanel );

        /// Move the location of the dialog...
        userTextDialog.setLocation( 200, 300 );
        fontInfoDialog.setLocation( 0, 400 );
    }

    /// RangeMenu object signals using this function
    /// when Unicode range has been changed and text needs to be redrawn
    public void fireRangeChanged() {
        int range[] = rm.getSelectedRange();
        fp.setTextToDraw( fp.RANGE_TEXT, range, null, null );
        if ( showFontInfoCBMI.getState() )
          fireUpdateFontInfo();
    }

    /// Changes the message on the status bar
    public void fireChangeStatus( String message, boolean error ) {
        /// If this is not ran as an applet, use own status bar,
        /// Otherwise, use the appletviewer/browser's status bar
        statusBar.setText( message );
        if ( error )
          fp.showingError = true;
        else
          fp.showingError = false;
    }

    /// Updates the information about the selected font
    public void fireUpdateFontInfo() {
        if ( showFontInfoCBMI.getState() ) {
            String infos[] = fp.getFontInfo();
            for ( int i = 0; i < fontInfos.length; i++ )
              fontInfos[i].setText( infos[i] );
            fontInfoDialog.pack();
        }
    }

    /// Displays a file load/save dialog and returns the specified file
    private String promptFile( boolean isSave, String initFileName ) {
	int retVal;
	String str;
	
	/// ABP
	if ( filePromptDialog == null)
		return null;
	
        if ( isSave ) {
            filePromptDialog.setDialogType( JFileChooser.SAVE_DIALOG );
            filePromptDialog.setDialogTitle( "Save..." );
            str = "Save";


        }
        else {
            filePromptDialog.setDialogType( JFileChooser.OPEN_DIALOG );
            filePromptDialog.setDialogTitle( "Load..." );
            str = "Load";
        }
        
	if (initFileName != null)
		filePromptDialog.setSelectedFile( new File( initFileName ) );        
	retVal = filePromptDialog.showDialog( this, str );

        if ( retVal == JFileChooser.APPROVE_OPTION ) {
	        File file = filePromptDialog.getSelectedFile();
	        String fileName = file.getAbsolutePath();
        	if ( fileName != null ) {
		        return fileName;
		}
	}
	
        return null;
    }

    /// Converts user text into arrays of String, delimited at newline character
    /// Also replaces any valid escape sequence with appropriate unicode character
    /// Support \\UXXXXXX notation for surrogates
    private String[] parseUserText( String orig ) {
        int length = orig.length();
        StringTokenizer perLine = new StringTokenizer( orig, "\n" );
        String textLines[] = new String[ perLine.countTokens() ];
        int lineNumber = 0;

        while ( perLine.hasMoreElements() ) {
            StringBuffer converted = new StringBuffer();
            String oneLine = perLine.nextToken();
            int lineLength = oneLine.length();
            int prevEscapeEnd = 0;
            int nextEscape = -1;
            do {
                int nextBMPEscape = oneLine.indexOf( "\\u", prevEscapeEnd );
                int nextSupEscape = oneLine.indexOf( "\\U", prevEscapeEnd );
                nextEscape = (nextBMPEscape < 0)
                    ? ((nextSupEscape < 0)
                       ? -1
                       : nextSupEscape)
                    : ((nextSupEscape < 0)
                       ? nextBMPEscape
                       : Math.min(nextBMPEscape, nextSupEscape));

                if ( nextEscape != -1 ) {
                    if ( prevEscapeEnd < nextEscape )
                        converted.append( oneLine.substring( prevEscapeEnd, nextEscape ));
                    
                    prevEscapeEnd = nextEscape + (nextEscape == nextBMPEscape ? 6 : 8);
                    try {
                        String hex = oneLine.substring( nextEscape + 2, prevEscapeEnd );
                        if (nextEscape == nextBMPEscape) {
                            converted.append( (char) Integer.parseInt( hex, 16 ));
                        } else {
                            converted.append( new String( Character.toChars( Integer.parseInt( hex, 16 ))));
                        }
                    }
                    catch ( Exception e ) {
                        int copyLimit = Math.min(lineLength, prevEscapeEnd);
                        converted.append( oneLine.substring( nextEscape, copyLimit ));
                    }
                }
            } while (nextEscape != -1);
            if ( prevEscapeEnd < lineLength )
              converted.append( oneLine.substring( prevEscapeEnd, lineLength ));
            textLines[ lineNumber++ ] = converted.toString();
        }
        return textLines;
    }

    /// Reads the text from specified file, detecting UTF-16 encoding
    /// Then breaks the text into String array, delimited at every line break
    private void readTextFile( String fileName ) {
        try {
            String fileText, textLines[];
            BufferedInputStream bis =
              new BufferedInputStream( new FileInputStream( fileName ));
            int numBytes = bis.available();
            if (numBytes == 0) {
                throw new Exception("Text file " + fileName + " is empty");
            }
            byte byteData[] = new byte[ numBytes ];
            bis.read( byteData, 0, numBytes );
            bis.close();

            /// If byte mark is found, then use UTF-16 encoding to convert bytes...
            if (numBytes >= 2 &&
                (( byteData[0] == (byte) 0xFF && byteData[1] == (byte) 0xFE ) ||
                 ( byteData[0] == (byte) 0xFE && byteData[1] == (byte) 0xFF )))
              fileText = new String( byteData, "UTF-16" );
            /// Otherwise, use system default encoding
            else
              fileText = new String( byteData );

            int length = fileText.length();
            StringTokenizer perLine = new StringTokenizer( fileText, "\n" );
            /// Determine "Return Char" used in this file
            /// This simply finds first occurrence of CR, CR+LF or LF...
            for ( int i = 0; i < length; i++ ) {
                char iTh = fileText.charAt( i );
                if ( iTh == '\r' ) {
                    if ( i < length - 1 && fileText.charAt( i + 1 ) == '\n' )
                      perLine = new StringTokenizer( fileText, "\r\n" );
                    else
                      perLine = new StringTokenizer( fileText, "\r" );
                    break;
                }
                else if ( iTh == '\n' )
                  /// Use the one already created
                  break;
            }
            int lineNumber = 0, numLines = perLine.countTokens();
            textLines = new String[ numLines ];

            while ( perLine.hasMoreElements() ) {
                String oneLine = perLine.nextToken();
                if ( oneLine == null )
                  /// To make LineBreakMeasurer to return a valid TextLayout
                  /// on an empty line, simply feed it a space char...
                  oneLine = " ";
                textLines[ lineNumber++ ] = oneLine;
            }
            fp.setTextToDraw( fp.FILE_TEXT, null, null, textLines );
            rm.setEnabled( false );
            methodsMenu.setEnabled( false );
        }
        catch ( Exception ex ) {
            fireChangeStatus( "ERROR: Failed to Read Text File; See Stack Trace", true );
            ex.printStackTrace();
        }
    }

    /// Returns a String storing current configuration
    private void writeCurrentOptions( String fileName ) {
        try {
            String curOptions = fp.getCurrentOptions();
            BufferedOutputStream bos =
              new BufferedOutputStream( new FileOutputStream( fileName ));
            /// Prepend title and the option that is only obtainable here
            int range[] = rm.getSelectedRange();
            String completeOptions =
              ( "Font2DTest Option File\n" + 
                displayGridCBMI.getState() + "\n" +
                force16ColsCBMI.getState() + "\n" +
                showFontInfoCBMI.getState() + "\n" +
                rm.getSelectedItem() + "\n" +
                range[0] + "\n" + range[1] + "\n" + curOptions + tFileName);
            byte toBeWritten[] = completeOptions.getBytes( "UTF-16" );
            bos.write( toBeWritten, 0, toBeWritten.length );
            bos.close();
        }
        catch ( Exception ex ) {
            fireChangeStatus( "ERROR: Failed to Save Options File; See Stack Trace", true );
            ex.printStackTrace();
        }
    }

    /// Updates GUI visibility/status after some parameters have changed
    private void updateGUI() {
        int selectedText = textMenu.getSelectedIndex();

        /// Set the visibility of User Text dialog
        if ( selectedText == fp.USER_TEXT )
          userTextDialog.show();
        else
          userTextDialog.hide();
        /// Change the visibility/status/availability of Print JDialog buttons
        printModeCBs[ fp.ONE_PAGE ].setSelected( true );
        if ( selectedText == fp.FILE_TEXT || selectedText == fp.USER_TEXT ) {
	    /// ABP
	    /// update methodsMenu to show that TextLayout.draw is being used
	    /// when we are in FILE_TEXT mode
	    if ( selectedText == fp.FILE_TEXT )
	        methodsMenu.setSelectedItem("TextLayout.draw");         
            methodsMenu.setEnabled( selectedText == fp.USER_TEXT );
            printModeCBs[ fp.CUR_RANGE ].setEnabled( false );
            printModeCBs[ fp.ALL_TEXT ].setEnabled( true );
        }
        else {
	    /// ABP
	    /// update methodsMenu to show that drawGlyph is being used
	    /// when we are in ALL_GLYPHS mode
	    if ( selectedText == fp.ALL_GLYPHS )
	        methodsMenu.setSelectedItem("drawGlyphVector"); 
            methodsMenu.setEnabled( selectedText == fp.RANGE_TEXT );
            printModeCBs[ fp.CUR_RANGE ].setEnabled( true );
            printModeCBs[ fp.ALL_TEXT ].setEnabled( false );
        }
        /// Modify RangeMenu and fontInfo label availabilty
        if ( selectedText == fp.RANGE_TEXT ) {
            fontInfos[1].setVisible( true );
            rm.setEnabled( true );
        }
        else {
            fontInfos[1].setVisible( false );
            rm.setEnabled( false );
        }
    }

    /// Loads saved options and applies them
    private void loadOptions( String fileName ) {
        try {
            BufferedInputStream bis =
              new BufferedInputStream( new FileInputStream( fileName ));
            int numBytes = bis.available();
            byte byteData[] = new byte[ numBytes ];
            bis.read( byteData, 0, numBytes );
            bis.close();
            if ( numBytes < 2 || 
		(byteData[0] != (byte) 0xFE || byteData[1] != (byte) 0xFF) )
              throw new Exception( "Not a Font2DTest options file" );
            
            String options = new String( byteData, "UTF-16" );
            StringTokenizer perLine = new StringTokenizer( options, "\n" );
            String title = perLine.nextToken();
            if ( !title.equals( "Font2DTest Option File" ))
              throw new Exception( "Not a Font2DTest options file" );

            /// Parse all options
            boolean displayGridOpt = Boolean.parseBoolean( perLine.nextToken() );
            boolean force16ColsOpt = Boolean.parseBoolean( perLine.nextToken() );
            boolean showFontInfoOpt = Boolean.parseBoolean( perLine.nextToken() );
            String rangeNameOpt = perLine.nextToken();
            int rangeStartOpt = Integer.parseInt( perLine.nextToken() );
            int rangeEndOpt = Integer.parseInt( perLine.nextToken() );
            String fontNameOpt = perLine.nextToken();
            int fontSizeOpt = Integer.parseInt( perLine.nextToken() );
            int fontStyleOpt = Integer.parseInt( perLine.nextToken() );
            int fontTransformOpt = Integer.parseInt( perLine.nextToken() );
            int g2TransformOpt = Integer.parseInt( perLine.nextToken() );
            int textToUseOpt = Integer.parseInt( perLine.nextToken() );
            int drawMethodOpt = Integer.parseInt( perLine.nextToken() );
            boolean useAntialiasOpt = Boolean.parseBoolean( perLine.nextToken() );
            boolean useFractionalOpt = Boolean.parseBoolean( perLine.nextToken() );
            String userTextOpt[] = { "Java2D!" }, dialogEntry = "Java2D!";	   
            if (textToUseOpt == fp.USER_TEXT )  {
                int numLines = perLine.countTokens(), lineNumber = 0;
                if ( numLines != 0 ) {
                    userTextOpt = new String[ numLines ];
                    dialogEntry = "";
                    for ( ; perLine.hasMoreElements(); lineNumber++ ) {
                        userTextOpt[ lineNumber ] = perLine.nextToken();
                        dialogEntry += userTextOpt[ lineNumber ] + "\n";
                    }
                }
	    }			

            /// Reset GUIs
            displayGridCBMI.setState( displayGridOpt );
            force16ColsCBMI.setState( force16ColsOpt );
            showFontInfoCBMI.setState( showFontInfoOpt );
            rm.setSelectedRange( rangeNameOpt, rangeStartOpt, rangeEndOpt );
            fontMenu.setSelectedItem( fontNameOpt );
            sizeField.setText( String.valueOf( fontSizeOpt ));
            styleMenu.setSelectedIndex( fontStyleOpt );
            transformMenu.setSelectedIndex( fontTransformOpt );
            transformMenuG2.setSelectedIndex( g2TransformOpt );
            textMenu.setSelectedIndex( textToUseOpt );
            methodsMenu.setSelectedIndex( drawMethodOpt );
            useAntialiasCB.setSelected( useAntialiasOpt );
            useFractionalCB.setSelected( useFractionalOpt );	  

            userTextArea.setText( dialogEntry );
            updateGUI();

	    if ( textToUseOpt == fp.FILE_TEXT ) {
	      tFileName = perLine.nextToken();
	      readTextFile(tFileName );
            }

            /// Reset option variables and repaint
            fp.loadOptions( displayGridOpt, force16ColsOpt, rangeStartOpt, rangeEndOpt,
                            fontNameOpt, fontSizeOpt, fontStyleOpt, fontTransformOpt, g2TransformOpt,
                            textToUseOpt, drawMethodOpt, useAntialiasOpt,
                            useFractionalOpt, userTextOpt );
            if ( showFontInfoOpt ) {
                fireUpdateFontInfo();
                fontInfoDialog.show();
            }
            else
              fontInfoDialog.hide();
        }
        catch ( Exception ex ) {
            fireChangeStatus( "ERROR: Failed to Load Options File; See Stack Trace", true );
            ex.printStackTrace();
        }
    }

    /// Loads a previously saved image 
    private void loadComparisonJPEG( String fileName ) {
        try {
            BufferedInputStream bis =
              new BufferedInputStream( new FileInputStream( fileName ));
            JPEGImageDecoder jid = JPEGCodec.createJPEGDecoder( bis );
            BufferedImage image = jid.decodeAsBufferedImage();
            bis.close();

            JFrame f = new JFrame( "Comparison JPEG" );
            ImagePanel ip = new ImagePanel( image );
            f.setResizable( false );
            f.getContentPane().add( ip );
            f.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent e ) {
                    ( (JFrame) e.getSource() ).dispose();
                }
            });
            f.pack();
            f.show();
        }
        catch ( Exception ex ) {
            fireChangeStatus( "ERROR: Failed to Load JPEG File; See Stack Trace", true );
            ex.printStackTrace();
        }
    }

    /// Interface functions...

    /// ActionListener interface function
    /// Responds to JMenuItem, JTextField and JButton actions
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();

        if ( source instanceof JMenuItem ) {
            JMenuItem mi = (JMenuItem) source;
            String itemName = mi.getLabel();

            if ( itemName.equals( "Save Selected Options..." )) {
                String fileName = promptFile( true, "options.txt" );
                if ( fileName != null )
                  writeCurrentOptions( fileName );
            }
            else if ( itemName.equals( "Load Options..." )) {
                String fileName = promptFile( false, "options.txt" );
                if ( fileName != null )
                  loadOptions( fileName );
            }
            else if ( itemName.equals( "Save as JPEG..." )) {
                String fileName = promptFile( true, fontMenu.getSelectedItem() + ".jpeg" );
                if ( fileName != null )
                  fp.doSaveJPEG( fileName );
            }
            else if ( itemName.equals( "Load JPEG File to Compare..." )) {
                String fileName = promptFile( false, null );
                if ( fileName != null )
                  loadComparisonJPEG( fileName );
            }
            else if ( itemName.equals( "Page Setup..." ))
              fp.doPageSetup();
            else if ( itemName.equals( "Print..." ))
              printDialog.show();
            else if ( itemName.equals( "Close" ))
              parent.dispose();
            else if ( itemName.equals( "Exit" ))
              System.exit(0);
        }

        else if ( source instanceof JTextField ) {
            JTextField tf = (JTextField) source;
            int sz = 12;
            try {
                 sz = Integer.parseInt(sizeField.getText());
                 if (sz < 1 || sz > 120) {
                      sz = 12;
                      sizeField.setText("12");
                 }
            } catch (Exception se) {
                 sizeField.setText("12");
            }
            if ( tf == sizeField )
              fp.setFontParams( fontMenu.getSelectedItem(),
                                sz,
                                styleMenu.getSelectedIndex(),
                                transformMenu.getSelectedIndex() );
        }

        else if ( source instanceof JButton ) {
            String itemName = ( (JButton) source ).getLabel();
            /// Print dialog buttons...
            if ( itemName.equals( "Print" )) {
                for ( int i = 0; i < printModeCBs.length; i++ )
                  if ( printModeCBs[i].isSelected() ) {
                      printDialog.hide();
                      fp.doPrint( i );
                  }
            }
            else if ( itemName.equals( "Cancel" ))
              printDialog.hide();
            /// Update button from Usert Text JDialog...
            else if ( itemName.equals( "Update" ))
              fp.setTextToDraw( fp.USER_TEXT, null,
                                parseUserText( userTextArea.getText() ), null );
        }
        else if ( source instanceof JComboBox ) {
            JComboBox c = (JComboBox) source;
            
            /// RangeMenu handles actions by itself and then calls fireRangeChanged,
            /// so it is not listed or handled here
            if ( c == fontMenu || c == styleMenu || c == transformMenu )
              fp.setFontParams( fontMenu.getSelectedItem(),
                                Integer.parseInt(sizeField.getText()),
                                styleMenu.getSelectedIndex(),
                                transformMenu.getSelectedIndex() );
            else if ( c == methodsMenu )
              fp.setDrawMethod( methodsMenu.getSelectedIndex() );
            else if ( c == textMenu ) {
                int selected = textMenu.getSelectedIndex();

                if ( selected == fp.RANGE_TEXT )
                  fp.setTextToDraw( fp.RANGE_TEXT, rm.getSelectedRange(),
                                    null, null );
                else if ( selected == fp.USER_TEXT )
                  fp.setTextToDraw( fp.USER_TEXT, null,
                                    parseUserText( userTextArea.getText() ), null );
                else if ( selected == fp.FILE_TEXT ) {
                    String fileName = promptFile( false, null );
                    if ( fileName != null ) {
		      tFileName = fileName;
		      readTextFile( fileName );
		    } else {
                        /// User cancelled selection; reset to previous choice
                        c.setSelectedIndex( currentTextChoice );
                        return;
                    }
                }
                else if ( selected == fp.ALL_GLYPHS )
                  fp.setTextToDraw( fp.ALL_GLYPHS, null, null, null );

                updateGUI();
                currentTextChoice = selected;
            }
            else if ( c == transformMenuG2 ) {
            	fp.setTransformG2( transformMenuG2.getSelectedIndex() );
            }
        }
    }

    /// ItemListener interface function
    /// Responds to JCheckBoxMenuItem, JComboBox and JCheckBox actions
    public void itemStateChanged( ItemEvent e ) {
        Object source = e.getSource();

        if ( source instanceof JCheckBoxMenuItem ) {
            JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) source;
            if ( cbmi == displayGridCBMI )
              fp.setGridDisplay( displayGridCBMI.getState() );
            else if ( cbmi == force16ColsCBMI )
              fp.setForce16Columns( force16ColsCBMI.getState() );
            else if ( cbmi == showFontInfoCBMI ) {
                if ( showFontInfoCBMI.getState() ) {
                    fireUpdateFontInfo();
                    fontInfoDialog.show();
                }
                else
                  fontInfoDialog.hide();
            }
        }

        else if ( source instanceof JCheckBox ) {
            fp.setRenderingHints( useAntialiasCB.isSelected(),
                                  useFractionalCB.isSelected() );
        }
    }

    /// Main function
    public static void main(String argv[]) {
        final JFrame f = new JFrame( "Font2DTest" );
        final Font2DTest f2dt = new Font2DTest( f, false );
        f.addWindowListener( new WindowAdapter() {
            public void windowOpening( WindowEvent e ) { f2dt.repaint(); }
            public void windowClosing( WindowEvent e ) { System.exit(0); }
        });

        f.getContentPane().add( f2dt );
        f.pack();
        f.show();
    }

    /// Inner class definitions...

    /// Class to display just an image file
    /// Used to show the comparison JPEG image
    private final class ImagePanel extends JPanel {
        private final BufferedImage bi;
        
        public ImagePanel( BufferedImage image ) {
            bi = image;
        }

        public Dimension getPreferredSize() {
            return new Dimension( bi.getWidth(), bi.getHeight() );
        }

        public void paintComponent( Graphics g ) {
            g.drawImage( bi, 0, 0, this );
        }
    }

    /// Classes made to avoid repetitive calls... (being lazy)
    private final class ButtonV2 extends JButton {
        public ButtonV2( String name, ActionListener al ) {
            super( name );
            this.setFont( labelFont );
            this.addActionListener( al );
        }
    }

    private final class ChoiceV2 extends JComboBox {
	public ChoiceV2() {;}
        public ChoiceV2( ActionListener al ) {
            super();
            this.addActionListener( al );
        }
    }

    private final class LabelV2 extends JLabel {
        public LabelV2( String name ) {
            super( name );
            this.setFont( labelFont );
        }
    }

    private final class MenuItemV2 extends JMenuItem {
        public MenuItemV2( String name, ActionListener al ) {
            super( name );
            this.addActionListener( al );
        }
    }

    private final class CheckboxMenuItemV2 extends JCheckBoxMenuItem {
        public CheckboxMenuItemV2( String name, boolean b, ItemListener il ) {
            super( name, b );
            this.addItemListener( il );
        }
    }
}
