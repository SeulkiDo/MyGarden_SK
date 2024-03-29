<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>비밀 정원 개설</title>
<jsp:include page="/WEB-INF/views/module/bootstrap_cdn.jsp"/>
<style>
	#iframeWrap{
		width : 100%;
		height : 1000px;
	}
	.contentswrap{
		margin-bottom : 100px;
	}
</style>
</head>
<body>
	<!-- header -->
	<jsp:include page="/WEB-INF/views/module/fixedHeader.jsp"/>
<!-- 	images -->
	<div class="container-fluid imageContainer m-0 p-0 my">
		<div class="row my">
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 my">
				<div id="carouselExampleFade" class="carousel slide carousel-fade" data-ride="carousel">
				  <div class="carousel-inner">
				    <div class="carousel-item active">
				      <img src="resources/img/mypage1.jpg" align="bottom" class="d-block w-100" alt="...">
				    </div>
				    <div class="carousel-item">
				      <img src="resources/img/mypage2.jpg" class="d-block w-100" alt="...">
				    </div>
				    <div class="carousel-item">
				      <img src="resources/img/mypage3.jpg" class="d-block w-100" alt="...">
				    </div>
				  </div>
				</div>
			</div>
		</div>
	</div>
	
<!-- 	contents -->
	<div class="container contentswrap">
		<div class="row text-center mt-5 my">
			<jsp:include page="/WEB-INF/views/module/mypage.jsp"/>
			<div class="col-lg-3 col-md-4 col-sm-12 col-xs-12 my">
				<div class="list-group">
				  <a href="#" class="list-group-item list-group-item-action currentActive">Overview</a>
				  <a href="/mypageInfo" class="list-group-item list-group-item-action">내 정보 수정</a>
				  <a href="orderList" class="list-group-item list-group-item-action">구매 내역</a>
				  <a href="private" class="list-group-item list-group-item-action">비밀 정원</a>
				  <a href="#" class="list-group-item list-group-item-action">Dapibus ac facilisis in</a>
				</div>
			</div>
			<div id="iframeWrap" class="col-lg-9 col-md-8 col-sm-12 col-xs-12 my">	
				<iframe id="privategarden" src="privategarden" scrolling="yes" width="100%" height="1000px" frameborder=0 framespacing=0 marginheight=0 marginwidth=0 scrolling=no vspace=0/>
			</div>
		</div>
	</div>

	<!-- footer -->
	<jsp:include page="/WEB-INF/views/module/fixedFooter.jsp"/>		
</body>
</html>