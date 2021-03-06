<%--
  ~ The Shepherd Project - A Mark-Recapture Framework
  ~ Copyright (C) 2011 Jason Holmberg
  ~
  ~ This program is free software; you can redistribute it and/or
  ~ modify it under the terms of the GNU General Public License
  ~ as published by the Free Software Foundation; either version 2
  ~ of the License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program; if not, write to the Free Software
  ~ Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java"
         import="org.ecocean.*, org.ecocean.servlet.ServletUtilities, java.io.File, java.io.FileOutputStream, java.io.OutputStreamWriter, java.util.*" %>


<html>
<head>


<%


  //let's load encounterSearch.properties
  String langCode = "en";
  if (session.getAttribute("langCode") != null) {
    langCode = (String) session.getAttribute("langCode");
  }

  Properties encprops = new Properties();
  encprops.load(getClass().getResourceAsStream("/bundles/" + langCode + "/searchResults.properties"));


  Shepherd myShepherd = new Shepherd();



  int startNum = 1;
  int endNum = 10;



//let's set up our Excel spreasheeting operations
  //String filenameOBIS = "searchResults_OBIS_" + request.getRemoteUser() + ".xls";
  //String filenameExport = "searchResults_" + request.getRemoteUser() + ".xls";
  //String kmlFilename = "KMLExport_" + request.getRemoteUser() + ".kml";
  //File fileOBIS = new File(getServletContext().getRealPath(("/encounters/" + filenameOBIS)));
  //File fileExport = new File(getServletContext().getRealPath(("/encounters/" + filenameExport)));

  try {

    if (request.getParameter("startNum") != null) {
      startNum = (new Integer(request.getParameter("startNum"))).intValue();
    }
    if (request.getParameter("endNum") != null) {
      endNum = (new Integer(request.getParameter("endNum"))).intValue();
    }

  } catch (NumberFormatException nfe) {
    startNum = 1;
    endNum = 10;
  }

  int numResults = 0;


  Vector rEncounters = new Vector();

  myShepherd.beginDBTransaction();

  EncounterQueryResult queryResult = EncounterQueryProcessor.processQuery(myShepherd, request, "year descending, month descending, day descending");
  rEncounters = queryResult.getResult();


//--let's estimate the number of results that might be unique

  int numUniqueEncounters = 0;
  int numUnidentifiedEncounters = 0;
  int numDuplicateEncounters = 0;
  ArrayList uniqueEncounters = new ArrayList();
  for (int q = 0; q < rEncounters.size(); q++) {
    Encounter rEnc = (Encounter) rEncounters.get(q);
    if (!rEnc.isAssignedToMarkedIndividual().equals("Unassigned")) {
      String assemblage = rEnc.getIndividualID() + ":" + rEnc.getYear() + ":" + rEnc.getMonth() + ":" + rEnc.getDay();
      if (!uniqueEncounters.contains(assemblage)) {
        numUniqueEncounters++;
        uniqueEncounters.add(assemblage);
      } else {
        numDuplicateEncounters++;
      }
    } else {
      numUnidentifiedEncounters++;
    }

  }

//--end unique counting------------------------------------------

%>
<title><%=CommonConfiguration.getHTMLTitle()%>
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta name="Description"
      content="<%=CommonConfiguration.getHTMLDescription()%>"/>
<meta name="Keywords"
      content="<%=CommonConfiguration.getHTMLKeywords()%>"/>
<meta name="Author" content="<%=CommonConfiguration.getHTMLAuthor()%>"/>
<link href="<%=CommonConfiguration.getCSSURLLocation(request)%>"
      rel="stylesheet" type="text/css"/>
<link rel="shortcut icon"
      href="<%=CommonConfiguration.getHTMLShortcutIcon()%>"/>
</head>

