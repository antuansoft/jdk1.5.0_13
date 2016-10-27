/*
 * @(#)RangeMenu.java	1.16 04/07/26
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
 * @(#)RangeMenu.java	1.16 04/07/26
 */

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

/**
 * RangeMenu.java
 *
 * @version @(#)RangeMenu.java	1.1 00/08/22
 * @author Shinsuke Fukuda
 * @author Ankit Patel [Conversion to Swing - 01/07/30]  
 */

/// Custom made choice menu that holds data for unicode range

public final class RangeMenu extends JComboBox implements ActionListener {

    /// Painfully extracted from java.lang.Character.UnicodeBlock.  Arrrgh!
    /// Unicode 3.0 data.

    private final int[][] UNICODE_RANGES = {
        { 0x000000, 0x00007f }, /// BASIC_LATIN
        { 0x000080, 0x0000ff }, /// LATIN_1_SUPPLEMENT
        { 0x000100, 0x00017f }, /// LATIN_EXTENDED_A
        { 0x000180, 0x00024f }, /// LATIN_EXTENDED_B
        { 0x000250, 0x0002af }, /// IPA_EXTENSIONS
        { 0x0002b0, 0x0002ff }, /// SPACING_MODIFIER_LETTERS
        { 0x000300, 0x00036f }, /// COMBINING_DIACRITICAL_MARKS
        { 0x000370, 0x0003ff }, /// GREEK
        { 0x000400, 0x0004ff }, /// CYRILLIC
        { 0x000500, 0x00052f }, /// CYRILLIC_SUPPLEMENTARY
        { 0x000530, 0x00058f }, /// ARMENIAN
        { 0x000590, 0x0005ff }, /// HEBREW
        { 0x000600, 0x0006ff }, /// ARABIC
        { 0x000700, 0x00074f }, /// SYRIAC
        { 0x000780, 0x0007bf }, /// THAANA
        { 0x000900, 0x00097f }, /// DEVANAGARI
        { 0x000980, 0x0009ff }, /// BENGALI
        { 0x000a00, 0x000a7f }, /// GURMUKHI
        { 0x000a80, 0x000aff }, /// GUJARATI
        { 0x000b00, 0x000b7f }, /// ORIYA
        { 0x000b80, 0x000bff }, /// TAMIL
        { 0x000c00, 0x000c7f }, /// TELUGU
        { 0x000c80, 0x000cff }, /// KANNADA
        { 0x000d00, 0x000d7f }, /// MALAYALAM
        { 0x000d80, 0x000dff }, /// SINHALA
        { 0x000e00, 0x000e7f }, /// THAI
        { 0x000e80, 0x000eff }, /// LAO
        { 0x000f00, 0x000fff }, /// TIBETAN
        { 0x001000, 0x00109f }, /// MYANMAR
        { 0x0010a0, 0x0010ff }, /// GEORGIAN
        { 0x001100, 0x0011ff }, /// HANGUL_JAMO
        { 0x001200, 0x00137f }, /// ETHIOPIC
        { 0x0013a0, 0x0013ff }, /// CHEROKEE
        { 0x001400, 0x00167f }, /// UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS
        { 0x001680, 0x00169f }, /// OGHAM
        { 0x0016a0, 0x0016ff }, /// RUNIC
        { 0x001700, 0x00171f }, /// TAGALOG
        { 0x001720, 0x00173f }, /// HANUNOO
        { 0x001740, 0x00175f }, /// BUHID
        { 0x001760, 0x00177f }, /// TAGBANWA
        { 0x001780, 0x0017ff }, /// KHMER
        { 0x001800, 0x0018af }, /// MONGOLIAN
        { 0x001900, 0x00194f }, /// LIMBU
        { 0x001950, 0x00197f }, /// TAI_LE
        { 0x0019e0, 0x0019ff }, /// KHMER_SYMBOLS
        { 0x001d00, 0x001d7f }, /// PHONETIC_EXTENSIONS
        { 0x001e00, 0x001eff }, /// LATIN_EXTENDED_ADDITIONAL
        { 0x001f00, 0x001fff }, /// GREEK_EXTENDED
        { 0x002000, 0x00206f }, /// GENERAL_PUNCTUATION
        { 0x002070, 0x00209f }, /// SUPERSCRIPTS_AND_SUBSCRIPTS
        { 0x0020a0, 0x0020cf }, /// CURRENCY_SYMBOLS
        { 0x0020d0, 0x0020ff }, /// COMBINING_MARKS_FOR_SYMBOLS
        { 0x002100, 0x00214f }, /// LETTERLIKE_SYMBOLS
        { 0x002150, 0x00218f }, /// NUMBER_FORMS
        { 0x002190, 0x0021ff }, /// ARROWS
        { 0x002200, 0x0022ff }, /// MATHEMATICAL_OPERATORS
        { 0x002300, 0x0023ff }, /// MISCELLANEOUS_TECHNICAL
        { 0x002400, 0x00243f }, /// CONTROL_PICTURES
        { 0x002440, 0x00245f }, /// OPTICAL_CHARACTER_RECOGNITION
        { 0x002460, 0x0024ff }, /// ENCLOSED_ALPHANUMERICS
        { 0x002500, 0x00257f }, /// BOX_DRAWING
        { 0x002580, 0x00259f }, /// BLOCK_ELEMENTS
        { 0x0025a0, 0x0025ff }, /// GEOMETRIC_SHAPES
        { 0x002600, 0x0026ff }, /// MISCELLANEOUS_SYMBOLS
        { 0x002700, 0x0027bf }, /// DINGBATS
        { 0x0027c0, 0x0027ef }, /// MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A
        { 0x0027f0, 0x0027ff }, /// SUPPLEMENTAL_ARROWS_A
        { 0x002800, 0x0028ff }, /// BRAILLE_PATTERNS
        { 0x002900, 0x00297f }, /// SUPPLEMENTAL_ARROWS_B
        { 0x002980, 0x0029ff }, /// MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B
        { 0x002a00, 0x002aff }, /// SUPPLEMENTAL_MATHEMATICAL_OPERATORS
        { 0x002b00, 0x002bff }, /// MISCELLANEOUS_SYMBOLS_AND_ARROWS
        { 0x002e80, 0x002eff }, /// CJK_RADICALS_SUPPLEMENT
        { 0x002f00, 0x002fdf }, /// KANGXI_RADICALS
        { 0x002ff0, 0x002fff }, /// IDEOGRAPHIC_DESCRIPTION_CHARACTERS
        { 0x003000, 0x00303f }, /// CJK_SYMBOLS_AND_PUNCTUATION
        { 0x003040, 0x00309f }, /// HIRAGANA
        { 0x0030a0, 0x0030ff }, /// KATAKANA
        { 0x003100, 0x00312f }, /// BOPOMOFO
        { 0x003130, 0x00318f }, /// HANGUL_COMPATIBILITY_JAMO
        { 0x003190, 0x00319f }, /// KANBUN
        { 0x0031a0, 0x0031bf }, /// BOPOMOFO_EXTENDED
        { 0x0031f0, 0x0031ff }, /// KATAKANA_PHONETIC_EXTENSIONS
        { 0x003200, 0x0032ff }, /// ENCLOSED_CJK_LETTERS_AND_MONTHS
        { 0x003300, 0x0033ff }, /// CJK_COMPATIBILITY
        { 0x003400, 0x004dbf }, /// CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
        { 0x004dc0, 0x004dff }, /// YIJING_HEXAGRAM_SYMBOLS
        { 0x004e00, 0x009fff }, /// CJK_UNIFIED_IDEOGRAPHS
        { 0x00a000, 0x00a48f }, /// YI_SYLLABLES
        { 0x00a490, 0x00a4cf }, /// YI_RADICALS
        { 0x00ac00, 0x00d7af }, /// HANGUL_SYLLABLES
        { 0x00d800, 0x00dfff }, /// SURROGATES_AREA
        { 0x00e000, 0x00f8ff }, /// PRIVATE_USE_AREA
        { 0x00f900, 0x00faff }, /// CJK_COMPATIBILITY_IDEOGRAPHS
        { 0x00fb00, 0x00fb4f }, /// ALPHABETIC_PRESENTATION_FORMS
        { 0x00fb50, 0x00fdff }, /// ARABIC_PRESENTATION_FORMS_A
        { 0x00fe00, 0x00fe0f }, /// VARIATION_SELECTORS
        { 0x00fe20, 0x00fe2f }, /// COMBINING_HALF_MARKS
        { 0x00fe30, 0x00fe4f }, /// CJK_COMPATIBILITY_FORMS
        { 0x00fe50, 0x00fe6f }, /// SMALL_FORM_VARIANTS
        { 0x00fe70, 0x00feff }, /// ARABIC_PRESENTATION_FORMS_B
        { 0x00ff00, 0x00ffef }, /// HALFWIDTH_AND_FULLWIDTH_FORMS
        { 0x00fff0, 0x00ffff }, /// SPECIALS
        { 0x010000, 0x01007f }, /// LINEAR_B_SYLLABARY
        { 0x010080, 0x0100ff }, /// LINEAR_B_IDEOGRAMS
        { 0x010100, 0x01013f }, /// AEGEAN_NUMBERS
        { 0x010300, 0x01032f }, /// OLD_ITALIC
        { 0x010330, 0x01034f }, /// GOTHIC
        { 0x010380, 0x01039f }, /// UGARITIC
        { 0x010400, 0x01044f }, /// DESERET
        { 0x010450, 0x01047f }, /// SHAVIAN
        { 0x010480, 0x0104af }, /// OSMANYA
        { 0x010800, 0x01083f }, /// CYPRIOT_SYLLABARY
        { 0x01d000, 0x01d0ff }, /// BYZANTINE_MUSICAL_SYMBOLS
        { 0x01d100, 0x01d1ff }, /// MUSICAL_SYMBOLS
        { 0x01d300, 0x01d35f }, /// TAI_XUAN_JING_SYMBOLS
        { 0x01d400, 0x01d7ff }, /// MATHEMATICAL_ALPHANUMERIC_SYMBOLS
        { 0x020000, 0x02a6df }, /// CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
        { 0x02f800, 0x02fa1f }, /// CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
        { 0x0e0000, 0x0e007f }, /// TAGS
        { 0x0e0100, 0x0e01ef }, /// VARIATION_SELECTORS_SUPPLEMENT
        { 0x0f0000, 0x0fffff }, /// SUPPLEMENTARY_PRIVATE_USE_AREA_A
        { 0x100000, 0x10ffff }, /// SUPPLEMENTARY_PRIVATE_USE_AREA_B
     };

