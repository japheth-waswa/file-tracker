<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="btn btn-group" role="group">
    <a type="button" class="btn btn-sm btn-outline-success fs-11px" href="/files/view?id=${file.id}">View</a>
    <c:if test="${manageAllowed == true}">
        <a type="button" class="btn btn-sm btn-outline-primary fs-11px" href="/files/manage?id=${file.id}">Edit</a>
    </c:if>
    <c:if test="${deleteAllowed == true}">
        <button type="button" class="btn btn-sm btn-outline-danger fs-11px"
                data-bs-toggle="modal" data-bs-target="#delete-${file.id}">Delete
        </button>
    </c:if>
</div>

<!-- Modal -->
<div class="modal fade" id="delete-${file.id}" data-bs-keyboard="false" tabindex="-1"
     aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-12px" id="staticBackdropLabel">Delete File</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to delete this file <strong>${file.lrNo}</strong>!
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary fs-11px" data-bs-dismiss="modal">Ignore</button>
                <c:if test="${deleteAllowed == true}">
                    <a type="button" class="btn bnt-sm btn-danger fs-11px" href="/files/delete?id=${file.id}">Delete</a>
                </c:if>
            </div>
        </div>
    </div>
</div>