<h2>Enter OTP</h2>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<form action="${twoFactorRoute}" method="post">
    <!-- form fields -->
    <input type="text" name="twoFactorCode"/>
    <button type="submit">Validate</button>
</form>