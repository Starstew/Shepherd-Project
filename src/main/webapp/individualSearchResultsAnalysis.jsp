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
         import="javax.jdo.*,org.ecocean.genetics.*,java.util.*,java.net.URI, org.ecocean.*" %>



<html>
<head>



  <%


    //let's load encounterSearch.properties
    String langCode = "en";
    if (session.getAttribute("langCode") != null) {
      langCode = (String) session.getAttribute("langCode");
    }
    Properties encprops = new Properties();
    encprops.load(getClass().getResourceAsStream("/bundles/" + langCode + "/individualSearchResultsAnalysis.properties"));

    Properties haploprops = new Properties();
    haploprops.load(getClass().getResourceAsStream("/bundles/haplotypeColorCodes.properties"));

    
    //get our Shepherd
    Shepherd myShepherd = new Shepherd();

    int numResults = 0;


    //kick off the transaction
    myShepherd.beginDBTransaction();

    //start the query and get the results
    String order = "";
    Vector<MarkedIndividual> rIndividuals = new Vector<MarkedIndividual>();
    myShepherd.beginDBTransaction();

    MarkedIndividualQueryResult result = IndividualQueryProcessor.processQuery(myShepherd, request, order);
    rIndividuals = result.getResult();
    
    //let's prep the HashTable for the haplo pie chart
    ArrayList<String> allHaplos2=myShepherd.getAllHaplotypes(); 
    int numHaplos2 = allHaplos2.size();
    Hashtable<String,Integer> pieHashtable = new Hashtable<String,Integer>();
 	for(int gg=0;gg<numHaplos2;gg++){
 		String thisHaplo=allHaplos2.get(gg);
 		pieHashtable.put(thisHaplo, new Integer(0));
 	}
 	
 	//let's prep the max years between sightings column chart
	Query yearsCoverageQuery=myShepherd.getPM().newQuery(result.getJDOQLRepresentation().replaceFirst("SELECT FROM","SELECT max(maxYearsBetweenResightings) FROM"));
 	int numYearsCoverage=0;
 	try{numYearsCoverage=1+((Integer)yearsCoverageQuery.execute()).intValue();yearsCoverageQuery.closeAll();}
 	catch(Exception e){
 		e.printStackTrace();
 		yearsCoverageQuery.closeAll();
 	}
 	int[] resightingYearsArray=new int[numYearsCoverage];
 	for(int t=0;t<numYearsCoverage;t++){
 		resightingYearsArray[t]=0;
 	}
    
 	//let's prep the HashTable for the sex pie chart
 	Hashtable<String,Integer> sexHashtable = new Hashtable<String,Integer>();
 	sexHashtable.put("male", new Integer(0));
 	sexHashtable.put("female", new Integer(0));
 	sexHashtable.put("unknown", new Integer(0));
 	
 	//let's prep for the firstSightings table
 	Hashtable<String,Integer> firstSightingsHashtable = new Hashtable<String,Integer>();
 	firstSightingsHashtable.put("First sighting", new Integer(0));
 	firstSightingsHashtable.put("Previously sighted", new Integer(0));

	 Float maxTravelDistance=new Float(0);
	 long maxTimeBetweenResights=0;
	 String longestResightedIndividual="";
	 String farthestTravelingIndividual="";
	 
 	int resultSize=rIndividuals.size();
 	 for(int y=0;y<resultSize;y++){
 		MarkedIndividual thisEnc=(MarkedIndividual)rIndividuals.get(y);
 		 //haplotype ie chart prep
 		 if(thisEnc.getHaplotype()!=null){
      	   if(pieHashtable.containsKey(thisEnc.getHaplotype().trim())){
      		   Integer thisInt = pieHashtable.get(thisEnc.getHaplotype().trim())+1;
      		   pieHashtable.put(thisEnc.getHaplotype().trim(), thisInt);
      	   }
 	 	}
 		 
 	    //sex pie chart 	 
 		if(thisEnc.getSex().equals("male")){
 		   Integer thisInt = sexHashtable.get("male")+1;
  		   sexHashtable.put("male", thisInt);
 		}
 		else if(thisEnc.getSex().equals("female")){
  		   Integer thisInt = sexHashtable.get("female")+1;
  		   sexHashtable.put("female", thisInt);
 		}
 	    else{
 	    	Integer thisInt = sexHashtable.get("unknown")+1;
   		    sexHashtable.put("unknown", thisInt);
 	    }
 	    
		 //max distance calc
		 if (thisEnc.getMaxDistanceBetweenTwoSightings()>maxTravelDistance){
			 maxTravelDistance=thisEnc.getMaxDistanceBetweenTwoSightings();
			 farthestTravelingIndividual=thisEnc.getIndividualID();
		 }
		 
		 //max time calc
		 if (thisEnc.getMaxTimeBetweenTwoSightings()>maxTimeBetweenResights){
			 maxTimeBetweenResights=thisEnc.getMaxTimeBetweenTwoSightings();
			 longestResightedIndividual=thisEnc.getIndividualID();
		 }
		 
		 //maxYearsBetweenSightings calc
		 resightingYearsArray[thisEnc.getMaxNumYearsBetweenSightings()]++;
		 
		 //firstSightings distribution
		 if(thisEnc.getEarliestSightingTime()<(new GregorianCalendar(Integer.parseInt(request.getParameter("year1")),Integer.parseInt(request.getParameter("month1")),Integer.parseInt(request.getParameter("day1")))).getTimeInMillis()){
	 		   Integer thisInt = firstSightingsHashtable.get("Previously sighted")+1;
	  		   firstSightingsHashtable.put("Previously sighted", thisInt);
	 		   
		 }
		 else{
			 Integer thisInt = firstSightingsHashtable.get("First sighting")+1;
	  		   firstSightingsHashtable.put("First sighting", thisInt);
		 }
		 
 		 
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



      #map {
        width: 600px;
        height: 400px;
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
  



    
<script type="text/javascript" src="https://www.google.com/jsapi"></script>

<script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawHaploChart);
      function drawHaploChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Haplotype');
        data.addColumn('number', 'No. Recorded');
        data.addRows([
          <%
          ArrayList<String> allHaplos=myShepherd.getAllHaplotypes(); 
          int numHaplos = allHaplos.size();
          

          
          for(int hh=0;hh<numHaplos;hh++){
          %>
          ['<%=allHaplos.get(hh)%>',    <%=pieHashtable.get(allHaplos.get(hh))%>],
		  <%
          }
		  %>
          
        ]);

        var options = {
          width: 450, height: 300,
          title: 'Haplotypes in Matched Encounters',
          colors: [
                   <%
                   String haploColor="CC0000";
                   if((encprops.getProperty("defaultMarkerColor")!=null)&&(!encprops.getProperty("defaultMarkerColor").trim().equals(""))){
                	   haploColor=encprops.getProperty("defaultMarkerColor");
                   }   

                   
                   for(int yy=0;yy<numHaplos;yy++){
                       String haplo=allHaplos.get(yy);
                       if((haploprops.getProperty(haplo)!=null)&&(!haploprops.getProperty(haplo).trim().equals(""))){
                     	  haploColor = haploprops.getProperty(haplo);
                        }
					%>
					'#<%=haploColor%>',
					<%
                   }
                   %>
                   
                   
          ]
        };

        var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
        chart.draw(data, options);
      }
      
      google.setOnLoadCallback(drawSexChart);
      function drawSexChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Sex');
        data.addColumn('number', 'No. Recorded');
        data.addRows([

          ['male',    <%=sexHashtable.get("male")%>],
           ['female',    <%=sexHashtable.get("female")%>],
           ['unknown',    <%=sexHashtable.get("unknown")%>],
          
        ]);

        <%
        haploColor="CC0000";
        if((encprops.getProperty("defaultMarkerColor")!=null)&&(!encprops.getProperty("defaultMarkerColor").trim().equals(""))){
     	   haploColor=encprops.getProperty("defaultMarkerColor");
        }
        
        %>
        var options = {
          width: 450, height: 300,
          title: 'Sex Distribution in Matched Encounters',
          colors: ['#0000FF','#FF00FF','<%=haploColor%>']
        };

        var chart = new google.visualization.PieChart(document.getElementById('sexchart_div'));
        chart.draw(data, options);
      }
      
      
      google.setOnLoadCallback(drawFirstSightingChart);
      function drawFirstSightingChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Status');
        data.addColumn('number', 'No. Recorded');
        data.addRows([

          ['First sighting',    <%=firstSightingsHashtable.get("First sighting")%>],
           ['Previously sighted',    <%=firstSightingsHashtable.get("Previously sighted")%>],
           

        ]);


        var options = {
          width: 450, height: 300,
          title: 'New/Previously Sighted Distribution in Matched Individuals',
          colors: ['#336600','#CC9900']
        };

        var chart = new google.visualization.PieChart(document.getElementById('firstSighting_div'));
        chart.draw(data, options);
      }
      
      
      
      
      <%
      if(numYearsCoverage>0){
      %>
      //num years analysis
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawColumnChart);
      function drawColumnChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Calendar years between resights');
        data.addColumn('number', 'No. marked individuals');
        data.addRows([
        <%              
        for(int p=0;p<numYearsCoverage;p++){
        %>
          ['<%=p%>', <%=resightingYearsArray[p]%>],
		<%
        }
		%>
        ]);

        var options = {
          width: 400, height: 240,
          title: 'Calendar Years Between Resights for Matched Individuals',
          hAxis: {title: 'Distribution: Number of Years Between Resightings', titleTextStyle: {color: 'red'}}
        };

        var chart = new google.visualization.ColumnChart(document.getElementById('columnchart_div'));
        chart.draw(data, options);
      }
      <%
      }
      %>
      
      
