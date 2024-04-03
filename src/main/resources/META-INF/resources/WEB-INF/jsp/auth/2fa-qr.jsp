<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@include file="../common/header.jspf" %>
<style>
    body{
        background-color: #eee;
    }
</style>
<div class="container">
    <div class="row mt-3">
        <div class="col"></div>
        <div class="col">
            <c:if test="${qrCodeBase64 ne null}">
                <p>Scan the QR code with your authenticator app such as <a target="_blank" href="https://authy.com/">Authy</a></p>
                <img src="data:image/png;base64,${qrCodeBase64}" alt="QR Code">
                <br/>
            </c:if>

            <c:if test="${qrCodeBase64 == null}">
                <p>Use your authenticator app such as <a target="_blank" href="https://authy.com/">Authy</a> to get the code</p>
            </c:if>

            <form th:action="${twoFactorTotpRoute}" method="post" class="mt-3 mb-1">
                <div class="input-group mb-3">
                    <input name="code" type="text" class="form-control" placeholder="Enter the code from the authenticator app"
                           aria-label="Enter the code from the app" id="enter-code-input" required>
                    <button class="btn btn-outline-secondary" type="submit">Verify</button>
                </div>
            </form>
        </div>
        <div class="col"></div>
    </div>
</div>

<%@include file="../common/footer.jspf" %>