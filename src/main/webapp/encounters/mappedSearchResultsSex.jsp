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
         import="java.util.Vector,java.util.Properties,org.ecocean.genetics.*,java.util.*,java.net.URI, org.ecocean.*" %>



<html>
<head>



  <%


    //let's load encounterSearch.properties
    String langCode = "en";
    if (session.getAttribute("langCode") != null) {
      langCode = (String) session.getAttribute("langCode");
    }
    Properties encprops = new Properties();
    encprops.load(getClass().getResourceAsStream("/bundles/" + langCode + "/mappedSearchResults.properties"));

    Properties map_props = new Properties();
    map_props.load(getClass().getResourceAsStream("/bundles/" + langCode + "/mappedSearchResults.properties"));


    
    //get our Shepherd
    Shepherd myShepherd = new Shepherd();





    int numResults = 0;

    //set up the vector for matching encounters
    Vector rEncounters = new Vector();

    //kick off the transaction
    myShepherd.beginDBTransaction();

    //start the query and get the results
    String order = "";
    request.setAttribute("gpsOnly", "yes");
    EncounterQueryResult queryResult = EncounterQueryProcessor.processQuery(myShepherd, request, order);
    rEncounters = queryResult.getResult();
    
    //let's prep the HashTable for the pie chart
    ArrayList<String> allHaplos2=myShepherd.getAllHaplotypes(); 
    int numHaplos2 = allHaplos2.size();
    Hashtable<String,Integer> pieHashtable = new Hashtable<String,Integer>();
 	for(int gg=0;gg<numHaplos2;gg++){
 		String thisHaplo=allHaplos2.get(gg);
 		pieHashtable.put(thisHaplo, new Integer(0));
 	}
    		
    		
  %>

  <title><%=CommonConfiguration.getHTMLTitle()%>
  </title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <meta name="Description" content="<%=CommonConfiguration.getHTMLDescription()%>"/>
  <meta name="Keywords" content="<%=CommonConfiguration.getHTMLKeywords()%>"/>
  <meta name="Author" content="<%=CommonConfiguration.getHTMLAuthor()%>"/>
  <link href="<%=CommonConfiguration.getCSSURLLocation(request)%>" rel="stylesheet" type="text/css"/>
  <link rel="shortcut icon" href="<%=CommonConfiguration.getHTMLShortcutIcon()%>"/>


    <style type="text/css">
      body {
        margin: 0;
        padding: 10px 20px 20px;
        font-family: Arial;
        font-size: 16px;
      }
      
      .full_screen_map {
position: absolute !important;
top: 0px !important;
left: 0px !important;
z-index: 1 !imporant;
width: 100% !important;
height: 100% !important;
margin-top: 0px !important;
margin-bottom: 8px !important;
}


    </style>
  

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
  
      <script>
        function getQueryParameter(name) {
          name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
          var regexS = "[\\?&]" + name + "=([^&#]*)";
          var regex = new RegExp(regexS);
          var results = regex.exec(window.location.href);
          if (results == null)
            return "";
          else
            return results[1];
        }
  </script>
  
  

    <script src="http://maps.google.com/maps/api/js?sensor=false"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.1/jquery.min.js"></script>
  