<style type="text/css">
  #tabmenu {
    color: #000;
    border-bottom: 2px solid black;
    margin: 12px 0px 0px 0px;
    padding: 0px;
    z-index: 1;
    padding-left: 10px
  }

  #tabmenu li {
    display: inline;
    overflow: hidden;
    list-style-type: none;
  }

  #tabmenu a, a.active {
    color: #DEDECF;
    background: #000;
    font: bold 1em "Trebuchet MS", Arial, sans-serif;
    border: 2px solid black;
    padding: 2px 5px 0px 5px;
    margin: 0;
    text-decoration: none;
    border-bottom: 0px solid #FFFFFF;
  }

  #tabmenu a.active {
    background: #FFFFFF;
    color: #000000;
    border-bottom: 2px solid #FFFFFF;
  }

  #tabmenu a:hover {
    color: #ffffff;
    background: #7484ad;
  }

  #tabmenu a:visited {
    color: #E8E9BE;
  }

  #tabmenu a.active:hover {
    background: #7484ad;
    color: #DEDECF;
    border-bottom: 2px solid #000000;
  }
</style>


<body onload="initialize()" onunload="GUnload()">
<div id="wrapper">
<div id="page">
<jsp:include page="../header.jsp" flush="true">

  <jsp:param name="isAdmin" value="<%=request.isUserInRole(\"admin\")%>" />
</jsp:include>
<div id="main">

<ul id="tabmenu">

  <li><a class="active"><%=encprops.getProperty("table")%>
  </a></li>
  <li><a
    href="thumbnailSearchResults.jsp?<%=request.getQueryString().replaceAll("startNum","uselessNum").replaceAll("endNum","uselessNum") %>"><%=encprops.getProperty("matchingImages")%>
  </a></li>
  <li><a
    href="mappedSearchResults.jsp?<%=request.getQueryString().replaceAll("startNum","uselessNum").replaceAll("endNum","uselessNum") %>"><%=encprops.getProperty("mappedResults")%>
  </a></li>
  <li><a
    href="../xcalendar/calendar2.jsp?<%=request.getQueryString().replaceAll("startNum","uselessNum").replaceAll("endNum","uselessNum") %>"><%=encprops.getProperty("resultsCalendar")%>
  </a></li>

</ul>


<table width="810px" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <p>

      <h1 class="intro"><%=encprops.getProperty("title")%>
      </h1>
      </p>    <p><%=encprops.getProperty("belowMatches")%>
    </p>
    </td>
  </tr>
</table>




<table width="810px">
<tr>
  <td class="lineitem" bgcolor="#99CCFF"></td>
  <td class="lineitem" align="left" valign="top" bgcolor="#99CCFF">
    <strong><%=encprops.getProperty("markedIndividual")%>
    </strong></td>
  <td class="lineitem" align="left" valign="top" bgcolor="#99CCFF">
    <strong><%=encprops.getProperty("number")%>
    </strong></td>
  
  <%
  if (CommonConfiguration.showProperty("showTaxonomy")) {
  %>
  <td class="lineitem" align="left" valign="top" bgcolor="#99CCFF">
    <strong><%=encprops.getProperty("taxonomy")%>
    </strong>
  </td>
  <%
  }
  %>
  
  <td class="lineitem" align="left" valign="top" bgcolor="#99CCFF">
    <strong><%=encprops.getProperty("submitterName")%>
    </strong></td>
  <td class="lineitem" align="left" valign="top" bgcolor="#99CCFF">
    <strong><%=encprops.getProperty("date")%>
    </strong></td>

  <td class="lineitem" align="left" valign="top" bgcolor="#99CCFF">
    <strong><%=encprops.getProperty("location")%>
    </strong></td>
  <td class="lineitem" align="left" valign="top" bgcolor="#99CCFF">
    <strong><%=encprops.getProperty("locationID")%>
    </strong></td>
</tr>

