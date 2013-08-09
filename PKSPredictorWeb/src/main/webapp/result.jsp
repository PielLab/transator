<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%--
  Created by IntelliJ IDEA.
  User: pmoreno
  Date: 29/6/13
  Time: 00:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>transAT-PKS Prediction Tool</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">

    <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

    <link rel="stylesheet" href="css/normalize.css">
    <script src="js/vendor/modernizr-2.6.2.min.js"></script>

    <!-- BioJS -->
    <script language="JavaScript" type="text/javascript" src="http://www.ebi.ac.uk/Tools/biojs/registry/src/Biojs.js"></script>
    <%--<script language="JavaScript" type="text/javascript" src="http://www.ebi.ac.uk/Tools/biojs/registry/src/Biojs.FeatureViewer.js"></script>--%>
    <script language="JavaScript" type="text/javascript" src="js/Biojs.FeatureViewer.js"></script>

    <!-- External -->
    <script language="JavaScript" type="text/javascript" src="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/jquery-1.7.2.min.js"></script>
    <script src="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/jquery-ui-1.8.2.custom.min.js" type="text/javascript"></script>
    <script language="JavaScript" type="text/javascript" src="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/jquery.tooltip.js"></script>
    <script language="JavaScript" type="text/javascript" src="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/graphics/raphael.js"></script>
    <script language="JavaScript" type="text/javascript" src="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/graphics/canvg.js"></script>
    <script language="JavaScript" type="text/javascript" src="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/graphics/rgbcolor.js"></script>

    <!-- CSS -->
    <link rel="stylesheet" href="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/jquery-ui-1.8.2.css" />
    <link rel="stylesheet" href="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/jquery.tooltip.css">
    <link rel="stylesheet" href="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/images/ui-bg_flat_0_aaaaaa_40x100.png">
    <link rel="stylesheet" href="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/images/ui-bg_flat_75_ffffff_40x100.png">
    <link rel="stylesheet" href="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/images/ui-bg_glass_65_ffffff_1x400.png">
    <link rel="stylesheet" href="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/images/ui-bg_glass_75_dadada_1x400.png">
    <link rel="stylesheet" href="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/images/ui-bg_glass_75_e6e6e6_1x400.png">
    <link rel="stylesheet" href="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/images/ui-bg_highlight-soft_75_cccccc_1x100.png">
    <link rel="stylesheet" href="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/images/ui-icons_222222_256x240.png">
    <link rel="stylesheet" href="http://www.ebi.ac.uk/Tools/biojs/biojs/dependencies/jquery/images/ui-icons_454545_256x240.png">

    <link rel="stylesheet" href="css/main.css">

    <script>
        $(function() {
            $( "#accordionSequences" ).accordion();
        });
    </script>
</head>
<body>

<h1 class="textCentering">trans-AT Polyketide prediction results</h1>

<p class="textCentering">The annotation of the different <i>trans</i>-AT KS clades on the submitted sequences produces
the following structure:</p>
<img class="resultingMol" id="pkMol" path="<%= request.getSession().getAttribute("tmp") %>">

<p class="textCentering">
    The annotation for each sequence submitted can be seen in the sections below.
</p>
<div id="accordionSequences">
<%
    int viewerNumber=0;
    for (String identifier : (List<String>)request.getSession().getAttribute("identifers")) {
%>
<h3 id="headerView<%= viewerNumber%>" class="newHeaderForAccordion"><%= URLEncoder.encode(identifier,"UTF-8")%> - processing..</h3>
<div class="seqResult" id="featureView<%= viewerNumber%>" viewerNumber="<%= viewerNumber%>"
     path="<%= request.getSession().getAttribute("tmp") %>" seqId="<%= URLEncoder.encode(identifier,"UTF-8")%>" >
    <img src="img/ajax-loader.gif" id="waitingImg" class="waitingImage">
</div>
<%
        viewerNumber++;
    } %>

</div>

<script>

    window.onload = function() {

        var $j = jQuery.noConflict();
        $j(".seqResult").each(function() {
            //var idDiv = $j(this).attr("id");
            var divObj = $j(this);
            $j.getJSON("rest/pkspredictor/query?path="+$j(this).attr("path")+"&seqId="+$j(this).attr("seqId"),
                    function(data) {
                        var json = data;
                        //$j("#"+idDiv).find("#waitingImg").hide()
                        divObj.find("#waitingImg").hide()
                        var myPainter = new Biojs.FeatureViewer({
                            target: divObj.attr("id"),
                            json: json
                        });
                        var viewNum = divObj.attr("viewerNumber");
                        $j("#headerView"+viewNum).html(divObj.attr("seqid"));
                    }
            )}
            );

        $j('#pkMol').attr('src', 'rest/pkspredictor/structure?path='+$j("#pkMol").attr("path"));
//        var img = $j("<img />").attr('src', '/rest/pkspredictor/structure?path='+$j("#pkMol").attr("path"))
//                .load(function() {
//                    if (!this.complete || typeof this.naturalWidth == "undefined" || this.naturalWidth == 0) {
//                        alert('broken image!');
//                    } else {
//                        $j("#pkMol").append(img);
//                    }
//                });
        };

//    $(".seqResult").on({
//        ajaxStart: function() {
//            $(this).addClass("loading");
//        },
//        ajaxStop: function() {
//            $(this).removeClass("loading");
//        }
//    });
</script>

<%--<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>--%>
<%--<script>window.jQuery || document.write('<script src="js/vendor/jquery-1.9.1.min.js"><\/script>')</script>--%>
<script src="js/plugins.js"></script>
<script src="js/main.js"></script>

</body>
</html>