    private final String[] UNICODE_RANGE_NAMES = {
        "Basic Latin",
        "Latin-1 Supplement",
        "Latin Extended-A",
        "Latin Extended-B",
        "IPA Extensions",
        "Spacing Modifier Letters",
        "Combining Diacritical Marks",
        "Greek",
        "Cyrillic",
        "Cyrillic Supplement",
        "Armenian",
        "Hebrew",
        "Arabic",
        "Syriac",
        "Thaana",
        "Devanagari",
        "Bengali",
        "Gurmukhi",
        "Gujarati",
        "Oriya",
        "Tamil",
        "Telugu",
        "Kannada",
        "Malayalam",
        "Sinhala",
        "Thai",
        "Lao",
        "Tibetan",
        "Myanmar",
        "Georgian",
        "Hangul Jamo",
        "Ethiopic",
        "Cherokee",
        "Unified Canadian Aboriginal Syllabics",
        "Ogham",
        "Runic",
        "Tagalog",
        "Hanunoo",
        "Buhid",
        "Tagbanwa",
        "Khmer",
        "Mongolian",
        "Limbu",
        "Tai Le",
        "Khmer Symbols",
        "Phonetic Extensions",
        "Latin Extended Additional",
        "Greek Extended",
        "General Punctuation",
        "Superscripts and Subscripts",
        "Currency Symbols",
        "Combining Marks for Symbols",
        "Letterlike Symbols",
        "Number Forms",
        "Arrows",
        "Mathematical Operators",
        "Miscellaneous Technical",
        "Control Pictures",
        "Optical Character Recognition",
        "Enclosed Alphanumerics",
        "Box Drawing",
        "Block Elements",
        "Geometric Shapes",
        "Miscellaneous Symbols",
        "Dingbats",
        "Miscellaneous Mathematical Symbols-A",
        "Supplemental Arrows-A",
        "Braille Patterns",
        "Supplemental Arrows-B",
        "Miscellaneous Mathematical Symbols-B",
        "Supplemental Mathematical Operators",
        "Miscellaneous Symbols and Arrows",
        "CJK Radicals Supplement",
        "Kangxi Radicals",
        "Ideographic Description Characters",
        "CJK Symbols and Punctuation",
        "Hiragana",
        "Katakana",
        "Bopomofo",
        "Hangul Compatibility Jamo",
        "Kanbun",
        "Bopomofo Extended",
        "Katakana Phonetic Extensions",
        "Enclosed CJK Letters and Months",
        "CJK Compatibility",
        "CJK Unified Ideographs Extension A",
        "Yijing Hexagram Symbols",
        "CJK Unified Ideographs",
        "Yi Syllables",
        "Yi Radicals",
        "Hangul Syllables",
        "Surrogates Area", // High Surrogates, High Private Use Surrogates, Low Surrogates
        "Private Use Area",
        "CJK Compatibility Ideographs",
        "Alphabetic Presentation Forms",
        "Arabic Presentation Forms-A",
        "Variation Selectors",
        "Combining Half Marks",
        "CJK Compatibility Forms",
        "Small Form Variants",
        "Arabic Presentation Forms-B",
        "Halfwidth and Fullwidth Forms",
        "Specials",
        "Linear B Syllabary",
        "Linear B Ideograms",
        "Aegean Numbers",
        "Old Italic",
        "Gothic",
        "Ugaritic",
        "Deseret",
        "Shavian",
        "Osmanya",
        "Cypriot Syllabary",
        "Byzantine Musical Symbols",
        "Musical Symbols",
        "Tai Xuan Jing Symbols",
        "Mathematical Alphanumeric Symbols",
        "CJK Unified Ideographs Extension B",
        "CJK Compatibility Ideographs Supplement",
        "Tags",
        "Variation Selectors Supplement",
        "Supplementary Private Use Area-A",
        "Supplementary Private Use Area-B",
    };

