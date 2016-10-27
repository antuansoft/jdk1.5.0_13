/*
 * @(#)TreeDemo.java	1.10 04/07/26
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
 * @(#)TreeDemo.java	1.10 04/07/26
 */


import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import java.applet.*;
import java.net.*;

/**
 * JTree Demo
 *
 * @version 1.10 07/26/04
 * @author Jeff Dinkins
 */
public class TreeDemo extends DemoModule {

    JTree tree;

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
	TreeDemo demo = new TreeDemo(null);
	demo.mainImpl();
    }

    /**
     * TreeDemo Constructor
     */
    public TreeDemo(SwingSet2 swingset) {
	// Set the title for this demo, and an icon used to represent this
	// demo inside the SwingSet2 app.
	super(swingset, "TreeDemo", "toolbar/JTree.gif");

	getDemoPanel().add(createTree(), BorderLayout.CENTER);
    }
 
    public JScrollPane createTree() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(getString("TreeDemo.music"));
        DefaultMutableTreeNode catagory = null ;
	DefaultMutableTreeNode artist = null;
	DefaultMutableTreeNode record = null;

	// open tree data 
	URL url = getClass().getResource("/resources/tree.txt");

	try {
	    // convert url to buffered string
	    InputStream is = url.openStream();
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader reader = new BufferedReader(isr);

	    // read one line at a time, put into tree
	    String line = reader.readLine();
	    while(line != null) {
		// System.out.println("reading in: ->" + line + "<-");
		char linetype = line.charAt(0);
		switch(linetype) {
		   case 'C':
		     catagory = new DefaultMutableTreeNode(line.substring(2));
		     top.add(catagory);
		     break;
		   case 'A':
		     if(catagory != null) {
		         catagory.add(artist = new DefaultMutableTreeNode(line.substring(2)));
		     }
		     break;
		   case 'R':
		     if(artist != null) {
		         artist.add(record = new DefaultMutableTreeNode(line.substring(2)));
		     }
		     break;
		   case 'S':
		     if(record != null) {
		         record.add(new DefaultMutableTreeNode(line.substring(2)));
		     }
		     break;
		   default:
		     break;
		}
		line = reader.readLine();
	    }
	} catch (IOException e) {
	}

	tree = new JTree(top) {
	    public Insets getInsets() {
		return new Insets(5,5,5,5);
	    }
	};
        
        tree.setEditable(true);
            
	return new JScrollPane(tree);
    }
    
    void updateDragEnabled(boolean dragEnabled) {
        tree.setDragEnabled(dragEnabled);
    }

}
