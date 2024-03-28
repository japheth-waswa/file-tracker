<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@include file="../common/header.jspf" %>

<div class="row">
    <div class="col-12">
        <div class="card">
            <div class="card-body">
                <h5>Settings</h5>
                <form:form action="/settings" method="post" modelAttribute="settingDto">
                    <form:hidden path="id"/>
                    <div class="form-floating mb-3">
                        <form:select id="durationType" path="durationType" cssClass="form-select">
                            <c:forEach items="${durationTypes}" var="durType">
                                <form:option value="${durType}">${durType}</form:option>
                            </c:forEach>
                        </form:select>
                        <label for="durationType">Reminder Duration Type</label>
                        <form:errors path="durationType" cssClass="text-danger"/>
                    </div>

                    <div class="form-floating mb-3">
                        <form:input type="number" path="duration" id="duration" cssClass="form-control"/>
                        <label for="duration">Duration (Min 10mins)</label>
                        <form:errors path="duration" cssClass="text-danger"/>
                    </div>

                    <div class="form-group">
                        <button type="submit" class="btn btn-sm btn-primary">Save Settings</button>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>

<%@include file="../common/footer.jspf" %>