    private boolean useCustomRange = false;
    private int[] customRange = { 0x0000, 0x007f };

    /// Custom range dialog variables
    private final JDialog customRangeDialog;
    private final JTextField customRangeStart = new JTextField( "0000", 4 );
    private final JTextField customRangeEnd   = new JTextField( "007F", 4 );
    private final int CUSTOM_RANGE_INDEX = UNICODE_RANGE_NAMES.length - 1;

    /// Parent Font2DTest Object holder
    private final Font2DTest parent;

    public RangeMenu( Font2DTest demo, JFrame f ) {
        super();
        parent = demo;

        for ( int i = 0; i < UNICODE_RANGE_NAMES.length; i++ )
          addItem( UNICODE_RANGE_NAMES[i] );

        setSelectedIndex( 0 );
        addActionListener( this );

        /// Set up custom range dialog...
        customRangeDialog = new JDialog( f, "Custom Unicode Range", true );
        customRangeDialog.setResizable( false );

        JPanel dialogTop = new JPanel();
        JPanel dialogBottom = new JPanel();
        JButton okButton = new JButton("OK");
        JLabel from = new JLabel( "From" );
        JLabel to = new JLabel("To:");
        Font labelFont = new Font( "dialog", Font.BOLD, 12 );
        from.setFont( labelFont );
        to.setFont( labelFont );
        okButton.setFont( labelFont );

        dialogTop.add( from );
        dialogTop.add( customRangeStart );
        dialogTop.add( to );
        dialogTop.add( customRangeEnd );
        dialogBottom.add( okButton );
        okButton.addActionListener( this );

        customRangeDialog.getContentPane().setLayout( new BorderLayout() );
        customRangeDialog.getContentPane().add( "North", dialogTop );
        customRangeDialog.getContentPane().add( "South", dialogBottom );
        customRangeDialog.pack();
    }

