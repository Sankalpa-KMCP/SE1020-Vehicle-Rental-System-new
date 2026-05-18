package com.sliit.vehiclerentalsystem01;

import com.sliit.vehiclerentalsystem01.user.auth.UserSessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public")
public class PublicPageController {

    @ModelAttribute
    public void navDisplayName(HttpSession session, Model model) {
        if (session != null && Boolean.TRUE.equals(session.getAttribute(UserSessionKeys.CUSTOMER_LOGGED_IN))) {
            Object u = session.getAttribute(UserSessionKeys.USERNAME);
            model.addAttribute("displayName", u != null ? u.toString() : "Customer");
        }
    }

    @GetMapping("/about")
    public String about() {
        return "public/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "public/contact";
    }

    @GetMapping("/terms")
    public String terms() {
        return "public/terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "public/privacy";
    }
}
