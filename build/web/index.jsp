<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
    "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>UploadTesteAlterado</title>
<style type="text/css">
    <%@include file="bootstrap/css/bootstrap.min.css" %>
    <%@include file="bootstrap/css/bootstrap-theme.min.css" %>
    <%@include file="dropzone/dropzone.css" %>
    <%@include file="custom/custom.css" %>
    
    
</style>
</head>
<body>
	
	<form id="dz" action="uploadfile" class="dropzone" method="post" enctype="multipart/form-data"></form>
	
	
	<script src="dropzone/dropzone.js" type="text/javascript"></script>
	<script src="jQuery/js/jQuery.js" type="text/javascript"></script>
	<script src="bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
	<script src="filestyle/js/bootstrap-filestyle.min.js" type="text/javascript"></script>
	
	<c:if test="${not empty message}">
		${message}
	</c:if>
</body>
</html>