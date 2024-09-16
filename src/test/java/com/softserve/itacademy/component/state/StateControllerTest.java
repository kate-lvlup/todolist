package com.softserve.itacademy.component.state;

import com.softserve.itacademy.controller.StateController;
import com.softserve.itacademy.dto.StateDto;
import com.softserve.itacademy.service.StateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StateController.class)
class StateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StateService stateService;

    @BeforeEach
    void setUp() {
        given(stateService.findAll()).willReturn(Arrays.asList(
                StateDto.builder().id(1L).name("State1").build(),
                StateDto.builder().id(2L).name("State2").build()
        ));
    }

    @Test
    @DisplayName("Should return list of states with correct view and model attributes")
    void shouldReturnListOfStates() throws Exception {
        mockMvc.perform(get("/states"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-list"))
                .andExpect(model().attributeExists("states"))
                .andExpect(model().attribute("states", Arrays.asList(
                        StateDto.builder().id(1L).name("State1").build(),
                        StateDto.builder().id(2L).name("State2").build()
                )));
    }

    @Test
    @DisplayName("Should return empty list when no states are available")
    void shouldReturnEmptyListWhenNoStates() throws Exception {
        given(stateService.findAll()).willReturn(Arrays.asList());
        mockMvc.perform(get("/states"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-list"))
                .andExpect(model().attributeExists("states"))
                .andExpect(model().attribute("states", Arrays.asList()));
    }

    @Test
    @DisplayName("Should return 404 status for unknown URL")
    void shouldReturn404ForUnknownUrl() throws Exception {
        mockMvc.perform(get("/unknown-url"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should contain state names in the response")
    void shouldContainStateNamesInResponse() throws Exception {
        given(stateService.findAll()).willReturn(Arrays.asList(
                StateDto.builder().id(1L).name("State1").build(),
                StateDto.builder().id(2L).name("State2").build()
        ));

        mockMvc.perform(get("/states"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-list"))
                .andExpect(model().attributeExists("states"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("State1")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("State2")));
    }


    @Test
    @DisplayName("Should call StateService exactly once")
    void shouldCallStateServiceOnce() throws Exception {
        mockMvc.perform(get("/states"))
                .andExpect(status().isOk());

        Mockito.verify(stateService, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Should return list of states for admin")
    void shouldReturnListOfStatesForAdmin() throws Exception {
        mockMvc.perform(get("/states").header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-list"));
    }

    @Test
    @DisplayName("Should return HTML content with correct structure and data")
    void shouldContainHtmlContent() throws Exception {
        given(stateService.findAll()).willReturn(Arrays.asList(
                StateDto.builder().id(1L).name("State1").build(),
                StateDto.builder().id(2L).name("State2").build()
        ));

        mockMvc.perform(get("/states"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-list"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<h1>Manage States</h1>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<a class=\"btn btn-primary\" href=\"/states/add\">Add State</a>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<td>State1</td>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<td>State2</td>")));
    }
}