    /// Return the range that is currently selected

    public int[] getSelectedRange() {
        if ( useCustomRange ) {
            int startIndex, endIndex;
            String startText, endText;
            String empty = "";
            try {
                startText = customRangeStart.getText().trim();
                endText = customRangeEnd.getText().trim();
                if ( startText.equals(empty) && !endText.equals(empty) ) {
                    endIndex = Integer.parseInt( endText, 16 );
                    startIndex = endIndex - 7*25;
                }
                else if ( !startText.equals(empty) && endText.equals(empty) ) {
                    startIndex = Integer.parseInt( startText, 16 );
                    endIndex = startIndex + 7*25;                    
                }
                else {
                    startIndex = Integer.parseInt( customRangeStart.getText(), 16 );
                    endIndex = Integer.parseInt( customRangeEnd.getText(), 16 );
                }
            }
            catch ( Exception e ) {
                /// Error in parsing the hex number ---
                /// Reset the range to what it was before and return that
                customRangeStart.setText( Integer.toString( customRange[0], 16 ));
                customRangeEnd.setText( Integer.toString( customRange[1], 16 ));
                return customRange;
            }

            if ( startIndex < 0 )
              startIndex = 0;
            if ( endIndex > 0xffff )
              endIndex = 0xffff;
            if ( startIndex > endIndex )
              startIndex = endIndex;

            customRange[0] = startIndex;
            customRange[1] = endIndex;
            return customRange;
        }
        else
          return UNICODE_RANGES[ getSelectedIndex() ];
    }

    /// Function used by loadOptions in Font2DTest main panel
    /// to reset setting and range selection
    public void setSelectedRange( String name, int start, int end ) {
        setSelectedItem( name );
        customRange[0] = start;
        customRange[1] = end;
        parent.fireRangeChanged();
    }

    /// ActionListener interface function
    /// ABP
    /// moved JComboBox event code into this fcn from
    /// itemStateChanged() method. Part of change to Swing.
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();
        
        if ( source instanceof JComboBox ) {
	        String rangeName = (String)((JComboBox)source).getSelectedItem();

	        if ( rangeName.equals("Other...") ) {
            	    useCustomRange = true;
        	    customRangeDialog.show();
	        }
        	else {
	          useCustomRange = false;
        	}
	        parent.fireRangeChanged();
	}
	else if ( source instanceof JButton ) {
	        /// Since it is only "OK" button that sends any action here...
        	customRangeDialog.hide();
        }
    }
}
