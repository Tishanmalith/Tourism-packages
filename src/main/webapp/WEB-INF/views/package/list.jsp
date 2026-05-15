<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="vh" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/fragments/head.jsp">
        <jsp:param name="pageTitle" value="Packages"/>
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

<main class="container-fluid px-lg-4 pb-5">
    <header class="tp-page-header d-flex flex-wrap justify-content-between gap-3 mb-4">
        <div>
            <h1 class="h2 fw-bold mb-1">${isAdmin ? 'Manage packages' : 'Explore packages'}</h1>
            <p class="text-secondary mb-0 small">Browse our curated collection of luxury and adventure getaways.</p>
        </div>
        <c:if test="${isAdmin}">
            <a class="btn btn-primary rounded-pill px-4" href="${pageContext.request.contextPath}/packages/new"><i class="bi bi-plus-lg me-1"></i>New package</a>
        </c:if>
    </header>

    <div class="tp-table-card">
        <div class="table-responsive">
            <table class="table align-middle mb-0 table-hover">
                <thead class="table-light">
                <tr><th>Experience</th><th>Destination</th><th>Days</th><th class="text-end">Price</th><th class="text-end">Actions</th></tr>
                </thead>
                <tbody>
                <c:forEach var="pkg" items="${packages}">
                    <c:set var="destName" value="—"/>
                    <c:forEach var="d" items="${destinations}">
                        <c:if test="${d.id eq pkg.destinationId}"><c:set var="destName" value="${d.name} (${d.country})"/></c:if>
                    </c:forEach>
                    <tr>
                        <td class="fw-semibold">${pkg.name}</td>
                        <td><span class="badge bg-light text-dark border">${destName}</span></td>
                        <td>${pkg.durationDays}</td>
                        <td class="text-end font-monospace"><vh:price-lkr value="${pkg.price}"/></td>
                        <td class="text-end text-nowrap">
                            <a class="btn btn-sm btn-outline-primary rounded-pill" href="${pageContext.request.contextPath}/packages/detail?id=${pkg.id}" title="View"><i class="bi bi-eye"></i> View</a>
                            <c:if test="${isAdmin}">
                                <a class="btn btn-sm btn-outline-secondary rounded-pill" href="${pageContext.request.contextPath}/packages/edit?id=${pkg.id}"><i class="bi bi-pencil"></i></a>
                                <a class="btn btn-sm btn-outline-danger rounded-pill" href="${pageContext.request.contextPath}/packages/delete?id=${pkg.id}" onclick="return confirm('Delete package?');"><i class="bi bi-trash"></i></a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</main>
<jsp:include page="/WEB-INF/views/fragments/footer-scripts.jsp"/>
</body>
</html>