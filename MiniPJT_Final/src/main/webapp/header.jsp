
<%@ page contentType="text/html; charset=euc-kr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

	<script type="text/javascript">
		$(function(){
			
			$('#loginButton').click(function(){
				var id = $($('.input-group-addon')[0]).next().val();
				var pwd = $('.input-group-addon:contains("PWD")').next().val();
				
				if(id.length == 0){
					alert("���̵� �Է��� �ּ���.");
					return;
				}
				
				if(pwd.length == 0){
					alert("�н����带 �Է��� �ּ���.");
					return;
				}
				
				$('form').attr('method','post').attr('action','/user/login').submit();
			});
		});
		
		$(function(){
			$('a:contains("ȸ������")').click(function(){
				console.log('����');
				$('form').attr('method','get').attr('action','/user/addUser').submit();
			});
			
			$('.btn-default:contains("��ٱ���")').click(function(){
				var href = $(this).attr('href');
				console.log(href);
				
				
				if(${empty user.role}){
					alert('�α����� ���ּ���');
					$(this).attr('href',"/user/loginView.jsp");
				}else{
					if(confirm('��ǰ�� �����ϴ�. ��ٱ��Ϸ� �̵��Ͻðڽ��ϱ�?') == true){
						$(this).attr('href',href);

					}else{
						$(this).attr('href','/index.jsp');
					}					
				}
			});
			
			$("#kakao-png").on("click",function(){
				Kakao.init('b3bed223fd618abc07f64c2103ca9659');
				console.log(Kakao.isInitialized());
				Kakao.Auth.login({
				      success: function(authObj) {
				        console.log(JSON.stringify(authObj))
				        $(window.parent.frames.document.location).attr("href","/user/login/pass");
				      },
				      fail: function(err) {
				        console.log(JSON.stringify(err))
				      },
				    })
			});
		});
		
	</script>

	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="container-fluid">
			<a class="navbar-brand" href="/">
				<span class="glyphicon glyphicon-gift"></span>
				<span> Model2 MVC SHOP </span>
				<span class="glyphicon glyphicon-gift"></span>
			</a>
			
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#target">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
			</div>	
			
			<div class="collapse navbar-collapse" id="target">
				<ul class="nav navbar-nav">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
							<span>BEST</span>
							<span class="caret"></span>
						</a>
						<ul class="dropdown-menu">
							<li><a href="#">1</a></li>
							<li><a href="#">2</a></li>					
						</ul>
					</li>
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
							<span>CATEGORY</span>
							<span class="caret"></span>
						</a>
						<ul class="dropdown-menu">
							<li><a href="#">1</a></li>
							<li><a href="#">2</a></li>			
						</ul>
					</li>
					<li><a>�ֱ� �� ��ǰ</a></li>
				</ul>
				
				<ul class="nav navbar-nav navbar-right">

				<c:if test="${empty user.role }">
					<li><a>ȸ������</a></li>
					<li class="dropdown">
						<a class="dropdown-toggle" data-toggle="dropdown">
							<span>�α���</span>
							<span class="caret"></span>
						</a>
						<ul class="dropdown-menu">
							<li class="dropdown-header">�α���</li>
							
						</ul>
					</li>
				</c:if>
					
				<c:if test="${!empty user.role && user.role eq 'admin' }">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
							<span>${user.userName}��</span>
							<span class="caret"></span>
						</a>
						<ul class="dropdown-menu">
							<li class="dropdown-header">�����</li>
							<li><a>����� ����</a></li>
							<li class="divider"></li>
							<li class="dropdown-header">�Ǹ�</li>
							<li><a>��ǰ ����</a></li>
							<li><a>��� ����</a></li>				
						</ul>
					</li>
					<li>
						<a href="/purchase/findCartList">
							<span class="glyphicon glyphicon-shopping-cart"></span>
							<span>��ٱ���</span>
						</a>
					</li>
					<li>
						<a href="/user/logout">�α׾ƿ�</a>
					</li>					
					
				</c:if>
				<c:if test="${!empty user.role && user.role eq 'user' }">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
							<span>${user.userName}��</span>
							<span class="caret"></span>
						</a>
						<ul class="dropdown-menu">
							<li class="dropdown-header">����</li>
							<li><a href="">����������</a></li>
							<li><a href="">���ų�����ȸ</a></li>		
						</ul>
					</li>
					<li>
						<a href="/purchase/findCartList">
							<span class="glyphicon glyphicon-shopping-cart"></span>
							<span>��ٱ���</span>
						</a>
					</li>
					<li>
						<a href="/user/logout">�α׾ƿ�</a>
					</li>				
					
				</c:if>
				</ul>
			</div>
		</div>
	</div>