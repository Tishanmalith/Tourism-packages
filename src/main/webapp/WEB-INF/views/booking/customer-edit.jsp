<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="vh" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/fragments/head.jsp">
        <jsp:param name="pageTitle" value="Edit booking"/>
    </jsp:include>
</head>
<body class="page-customer pb-5">
<jsp:include page="/WEB-INF/views/fragments/customer-nav.jsp"/>

<div class="container pb-5" style="max-width:560px;">
    <div class="mb-4">
        <span class="badge bg-secondary rounded-pill">Booking #${booking.id}</span>
        <h1 class="h3 fw-bold mt-2 mb-1 text-page-title">Edit your booking</h1>
        <p class="text-page-sub small mb-0">
            You can change your package, trip date, or notes while the booking is still
            <strong>Pending</strong> review.
        </p>
    </div>

    <div class="tp-content-card tp-glass-rise tp-animate-fade-up">
        <div class="card-body p-4 p-lg-5">
            <c:if test="${not empty message}">
                <div class="alert alert-warning rounded-3 d-flex gap-2 mb-4">
                    <i class="bi bi-exclamation-triangle mt-1"></i>${message}
                </div>
            </c:if>

            <form class="needs-validation" method="post"
                  action="${pageContext.request.contextPath}/bookings/customer/update" novalidate>
                <input type="hidden" name="id" value="${booking.id}"/>

                <%-- Package --%>
                <div class="mb-4">
                    <label class="form-label fw-bold">Package</label>
                    <select class="form-select form-select-lg" name="packageId"
                            id="editBookingPackage" required>
                        <option value="" disabled>Select an experience…</option>
                        <c:forEach var="p" items="${packages}">
                            <option value="${p.id}"
                                    ${booking.packageId eq p.id ? 'selected' : ''}>
                                ${p.name} — <vh:price-lkr value="${p.price}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <div class="invalid-feedback">Choose a package.</div>
                </div>

                <%-- Trip start date --%>
                <div class="mb-4">
                    <label class="form-label fw-bold" for="editTripDate">Trip start date</label>
                    <input class="form-control form-control-lg" type="date"
                           name="tripDate" id="editTripDate" required
                           min="${minTripDate}" max="${maxTripDate}"
                           value="${booking.bookingDate}"/>
                    <div class="form-text mb-1">Must be today or later (within two years).</div>
                    <div class="invalid-feedback">Choose when your trip should start.</div>
                </div>

                <%-- Notes --%>
                <div class="mb-4">
                    <label class="form-label fw-bold">Notes for planners</label>
                    <textarea class="form-control" name="notes" rows="4"
                              maxlength="500"
                              placeholder="Diet preferences, arrivals, birthdays…"><c:out value="${booking.notes}"/></textarea>
                </div>

                <div class="d-grid gap-2">
                    <button class="btn btn-primary btn-lg rounded-3 fw-bold" type="submit"
                            id="saveBookingEdit">
                        <i class="bi bi-check-lg me-2"></i>Save changes
                    </button>
                    <a class="btn btn-outline-secondary rounded-3"
                       href="${pageContext.request.contextPath}/bookings/my">
                        ← Back to my trips
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer-scripts.jsp"/>
<jsp:include page="/WEB-INF/views/fragments/form-validation-script.jsp"/>
</body>
</html>
