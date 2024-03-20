<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@include file="../common/header.jspf" %>

<div class="row">
    <div class="col-12">
        <div class="card">
            <div class="card-body">
                <h6 class="text-primary-emphasis"><u>Manage Department</u></h6>
                <form:form action="/departments/manage" method="post" modelAttribute="department">
                    <form:hidden path="id"/>

                    <div class="form-floating mb-3">
                        <form:input type="text" path="name" id="name" cssClass="form-control"/>
                        <label for="name">Deparment Name</label>
                        <form:errors path="name" cssClass="text-danger"/>
                    </div>

                    <div class="form-group">
                        <button type="submit" class="btn btn-sm btn-primary">
                            <c:if test="${empty department.id}">Create</c:if>
                            <c:if test="${not empty department.id}">Update</c:if>
                        </button>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>

<%@include file="../common/footer.jspf" %>