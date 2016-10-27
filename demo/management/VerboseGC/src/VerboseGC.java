/*
 * @(#)VerboseGC.java	1.3 04/07/27
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
 * @(#)VerboseGC.java	1.3 04/07/27
 */

import javax.management.*;
import javax.management.remote.*;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * This VerboseGC class demonstrates the capability to get
 * the garbage collection statistics and memory usage remotely.
 */
public class VerboseGC {
    private MBeanServerConnection server;
    private JMXConnector jmxc;
    public VerboseGC(String hostname, int port) {
        System.out.println("Connecting to " + hostname + ":" + port);

        // Create an RMI connector client and connect it to
        // the RMI connector server
        String urlPath = "/jndi/rmi://" + hostname + ":" + port + "/jmxrmi";
        connect(urlPath);
   }
   
   public void dump(long interval, long samples) {
        try {
            PrintGCStat pstat = new PrintGCStat(server);
            for (int i = 0; i < samples; i++) {
                pstat.printVerboseGc(); 
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            System.err.println("\nCommunication error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Connect to a JMX agent of a given URL. 
     */
    private void connect(String urlPath) {
        try {
            JMXServiceURL url = new JMXServiceURL("rmi", "", 0, urlPath);
            this.jmxc = JMXConnectorFactory.connect(url);
            this.server = jmxc.getMBeanServerConnection();
        } catch (MalformedURLException e) {
            // should not reach here
        } catch (IOException e) {
            System.err.println("\nCommunication error: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            usage();
        }

        String hostname = "";
        int port = -1;
        long interval = 5000; // default is 5 second interval
        long mins = 5;
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
           String arg = args[argIndex];
            if (args[argIndex].startsWith("-")) {
                if (arg.equals("-h") ||
                    arg.equals("-help") ||
                    arg.equals("-?")) {
                    usage();
                } else if (arg.startsWith("-interval=")) {
                    try {
                        interval = Integer.parseInt(arg.substring(10)) * 1000;
                    } catch (NumberFormatException ex) {
                        usage();
                    }
                } else if (arg.startsWith("-duration=")) {
                    try {
                        mins = Integer.parseInt(arg.substring(10));
                    } catch (NumberFormatException ex) {
                        usage();
                    }
                } else {
                    // Unknown switch
                    System.err.println("Unrecognized option: " + arg);
                    usage();
                }
            } else {
                String[] arg2 = arg.split(":");
                if (arg2.length != 2) {
                    usage();
                }
                hostname = arg2[0];
                try {
                    port = Integer.parseInt(arg2[1]);
                } catch (NumberFormatException x) {
                    usage();
                }
                if (port < 0) {
                    usage();
                }
            }
        }

        // get full thread dump and perform deadlock detection
        VerboseGC vgc = new VerboseGC(hostname, port);
        long samples = (mins * 60 * 1000) / interval;
        vgc.dump(interval, samples);

    }

    private static void usage() {
        System.out.print("Usage: java VerboseGC <hostname>:<port> ");
        System.out.println(" [-interval=seconds] [-duration=minutes]");
    }
}
