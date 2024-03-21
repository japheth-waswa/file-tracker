<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@include file="../common/header.jspf" %>
<style>
    .dt-search {
        display: none !important;
    }
</style>
<div class="row">
    <div class="col-1"></div>
    <div class="col-10">
        <div class="card">
            <div class="card-body">
                <p class="fs-14px fw-medium"><u>Files</u></p>
                <c:if test="${manageAllowed == true}">
                    <div class="row">
                        <div class="col-12">
                            <div class="float-end">
                                <a class="btn btn-sm btn-primary" href="/files/manage">Add New File</a>
                            </div>
                        </div>
                    </div>
                </c:if>
                <div class="row">
                    <div class="col-12 table-responsive">
                        <table id="myTable" class="table">
                            <thead>
                            <tr>
                                <th>LR No</th>
                                <th>IR No</th>
                                <th>CF No</th>
                                <th>Area Size</th>
                                <th>Nature</th>
                                <th>Status</th>
                                <th>Current User</th>
                                <th>Current Department</th>
                                <th>Created By</th>
                                <th>Action</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-1"></div>
</div>
<script>
    let table;
    document.addEventListener('DOMContentLoaded', function () {
        table = $('#myTable').DataTable({
            "searchDelay": 1000,
            "processing": true, //Feature control the processing indicator.
            "serverSide": true, //Feature control DataTables' server-side processing mode.
            "lengthMenu": [10, 25, 50, 75, 100, 200, 300, 400, 500], // Change per page values here
            // Load data for the table's content from an Ajax source
            "ajax": {
                "url": "/files",
                "type": "POST",
                "data": function (d) {
                },
                "dataSrc": function (json) {
                    return json.data;
                }
            },
            "deferRender": 100,
            "language": {
                processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><p class="sr-ofnly">Loading...</p> '
            },
            ordering: false
        });
    }, false);
</script>
<%@include file="../common/footer.jspf" %>