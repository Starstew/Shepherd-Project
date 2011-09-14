<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
        <%@ page contentType="text/html; charset=utf-8" language="java" import="org.ecocean.*,java.util.Properties, java.io.FileInputStream, java.io.File, java.io.FileNotFoundException" %>
<%

//setup our Properties object to hold all properties
	Properties props=new Properties();
	String langCode="en";
	
	
	Shepherd myShepherd=new Shepherd();
	
	//set up the file input stream
	//FileInputStream propsInputStream=new FileInputStream(new File(myShepherd.getWebServerInstallDirectory()+"/webapps/ROOT/WEB-INF/classes/bundles/"+langCode+"/submit.properties"));
	//props.load(propsInputStream);
	props.load(getClass().getResourceAsStream("/bundles/"+langCode+"/submit.properties"));
	
	
	//load our variables for the submit page
	String title=props.getProperty("submit_title");
	String submit_maintext=props.getProperty("submit_maintext");
	String submit_reportit=props.getProperty("reportit");
	String submit_language=props.getProperty("language");
	String what_do=props.getProperty("what_do");
	String read_overview=props.getProperty("read_overview");
	String see_all_encounters=props.getProperty("see_all_encounters");
	String see_all_sharks=props.getProperty("see_all_sharks");
	String report_encounter=props.getProperty("report_encounter");
	String log_in=props.getProperty("log_in");
	String contact_us=props.getProperty("contact_us");
	String search=props.getProperty("search");
	String encounter=props.getProperty("encounter");
	String shark=props.getProperty("shark");
	String join_the_dots=props.getProperty("join_the_dots");
	String menu=props.getProperty("menu");
	String last_sightings=props.getProperty("last_sightings");
	String more=props.getProperty("more");
	String ws_info=props.getProperty("ws_info");
	String about=props.getProperty("about");
	String contributors=props.getProperty("contributors");
	String forum=props.getProperty("forum");
	String blog=props.getProperty("blog");
	String area=props.getProperty("area");
	String match=props.getProperty("match");
	
	//link path to submit page with appropriate language
	String submitPath="submit.jsp?langCode="+langCode;
	
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
<div id="page">
<jsp:include page="header.jsp" flush="true">
	<jsp:param name="isResearcher" value="<%=request.isUserInRole("researcher")%>"/>
	<jsp:param name="isManager" value="<%=request.isUserInRole("manager")%>"/>
	<jsp:param name="isReviewer" value="<%=request.isUserInRole("reviewer")%>"/>
	<jsp:param name="isAdmin" value="<%=request.isUserInRole("admin")%>"/>
</jsp:include>	
<div id="main">

	<div id="maincol-wide-solo">

		<div id="maintext">
		  <h1 class="intro">Photographing a polar bear</h1>
		</div>
			
			<p>By photographing a polar bear you can participate in research that will contribute to understanding and protecting this species at risk. The polar bear is listed as vulnerable to extinction in the <a href="http://www.iucnredlist.org/search/details.php/22823/all">IUCN Red List of Threatened Species</a>. Photographs of the distinctive whisker spot patterns or facial scars on polar bears are used to identify individuals for long-term tracking of movements and behavior.</p>
			<p>In many places, polar bears can be photographed safely from vehicles or viewing structures. Polar bears habituate quickly to vehicles, but you are less likely to disturb a stationary polar bear if your vehicle approaches the bear very slowly and at an indirect angle (the bear is not in the vehicle’s path). A bear that responds negatively to a vehicle approach expends energy reserves that may be critical (especially during the fasting season) and may be more difficult to photograph in the future.</p>
			<p>The most important photographs are sharp, clearly focused, close-ups of the bear’s facial profile, taken perpendicular to the camera’s axis. Here are 4 examples.<br />
		</p>
			<p><img src="images/fourbears.jpg" width="625" height="469" border="1" />	          </p>
			<p>Either the right profile (as all 4 examples above) or the left profile of a bear can be used (left or right is identified when you upload the image). We have found that photographs taken &gt; 50 m away from a polar bear (with a 6.0-megapixel camera and 300 mm lens) resulted in poor photographic quality. Other factors that can lessen the utility of a photo for our identification system include focus, lighting, and angle.</p>
			<p><img src="images/fourbears2.jpg" width="622" height="466" border="1" /></p>
			<p>Once an image is uploaded into our system, an image pre-processing algorithm automatically extracts the natural pattern of interest by standardizing and enhancing the image through a series of steps (a-d below).</p>
			<p align="center"><img src="images/fourbears3.jpg" width="250" height="239" /></p>
			<p align="left">A matching algorithm then compares the image pattern to every other image in our database. Similarity scores that reach a certain threshold are considered to be images of the same bear. Below are 2 images of the same bear taken in 2 different years, and the extracted image pattern for each.</p>
			<p align="left"><img src="images/fourbears4.jpg" width="624" height="301" /></p>
			<p align="left">Other distinguishing characteristics that can be used to identify individual polar bears, besides whisker spots, are facial scars.</p>
			<p align="left"><img src="images/fourbears5.jpg" width="624" height="470" border="0" /></p>
			<p align="left">Since scars must be identified manually (a process more prone to error than our automated whisker spot pattern analysis), they should be matched with whisker spot photographs of the same bear to help confirm the identity.</p>
			<p>More information on polar bears:<br />
			  <a href="http://www.worldwildlife.org/species/finder/polarbear/polarbear.html">http://www.worldwildlife.org/species/finder/polarbear/polarbear.html</a><br />
	    <a href="http://www.polarbearsinternational.org">http://www.polarbearsinternational.org</a></p>
			<p><br />
	          </p>
	</div>
	<!-- end maintext -->

  </div><!-- end maincol -->

<jsp:include page="footer.jsp" flush="true" />
</div><!-- end page -->
</div><!--end wrapper -->
</body>
</html>
