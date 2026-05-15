<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/fragments/head.jsp">
        <jsp:param name="pageTitle" value="Notifications"/>
    </jsp:include>
    <style>
        .notif-card {
            border-left: 4px solid #6c757d;
            transition: border-color 0.2s, box-shadow 0.2s;
        }
        .notif-card.unread {
            border-left-color: #0d6efd;
            background: rgba(13,110,253,.04);
        }
        .notif-card:hover {
            box-shadow: 0 4px 18px rgba(0,0,0,.10);
        }
        .notif-icon {
            width: 2.4rem; height: 2.4rem;
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.1rem;
            flex-shrink: 0;
        }
        .notif-icon.completed { background: #d1fae5; color: #065f46; }
        .notif-time { font-size: .78rem; color: #9ca3af; }
    </style>
</head>
<body class="page-customer pb-5">
<jsp:include page="/WEB-INF/views/fragments/customer-nav.jsp"/>

<div class="container pb-5" style="max-width:700px;">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
        <div>
            <h1 class="h3 fw-bold text-page-title mb-1">
                <i class="bi bi-bell-fill me-2 text-primary"></i>Notifications
            </h1>
            <p class="text-page-sub mb-0 small">Stay updated on your trip status changes.</p>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty notifications}">
            <div class="tp-content-card tp-glass-rise tp-animate-fade-up">
                <div class="card-body py-5 text-center">
                    <i class="bi bi-bell-slash display-4 text-muted mb-3 d-block"></i>
                    <p class="text-muted mb-0">You have no notifications yet.</p>
                    <p class="text-muted small">When your trip is marked completed, you'll see it here.</p>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="d-flex flex-column gap-3">
                <c:forEach var="n" items="${notifications}">
                    <div class="card border-0 shadow-sm rounded-4 notif-card ${n.read ? '' : 'unread'} tp-animate-fade-up">
                        <div class="card-body d-flex align-items-start gap-3 p-4">
                            <div class="notif-icon completed">
                                <i class="bi bi-check2-circle"></i>
                            </div>
                            <div class="flex-grow-1">
                                <p class="mb-1 fw-semibold small">${n.message}</p>
                                <span class="notif-time">
                                    <i class="bi bi-clock me-1"></i>${n.createdAt}
                                </span>
                            </div>
                            <c:if test="${not n.read}">
                                <span class="badge rounded-pill bg-primary align-self-center"
                                      style="font-size:.65rem;">New</span>
                            </c:if>
                        </div>
                        <c:if test="${n.bookingId != null}">
                            <div class="card-footer bg-transparent border-0 pt-0 pb-3 px-4">
                                <a class="btn btn-sm btn-outline-success rounded-pill px-3"
                                   href="${pageContext.request.contextPath}/feedback/customer/add?bookingId=${n.bookingId}">
                                    <i class="bi bi-star me-1"></i>Leave feedback
                                </a>
                            </div>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer-scripts.jsp"/>
</body>
</html>