<%
  Vector haveGPSData = new Vector();
  int count = 0;

  for (int f = 0; f < rEncounters.size(); f++) {

    Encounter enc = (Encounter) rEncounters.get(f);
    count++;
    numResults++;
    if ((enc.getDWCDecimalLatitude() != null) && (enc.getDWCDecimalLongitude() != null)) {
      haveGPSData.add(enc);
    }


    if ((numResults >= startNum) && (numResults <= endNum)) {
%>
<tr class="lineitem">
  <td width="100" class="lineitem"><img src="/<%=CommonConfiguration.getDataDirectoryName() %>/encounters/<%=(enc.getEncounterNumber()+"/thumb.jpg")%>"></td>

  <%
    if (enc.isAssignedToMarkedIndividual().trim().toLowerCase().equals("unassigned")) {
  %>
  <td class="lineitem"><%=encprops.getProperty("unassigned")%>
  </td>
  <%
  } else {
  %>
  <td class="lineitem"><a
    href="../individuals.jsp?number=<%=enc.isAssignedToMarkedIndividual()%>"><%=enc.isAssignedToMarkedIndividual()%>
  </a></td>
  <%
    }
  %>
  <td class="lineitem"><a
    href="http://<%=CommonConfiguration.getURLLocation(request)%>/encounters/encounter.jsp?number=<%=enc.getEncounterNumber()%>"><%=enc.getEncounterNumber()%>
  </a>
  </td>
  
  <%
  if (CommonConfiguration.showProperty("showTaxonomy")) {
  
    String genusSpeciesFound=encprops.getProperty("notAvailable");
    if((enc.getGenus()!=null)&&(enc.getSpecificEpithet()!=null)){genusSpeciesFound=enc.getGenus()+" "+enc.getSpecificEpithet();}
  %>
  
  <td class="lineitem">

    <em><%=genusSpeciesFound%></em>

  </td>
<%
}
%>


  <td class="lineitem"><%=enc.getSubmitterName()%>
  </td>
  <td class="lineitem"><%=enc.getDate()%>
  </td>

  <td class="lineitem"><%=enc.getLocation()%>
  </td>
  <td class="lineitem"><%=enc.getLocationCode()%>
  </td>
</tr>
<%
    } //end if to control number displayed

  
  } //end for loop

%>
</table>

<%


  myShepherd.rollbackDBTransaction();

  startNum = startNum + 10;
  endNum = endNum + 10;

  if (endNum > numResults) {
    endNum = numResults;
  }
  String numberResights = "";
  if (request.getParameter("numResights") != null) {
    numberResights = "&numResights=" + request.getParameter("numResights");
  }
  String qString = request.getQueryString();
  int startNumIndex = qString.indexOf("&startNum");
  if (startNumIndex > -1) {
    qString = qString.substring(0, startNumIndex);
  }

%>
<table width="810px">
  <tr>
    <%
      if ((startNum - 10) > 1) {
    %>
    <td align="left">
      <p>
        <a
          href="searchResults.jsp?<%=qString%><%=numberResights%>&startNum=<%=(startNum-20)%>&endNum=<%=(startNum-11)%>"><img
          src="../images/Black_Arrow_left.png" width="28" height="28" border="0" align="absmiddle"
          title="<%=encprops.getProperty("seePreviousResults")%>"/></a> <a
        href="searchResults.jsp?<%=qString%><%=numberResights%>&startNum=<%=(startNum-20)%>&endNum=<%=(startNum-11)%>"><%=(startNum - 20)%>
        - <%=(startNum - 11)%>
      </a>
      </p></td>
    <%
     }
      if (startNum < numResults) {
    %>
    <td align="right">
      <p><a
        href="searchResults.jsp?<%=qString%><%=numberResights%>&startNum=<%=startNum%>&endNum=<%=endNum%>"><%=startNum%>
        - <%=endNum%>
      </a> <a
        href="searchResults.jsp?<%=qString%><%=numberResights%>&startNum=<%=startNum%>&endNum=<%=endNum%>"><img
        src="../images/Black_Arrow_right.png" border="0" align="absmiddle"
        title="<%=encprops.getProperty("seeNextResults")%>"/></a>

      </p>
    </td>
    <%
      }
    %>
  </tr>
