<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="vh" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/fragments/head.jsp">
        <jsp:param name="pageTitle" value="Package detail"/>
    </jsp:include>
</head>
<c:set var="isAdmin" value="${not empty sessionScope.SESSION_ADMIN}"/>
<c:set var="isStaff" value="${not empty sessionScope.SESSION_STAFF}"/>
<c:set var="isCustomer" value="${not empty sessionScope.SESSION_CUSTOMER}"/>

<body class="${isAdmin ? 'page-admin' : (isStaff ? 'page-staff' : 'page-customer')}">
<c:choose>
    <c:when test="${isAdmin}"><jsp:include page="/WEB-INF/views/fragments/admin-nav.jsp"/></c:when>
    <c:when test="${isStaff}"><jsp:include page="/WEB-INF/views/fragments/staff-nav.jsp"/></c:when>
    <c:otherwise><jsp:include page="/WEB-INF/views/fragments/customer-nav.jsp"/></c:otherwise>
</c:choose>

<main class="container pb-5" style="max-width:800px;">
    <div class="d-flex flex-wrap justify-content-between gap-3 mb-4">
        <h1 class="h2 fw-bold mb-0">${tourPackage.name}</h1>
        <div class="btn-group shadow-sm">
            <c:if test="${isAdmin}">
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/packages/edit?id=${tourPackage.id}"><i class="bi bi-pencil me-1"></i>Edit</a>
                <a class="btn btn-outline-danger" href="${pageContext.request.contextPath}/packages/delete?id=${tourPackage.id}" onclick="return confirm('Delete?');"><i class="bi bi-trash me-1"></i>Delete</a>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/packages/list">Back</a>
            </c:if>
            <c:if test="${isCustomer}">
                <a class="btn btn-primary rounded-pill px-4" href="${pageContext.request.contextPath}/bookings/my">
                    <i class="bi bi-arrow-left me-1"></i>Back to My trips
                </a>
            </c:if>
            <c:if test="${isStaff}">
                <a class="btn btn-primary rounded-pill px-4" href="${pageContext.request.contextPath}/staff/packages">
                    <i class="bi bi-arrow-left me-1"></i>Back to Browse
                </a>
            </c:if>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-md-5">
            <div class="card border-0 shadow-sm rounded-4 h-100 tp-glass-rise">
                <div class="card-body p-4">
                    <h2 class="h6 text-uppercase text-secondary fw-bold mb-3">Snapshot</h2>
                    <ul class="list-unstyled mb-0">
                        <li class="mb-3"><span class="text-secondary small d-block">Price</span><span class="fs-4 fw-bold text-primary font-monospace"><vh:price-lkr value="${tourPackage.price}"/></span></li>
                        <li class="mb-3"><span class="text-secondary small d-block">Duration</span><span class="fw-semibold">${tourPackage.durationDays} days</span></li>
                        <li><span class="text-secondary small d-block">Destination</span>
                            <c:choose>
                                <c:when test="${not empty destination}"><span class="fw-semibold">${destination.name}<br/><span class="text-muted small">${destination.country}</span></span></c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="col-md-7">
            <div class="card border-0 shadow-sm rounded-4 h-100 tp-glass-rise">
                <div class="card-body p-4">
                    <h2 class="h5 fw-bold mb-3"><i class="bi bi-text-paragraph me-2 text-primary"></i>Narrative</h2>
                    <p class="text-secondary lh-lg mb-0">${empty tourPackage.description ? 'No marketing copy uploaded yet.' : tourPackage.description}</p>
                </div>
            </div>
        </div>
    </div>
</main>
<jsp:include page="/WEB-INF/views/fragments/footer-scripts.jsp"/>
</body>
</html>