</script>

    
  </head>
 <body onunload="GUnload()">
 <div id="wrapper">
 <div id="page">
<jsp:include page="header.jsp" flush="true">

  <jsp:param name="isAdmin" value="<%=request.isUserInRole(\"admin\")%>" />
</jsp:include>
 <div id="main">
 
 <ul id="tabmenu">
 
  <li><a href="individualSearchResults.jsp?<%=request.getQueryString().replaceAll("startNum","uselessNum").replaceAll("endNum","uselessNum") %>"><%=encprops.getProperty("table")%>
  </a></li>
  <li><a href="individualThumbnailSearchResults.jsp?<%=request.getQueryString().replaceAll("startNum","uselessNum").replaceAll("endNum","uselessNum") %>"><%=encprops.getProperty("matchingImages")%>
  </a></li>
  <li><a class="active"><%=encprops.getProperty("analysis")%>
  </a></li>
    <li><a href="individualSearchResultsExport.jsp?<%=request.getQueryString().replaceAll("startNum","uselessNum").replaceAll("endNum","uselessNum") %>"><%=encprops.getProperty("export")%>
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

<p>
Number matching marked individuals: <%=resultSize %>
</p>
<%
if(maxTravelDistance>0){
%>
<p>Marked individual with largest distance between resights: <a href="individuals.jsp?number=<%=farthestTravelingIndividual %>"><%=farthestTravelingIndividual %></a> (<%=(maxTravelDistance/1000) %> km)</p>
 <%
}
if(maxTimeBetweenResights>0){
	 //long maxTimeBetweenResights=0;
	 //String longestResightedIndividual="";
	 double bigTime=((double)maxTimeBetweenResights/1000/60/60/24/365);
%>
<p>Marked individual with longest time between resights: <a href="individuals.jsp?number=<%=longestResightedIndividual %>"><%=longestResightedIndividual %></a> (<%=bigTime %> years)</p>
 <%
}

     try {
 %>
 


 <div id="chart_div"></div>

<div id="sexchart_div"></div>

<div id="firstSighting_div"></div>

<%
if(numYearsCoverage>0){
%>
 <div id="columnchart_div"></div>
 <%
}
     } 
     catch (Exception e) {
       e.printStackTrace();
     }
 



 
 
   myShepherd.rollbackDBTransaction();
   myShepherd.closeDBTransaction();
   rIndividuals = null;
 
