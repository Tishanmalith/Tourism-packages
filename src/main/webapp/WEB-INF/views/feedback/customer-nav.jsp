<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<nav class="navbar navbar-expand-lg tp-navbar navbar-dark sticky-top py-3 mb-4">
  <div class="container">
    <a class="navbar-brand d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/bookings/my">
      <i class="bi bi-compass-fill text-warning"></i>
      <span>My Voyages</span>
    </a>
    <button class="navbar-toggler border-0 shadow-sm" type="button" data-bs-toggle="collapse" data-bs-target="#cuNav">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="cuNav">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item"><a class="nav-link px-3" href="${pageContext.request.contextPath}/packages/list"><i class="bi bi-search me-1"></i>Explore packages</a></li>
        <li class="nav-item"><a class="nav-link px-3" href="${pageContext.request.contextPath}/bookings/new"><i class="bi bi-plus-circle me-1"></i>New booking</a></li>
        <li class="nav-item"><a class="nav-link px-3" href="${pageContext.request.contextPath}/bookings/my"><i class="bi bi-ticket-detailed me-1"></i>My trips</a></li>
        <li class="nav-item"><a class="nav-link px-3" href="${pageContext.request.contextPath}/feedback/customer/add"><i class="bi bi-star me-1"></i>Give feedback</a></li>
        <li class="nav-item"><a class="nav-link px-3" href="${pageContext.request.contextPath}/feedback/customer/my"><i class="bi bi-chat-heart me-1"></i>My feedback</a></li>
        <li class="nav-item">
          <a class="nav-link px-3 position-relative" href="${pageContext.request.contextPath}/notifications/my" id="notifBellLink">
            <i class="bi bi-bell me-1"></i>Notifications
            <c:if test="${_navUnread > 0}">
              <span class="position-absolute top-0 translate-middle badge rounded-pill bg-danger"
                    style="font-size:.6rem;min-width:1.2rem;padding:.25rem .4rem;" id="notifBadge">
                ${_navUnread}
              </span>
            </c:if>
          </a>
        </li>
      </ul>
      <div class="d-flex gap-2">
        <a class="btn btn-outline-light btn-sm rounded-pill" href="${pageContext.request.contextPath}/"><i class="bi bi-house me-1"></i>Home</a>
        <a class="btn btn-primary btn-sm rounded-pill px-3" href="${pageContext.request.contextPath}/logout">Sign out</a>
      </div>
    </div>
  </div>
</nav>