<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>System Error - Vehicle Rental System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<c:url value='/assets/css/style.css'/>" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card border-danger">
                    <div class="card-header bg-danger text-white">
                        <h4 class="mb-0">
                            <i class="fas fa-exclamation-triangle"></i>
                            System Error
                        </h4>
                    </div>
                    <div class="card-body">
                        <div class="alert alert-danger" role="alert">
                            <strong>Error:</strong> ${error}
                        </div>
                        
                        <c:if test="${not empty errorType and errorType == 'system'}">
                            <div class="alert alert-warning">
                                <i class="fas fa-info-circle"></i>
                                An unexpected error occurred in the system. Our team has been notified.
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty details}">
                            <div class="alert alert-secondary">
                                <strong>Technical Details:</strong> ${details}
                            </div>
                        </c:if>
                        
                        <div class="row mt-4">
                            <div class="col-md-6">
                                <h6>What you can do:</h6>
                                <ul class="list-unstyled">
                                    <li><i class="fas fa-redo"></i> Try refreshing the page</li>
                                    <li><i class="fas fa-arrow-left"></i> Go back to previous page</li>
                                    <li><i class="fas fa-home"></i> Return to home page</li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <h6>If the problem persists:</h6>
                                <ul class="list-unstyled">
                                    <li><i class="fas fa-phone"></i> Contact support</li>
                                    <li><i class="fas fa-envelope"></i> Email admin</li>
                                    <li><i class="fas fa-clock"></i> Try again later</li>
                                </ul>
                            </div>
                        </div>
                        
                        <div class="d-grid gap-2 d-md-flex justify-content-md-end mt-4">
                            <a href="javascript:history.back()" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Go Back
                            </a>
                            <a href="javascript:location.reload()" class="btn btn-warning">
                                <i class="fas fa-redo"></i> Refresh
                            </a>
                            <a href="<c:url value='/'/>" class="btn btn-primary">
                                <i class="fas fa-home"></i> Home
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://kit.fontawesome.com/your-fontawesome-key.js" crossorigin="anonymous"></script>
</body>
</html>
