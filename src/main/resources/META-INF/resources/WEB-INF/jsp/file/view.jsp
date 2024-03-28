<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@include file="../common/header.jspf" %>
<%
    String urlError = request.getParameter("error") != null ? URLDecoder.decode(request.getParameter("error"), StandardCharsets.UTF_8.name()) : null;
    request.setAttribute("urlError", urlError);
    String urlSuccess = request.getParameter("success") != null ? URLDecoder.decode(request.getParameter("success"), StandardCharsets.UTF_8.name()) : null;
    request.setAttribute("urlSuccess", urlSuccess);
%>
<div class="row">
    <div class="col">
        <c:if test="${not empty urlError}">
            <div class="alert alert-danger" role="alert">
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                <%= urlError %>
            </div>
        </c:if>
        <c:if test="${not empty urlSuccess}">
            <div class="alert alert-success" role="alert">
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                <%= urlSuccess %>
            </div>
        </c:if>
    </div>
</div>

<div class="row">
    <div class="col">
        <div class="card">
            <div class="card-body">
                <h6 class="mb-3">File Details</h6>
                <div class="table-responsive">
                    <table class="table table-striped text-uppercase">
                        <tbody>
                        <tr>
                            <th>LR No</th>
                            <td>${fileDto.lrNo}</td>
                        </tr>
                        <c:if test="${fileDto.irNo != null}">
                            <tr>
                                <th>IR No</th>
                                <td>${fileDto.irNo}</td>
                            </tr>
                        </c:if>
                        <c:if test="${fileDto.cfNo != null}">
                            <tr>
                                <th>Cf No</th>
                                <td>${fileDto.cfNo}</td>
                            </tr>
                        </c:if>
                        <tr>
                            <th>Area Size</th>
                            <td>${fileDto.areaSize}</td>
                        </tr>
                        <tr>
                            <th>Status</th>
                            <td>${fileDto.fileStatus}</td>
                        </tr>
                        <tr>
                            <th>Nature</th>
                            <td>${fileDto.fileNature}</td>
                        </tr>
                        <c:if test="${fileDto.currentDepartment != null}">
                            <tr>
                                <th>Current Department</th>
                                <td>${fileDto.currentDepartment}</td>
                            </tr>
                        </c:if>
                        <c:if test="${fileDto.currentUserFullNames != null}">
                            <tr>
                                <th>Current User</th>
                                <td>${fileDto.currentUserFullNames}</td>
                            </tr>
                        </c:if>
                        <c:if test="${fileDto.createdBy != null}">
                            <tr>
                                <th>Created By</th>
                                <td>${fileDto.createdBy}</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
                <c:if test="${dispatchAllowed}">
                    <button class="btn btn-sm btn-primary"
                            data-bs-toggle="modal" data-bs-target="#dispatch-${fileDto.id}">Dispatch
                    </button>
                </c:if>
            </div>
        </div>
    </div>
    <div class="col">
        <div class="card">
            <div class="card-body">
                <%@include file="file-trail.jsp" %>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="dispatch-${fileDto.id}" data-bs-keyboard="false" tabindex="-1"
     aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <form:form action="/files/dispatch?fileId=${fileDto.id}" method="post" modelAttribute="fileTrailDto">
                <div class="modal-header">
                    <h1 class="modal-title fs-12px" id="staticBackdropLabel">Dispatch File (${fileDto.lrNo})</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col">
                            <form:hidden path="id"/>
                            <form:hidden path="lrNo" value="${fileDto.lrNo}"/>

                            <div class="form-floating mb-3">
                                <form:select id="assignedToIdNumber" path="assignedToIdNumber"
                                             cssClass="form-select text-capitalize">
                                    <c:forEach items="${assignableUsers}" var="assignableUser">
                                        <form:option value="${assignableUser.idNumber}">
                                            ${assignableUser.firstName} ${assignableUser.middleName} ${assignableUser.otherNames}
                                            <c:if test="${assignableUser.department != null}">
                                                (${assignableUser.department})
                                            </c:if>
                                        </form:option>
                                    </c:forEach>
                                </form:select>
                                <label for="assignedToIdNumber">Choose user to assign</label>
                            </div>

                            <div class="form-floating mb-3">
                                <form:textarea id="dispatchNote" path="dispatchNote" placeholder="Dispatch reason"
                                               cssClass="form-control"/>
                                <label for="dispatchNote">Dispatch Reason</label>
                            </div>

                            <div class="form-floating mb-3">
                                <form:input id="dueDate" path="dueDate"
                                            cssClass="form-control due-date"/>
                                <label for="dueDate">Due Date</label>
                            </div>

                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-sm btn-danger fs-11px" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-sm btn-primary fs-11px">Proceed & dispatch</button>
                </div>
            </form:form>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {

        $('.due-date').datepicker({
            format: 'yyyy-mm-dd 00:00:00',
            startDate: '-0d'
        });

    }, false);
</script>

<%@include file="../common/footer.jspf" %>