%>
 <table>
  <tr>
    <td align="left">

      <p><strong><%=encprops.getProperty("queryDetails")%>
      </strong></p>

      <p class="caption"><strong><%=encprops.getProperty("prettyPrintResults") %>
      </strong><br/>
        <%=result.getQueryPrettyPrint().replaceAll("locationField", encprops.getProperty("location")).replaceAll("locationCodeField", encprops.getProperty("locationID")).replaceAll("verbatimEventDateField", encprops.getProperty("verbatimEventDate")).replaceAll("alternateIDField", encprops.getProperty("alternateID")).replaceAll("behaviorField", encprops.getProperty("behavior")).replaceAll("Sex", encprops.getProperty("sex")).replaceAll("nameField", encprops.getProperty("nameField")).replaceAll("selectLength", encprops.getProperty("selectLength")).replaceAll("numResights", encprops.getProperty("numResights")).replaceAll("vesselField", encprops.getProperty("vesselField"))%>
      </p>

      <p class="caption"><strong><%=encprops.getProperty("jdoql")%>
      </strong><br/>
        <%=result.getJDOQLRepresentation()%>
      </p>

    </td>
  </tr>
</table>
 
 <jsp:include page="footer.jsp" flush="true"/>
</div>
</div>
<!-- end page --></div>
<!--end wrapper -->

</body>
</html>
