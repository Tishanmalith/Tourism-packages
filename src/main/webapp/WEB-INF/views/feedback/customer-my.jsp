<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="vh" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/fragments/head.jsp">
        <jsp:param name="pageTitle" value="My Feedback"/>
    </jsp:include>
    <style>
        .feedback-card { border-left: 4px solid #0d6efd; transition: box-shadow .2s; }
        .feedback-card:hover { box-shadow: 0 6px 24px rgba(0,0,0,.10); }
        .star-display { color: #f59e0b; font-size: 1.1rem; letter-spacing: .1rem; }
        .pkg-badge { font-size: .72rem; padding: .3em .75em; }
    </style>
</head>
<body class="page-customer pb-5">
<jsp:include page="/WEB-INF/views/fragments/customer-nav.jsp"/>

<div class="container pb-5" style="max-width:760px;">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
        <div>
            <h1 class="h3 fw-bold text-page-title mb-1">
                <i class="bi bi-chat-heart-fill me-2 text-primary"></i>My Feedback
            </h1>
            <p class="text-page-sub mb-0 small">Manage the reviews you've submitted for your trips.</p>
        </div>
        <a class="btn btn-primary rounded-pill px-4 fw-semibold"
           href="${pageContext.request.contextPath}/feedback/customer/add">
            <i class="bi bi-plus-lg me-1"></i>New review
        </a>
    </div>

    <c:choose>
        <c:when test="${empty feedbackList}">
            <div class="tp-content-card tp-glass-rise tp-animate-fade-up">
                <div class="card-body py-5 text-center">
                    <i class="bi bi-chat-square-dots display-4 text-muted mb-3 d-block"></i>
                    <p class="text-muted mb-1 fw-semibold">No feedback yet.</p>
                    <p class="text-muted small mb-3">Complete a trip and share your experience!</p>
                    <a class="btn btn-primary rounded-pill px-4"
                       href="${pageContext.request.contextPath}/feedback/customer/add">
                        <i class="bi bi-star me-1"></i>Give feedback
                    </a>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="d-flex flex-column gap-3">
                <c:forEach var="f" items="${feedbackList}">
                    <%-- Resolve package name --%>
                    <c:set var="pkgName" value="Package removed"/>
                    <c:forEach var="p" items="${packages}">
                        <c:if test="${p.id eq f.packageId}">
                            <c:set var="pkgName" value="${p.name}"/>
                        </c:if>
                    </c:forEach>

                    <div class="card border-0 shadow-sm rounded-4 feedback-card tp-animate-fade-up">
                        <div class="card-body p-4">
                            <div class="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-2">
                                <div>
                                    <span class="badge text-bg-light border pkg-badge rounded-pill mb-1">
                                        <i class="bi bi-geo-alt me-1"></i>${pkgName}
                                    </span>
                                    <div class="star-display mt-1">
                                        <c:forEach begin="1" end="5" var="s">
                                            <c:choose>
                                                <c:when test="${s <= f.rating}">&#9733;</c:when>
                                                <c:otherwise><span class="text-muted">&#9733;</span></c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                        <span class="text-muted small ms-1">(${f.rating}/5)</span>
                                    </div>
                                </div>
                                <span class="text-muted small">
                                    <i class="bi bi-calendar3 me-1"></i>${f.createdAt}
                                </span>
                            </div>

                            <c:if test="${not empty f.comment}">
                                <p class="mb-3 text-secondary small fst-italic">"${f.comment}"</p>
                            </c:if>
                            <c:if test="${empty f.comment}">
                                <p class="mb-3 text-muted small fst-italic">No comment left.</p>
                            </c:if>

                            <div class="d-flex gap-2 flex-wrap">
                                <a class="btn btn-sm btn-outline-primary rounded-pill px-3"
                                   href="${pageContext.request.contextPath}/feedback/customer/edit?id=${f.id}"
                                   id="editFeedback${f.id}">
                                    <i class="bi bi-pencil me-1"></i>Edit
                                </a>
                                <form method="post"
                                      action="${pageContext.request.contextPath}/feedback/customer/delete"
                                      id="delForm${f.id}"
                                      onsubmit="return confirm('Delete this review? This cannot be undone.');">
                                    <input type="hidden" name="id" value="${f.id}"/>
                                    <button type="submit" class="btn btn-sm btn-outline-danger rounded-pill px-3"
                                            id="deleteFeedback${f.id}">
                                        <i class="bi bi-trash me-1"></i>Delete
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer-scripts.jsp"/>
</body>
</html>
