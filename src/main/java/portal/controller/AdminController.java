package portal.controller;

import portal.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "ADMIN".equals(role);
    }

    @GetMapping
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("adminName", session.getAttribute("studentName"));
        model.addAttribute("allUsers", adminService.getAllUsers());
        model.addAttribute("auditLogs", adminService.getAllAuditLogs());
        return "admin";
    }

    @PostMapping("/faculty/create")
    public String createFaculty(HttpSession session,
                                @RequestParam String name,
                                @RequestParam String facultyId,
                                @RequestParam String department,
                                @RequestParam String email,
                                @RequestParam String password) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        adminService.createFaculty(name, facultyId, department, email, password);
        return "redirect:/admin";
    }

    @PostMapping("/toggle/{userId}")
    public String toggleStatus(HttpSession session, @PathVariable Long userId) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        adminService.toggleAccountStatus(userId);
        return "redirect:/admin";
    }

    @PostMapping("/reset-password/{userId}")
    public String resetPassword(HttpSession session, @PathVariable Long userId, @RequestParam String newPassword) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        adminService.resetPassword(userId, newPassword);
        return "redirect:/admin";
    }
}