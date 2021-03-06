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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=iso-8859-1" language="java"
         import="org.ecocean.CommonConfiguration" %>
<%@ page import="org.ecocean.Shepherd" %>

<%
  //handle some cache-related security
  response.setHeader("Cache-Control", "no-cache"); //Forces caches to obtain a new copy of the page from the origin server
  response.setHeader("Cache-Control", "no-store"); //Directs caches not to store the page under any circumstance
  response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
  response.setHeader("Pragma", "no-cache"); //HTTP 1.0 backward compatibility
  Shepherd myShepher = new Shepherd();
%>

<html>
<head>
  <title><%=CommonConfiguration.getHTMLTitle() %>
  </title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <meta name="Description"
        content="<%=CommonConfiguration.getHTMLDescription() %>"/>
  <meta name="Keywords"
        content="<%=CommonConfiguration.getHTMLKeywords() %>"/>
  <meta name="Author" content="<%=CommonConfiguration.getHTMLAuthor() %>"/>
  <link href="<%=CommonConfiguration.getCSSURLLocation(request) %>"
        rel="stylesheet" type="text/css"/>
  <link rel="shortcut icon"
        href="<%=CommonConfiguration.getHTMLShortcutIcon() %>"/>
  <style type="text/css">
    <!--
    .style1 {
      color: #FF0000
    }

    -->
  </style>
</head>

<body>
<div id="wrapper">
  <div id="page">
    <jsp:include page="../header.jsp" flush="true">
      <jsp:param name="isResearcher"
                 value="<%=request.isUserInRole("researcher")%>"/>
      <jsp:param name="isManager"
                 value="<%=request.isUserInRole("manager")%>"/>
      <jsp:param name="isReviewer"
                 value="<%=request.isUserInRole("reviewer")%>"/>
      <jsp:param name="isAdmin" value="<%=request.isUserInRole("admin")%>"/>
    </jsp:include>
    <div id="main">
      <p>

      <h1 class="intro"><%=request.getParameter("number")%> Results</h1>
      </p>
      <p><a
        href="<%=request.getParameter("number")%>_tuningTaskOutput.xls"
        title="<%=request.getParameter("number")%>_tuningTaskOutput.xls">Right-click
        here to download the xls file to your Desktop.</a></p>

      <p><a
        href="boostedResults_<%=request.getParameter("number")%>.train"
        title="boostedResults_<%=request.getParameter("number")%>.train">Right-click
        here to download boostedResults_<%=request.getParameter("number")%>.train
        (left sides) to your Desktop.</a></p>

      <p><a href="boostedResults_<%=request.getParameter("number")%>.test"
            title="boostedResults_<%=request.getParameter("number")%>.test">Right-click
        here to download boostedResults_<%=request.getParameter("number")%>.test
        (right sides) to your Desktop.</a></p>

      <p>&nbsp;</p>

      <p>&nbsp;</p>

      <p>&nbsp;</p>

      <p>&nbsp;</p>
      <jsp:include page="../footer.jsp" flush="true"/>
    </div>
  </div>
  <!-- end page --></div>
<!--end wrapper -->
</body>
</html>


