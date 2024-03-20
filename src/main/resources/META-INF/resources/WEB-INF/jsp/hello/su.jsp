<%@include file="../common/header.jspf"%>
<h1>Wow Su in HTML</h1>
<h2>Department=${sessionScope.department}</h2>
<h2>Department1=<% String department = (String) session.getAttribute("department");%>${department}</h2>
<h2>Location = ${location}</h2>
<h2>Amount = ${amount}</h2>
<h2>Gupta = ${g}</h2>
<p>this is just a random tesxt here.</p>
<p>Department1=My custom department</p>
<%@include file="../common/footer.jspf"%>