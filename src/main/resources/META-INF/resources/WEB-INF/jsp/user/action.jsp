<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="btn btn-group" role="group">
    <a type="button" class="btn btn-sm btn-outline-primary fs-11px" href="/users/manage?id=${user.id}">Edit</a>
    <c:if test="${isSu == true}">
        <button type="button" class="btn btn-sm btn-outline-success fs-11px"
                data-bs-toggle="modal" data-bs-target="#reset-link-${user.id}">Reset
        </button>
    </c:if>
    <button type="button" class="btn btn-sm btn-outline-danger fs-11px"
            data-bs-toggle="modal" data-bs-target="#delete-${user.id}">Delete
    </button>
</div>

<!-- Modal -->
<div class="modal fade" id="delete-${user.id}" data-bs-keyboard="false" tabindex="-1"
     aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-12px" id="staticBackdropLabel">Delete Account</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to delete the account belonging to <strong>${fullNames}</strong>!
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary fs-11px" data-bs-dismiss="modal">Ignore</button>
                <a type="button" class="btn bnt-sm btn-danger fs-11px" href="/users/delete?id=${user.id}">Delete</a>
            </div>
        </div>
    </div>
</div>

<c:if test="${isSu == true}">
    <!-- Modal -->
    <div class="modal fade" id="reset-link-${user.id}" data-bs-keyboard="false" tabindex="-1"
         aria-labelledby="resetLinkLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-12px" id="resetLinkLabel">Reset Link</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body text-center">
                    <p>Are you sure you want to generate password reset link for <strong>${fullNames}</strong>!</p>
                    <div id="reset-link-error-${user.id}" class="alert alert-danger" style="display: none;"></div>
                    <div id="reset-link-success-${user.id}" class="alert alert-primary d-none"
                         style="display: none;"></div>
                    <p id="reset-link-url-${user.id}" class="text-primary fs-13px fw-bold" style="display: none;"></p>
                    <div id="reset-link-spinner-${user.id}" class="spinner-border text-success" role="status"
                         style="display: none;">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button"
                            class="btn btn-sm btn-primary fs-11px fw-bold"
                            data-href="/users/reset-link?idNumber=${user.idNumber}"
                        <%--                        data-href="/users/reset-link"--%>
                            idNumber="${user.idNumber}"
                            reset-error-id="reset-link-error-${user.id}"
                            reset-success-id="reset-link-success-${user.id}"
                            reset-url-id="reset-link-url-${user.id}"
                            reset-spinner-id="reset-link-spinner-${user.id}"
                            onclick="generateResetLink(this)">Generate
                    </button>
                    <button type="button" class="btn bnt-sm btn-danger fs-11px" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</c:if>
