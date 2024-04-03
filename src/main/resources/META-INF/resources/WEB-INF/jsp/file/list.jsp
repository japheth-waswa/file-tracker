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
                <div class="row mb-2">
                    <div class="col">
                        <div class="float-start">
                            <div class="row">
                                <div class="col">
                                    <select id="fileSearchFilter" class="form-control">
                                        <c:forEach items="${fileSearchFilters}" var="fileSearchFilter">
                                            <option value="${fileSearchFilter}">${fileSearchFilter}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col">
                                    <input type="text" id="filterSearchValue" class="form-control"
                                           placeholder="Search">
                                    <select id="fileStatus" class="form-control">
                                        <c:forEach items="${fileStatuses}" var="fileStatus">
                                            <option value="${fileStatus}">${fileStatus}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col">
                                    <button class="btn btn-sm btn-primary" onclick="table.ajax.reload();">Search</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col">
                        <div class="float-end">
                            <c:if test="${manageAllowed == true}">
                                <a class="btn btn-sm btn-primary" href="/files/manage">Add New File</a>
                            </c:if>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-12 table-responsive">
                        <table id="myTable" class="table">
                            <thead>
                            <tr>
                                <th>#</th>
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
    let fileSearchFilter = document.getElementById("fileSearchFilter");
    let filterSearchValue = document.getElementById("filterSearchValue");
    let fileStatus = document.getElementById("fileStatus");

    let table;
    document.addEventListener('DOMContentLoaded', function () {
        toggleSearchElems();

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
                    d.fileSearchFilter = fileSearchFilter.value;
                    d.fileStatuses = fileStatus.value;
                    d.filterSearchValue = filterSearchValue.value;
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
    fileSearchFilter.addEventListener("change", function () {
        toggleSearchElems();
    });

    const toggleSearchElems = () => {
        if (fileSearchFilter.value === "LR_NO") {
            fileStatus.style.display = "none";
            filterSearchValue.style.display = "block";
        } else if (fileSearchFilter.value === "FILE_STATUS") {
            filterSearchValue.style.display = "none";
            fileStatus.style.display = "block";
        } else if (fileSearchFilter.value === "ALL_FILES" || fileSearchFilter.value === "MY_FILES") {
            filterSearchValue.style.display = "none";
            fileStatus.style.display = "none";
        }
    };
</script>
<%@include file="../common/footer.jspf" %>