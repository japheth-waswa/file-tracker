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
                <p class="fs-14px fw-medium"><u>System Users</u></p>
                <div class="row">
                    <div class="col-12">
                        <div class="float-end">
                            <a class="btn btn-sm btn-primary" href="/users/manage">Add User</a>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-12 table-responsive">
                        <table id="myTable" class="table">
                            <thead>
                            <tr>
                                <th>ID Number</th>
                                <th>Full Names</th>
                                <th>Email</th>
                                <th>Department</th>
                                <th>Roles</th>
                                <th>Rights</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>
                </div>
                <%--        <p>--%>
                <%--            <button class="btn btn-info" onclick="table.ajax.reload();">Reload</button>--%>
                <%--        </p>--%>
                <%--        <input type="hidden" id="location" value="nairobi & mombasa"/>--%>
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
            // "order": [], //Initial no order.
            "lengthMenu": [10, 25, 50, 75, 100, 200, 300, 400, 500], // Change per page values here
            // Load data for the table's content from an Ajax source
            "ajax": {
                "url": "/users",
                "type": "POST",
                "data": function (d) {
                    // d.location = $("#location").val();
                },
                "dataSrc": function (json) {
                    //todo update html elements here ie $("#location").val(json.location);
                    return json.data;
                }
            },
            "deferRender": 100,
            "language": {
                processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><p class="sr-ofnly">Loading...</p> '
            },
            ordering: false
            // "columnDefs": [
            //     {
            //         "targets": [0], //first column / numbering column
            //         "orderable": false, //set not orderable
            //     },
            // ],
            // "dom": 'Blfrtip',
            // "stateSave": true,
            // "select": true,
            // "buttons": [
            //     'colvis',
            //     {
            //         extend: 'copy',
            //         exportOptions: {
            //             columns: ':visible'
            //         }
            //     },
            //     {
            //         extend: 'excel',
            //         exportOptions: {
            //             columns: ':visible'
            //         }
            //     },
            //     {
            //         extend: 'pdf',
            //         orientation: 'landscape',
            //         pageSize: 'LEGAL',
            //         exportOptions: {
            //             columns: ':visible'
            //         }
            //     },
            //     {
            //         extend: 'csv',
            //         exportOptions: {
            //             columns: ':visible'
            //         }
            //     },
            //     {
            //         extend: 'print',
            //         text: 'Print all',
            //         exportOptions: {
            //             modifier: {
            //                 selected: null
            //             },
            //             columns: ':visible'
            //         }
            //     },
            // ],
        });
    }, false);
</script>
<%@include file="../common/footer.jspf" %>