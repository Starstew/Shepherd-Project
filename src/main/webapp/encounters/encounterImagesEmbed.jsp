<%@ page contentType="text/html; charset=utf-8" language="java"
         import="org.ecocean.servlet.ServletUtilities, org.ecocean.*,org.ecocean.servlet.ServletUtilities,org.ecocean.Util,org.ecocean.Measurement, org.ecocean.Util.*, org.ecocean.genetics.*, org.ecocean.tag.*, java.awt.Dimension, javax.jdo.Extent, javax.jdo.Query, java.io.File, java.io.FileInputStream,java.text.DecimalFormat, java.util.*" %>
<%@ taglib uri="http://www.sunwesttek.com/di" prefix="di" %>
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

<%
String context="context0";
context=ServletUtilities.getContext(request);
Shepherd imageShepherd = new Shepherd(context);
imageShepherd.beginDBTransaction();
Extent allKeywords = imageShepherd.getPM().getExtent(Keyword.class, true);
Query kwImagesQuery = imageShepherd.getPM().newQuery(allKeywords);
try {

//get the encounter number
String imageEncNum = request.getParameter("encounterNumber");
	
//set up the JDO pieces and Shepherd


boolean haveRendered = false;

//let's set up references to our file system components
String rootWebappPath = getServletContext().getRealPath("/");
File webappsDir = new File(rootWebappPath).getParentFile();
File shepherdDataDir = new File(webappsDir, CommonConfiguration.getDataDirectoryName(context));
File encountersDir=new File(shepherdDataDir.getAbsolutePath()+"/encounters");


//handle translation
//String langCode = "en";
String langCode=ServletUtilities.getLanguageCode(request);


//let's load encounters.properties
Properties encprops = new Properties();
//encprops.load(getClass().getResourceAsStream("/bundles/" + langCode + "/encounter.properties"));
encprops=ShepherdProperties.getProperties("encounter.properties", langCode,context);


String baseDir = ServletUtilities.dataDir(context, rootWebappPath);
Encounter imageEnc=imageShepherd.getEncounter(imageEncNum);
File thisEncounterDir = new File(imageEnc.dir(baseDir));
String encUrlDir = "/" + CommonConfiguration.getDataDirectoryName(context) + imageEnc.dir("");



%>

 <script type="text/javascript">
  

  
      hs.graphicsDir = '../highslide/highslide/graphics/';
      hs.align = 'auto';
      hs.transitions = ['expand', 'crossfade'];
      hs.outlineType = 'rounded-white';
      hs.fadeInOut = true;
      hs.anchor = 'top';


    //block right-click user copying if no permissions available
    <%
    if(request.getUserPrincipal()!=null){
    %>
    hs.blockRightClick = false;
    <%
    }
    else{
    %>
    hs.blockRightClick = true;
	<%
    }
	%>
    // Add the controlbar
    hs.addSlideshow({
      //slideshowGroup: 'group1',
      interval: 5000,
      repeat: false,
      useControls: true,
      fixedControls: 'fit',
      overlayOptions: {
        opacity: 0.75,
        position: 'bottom center',
        hideOnMouseOut: true
      }
    });
    
  


  </script>




<h2><img align="absmiddle" src="../images/Crystal_Clear_device_camera.gif" width="37px"
                     height="25px">&nbsp;<%=encprops.getProperty("images")%></h2>
<p> <%
  if (session.getAttribute("logged") != null) {
%> <em><%=encprops.getProperty("click2view")%>
</em>
</p>
<%
  }
%>
<table>
<%
List<SinglePhotoVideo> images=imageShepherd.getAllSinglePhotoVideosForEncounter(imageEnc.getCatalogNumber());
int numImagesHere=images.size();
int imageCount = 0;
  for(int myImage=0;myImage<numImagesHere;myImage++ ) {
    imageCount++;
    //String addTextFile = ((String) images.nextElement()).replaceAll("%20"," ");
    String addTextFile = images.get(myImage).getFilename().replaceAll("%20"," ");
    
    try {
      if ((imageShepherd.isAcceptableImageFile(addTextFile)) || (imageShepherd.isAcceptableVideoFile(addTextFile))) {
        String addText = imageEncNum + "/" + addTextFile;
%>
<tr>
<td>
<table>
<tr>
  <td class="para">
  <br />
  	<strong>
  		<%=encprops.getProperty("image") %> <%=imageCount%>
  	</strong>
  </td>
</tr>
<%
  if (request.getParameter("isOwner").equals("true")) {
%>
<tr>
  <td class="para">
  	<img align="absmiddle" src="../images/Crystal_Clear_app_xmag.png" width="30px" height="30px" />
    <em>
    	<%=encprops.getProperty("image_commands") %>
    </em>:<br/>
<ul class="image-commands">

<%
if(CommonConfiguration.useSpotPatternRecognition(context)){
	
	String isDorsalFin="";
	String genusSpecies="";
	if((imageEnc.getGenus()!=null)&&(imageEnc.getSpecificEpithet()!=null)){genusSpecies=imageEnc.getGenus()+imageEnc.getSpecificEpithet();}
	if((genusSpecies.equals("Physetermacrocephalus"))||(genusSpecies.equals("Megapteranovaeangliae"))){
		isDorsalFin="&isDorsalFin=false";
	}
	else if(genusSpecies.equals("Tursiopstruncatus")){
		isDorsalFin="&isDorsalFin=true";
	}
	
%>
<li>
	<a href="encounterSpotTool.jsp?imageID=<%=images.get(myImage).getDataCollectionEventID()%><%=isDorsalFin %>"><%=encprops.getProperty("matchPattern") %></a>
</li>
<%
}
%>

<li>
	<a href="encounterSearch.jsp?referenceImageName=<%=images.get(myImage).getDataCollectionEventID() %>"><%=encprops.getProperty("look4photos") %></a>
</li>


</td>
</tr>

<%
  }
  if (request.getParameter("isOwner").equals("true")) {
    int totalKeywords = imageShepherd.getNumKeywords();


%>

<tr>
  <td class="para">

    
    <br />
    <em><img align="absmiddle" src="../images/keyword_icon_small.gif" /> <%=encprops.getProperty("matchingKeywords") %>
    </em>

  
    
    <%
      Iterator<Keyword> indexes = imageShepherd.getAllKeywords();
      if (totalKeywords > 0) {
        boolean haveAddedKeyword = false;
        for (int m = 0; m < totalKeywords; m++) {
          Keyword word = indexes.next();
          if (images.get(myImage).getKeywords().contains(word)) {
            haveAddedKeyword = true;
%>
<p>
<%
            if (CommonConfiguration.isCatalogEditable(context)) {
    %>
    <a href="../SinglePhotoVideoRemoveKeyword?number=<%=imageEncNum%>&photoName=<%=images.get(myImage).getDataCollectionEventID()%>&keyword=<%=word.getReadableName()%>">
         <%
        }
      %>
<img src="../images/cancel.gif" width="16px" height="16px" align="left" />
 <%
  if (CommonConfiguration.isCatalogEditable(context)) {
    %> 
    </a>
          <%
        }
      %>

      <em>&nbsp;<%=word.getReadableName()%></em></p>


    <%
        } //end if
      } //end for
      if (!haveAddedKeyword) {%>

    <p><%=encprops.getProperty("none_assigned")%></p>

    <% }
    } //end if
    else { %>
    <%=encprops.getProperty("none_defined")%>


    <% }
    %>
  </td>
</tr>
<%
  if (CommonConfiguration.isCatalogEditable(context)) {
%>
<tr>
  <td>

    <table>
      <tr>
        <td class="para">
          <em><%=encprops.getProperty("add_keyword") %> 
          	<a href="<%=CommonConfiguration.getWikiLocation(context)%>photo_keywords" target="_blank">
            	<img src="../images/information_icon_svg.gif" alt="Help" border="0" align="absmiddle"/></a>
            </em>
        </td>
      </tr>
      <tr>
        <td class="para">
          <%
            if (totalKeywords > 0) {
          %>
          <form action="../SinglePhotoVideoAddKeyword" method="post" name="keyword">
            <select multiple="multiple" name="keyword" id="keyword" size="5" required="required">
              <option value=" " selected>&nbsp;</option>
              <%
                Iterator<Keyword> keys = imageShepherd.getAllKeywords(kwImagesQuery);
                for (int n = 0; n < totalKeywords; n++) {
                  Keyword word = keys.next();
                  String indexname = word.getIndexname();
                  String readableName = word.getReadableName();
              %>
              <option value="<%=readableName%>"><%=readableName%></option>
              <%
                }
              %>

            </select>
            <input name="number" type="hidden" value="<%=imageEncNum%>" />
            <input name="photoName" type="hidden" value="<%=images.get(myImage).getDataCollectionEventID()%>" />
            <br />
            <input name="AddKW" type="submit" id="AddKW" value="<%=encprops.getProperty("add") %>" />
          </form>
          <%
          } else {
          %>
          <%=encprops.getProperty("no_keywords") %>
          <%
            }
          %>

        </td>
      </tr>
    </table>

  </td>
</tr>
<%
  }
%>

<%


  }
%>
<tr>
  <td>
    <%
      boolean isBMP = false;
      boolean isVideo = false;
      if (addTextFile.toLowerCase().indexOf(".bmp") != -1) {
        isBMP = true;
      }
      if (imageShepherd.isAcceptableVideoFile(addTextFile)) {
        isVideo = true;
      }
      if (request.getParameter("isOwner").equals("true") && (!isBMP) && (!isVideo)) {
    %>
    <a id="<%=images.get(myImage).getDataCollectionEventID() %>" href="<%= images.get(myImage).asUrl(imageEnc, CommonConfiguration.getDataDirectoryName(context)) %>" class="highslide" onclick="return hs.expand(this)"
       title="<%=encprops.getProperty("clickEnlarge")%>">
      <%
      } 
      else if (request.getParameter("isOwner").equals("true")||(request.getParameter("loggedIn").equals("true"))) {
      %>
      <a href="<%= images.get(myImage).asUrl(imageEnc, CommonConfiguration.getDataDirectoryName(context)) %>" id="<%=images.get(myImage).getDataCollectionEventID() %>"
        <%
        if(!isVideo){
        %>
      class="highslide" onclick="return hs.expand(this)" 
		<%
            }
		%>
          
         title="<%=encprops.getProperty("clickEnlarge")%>">
         
         
         <%
        }

        String thumbPath = thisEncounterDir.getAbsolutePath() + "/" + images.get(myImage).getDataCollectionEventID() + ".jpg";
        String thumbLocation = "file-" + thumbPath;
        String srcurl = images.get(myImage).getFullFileSystemPath();
        File processedImage = new File(thumbPath);


        int intWidth = 250;
        int intHeight = 200;
        int thumbnailHeight = 200;
        int thumbnailWidth = 250;


        if(!isVideo){
        	File file2process = new File(encountersDir.getAbsolutePath()+"/"+ addText);
        	if(file2process.exists()){
        		Dimension imageDimensions = org.apache.sanselan.Sanselan.getImageSize(file2process);
        		String width = Double.toString(imageDimensions.getWidth());
        		String height = Double.toString(imageDimensions.getHeight());
        		intHeight = ((new Double(height)).intValue());
        		intWidth = ((new Double(width)).intValue());
        	}
        }
        
        if (intWidth > thumbnailWidth) {
          double scalingFactor = intWidth / thumbnailWidth;
          intWidth = (int) (intWidth / scalingFactor);
          intHeight = (int) (intHeight / scalingFactor);
          if (intHeight < thumbnailHeight) {
            thumbnailHeight = intHeight;
          }
        } else {
          thumbnailWidth = intWidth;
          thumbnailHeight = intHeight;
        }
        int copyrightTextPosition = (int) (thumbnailHeight / 3);


        if (isVideo) {
      %> <img width="250" height="200" alt="video <%=imageEnc.getLocation()%>"
              src="../images/video.jpg" border="0" align="left" valign="left">

      </a>


      <%


      } else if ((!processedImage.exists()) && (!haveRendered)) {

				//thanks to magic of short-circuiting boolean, this will try watermark first then regular if not available
				if (images.get(myImage).scaleToWatermark(context, thumbnailWidth, thumbnailHeight, thumbPath, "") ||
						images.get(myImage).scaleTo(context, thumbnailWidth, thumbnailHeight, thumbPath)) {
					//work forks off in background, so we use this placeholder for now:
System.out.println("trying to fork/create " + thumbPath);
      %> <img width="250" height="200" alt="in progress" src="../images/processed.gif" align="left" /> <%

				} else {  //fallback to old dynamic ways:
        	haveRendered = true;
        //System.out.println("Using DynamicImage to render thumbnail: "+imageEncNum);
        //System.gc();
//System.out.println("srcurl="+srcurl + " --> thumbLocation=" + thumbLocation);
      %>
      <di:img width="<%=thumbnailWidth %>" height="<%=thumbnailHeight %>"
              imgParams="rendering=speed,quality=low" border="0"
              output="<%=thumbLocation%>" expAfter="0" threading="limited"
              fillPaint="#FFFFFF" align="left" valign="left">
        <di:image width="<%=Integer.toString(thumbnailWidth) %>"
                  height="<%=Integer.toString(thumbnailHeight) %>" composite="70"
                  srcurl="<%=srcurl %>" />
        <di:rectangle x="0" y="<%=copyrightTextPosition %>" width="<%=thumbnailWidth %>"
                      composite="30" height="13" fillPaint="#99CCFF"></di:rectangle>

        <di:text x="4" y="<%=copyrightTextPosition %>" align="left" font="Arial-bold-11"
                 fillPaint="#000000"><%=encprops.getProperty("nocopying") %>
        </di:text>
      </di:img>
      <img id="<%= images.get(myImage).getDataCollectionEventID()%>" width="<%=thumbnailWidth %>" class="enc-photo" alt="photo <%=imageEnc.getLocation()%>"
           src="<%=encUrlDir%>/<%=(images.get(myImage).getDataCollectionEventID()+".jpg")%>" border="0" align="left" valign="left"> <%
				}

      if (request.getParameter("isOwner").equals("true")) {
    %>
    </a>
    <%
      }
    %> <%
  } else if ((!processedImage.exists()) && (haveRendered)) {
  %> <img width="250" height="200" class="enc-photo" alt="photo <%=imageEnc.getLocation()%>"
          src="../images/processed.gif" border="0" align="left" valign="left">
      <%
		if (session.getAttribute("logged")!=null) {
		%>
		</a>
    <%
      }
    %> <%
  } else {
			String wmDiv = "";
			String wmText = encprops.getProperty("imgWatermark");
			if ((wmText != null) && !wmText.equals("")) wmDiv = "<div class=\"img-watermark\">" + wmText + "</div>";
  %> <div style="position: relative"><img id="img<%=images.get(myImage).getDataCollectionEventID()%> " width="<%=thumbnailWidth %>" class="enc-photo" alt="photo <%=imageEnc.getLocation()%>"
          src="<%=encUrlDir%>/<%=(images.get(myImage).getDataCollectionEventID()+".jpg")%>" border="0" align="left"
          valign="left"><%=wmDiv%> <%

	if (session.getAttribute("logged")!=null) {
				%></a>
                <div 
            <%
            if(!isVideo){
            %>
            class="highslide-caption" 
            <%
            }
            %>
            >
      <h3><%=encprops.getProperty("imageMetadata") %>
      </h3>
      <table>
        <tr>
          <td align="left" valign="top">

            <table>

              <tr>
                <td align="left" valign="top"><span
                  class="caption"><%=encprops.getProperty("filename") %>: <%=addTextFile%></span>
                </td>
              </tr>


              <tr>
                <td align="left" valign="top"><span
                  class="caption"><%=encprops.getProperty("location") %>: <%=imageEnc.getLocation() %></span>
                </td>
              </tr>


              <tr>
                <td align="left" valign="top"><span
                  class="caption"><%=encprops.getProperty("location") %>: <%=imageEnc.getLocation() %></span>
                </td>
              </tr>
              <tr>
                <td><span
                  class="caption"><%=encprops.getProperty("locationID") %>: <%=imageEnc.getLocationID() %></span>
                </td>
              </tr>
              <tr>
                <td><span
                  class="caption"><%=encprops.getProperty("date") %>: <%=imageEnc.getDate() %></span>
                </td>
              </tr>
              <tr>
                <td>
	                <span class="caption"><%=encprops.getProperty("individualID") %>: 
	                <%
	                if(imageEnc.getIndividualID()!=null){
	                %>
	                <a href="../individuals.jsp?number=<%=imageEnc.getIndividualID() %>"><%=imageEnc.getIndividualID() %>
	                </a>
	                <%
	                }
	                %>
	                </span>
                </td>
              </tr>
              <tr>
                <td><span class="caption"><%=encprops.getProperty("title") %>: <a
                  href="encounter.jsp?number=<%=imageEnc.getCatalogNumber() %>"><%=imageEnc.getCatalogNumber() %>
                </a></span></td>
              </tr>
              <tr>
                <td><span class="caption">
											<%=encprops.getProperty("matchingKeywords") %>
											<%
                        Iterator<Keyword> it = imageShepherd.getAllKeywords();
                        while (it.hasNext()) {
                          Keyword word = it.next();
							

                         if (images.get(myImage).getKeywords().contains(word)) {
                      %>
														<br/><%= word.getReadableName()%>
														
														<%


                                }

                              }
                            %>
										</span></td>
              </tr>

            </table>


            <%
              if (CommonConfiguration.showEXIFData(context)&&!isVideo) {
            	  FileInputStream jin=null;
            	  try{
            	  File exifImage = new File(Encounter.dir(shepherdDataDir, imageEnc.getCatalogNumber()) + "/" + addTextFile);
              	jin=new FileInputStream(exifImage);
            %>


            <p><strong>EXIF</strong></p>
            <span class="caption">
					<div class="scroll">
						<span class="caption">	
					<%
            if ((addTextFile.toLowerCase().endsWith("jpg")) || (addTextFile.toLowerCase().endsWith("jpeg"))) {
            	//File exifImage = new File(Encounter.dir(shepherdDataDir, thisEnc.getCatalogNumber()) + "/" + thumbLocs.get(countMe).getFilename());
            	%>
            	<%=Util.getEXIFDataFromJPEGAsHTML(exifImage) %>
            	<%
            	
              } //end if
 
                %>
   									</span>
          </div>
   								
   								</span>
          </td>
          <%
              }
            	  catch(Exception e){e.printStackTrace();}
            	  finally{if(jin!=null){jin.close();}}
            }
          %>


        </tr>
      </table>

    </div>

    <%
        }

      }
    %>
  </td>
</tr>

</table>



  <%
						}
				else {
					%>
<tr>
  <td>
    <p><img src="../images/alert.gif"> <strong><%=encprops.getProperty("badfile") %>
      :</strong> <%=addTextFile%> <%
      if (request.getParameter("isOwner").equals("true") && CommonConfiguration.isCatalogEditable(context)) {
    %> <br/>
    <a href="../EncounterRemoveImage?number=<%=imageEncNum%>&filename=<%=(addTextFile.replaceAll(" ","%20"))%>&dcID=<%=images.get(myImage).getDataCollectionEventID()%>"><%=encprops.getProperty("clickremove") %>
    </a></p>
    <%
      }
    %>
  </td>
</tr>
<%
  } //close else of if
} //close try
catch (Exception e) {
  e.printStackTrace();
%>
<table width="250px">
<tr>
<td>
<img width="250px" height="200px" src="../images/Crystal_Clear_filesystem_file_broken.png" />
</td></tr>
<tr>
<td class="para">
<p>Error message:<br /> <%=e.getMessage()%></p>
</td></tr>
</table>
<%
    }
  } //close while
%>

</table>

<p class="para">
    <%
		 			if (request.getParameter("isOwner").equals("true")&&CommonConfiguration.isCatalogEditable(context)) {
		 		%>
<table width="250" bgcolor="#99CCFF">
  <tr>
    <td class="para">
      <form action="../EncounterAddImage" method="post"
            enctype="multipart/form-data" name="encounterAddImage"><input
        name="action" type="hidden" value="imageadder" id="action">
        <input name="number" type="hidden" value="<%=imageEncNum%>" id="shark">
        <strong><img align="absmiddle"
                     src="../images/upload_small.gif"/> <%=encprops.getProperty("addfile") %>:</strong><br/>
        <input name="file2add" accept=".jpg, .jpeg, .png, .bmp, .gif, .mov, .wmv, .avi, .mp4, .mpg" type="file" size="20">

        <p><input name="addtlFile" type="submit" id="addtlFile"
                  value="Upload"></p></form>

    </td>
  </tr>
</table>
<br />
<table width="250" bgcolor="#99CCFF">
  <tr>
    <td align="left" valign="top" class="para">
      <font color="#990000"><img
        align="absmiddle" src="../images/thumbnail_image.gif"/></font>
      <strong><%=encprops.getProperty("resetThumbnail")%>
      </strong>&nbsp;</font></td>
  </tr>
  <tr>
    <td align="left">
      <form action="../resetThumbnail.jsp" method="get" enctype="multipart/form-data"
            name="resetThumbnail">
        <input name="number" type="hidden" value="<%=imageEncNum%>" id="numreset"><br/>
        <%=encprops.getProperty("useImage")%>: <select name="imageNum">
        <%
          for (int rmi2 = 1; rmi2 <= numImagesHere; rmi2++) {
        %>
        <option value="<%=rmi2%>"><%=rmi2%>
        </option>
        <%
          }
        %>
      </select><br/>
        <input name="resetSubmit" type="submit" id="resetSubmit"
               value="<%=encprops.getProperty("resetThumbnail")%>"></form>
    </td>
  </tr>
</table>
<br/>
<table width="250" bgcolor="#99CCFF">
  <tr>
    <td class="para">
      <form onsubmit="return confirm('Are you sure you want to delete this image?');"  action="../EncounterRemoveImage" method="post"
            name="encounterRemoveImage">
            <input name="action" type="hidden" value="imageremover" id="action" />
        <input
          name="number" type="hidden" value="<%=imageEncNum%>" /> <strong><img
          align="absmiddle" src="../images/cancel.gif"/> <%=encprops.getProperty("removefile") %>:
        </strong> <select name="dcID">
          <%
            for (int rmi = 0; rmi < imageCount; rmi++) {
          %>
          <option value="<%=imageEnc.getImages().get(rmi).getDataCollectionEventID()%>"><%=(rmi+1)%></option>
          <%
            }
          %>
        </select><br/>

        <p><input name="rmFile" type="submit" id="rmFile" value="Remove" /></p></form>

    </td>
  </tr>
</table>

<!-- ***** START EDITS ***** 

     Ecological Software Solutions LLC Fluke Tracer.-->
<!-- Requires jQuery. Also get the lastest version of jQuery if needed.  -->
<!-- <script type="text/javascript" src="//code.jquery.com/jquery-2.1.3.js"></script> -->
<script type="text/javascript" src="../tracing/js/paper.js"></script>
<script type="text/javascript" src="../tracing/js/tracing.js"></script>
<script type="text/javascript">
  comEcostatsTracing.addFlukeTrace('.highslide','<%=imageEncNum%>');
</script>

<!-- ***** END EDITS *****  -->

<%
  }

}
catch(Exception e){
	e.printStackTrace();
}
finally{
	imageShepherd.rollbackDBTransaction();
	imageShepherd.closeDBTransaction();
}
kwImagesQuery.closeAll();
%>
