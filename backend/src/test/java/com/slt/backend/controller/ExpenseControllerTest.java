package com.slt.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.slt.backend.dto.ExpenseDto;
import com.slt.backend.entity.ExpenseCategory;
import com.slt.backend.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private com.slt.backend.security.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldAddExpense() throws Exception {
        ExpenseDto dto = ExpenseDto.builder()
                .title("Lunch")
                .amount(new BigDecimal("15.50"))
                .category(ExpenseCategory.FOOD)
                .transactionDate(LocalDate.now())
                .build();

        ExpenseDto responseDto = ExpenseDto.builder()
                .id(1L)
                .title("Lunch")
                .amount(new BigDecimal("15.50"))
                .category(ExpenseCategory.FOOD)
                .transactionDate(LocalDate.now())
                .build();

        when(expenseService.addExpense(any(ExpenseDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lunch"));
    }

    @Test
    void shouldGetAllExpenses() throws Exception {
        ExpenseDto responseDto = ExpenseDto.builder()
                .id(1L)
                .title("Lunch")
                .amount(new BigDecimal("15.50"))
                .category(ExpenseCategory.FOOD)
                .transactionDate(LocalDate.now())
                .build();

        when(expenseService.getAllExpenses()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Lunch"));
    }

    @Test
    void shouldDeleteExpense() throws Exception {
        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isOk());

        verify(expenseService).deleteExpense(1L);
    }
}
