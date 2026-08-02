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

    @GetMapping
    public String adminDashboard(HttpSession session, Model model) {
        model.addAttribute("adminName", session.getAttribute("studentName"));
        model.addAttribute("allUsers", adminService.getAllUsers());
        model.addAttribute("auditLogs", adminService.getAllAuditLogs());
        return "admin";
    }

    @PostMapping("/faculty/create")
    public String createFaculty(@RequestParam String name,
                                @RequestParam String facultyId,
                                @RequestParam String department,
                                @RequestParam String email,
                                @RequestParam String password) {
        adminService.createFaculty(name, facultyId, department, email, password);
        return "redirect:/admin";
    }

    @PostMapping("/toggle/{userId}")
    public String toggleStatus(@PathVariable Long userId) {
        adminService.toggleAccountStatus(userId);
        return "redirect:/admin";
    }

    @PostMapping("/reset-password/{userId}")
    public String resetPassword(@PathVariable Long userId, @RequestParam String newPassword) {
        adminService.resetPassword(userId, newPassword);
        return "redirect:/admin";
    }
}