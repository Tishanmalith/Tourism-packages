<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="vh" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/fragments/head.jsp">
        <jsp:param name="pageTitle" value="Edit Feedback"/>
    </jsp:include>
</head>
<body class="page-customer pb-5">
<jsp:include page="/WEB-INF/views/fragments/customer-nav.jsp"/>

<div class="container pb-5" style="max-width:560px;">
    <div class="mb-4">
        <span class="badge bg-secondary rounded-pill">Feedback #${feedback.id}</span>
        <h1 class="h3 fw-bold mt-2 mb-1 text-page-title">Edit your review</h1>
        <p class="text-page-sub small mb-0">You can update your star rating and comments below.</p>
    </div>

    <div class="tp-content-card tp-glass-rise tp-animate-fade-up">
        <div class="card-body p-4 p-lg-5">
            <c:if test="${not empty message}">
                <div class="alert alert-warning rounded-3 small mb-4">${message}</div>
            </c:if>

            <form method="post"
                  action="${pageContext.request.contextPath}/feedback/customer/edit"
                  class="needs-validation" novalidate>
                <input type="hidden" name="id" value="${feedback.id}"/>

                <%-- Star rating --%>
                <div class="mb-4 p-4 rounded-4 bg-light">
                    <label class="form-label fw-bold d-block mb-2">Rating</label>
                    <vh:star-rating-input hiddenInputId="editRatingHidden"
                                         initialRating="${feedback.rating}"/>
                    <div class="form-text mt-3 mb-0">Tap a star to change your rating (1–5).</div>
                </div>

                <%-- Comment --%>
                <div class="mb-4">
                    <label class="form-label fw-bold" for="editComment">Comment</label>
                    <textarea class="form-control" id="editComment" name="comment"
                              rows="5" maxlength="900"
                              placeholder="What did you enjoy (or not) about this trip?">${feedback.comment}</textarea>
                    <div class="form-text">Max 900 characters.</div>
                </div>

                <div class="d-grid gap-2">
                    <button class="btn btn-primary btn-lg rounded-3 fw-semibold" type="submit"
                            id="saveEditFeedback">
                        <i class="bi bi-check-lg me-1"></i>Save changes
                    </button>
                    <a class="btn btn-outline-secondary rounded-3"
                       href="${pageContext.request.contextPath}/feedback/customer/my">
                        ← Back to my feedback
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer-scripts.jsp"/>
<jsp:include page="/WEB-INF/views/fragments/star-rating-script.jsp"/>
<jsp:include page="/WEB-INF/views/fragments/form-validation-script.jsp"/>
</body>
</html>