</table>

<p><strong><%=encprops.getProperty("exportOptions")%></strong></p>
<p><%=encprops.getProperty("exportedOBIS")%>: <a href="http://<%=CommonConfiguration.getURLLocation(request)%>/EncounterSearchExportExcelFile?<%=request.getQueryString()%>"><%=encprops.getProperty("clickHere")%></a><br />
<%=encprops.getProperty("exportedOBISLocales")%>: <a href="http://<%=CommonConfiguration.getURLLocation(request)%>/EncounterSearchExportExcelFile?<%=request.getQueryString()%>&locales=trues"><%=encprops.getProperty("clickHere")%></a>
</p>

<p><%=encprops.getProperty("exportedEmail")%>: <a
  href="http://<%=CommonConfiguration.getURLLocation(request)%>/EncounterSearchExportEmailAddresses?<%=request.getQueryString()%>"><%=encprops.getProperty("clickHere")%>
</a>
</p>

<p><%=encprops.getProperty("exportedGeneGIS")%>: <a href="http://<%=CommonConfiguration.getURLLocation(request)%>/EncounterSearchExportGeneGISFormat?<%=request.getQueryString()%>">
<%=encprops.getProperty("clickHere")%></a>
</p>


<p>
<table width="810" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left">
      <p><strong><%=encprops.getProperty("matchingEncounters")%>
      </strong>: <%=numResults%>
        <%
          if (request.isUserInRole("admin")) {
        %>
        <br/>
        <%=numUniqueEncounters%> <%=encprops.getProperty("identifiedUnique")%><br/>
        <%=numUnidentifiedEncounters%> <%=encprops.getProperty("unidentified")%><br/>
        <%=(numDuplicateEncounters)%> <%=encprops.getProperty("dailyDuplicates")%>
        <%
          }
        %>
      </p>
      <%
        myShepherd.beginDBTransaction();
      %>
      <p><strong><%=encprops.getProperty("totalEncounters")%>
      </strong>: <%=(myShepherd.getNumEncounters() + (myShepherd.getNumUnidentifiableEncounters()))%>
      </p>
    </td>
    <%
      myShepherd.rollbackDBTransaction();
    %>
  </tr>
</table>

<table>
  <tr>
    <td align="left">

      <p><strong><%=encprops.getProperty("queryDetails")%>
      </strong></p>

      <p class="caption"><strong><%=encprops.getProperty("prettyPrintResults") %>
      </strong><br/>
        <%=queryResult.getQueryPrettyPrint().replaceAll("locationField", encprops.getProperty("location")).replaceAll("locationCodeField", encprops.getProperty("locationID")).replaceAll("verbatimEventDateField", encprops.getProperty("verbatimEventDate")).replaceAll("alternateIDField", encprops.getProperty("alternateID")).replaceAll("behaviorField", encprops.getProperty("behavior")).replaceAll("Sex", encprops.getProperty("sex")).replaceAll("nameField", encprops.getProperty("nameField")).replaceAll("selectLength", encprops.getProperty("selectLength")).replaceAll("numResights", encprops.getProperty("numResights")).replaceAll("vesselField", encprops.getProperty("vesselField"))%>
      </p>

      <p class="caption"><strong><%=encprops.getProperty("jdoql")%>
      </strong><br/>
        <%=queryResult.getJDOQLRepresentation()%>
      </p>

    </td>
  </tr>
</table>


</p>
<br>

<%
  myShepherd.rollbackDBTransaction();
  myShepherd.closeDBTransaction();
  rEncounters = null;

%>
<jsp:include page="../footer.jsp" flush="true"/>
</div>
</div>
<!-- end page --></div>
<!--end wrapper -->

</body>
</html>