<script type="text/javascript" src="StyledMarker.js"></script>


    <script type="text/javascript">
      function initialize() {
        var center = new google.maps.LatLng(0,0);
        var mapZoom = 1;
    	if($("#map_canvas").hasClass("full_screen_map")){mapZoom=3;}
    	var bounds = new google.maps.LatLngBounds();
        
        var map = new google.maps.Map(document.getElementById('map_canvas'), {
          zoom: mapZoom,
          center: center,
          mapTypeId: google.maps.MapTypeId.HYBRID
        });

    	  //adding the fullscreen control to exit fullscreen
    	  var fsControlDiv = document.createElement('DIV');
    	  var fsControl = new FSControl(fsControlDiv, map);
    	  fsControlDiv.index = 1;
    	  map.controls[google.maps.ControlPosition.TOP_RIGHT].push(fsControlDiv);

        
        var markers = [];
 
 
        
        <%
        //Vector haveGPSData = new Vector();
        int rEncountersSize=rEncounters.size();
        int count = 0;

          
      
        
      
if(rEncounters.size()>0){
	int havegpsSize=rEncounters.size();
 for(int y=0;y<havegpsSize;y++){
	 Encounter thisEnc=(Encounter)rEncounters.get(y);
	 

 %>
          
          var latLng = new google.maps.LatLng(<%=thisEnc.getDecimalLatitude()%>, <%=thisEnc.getDecimalLongitude()%>);
          bounds.extend(latLng);
           <%

           
           //currently unused programatically
           String markerText="";
           
           String haploColor="CC0000";
           if((encprops.getProperty("defaultMarkerColor")!=null)&&(!encprops.getProperty("defaultMarkerColor").trim().equals(""))){
        	   haploColor=encprops.getProperty("defaultMarkerColor");
           }
		   
           //map by sex
           if(thisEnc.getSex().equals("male")){haploColor="0000FF";}
           else if(thisEnc.getSex().equals("female")){haploColor="FF00FF";}

           
           %>
           var marker = new StyledMarker({styleIcon:new StyledIcon(StyledIconTypes.MARKER,{color:"<%=haploColor%>",text:"<%=markerText%>"}),position:latLng,map:map});
	    

            google.maps.event.addListener(marker,'click', function() {
                 (new google.maps.InfoWindow({content: '<strong><a target=\"_blank\" href=\"http://<%=CommonConfiguration.getURLLocation(request)%>/individuals.jsp?number=<%=thisEnc.isAssignedToMarkedIndividual()%>\"><%=thisEnc.isAssignedToMarkedIndividual()%></a></strong><br /><table><tr><td><img align=\"top\" border=\"1\" src=\"/<%=CommonConfiguration.getDataDirectoryName()%>/encounters/<%=thisEnc.getEncounterNumber()%>/thumb.jpg\"></td><td>Date: <%=thisEnc.getDate()%><br />Sex: <%=thisEnc.getSex()%><%if(thisEnc.getSizeAsDouble()!=null){%><br />Size: <%=thisEnc.getSize()%> m<%}%><br /><br /><a target=\"_blank\" href=\"http://<%=CommonConfiguration.getURLLocation(request)%>/encounters/encounter.jsp?number=<%=thisEnc.getEncounterNumber()%>\" >Go to encounter</a></td></tr></table>'})).open(map, this);
             });
 
	
          markers.push(marker);
          map.fitBounds(bounds); 
 
 <%
 
	 }
} 

myShepherd.rollbackDBTransaction();
 %>
 
 //markerClusterer = new MarkerClusterer(map, markers, {gridSize: 10});

      }
      
      

      function fullScreen(){
    		$("#map_canvas").addClass('full_screen_map');
    		$('html, body').animate({scrollTop:0}, 'slow');
    		initialize();
    		
    		//hide header
    		$("#header_menu").hide();
    		
    		if(overlaysSet){overlaysSet=false;setOverlays();}
    		//alert("Trying to execute fullscreen!");
    	}


    	function exitFullScreen() {
    		$("#header_menu").show();
    		$("#map_canvas").removeClass('full_screen_map');

    		initialize();
    		if(overlaysSet){overlaysSet=false;setOverlays();}
    		//alert("Trying to execute exitFullScreen!");
    	}


    	//making the exit fullscreen button
    	function FSControl(controlDiv, map) {

    	  // Set CSS styles for the DIV containing the control
    	  // Setting padding to 5 px will offset the control
    	  // from the edge of the map
    	  controlDiv.style.padding = '5px';

    	  // Set CSS for the control border
    	  var controlUI = document.createElement('DIV');
    	  controlUI.style.backgroundColor = '#f8f8f8';
    	  controlUI.style.borderStyle = 'solid';
    	  controlUI.style.borderWidth = '1px';
    	  controlUI.style.borderColor = '#a9bbdf';;
    	  controlUI.style.boxShadow = '0 1px 3px rgba(0,0,0,0.5)';
    	  controlUI.style.cursor = 'pointer';
    	  controlUI.style.textAlign = 'center';
    	  controlUI.title = 'Toggle the fullscreen mode';
    	  controlDiv.appendChild(controlUI);

    	  // Set CSS for the control interior
    	  var controlText = document.createElement('DIV');
    	  controlText.style.fontSize = '12px';
    	  controlText.style.fontWeight = 'bold';
    	  controlText.style.color = '#000000';
    	  controlText.style.paddingLeft = '4px';
    	  controlText.style.paddingRight = '4px';
    	  controlText.style.paddingTop = '3px';
    	  controlText.style.paddingBottom = '2px';
    	  controlUI.appendChild(controlText);
    	  //toggle the text of the button
    	   if($("#map_canvas").hasClass("full_screen_map")){
    	      controlText.innerHTML = 'Exit Fullscreen';
    	    } else {
    	      controlText.innerHTML = 'Fullscreen';
    	    }

    	  // Setup the click event listeners: toggle the full screen

    	  google.maps.event.addDomListener(controlUI, 'click', function() {

    	   if($("#map_canvas").hasClass("full_screen_map")){
    	    exitFullScreen();
    	    } else {
    	    fullScreen();
    	    }
    	  });

    	}

      
      
      google.maps.event.addDomListener(window, 'load', initialize);
    </script>

    
  </head>
 <body onunload="GUnload()">
 <div id="wrapper">
 <div id="page">
