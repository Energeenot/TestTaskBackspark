package Energeenot.TestTaskBackspark.controller;

import Energeenot.TestTaskBackspark.service.SockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SockController.class)
class SockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SockService sockService;

    @Test
    void incomeShouldCompletedSuccessfully() throws Exception {
        String color = "red";
        int cottonPart = 50;
        int quantity = 10;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/socks/income")
                        .param("color", color)
                        .param("cottonPart", String.valueOf(cottonPart))
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isOk())
                .andExpect(content().string("Sock income registered successfully"));

        verify(sockService, times(1)).registerSocksIncome(color, cottonPart, quantity);
    }

    @Test
    void outcomeShouldCompletedSuccessfully() throws Exception {
        String color = "blue";
        int cottonPart = 60;
        int quantity = 5;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/socks/outcome")
                        .param("color", color)
                        .param("cottonPart", String.valueOf(cottonPart))
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isOk())
                .andExpect(content().string("Sock outcome registered successfully"));

        verify(sockService, times(1)).registerSocksOutcome(color, cottonPart, quantity);
    }

    @Test
    void getSocksCountShouldCompletedSuccessfully() throws Exception {
        String color = "red";
        int cottonPart = 50;
        int maxCottonPart = 60;
        int expectedCount = 100;
        when(sockService.getSocksCount(color, ">", cottonPart, maxCottonPart)).thenReturn(expectedCount);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/socks")
                        .param("color", color)
                        .param("comparison", ">")
                        .param("cottonPart", String.valueOf(cottonPart))
                        .param("maxCottonPart", String.valueOf(maxCottonPart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedCount));

        verify(sockService, times(1)).getSocksCount(color, ">", cottonPart, maxCottonPart);
    }

    @Test
    void editSocksShouldCompletedSuccessfully() throws Exception {
        int id = 1;
        String color = "green";
        int cottonPart = 80;
        int quantity = 20;
        mockMvc.perform(MockMvcRequestBuilders.put("/api/socks/{id}", id)
                        .param("color", color)
                        .param("cottonPart", String.valueOf(cottonPart))
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isOk())
                .andExpect(content().string("Sock edited successfully"));

        verify(sockService, times(1)).editSock(id, color, cottonPart, quantity);
    }
}
