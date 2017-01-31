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
import org.ecocean.genetics.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;


//Set alternateID for this encounter/sighting
public class EncounterRemoveTissueSample extends HttpServlet {

  public void init(ServletConfig config) throws ServletException {
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String context="context0";
    context=ServletUtilities.getContext(request);
    Shepherd myShepherd = new Shepherd(context);
    myShepherd.setAction("EncounterRemoveTissueSample.class");
    //set up for response
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    boolean locked = false;

    String sharky = "None";


    sharky = request.getParameter("encounter");
    myShepherd.beginDBTransaction();
    if ((myShepherd.isEncounter(sharky)) && (request.getParameter("sampleID") != null) && (!request.getParameter("sampleID").equals(""))) {
      Encounter enc = myShepherd.getEncounter(sharky);
      try {
        
        
        
        TissueSample genSample=new TissueSample();
        if(myShepherd.isTissueSample(request.getParameter("sampleID"), sharky)){
          genSample=myShepherd.getTissueSample(request.getParameter("sampleID"), sharky);
          enc.removeTissueSample(genSample);
          enc.addComments("<p><em>" + request.getRemoteUser() + " on " + (new java.util.Date()).toString() + "</em><br />" + "Removed tissue sample ID "+request.getParameter("sampleID")+".<br />");
          
          myShepherd.throwAwayTissueSample(genSample);          
          
          
          
        }

        
      } 
      catch (Exception le) {
        locked = true;
        le.printStackTrace();
        myShepherd.rollbackDBTransaction();
        //myShepherd.closeDBTransaction();
      }

      if (!locked) {
        myShepherd.commitDBTransaction();
        //myShepherd.closeDBTransaction();
        out.println(ServletUtilities.getHeader(request));
        out.println("<strong>Success!</strong> I have successfully removed tissue sample "+request.getParameter("sampleID")+" for encounter " + sharky + ".</p>");

        out.println("<p><a href=\"http://" + CommonConfiguration.getURLLocation(request) + "/encounters/encounter.jsp?number=" + sharky + "\">Return to encounter " + sharky + "</a></p>\n");
        out.println(ServletUtilities.getFooter(context));
        } 
      else {

        out.println(ServletUtilities.getHeader(request));
        out.println("<strong>Failure!</strong> This encounter is currently being modified by another user or is inaccessible. Please wait a few seconds before trying to modify this encounter again.");

        out.println("<p><a href=\"http://" + CommonConfiguration.getURLLocation(request) + "/encounters/encounter.jsp?number=" + sharky + "\">Return to encounter " + sharky + "</a></p>\n");
        out.println(ServletUtilities.getFooter(context));

      }
    } 
    else {
      myShepherd.rollbackDBTransaction();
      out.println(ServletUtilities.getHeader(request));
      out.println("<strong>Error:</strong> I was unable to remove the tissue sample. I cannot find the encounter that you intended it for in the database.");
      out.println(ServletUtilities.getFooter(context));

    }
    out.close();
    myShepherd.closeDBTransaction();
  }


}
  
  