<jsp:include page="../header.jsp" flush="true">

  <jsp:param name="isAdmin" value="<%=request.isUserInRole(\"admin\")%>" />
</jsp:include>
 <div id="main">
 
 <ul id="tabmenu">
 
   <li><a href="searchResults.jsp?<%=request.getQueryString() %>"><%=encprops.getProperty("table")%>
   </a></li>
   <li><a href="thumbnailSearchResults.jsp?<%=request.getQueryString() %>"><%=encprops.getProperty("matchingImages")%>
   </a></li>
   <li><a class="active"><%=encprops.getProperty("mappedResults") %>
   </a></li>
   <li><a href="../xcalendar/calendar2.jsp?<%=request.getQueryString() %>"><%=encprops.getProperty("resultsCalendar")%>
   </a></li>
         <li><a href="searchResultsAnalysis.jsp?<%=request.getQueryString() %>"><%=encprops.getProperty("analysis")%>
   </a></li>
      <li><a
     href="exportSearchResults.jsp?<%=request.getQueryString() %>"><%=encprops.getProperty("export")%>
   </a></li>
 
 </ul>
 <table width="810px" border="0" cellspacing="0" cellpadding="0">
   <tr>
     <td>
       <br/>
 
       <h1 class="intro"><%=encprops.getProperty("title")%>
       </h1>
     </td>
   </tr>
</table>
 
 
 
 
 <br />
 
 
 
 
 <p><strong>
 	<img src="../images/2globe_128.gif" width="64" height="64" align="absmiddle"/> <%=encprops.getProperty("mappedResults")%>
 </strong>
 <%
 
 //read from the encprops property file the value determining how many entries to map. Thousands can cause map delay or failure from Google.
 int numberResultsToMap = -1;

 %>
 </p>
 <p><%=map_props.getProperty("aspects") %>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <%
  boolean hasMoreProps=true;
  int propsNum=0;
  while(hasMoreProps){
	if((map_props.getProperty("displayAspectName"+propsNum)!=null)&&(map_props.getProperty("displayAspectFile"+propsNum)!=null)){
		%>
		<a href="<%=map_props.getProperty("displayAspectFile"+propsNum)%>?<%=request.getQueryString()%>"><%=map_props.getProperty("displayAspectName"+propsNum) %></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		
		<%
		propsNum++;
	}
	else{hasMoreProps=false;}
  }
  %>
</p>
 <%
   if (rEncounters.size() > 0) {
     myShepherd.beginDBTransaction();
     try {
 %>
 
<p><%=encprops.getProperty("mapNote")%></p>
 
 <div id="map-container">
 
 
 <table cellpadding="3">
 <tr>
 <td valign="top">
  <<div id="map_canvas" style="width: 770px; height: 510px; "></div>
 </td>
 <td valign="top">
 <table>
 <tr><th>Color Key</th></tr>
<%
String haploColor="CC0000";
if((encprops.getProperty("defaultMarkerColor")!=null)&&(!encprops.getProperty("defaultMarkerColor").trim().equals(""))){
	   haploColor=encprops.getProperty("defaultMarkerColor");
}
%>
	<tr bgcolor="#0000FF"><td><strong>Male</strong></td></tr>
	<tr bgcolor="#FF00FF"><td><strong>Female</strong></td></tr>
	<tr bgcolor="#<%=haploColor%>"><td><strong>Unknown</strong></td></tr>
 </table>
 </td>
 </tr>
 </table>

 

 <div id="chart_div"></div>

 </div>
 

 
 <%
 
     } 
     catch (Exception e) {
       e.printStackTrace();
     }
 
   }
 else {
 %>
 <p><%=encprops.getProperty("noGPS")%></p>
 <%
 }  

 
 
   myShepherd.rollbackDBTransaction();
   myShepherd.closeDBTransaction();
   rEncounters = null;
   //haveGPSData = null;
 
%>
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
 
 <jsp:include page="../footer.jsp" flush="true"/>
</div>
</div>
<!-- end page --></div>
<!--end wrapper -->

</body>
</html>
