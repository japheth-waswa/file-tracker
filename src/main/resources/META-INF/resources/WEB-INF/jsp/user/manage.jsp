<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@include file="../common/header.jspf" %>

<div class="row">
    <div class="col-12">
        <div class="card">
            <div class="card-body">
                <form:form action="/users/manage" method="post" modelAttribute="userDto">
                    <form:hidden path="id"/>
                    <div class="form-floating mb-3">
                        <form:select id="accountStatus" path="accountStatus" cssClass="form-select">
                            <c:forEach items="${accountStatuses}" var="accStatus">
                                <form:option value="${accStatus}">${accStatus}</form:option>
                            </c:forEach>
                        </form:select>
                        <label for="accountStatus">Account Status</label>
                        <form:errors path="accountStatus" cssClass="text-danger"/>
                    </div>

                    <div class="form-floating mb-3">
                        <form:input type="text" path="idNumber" id="idNumber" cssClass="form-control"/>
                        <label for="idNumber">ID Number</label>
                        <form:errors path="idNumber" cssClass="text-danger"/>
                    </div>

                    <div class="form-floating mb-3">
                        <form:input type="text" path="email" id="email" cssClass="form-control"/>
                        <label for="email">Email</label>
                        <form:errors path="email" cssClass="text-danger"/>
                    </div>

                    <div class="form-floating mb-3">
                        <form:input type="text" path="firstName" id="firstName" cssClass="form-control"/>
                        <label for="firstName">First Name</label>
                        <form:errors path="firstName" cssClass="text-danger"/>
                    </div>

                    <div class="form-floating mb-3">
                        <form:input type="text" path="middleName" id="middleName" cssClass="form-control"/>
                        <label for="middleName">Middle Name</label>
                        <form:errors path="middleName" cssClass="text-danger"/>
                    </div>

                    <div class="form-floating mb-3">
                        <form:input type="text" path="otherNames" id="otherNames" cssClass="form-control"/>
                        <label for="otherNames">Other Names</label>
                        <form:errors path="otherNames" cssClass="text-danger"/>
                    </div>

                    <div class="form-floating mb-3">
                        <form:select id="roles" path="roles" cssClass="form-select">
                            <c:forEach items="${userRoles}" var="role">
                                <form:option value="${role}">${role}</form:option>
                            </c:forEach>
                        </form:select>
                        <label for="roles">Roles</label>
                        <form:errors path="roles" cssClass="text-danger"/>
                    </div>

                    <div class="form-floating mb-3">
                        <form:select id="rights" path="rights" cssClass="form-select">
                            <c:forEach items="${userRights}" var="right">
                                <form:option value="${right}">${right}</form:option>
                            </c:forEach>
                        </form:select>
                        <label for="roles">Rights</label>
                        <form:errors path="rights" cssClass="text-danger"/>
                    </div>

                    <div class="form-floating mb-3">
                        <form:select id="departmentId" path="departmentId" cssClass="form-select">
                            <c:forEach items="${departments}" var="department">
                                <form:option value="${department.id}">${department.name}</form:option>
                            </c:forEach>
                        </form:select>
                        <label for="roles">Department</label>
                        <form:errors path="departmentId" cssClass="text-danger"/>
                    </div>

                    <div class="form-group">
                        <button type="submit" class="btn btn-sm btn-primary">
                            <c:if test="${empty userDto.id}">Create</c:if>
                            <c:if test="${not empty userDto.id}">Update</c:if>
                        </button>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>

<%@include file="../common/footer.jspf" %>