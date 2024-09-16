package com.softserve.itacademy.controller;

import com.softserve.itacademy.service.StateService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/states")
@RequiredArgsConstructor
public class StateController {

    private final StateService stateService;

    @GetMapping("")
    public String listStates(Model model) {
        log.info("Received request to get all states");
        try {
            model.addAttribute("states", stateService.findAll());
            log.info("Successfully retrieved all states");
        } catch (Exception e) {
            log.error("Error occurred while retrieving states", e);
        }
        return "state/state-list";
    }
}
