
<div class="btn btn-group" role="group">
    <a type="button" class="btn btn-sm btn-outline-primary fs-11px" href="/departments/manage?id=${department.id}">Edit</a>
    <button type="button" class="btn btn-sm btn-outline-danger fs-11px"
            data-bs-toggle="modal" data-bs-target="#delete-${department.id}">Delete</button>
</div>

<!-- Modal -->
<div class="modal fade" id="delete-${department.id}" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-12px" id="staticBackdropLabel">Delete Department</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to delete this department <strong>${department.name}</strong>!
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary fs-11px" data-bs-dismiss="modal">Ignore</button>
                <a type="button" class="btn bnt-sm btn-danger fs-11px" href="/departments/delete?id=${department.id}">Delete</a>
            </div>
        </div>
    </div>
</div>