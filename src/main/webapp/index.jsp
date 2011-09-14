<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java"
	import="org.ecocean.*,org.ecocean.grid.GridManager,org.ecocean.grid.GridManagerFactory,java.util.Properties, java.io.FileInputStream, java.io.File, java.io.FileNotFoundException, java.io.IOException"%>




<%

//grab a gridManager
GridManager gm=GridManagerFactory.getGridManager();
int numProcessors = gm.getNumProcessors();
int numWorkItems = gm.getIncompleteWork().size();

Shepherd myShepherd=new Shepherd();

//setup our Properties object to hold all properties
	
	//language setup
	String langCode="en";
	if(session.getAttribute("langCode")!=null){langCode=(String)session.getAttribute("langCode");}

	Properties props=new Properties();
	props.load(getClass().getResourceAsStream("/bundles/"+langCode+"/overview.properties"));
	
	

%>

<html>
<head>
  <title><%=CommonConfiguration.getHTMLTitle()%>
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

  <link rel="shortcut icon" href="images/favicon.ico"/>
  <style type="text/css">
    <!--

    table.adopter {
      border-width: 1px 1px 1px 1px;
      border-spacing: 0px;
      border-style: solid solid solid solid;
      border-color: black black black black;
      border-collapse: separate;
      background-color: white;
    }

    table.adopter td {
      border-width: 1px 1px 1px 1px;
      padding: 3px 3px 3px 3px;
      border-style: none none none none;
      border-color: gray gray gray gray;
      background-color: white;
      -moz-border-radius: 0px 0px 0px 0px;
      font-size: 12px;
      color: #330099;
    }

    table.adopter td.name {
      font-size: 12px;
      text-align: center;
    }

    table.adopter td.image {
      padding: 0px 0px 0px 0px;
    }

    .style2 {
      font-size: x-small;
      color: #000000;
    }

    -->
  </style>

</head>

<body>
<div id="wrapper">
<div id="page"><jsp:include page="header.jsp" flush="true">
	<jsp:param name="isResearcher"
		value="<%=request.isUserInRole("researcher")%>" />
	<jsp:param name="isManager"
		value="<%=request.isUserInRole("manager")%>" />
	<jsp:param name="isReviewer"
		value="<%=request.isUserInRole("reviewer")%>" />
	<jsp:param name="isAdmin" value="<%=request.isUserInRole("admin")%>" />
</jsp:include>
<div id="main">
<div id="leftcol">
<div id="menu">



	    <div class="module">
		 <h3>Photographing</h3>
			 <table align="left" cellpadding="2">
			   <tr>
			   	<td align="left" valign="top">
			   		<img src="images/whisker_left.gif" align="top"/>
			   	</td>
		   		
			   </tr>	   
			   <tr>
			   	<td><span class="caption style1"><strong>Anchor points for a whisker spot pattern</strong></span>
		   		</td>
			   </tr>
			   		    <tr>
			 			   	<td align="left" valign="top">
			 			   		<img src="images/whiskerspots.gif" align="top"/>
			 			   	</td>
			 		   		
			   </tr>
			   	   <tr>
			   			   	<td><strong><span class="caption style1">Extracted whisker spots can be compared using a computer algorithm</span>
			   		   		</strong></td>
			   </tr>
			 </table>
			

        </div>


</div>
<!-- end menu --></div>
<!-- end leftcol -->
<div id="maincol">
<div id="maintext">
<h1 class="intro">Polar Bear Photo-identification Library</h1>
<p>The Polar Bear Photo-identification Library is a visual database of polar bear (<em>Ursus maritimus</em>) encounters and of individually catalogued polar bears. The library is maintained and used by biologists to collect and analyze polar bear encounter data to learn more about the behavior and movements of this threatened species. The Library uses photographs of the polar bear's facial profile (left or right side) to distinguish among individuals based on whisker spot patterns and scars. Customized image pattern recognition software developed using computer vision techniques allow rapid identification of individual polar bears. </p>
<p>You can assist with polar bear research by submitting photos and sighting information. The information you submit will be used in studies on polar bear populations to help understand and conserve this species.<br/>
</p>
</div>
</div>
<!-- end maincol -->
<div id="rightcol">

	    <div class="module">
		 <h3>Latest News </h3>
			 <table align="left" cellpadding="2">
			   <tr><td align="left" valign="top"><a href="http://www.smithsonianmag.com/science-nature/wild-things-200803.html"><img src="images/NationalGeographic_logo.jpg" alt="ECOCEAN Library in USA Weekend " width="75" height="75" border="1" align="top" title="National Geographic November 2007"/></a></td>
		   <td><span class="caption"> Tracking whale sharks and polar bears in <a href="http://news.nationalgeographic.com/news/2008/08/080825-whale-sharks-missions.html">National Geographic News</a>. </span></td>
			   </tr></table>
			 <br />
	      <br /> 
        </div>


 <div class="module">
		 	<h3>Find Record</h3>
		   
		 	<form name="form2" method="get" action="individuals.jsp">
		 	<em>Enter a marked animal number, encounter number, animal nickname, or alternate ID.</em><br/>
		 	<input name="number" type="text" id="shark" size="25" />
		 	<input type="hidden" name="langCode" value="<%=langCode%>" /><br/>
		 	<input name="Go" type="submit" id="Go2" value="Go" />
		 	</form>
			
	  </div>
		 
		 
<div class="module">
<h3>RSS/Atom Feeds</h3>
<p align="left"><a href="rss.xml"><img src="images/rssfeed.gif"
	width="80" height="15" border="0" alt="RSS News Feed" /></a></p>
<p align="left"><a href="atom.xml"><img
	src="images/atom-feed-icon.gif" border="0" alt="ATOM News Feed" /></a></p>
</div>

<!-- end rightcol --></div>
<!-- end main --> <jsp:include page="footer.jsp" flush="true" /></div>
<!-- end page --></div>
<!--end wrapper -->

</body>
</html>
