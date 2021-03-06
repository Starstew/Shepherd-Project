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

package org.ecocean.servlet;

import org.ecocean.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EncounterDelete extends HttpServlet {

  public void init(ServletConfig config) throws ServletException {
    super.init(config);
  }


  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }


  private void setDateLastModified(Encounter enc) {
    String strOutputDateTime = ServletUtilities.getDate();
    enc.setDWCDateLastModified(strOutputDateTime);
  }


  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Shepherd myShepherd = new Shepherd();
    //set up for response
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    boolean locked = false;

    //setup data dir
    String rootWebappPath = getServletContext().getRealPath("/");
    File webappsDir = new File(rootWebappPath).getParentFile();
    File shepherdDataDir = new File(webappsDir, CommonConfiguration.getDataDirectoryName());
    //if(!shepherdDataDir.exists()){shepherdDataDir.mkdir();}
    File encountersDir=new File(shepherdDataDir.getAbsolutePath()+"/encounters");
    //if(!encountersDir.exists()){encountersDir.mkdir();}
    
    boolean isOwner = true;


    if (!(request.getParameter("number") == null)) {
      String message = "Encounter #" + request.getParameter("number") + " was deleted from the database.";
      ServletUtilities.informInterestedParties(request, request.getParameter("number"), message);
      myShepherd.beginDBTransaction();
      Encounter enc2trash = myShepherd.getEncounter(request.getParameter("number"));
      setDateLastModified(enc2trash);


      if (enc2trash.isAssignedToMarkedIndividual().equals("Unassigned")) {

        try {

          Encounter backUpEnc = myShepherd.getEncounterDeepCopy(enc2trash.getEncounterNumber());

          String savedFilename = request.getParameter("number") + ".dat";
          File thisEncounterDir = new File(encountersDir.getAbsolutePath()+"/" + request.getParameter("number"));


          File serializedBackup = new File(thisEncounterDir, savedFilename);
          FileOutputStream fout = new FileOutputStream(serializedBackup);
          ObjectOutputStream oos = new ObjectOutputStream(fout);
          oos.writeObject(backUpEnc);
          oos.close();

          //record who deleted this encounter
          enc2trash.addComments("<p><em>" + request.getRemoteUser() + " on " + (new java.util.Date()).toString() + "</em><br>" + "Deleted this encounter from the database.");
          myShepherd.commitDBTransaction();

          //now delete for good
          myShepherd.beginDBTransaction();
          myShepherd.throwAwayEncounter(enc2trash);


        } catch (Exception edel) {
          locked = true;
          edel.printStackTrace();
          myShepherd.rollbackDBTransaction();

        }


        if (!locked) {
          myShepherd.commitDBTransaction();

          //log it
          Logger log = LoggerFactory.getLogger(EncounterDelete.class);
		  log.info("Click to restore deleted encounter: <a href=\"http://" + CommonConfiguration.getURLLocation(request) + "/ResurrectDeletedEncounter?number=" + request.getParameter("number")+"\">"+request.getParameter("number")+"</a>");


          out.println(ServletUtilities.getHeader(request));
          out.println("<strong>Success:</strong> I have removed encounter " + request.getParameter("number") + " from the database. If you have deleted this encounter in error, please contact the webmaster and reference encounter " + request.getParameter("number") + " to have it restored.");
          out.println("<p><a href=\"encounters/allEncounters.jsp\">View all encounters</a></font></p>");
          out.println("<p><a href=\"encounters/allEncountersUnapproved.jsp\">View all unapproved encounters</a></font></p>");

          out.println("<p><a href=\"allIndividuals.jsp\">View all individuals</a></font></p>");

          out.println(ServletUtilities.getFooter());
          Vector e_images = new Vector();
          NotificationMailer mailer = new NotificationMailer(CommonConfiguration.getMailHost(), CommonConfiguration.getAutoEmailAddress(), CommonConfiguration.getNewSubmissionEmail(), ("Removed encounter " + request.getParameter("number")), "Encounter " + request.getParameter("number") + " has been removed from the database by user " + request.getRemoteUser() + ".", e_images);

		  //let's get ready for emailing
          ThreadPoolExecutor es = MailThreadExecutorService.getExecutorService();
		  es.execute(mailer);


        } else {
          out.println(ServletUtilities.getHeader(request));
          out.println("<strong>Failure:</strong> I have NOT removed encounter " + request.getParameter("number") + " from the database. This encounter is currently being modified by another user.");
          out.println("<p><a href=\"encounters/allEncounters.jsp\">View all encounters</a></font></p>");
          out.println("<p><a href=\"allIndividuals.jsp\">View all individuals</a></font></p>");

          out.println(ServletUtilities.getFooter());


        }
      } else {
        myShepherd.commitDBTransaction();
        out.println(ServletUtilities.getHeader(request));
        out.println("Encounter# " + request.getParameter("number") + " is assigned to an individual and cannot be rejected until it has been removed from that individual.");
        out.println(ServletUtilities.getFooter());
      }
    } else {
      out.println(ServletUtilities.getHeader(request));
      out.println("<strong>Error:</strong> I don't know which encounter you're trying to remove.");
      out.println(ServletUtilities.getFooter());

    }


    out.close();
    myShepherd.closeDBTransaction();
  }
}


