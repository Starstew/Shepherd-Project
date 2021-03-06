/*
 * The Shepherd Project - A Mark-Recapture Framework
 * Copyright (C) 2011 Jason Holmberg
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.ecocean.grid;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * COmment
 *
 * @author jholmber
 *         more
 */
public class AppletHeartbeatThread implements Runnable, ISharkGridThread {

  public Thread heartbeatThread;
  public boolean finished = false;
  private int numProcessors = 1;
  private String appletID = "";
  private String rootURL = "";
  private String version = "";

  /**
   * Constructor to create a new thread object
   */
  public AppletHeartbeatThread(String appletID, int numProcessors, String thisURLRoot, String version) {
    this.numProcessors = numProcessors;
    this.appletID = appletID;
    heartbeatThread = new Thread(this, ("sharkGridNodeHeartbeat_" + appletID));
    this.rootURL = thisURLRoot;
    heartbeatThread.start();
    this.version = version;
  }


  /**
   * main method of the heartbeat thread
   */
  public void run() {
    //boolean ok2run=true;
    while (!finished) {
      try {
        sendHeartbeat(appletID);
        Thread.sleep(90000);
      } catch (Exception e) {
        System.out.println("Heartbeat thread registering an exception while trying to sleep!");
      }
    }
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean finish) {
    this.finished = finish;
  }


  private void sendHeartbeat(String appletID) {
    try {
      System.out.println("...sending heartbeat...thump...thump...");
      URL u = new URL(rootURL + "/GridHeartbeatReceiver?nodeIdentifier=" + appletID + "&numProcessors=" + numProcessors + "&version=" + version);
      URLConnection finishConnection = u.openConnection();

      InputStream inputStreamFromServlet = finishConnection.getInputStream();
      BufferedReader in = new BufferedReader(new InputStreamReader(inputStreamFromServlet));
      String line = in.readLine();
      in.close();

      //process the returned line however needed


    } catch (MalformedURLException mue) {
      System.out.println("!!!!!I hit a MalformedURLException in the heartbeat thread!!!!!");
      mue.printStackTrace();

    } catch (IOException ioe) {
      System.out.println("!!!!!I hit a MalformedURLException in the heartbeat thread!!!!!");
      ioe.printStackTrace();
    } catch (Exception e) {
      System.out.println("!!!!!I hit a MalformedURLException in the heartbeat thread!!!!!");
      e.printStackTrace();
    }
  }


}