<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<style>
    .dt-search {
        display: none !important;
    }
</style>
<h6 class="mb-3 text-uppercase">File movement for ${fileDto.lrNo}</h6>
<div class="row">
    <div class="col-12">
        <div class="row">
            <div class="col">
                <div class="float-end">
                    <c:if test="${manageFileTrail}">
                        <button class="btn btn-sm btn-primary" data-bs-toggle="modal"
                                data-bs-target="#add-file-trail-${fileDto.id}">Add File Trail
                        </button>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
    <div class="col-12 table-responsive">
        <table id="myTable" class="table">
            <thead>
            <tr>
                <th>#</th>
                <th>Assigned To</th>
                <th>Assigned On</th>
                <th>Department</th>
                <th>Assigned By</th>
                <th>Assign Remarks</th>
                <th>Dispatched By</th>
                <th>Dispatched On</th>
                <th>Duration</th>
            </tr>
            </thead>
            <tbody></tbody>
        </table>
    </div>
</div>

<c:if test="${manageFileTrail}">
    <div class="modal fade" id="add-file-trail-${fileDto.id}" data-bs-keyboard="false" tabindex="-1"
         aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <form:form action="/files/add-file_trail?fileId=${fileDto.id}" method="post"
                           modelAttribute="fileTrailDto">
                    <div class="modal-header">
                        <h1 class="modal-title fs-12px" id="staticBackdropLabel">File Trail (${fileDto.lrNo})</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col">
                                <form:hidden path="id"/>
                                <form:hidden path="lrNo" value="${fileDto.lrNo}"/>


                                <div class="form-floating mb-3">
                                    <form:select id="fileTrailOrigin" path="fileTrailOrigin"
                                                 cssClass="form-select text-capitalize">
                                        <c:forEach items="${fileTrailOrigins}" var="fileTrailOrigin">
                                            <form:option value="${fileTrailOrigin}">
                                                ${fileTrailOrigin}
                                            </form:option>
                                        </c:forEach>
                                    </form:select>
                                    <label for="fileTrailOrigin">Origin</label>
                                </div>

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
                                    <label for="assignedToIdNumber">Assigned To</label>
                                </div>

                                <div class="form-floating mb-3">
                                    <form:input id="assignedOn" path="assignedOn" cssClass="form-control datepicker"/>
                                    <label for="assignedOn">Assigned On</label>
                                </div>

                                <div class="form-floating mb-3">
                                    <form:select id="assignedByIdNumber" path="assignedByIdNumber"
                                                 cssClass="form-select text-capitalize">
                                        <form:option value="">None</form:option>
                                        <c:forEach items="${assignableUsers}" var="assignableUser">
                                            <form:option value="${assignableUser.idNumber}">
                                                ${assignableUser.firstName} ${assignableUser.middleName} ${assignableUser.otherNames}
                                                <c:if test="${assignableUser.department != null}">
                                                    (${assignableUser.department})
                                                </c:if>
                                            </form:option>
                                        </c:forEach>
                                    </form:select>
                                    <label for="assignedByIdNumber">Assigned By</label>
                                </div>

                                <div class="form-floating mb-3">
                                    <form:textarea id="dispatchNote" path="dispatchNote" placeholder="Dispatch reason"
                                                   cssClass="form-control"/>
                                    <label for="dispatchNote">Assign Remarks/Dispatch Reason</label>
                                </div>

                                <div class="form-floating mb-3">
                                    <form:select id="dispatchedByIdNumber" path="dispatchedByIdNumber"
                                                 cssClass="form-select text-capitalize">
                                        <form:option value="">None</form:option>
                                        <c:forEach items="${assignableUsers}" var="assignableUser">
                                            <form:option value="${assignableUser.idNumber}">
                                                ${assignableUser.firstName} ${assignableUser.middleName} ${assignableUser.otherNames}
                                                <c:if test="${assignableUser.department != null}">
                                                    (${assignableUser.department})
                                                </c:if>
                                            </form:option>
                                        </c:forEach>
                                    </form:select>
                                    <label for="dispatchedByIdNumber">Dispatched By</label>
                                </div>

                                <div class="form-floating mb-3">
                                    <form:input id="dispatchedOn" path="dispatchedOn"
                                                cssClass="form-control datepicker"/>
                                    <label for="dispatchedOn">Dispatched On</label>
                                </div>

                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-sm btn-danger fs-11px" data-bs-dismiss="modal">Cancel
                        </button>
                        <button type="submit" class="btn btn-sm btn-primary fs-11px">Add File Trail</button>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</c:if>


<script>
    let table;
    document.addEventListener('DOMContentLoaded', function () {

        $('.datepicker').datepicker({
            format: 'yyyy-mm-dd 00:00:00',
        });

        table = $('#myTable').DataTable({
            "searchDelay": 1000,
            "processing": true, //Feature control the processing indicator.
            "serverSide": true, //Feature control DataTables' server-side processing mode.
            "lengthMenu": [10, 25, 50, 75, 100, 200, 300, 400, 500], // Change per page values here
            // Load data for the table's content from an Ajax source
            "ajax": {
                "url": "/files/file-trail?lrNo=${fileDto.lrNo}",
                "type": "POST",
                "data": function (d) {
                },
                "dataSrc": function (json) {
                    return json.data;
                }
            },
            "deferRender": 100,
            "language": {
                processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><p class="sr-ofnly">Loading...</p>'
            },
            ordering: false
        });
    }, false);
</script>