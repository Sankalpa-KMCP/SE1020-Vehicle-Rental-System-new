<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.BookingDAO" %>
<%@ page import="com.example.vehiclerentalsystem01.dao.ReviewDAO" %>
<%@ page import="com.example.vehiclerentalsystem01.models.Booking" %>
<%@ page import="java.util.List" %>
<%
    // Ensure the user is actually logged in as a customer
    Boolean customerLoggedIn = (Boolean) session.getAttribute("customerLoggedIn");
    if (customerLoggedIn == null || !customerLoggedIn) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Attempt to grab the full name, fallback to username, then to 'Customer'
    String displayName = (String) session.getAttribute("name");
    if (displayName == null) {
        displayName = (String) session.getAttribute("username");
        if (displayName == null) {
            displayName = "Customer";
        }
    }

    String eligibilityUser = (String) session.getAttribute("username");
    if (eligibilityUser == null || eligibilityUser.trim().isEmpty()) {
        eligibilityUser = displayName;
    }

    BookingDAO bookingDAO = new BookingDAO();
    ReviewDAO reviewDAO = new ReviewDAO();
    boolean reviewEligible = bookingDAO.hasEligibleReviewBooking(eligibilityUser);
    List<Booking> customerBookings = bookingDAO.getAllBookings();
    String reviewError = request.getParameter("error");

    // Filter bookings for this customer that are CONFIRMED or COMPLETED
    List<Booking> eligibleBookings = new java.util.ArrayList<>();
    for (Booking b : customerBookings) {
        if (b.getCustomerName() != null && b.getCustomerName().trim().equalsIgnoreCase(eligibilityUser.trim())
                && ("CONFIRMED".equalsIgnoreCase(b.getStatus()) || "COMPLETED".equalsIgnoreCase(b.getStatus()))) {
            eligibleBookings.add(b);
        }
    }

    // Check account review limit
    int accountReviewCount = reviewDAO.countAccountReviewsByCustomer(eligibilityUser);
    boolean canSubmitAccountReview = accountReviewCount < 1;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DriveAway | Write a Review</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/@phosphor-icons/web"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #F4F7F6; }
        .fade-in { animation: fadeIn 0.4s ease-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }

        /* Star Rating Styles - Reverse direction so hover highlights preceding stars */
        .star-rating { display: flex; flex-direction: row-reverse; justify-content: flex-end; }
        .star-rating input { display: none; }
        .star-rating label { color: #E5E7EB; cursor: pointer; transition: color 0.2s; }
        .star-rating input:checked ~ label { color: #F59E0B; } /* Amber-500 */
        .star-rating label:hover, .star-rating label:hover ~ label { color: #FBBF24; } /* Amber-400 */
    </style>
</head>
<body class="min-h-screen text-gray-800 flex flex-col">

<nav class="bg-white border-b border-gray-200 sticky top-0 z-20 shadow-sm">
    <div class="max-w-screen-xl mx-auto px-8 h-20 flex items-center justify-between">
        <div class="flex items-center gap-2 text-gray-900 cursor-pointer" onclick="window.location.href='customer-home.jsp'">
            <i class="ph-bold ph-car-profile text-3xl text-blue-600"></i>
            <span class="text-2xl font-extrabold tracking-tight">DriveAway</span>
        </div>
        <div class="flex items-center gap-6">
            <a href="customer-home.jsp" class="text-gray-500 hover:text-blue-600 font-semibold transition-colors flex items-center gap-2">
                <i class="ph-bold ph-arrow-left"></i> Back to Dashboard
            </a>
        </div>
    </div>
</nav>

<main class="flex-grow max-w-screen-md mx-auto w-full px-8 py-10 fade-in">
    <div class="bg-white rounded-3xl p-10 sm:p-12 shadow-lg border border-gray-100">
        <% if ("not_eligible".equals(reviewError)) { %>
        <div class="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl font-semibold">
            You can submit a review only after at least one booking is CONFIRMED or COMPLETED.
        </div>
        <% } %>

        <% if ("account_limit".equals(reviewError)) { %>
        <div class="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl font-semibold">
            You have already submitted your account review. You can only submit one account review.
        </div>
        <% } %>

        <% if ("booking_limit".equals(reviewError)) { %>
        <div class="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl font-semibold">
            You have already submitted 2 reviews for this booking. Maximum 2 reviews per booking allowed.
        </div>
        <% } %>

        <% if ("invalid_booking".equals(reviewError)) { %>
        <div class="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl font-semibold">
            Invalid booking selected. Please select a valid booking from your confirmed reservations.
        </div>
        <% } %>

        <% if (!reviewEligible && eligibleBookings.isEmpty()) { %>
        <div class="mb-6 bg-amber-50 border border-amber-200 text-amber-800 px-4 py-3 rounded-xl font-semibold">
            Review locked. You need a confirmed/completed booking before posting feedback.
        </div>
        <% } %>

        <div class="flex items-center gap-5 mb-10">
            <div class="w-16 h-16 rounded-2xl bg-orange-50 text-orange-600 flex items-center justify-center text-3xl shadow-inner border border-orange-100/50">
                <i class="ph-fill ph-star"></i>
            </div>
            <div>
                <h1 class="text-3xl font-extrabold text-gray-900 tracking-tight">Write a Review</h1>
                <p class="text-gray-500 font-medium mt-1">Share your DriveAway experience with us!</p>
            </div>
        </div>

        <form action="reviews" method="post" class="space-y-7">
            <!-- Used generic save action since controller detects empty action as new save -->
            <input type="hidden" name="action" value="save">
            <input type="hidden" name="source" value="customer">

            <div>
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Your Name</label>
                <input type="text" name="reviewerName" value="<%= displayName %>" required placeholder="Enter your full name"
                       class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
            </div>

            <div>
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Review Type</label>
                <select name="bookingId" id="bookingSelect" required onchange="updateReviewTypeHint()"
                        class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 shadow-inner">
                    <option value="">-- Select Review Type --</option>
                    <% if (canSubmitAccountReview) { %>
                    <option value="">Account Review (General Experience)</option>
                    <% } else { %>
                    <option value="" disabled>Account Review (Already Submitted)</option>
                    <% } %>
                    <% for (Booking b : eligibleBookings) { %>
                    <option value="<%= b.getTransactionId() %>">Booking: <%= b.getTransactionId() %> - <%= b.getVehicleId() %></option>
                    <% } %>
                </select>
                <p id="reviewTypeHint" class="text-xs text-gray-500 mt-2 font-medium">Select whether to review a specific booking or your overall account experience.</p>
            </div>

            <div>
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Rating</label>
                <div class="star-rating gap-2">
                    <input type="radio" id="star5" name="rating" value="5" required />
                    <label for="star5" class="text-4xl" title="5 Stars"><i class="ph-fill ph-star"></i></label>
                    <input type="radio" id="star4" name="rating" value="4" />
                    <label for="star4" class="text-4xl" title="4 Stars"><i class="ph-fill ph-star"></i></label>
                    <input type="radio" id="star3" name="rating" value="3" />
                    <label for="star3" class="text-4xl" title="3 Stars"><i class="ph-fill ph-star"></i></label>
                    <input type="radio" id="star2" name="rating" value="2" />
                    <label for="star2" class="text-4xl" title="2 Stars"><i class="ph-fill ph-star"></i></label>
                    <input type="radio" id="star1" name="rating" value="1" />
                    <label for="star1" class="text-4xl" title="1 Star"><i class="ph-fill ph-star"></i></label>
                </div>
            </div>

            <div>
                <label class="block text-sm font-bold text-gray-700 mb-2 uppercase tracking-wide">Your Comments</label>
                <textarea name="comments" rows="5" required placeholder="Tell us what you liked or what we can improve..."
                          class="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all font-medium text-gray-900 resize-none shadow-inner"></textarea>
            </div>

            <div class="pt-6 flex flex-col sm:flex-row justify-between items-center gap-4">
                <a href="customer-home.jsp" class="text-gray-500 hover:text-gray-800 font-bold px-4 py-2 transition-colors order-2 sm:order-1">Cancel</a>
                <button type="submit" <%= (reviewEligible || canSubmitAccountReview) ? "" : "disabled" %> class="w-full sm:w-auto bg-[#0D6EFD] hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed text-white font-bold px-8 py-3.5 rounded-xl shadow-lg shadow-blue-600/20 hover:shadow-blue-600/40 transition-all flex justify-center items-center gap-2 transform hover:-translate-y-0.5 order-1 sm:order-2">
                    Submit Review <i class="ph-bold ph-paper-plane-right text-lg"></i>
                </button>
            </div>
        </form>
    </div>
</main>

<script>
    function updateReviewTypeHint() {
        const select = document.getElementById('bookingSelect');
        const hint = document.getElementById('reviewTypeHint');

        if (select.value === '') {
            hint.textContent = 'Account Review: Share your overall experience with DriveAway (1 per account).';
        } else {
            hint.textContent = 'Booking Review: Rate this specific rental experience (up to 2 per booking).';
        }
    }

    // Initialize hint on page load
    updateReviewTypeHint();
</script>

</body>
</html>