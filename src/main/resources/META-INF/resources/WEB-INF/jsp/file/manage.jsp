<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@include file="../common/header.jspf" %>

<div class="row">
    <div class="col-12">
        <div class="card">
            <div class="card-body">
                <h6 class="text-primary-emphasis"><u>Manage File</u></h6>
                <form:form action="/files/manage" method="post" modelAttribute="fileDto">
                    <form:hidden path="id"/>

                    <div class="form-floating mb-3">
                        <form:input type="text" path="lrNo" id="lrNo" cssClass="form-control"/>
                        <label for="lrNo">LR No</label>
                        <form:errors path="lrNo" cssClass="text-danger"/>
                    </div>

                    <div class="form-floating mb-3">
                        <form:input type="text" path="irNo" id="irNo" cssClass="form-control"/>
                        <label for="irNo">IR No</label>
                    </div>

                    <div class="form-floating mb-3">
                        <form:input type="text" path="cfNo" id="cfNo" cssClass="form-control"/>
                        <label for="cfNo">CF No</label>
                    </div>

                    <div class="form-floating mb-3">
                        <form:input type="number" step="any" path="areaSize" id="areaSize" cssClass="form-control"/>
                        <label for="areaSize">Area Size</label>
                    </div>

                    <div class="form-floating mb-3">
                        <form:select id="fileStatus" path="fileStatus" cssClass="form-select">
                            <c:forEach items="${fileStatuses}" var="fileStatus">
                                <form:option value="${fileStatus}">${fileStatus}</form:option>
                            </c:forEach>
                        </form:select>
                        <label for="fileStatus">File Status</label>
                        <form:errors path="fileStatus" cssClass="text-danger"/>
                    </div>

                    <div class="form-floating mb-3">
                        <form:select id="fileNature" path="fileNature" cssClass="form-select">
                            <c:forEach items="${fileNatures}" var="fileNature">
                                <form:option value="${fileNature}">${fileNature}</form:option>
                            </c:forEach>
                        </form:select>
                        <label for="fileNature">File Nature</label>
                        <form:errors path="fileNature" cssClass="text-danger"/>
                    </div>

                    <div class="form-group">
                        <button type="submit" class="btn btn-sm btn-primary">
                            <c:if test="${empty fileDto.id}">Create</c:if>
                            <c:if test="${not empty fileDto.id}">Update</c:if>
                        </button>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>

<%@include file="../common/footer.jspf" %>