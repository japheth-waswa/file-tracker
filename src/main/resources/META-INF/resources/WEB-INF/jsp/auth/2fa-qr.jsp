<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Setup 2FA</title>
</head>
<body>
<h1>Setup 2FA</h1>
<p>Scan the QR code with your authenticator app:</p>
<img src="data:image/png;base64,${qrCodeBase64}" alt="QR Code">
<form th:action="${twoFactorTotpRoute}" method="post">
    <label for="code">Enter the code from the app:</label>
    <input type="text" id="code" name="code">
    <button type="submit">Verify</button>
</form>
</body>
</html>