package Energeenot.TestTaskBackspark.service;

import Energeenot.TestTaskBackspark.exception.InsufficientSockQuantityException;
import Energeenot.TestTaskBackspark.exception.InvalidCottonPartException;
import Energeenot.TestTaskBackspark.exception.InvalidQuantityException;
import Energeenot.TestTaskBackspark.exception.SockNotFoundException;
import Energeenot.TestTaskBackspark.model.Sock;
import Energeenot.TestTaskBackspark.repository.SockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SockServiceTest {

    @Mock
    private SockRepository sockRepository;

    @InjectMocks
    private SockService sockService;

    @Test
    void testRegisterSocksIncomeShouldEditQuantityForExistingSock() {
        String color = "red";
        int cottonPart = 50;
        int quantity = 10;
        Sock existingSock = new Sock(color, cottonPart, 20);
        when(sockRepository.findByColorAndCottonPart(color, cottonPart)).thenReturn(Optional.of(existingSock));
        when(sockRepository.save(existingSock)).thenReturn(existingSock);
        sockService.registerSocksIncome(color, cottonPart, quantity);

        verify(sockRepository, times(1)).save(existingSock);
        assertEquals(30, existingSock.getQuantity());
    }

    @Test
    void registerSocksIncomeShouldSaveNewSock() {
        String color = "blue";
        int cottonPart = 100;
        int quantity = 50;
        when(sockRepository.findByColorAndCottonPart(color, cottonPart)).thenReturn(Optional.empty());
        Sock newSock = new Sock(color, cottonPart, quantity);
        when(sockRepository.save(any(Sock.class))).thenReturn(newSock);
        sockService.registerSocksIncome(color, cottonPart, quantity);

        ArgumentCaptor<Sock> sockCaptor = ArgumentCaptor.forClass(Sock.class);
        verify(sockRepository, times(1)).save(sockCaptor.capture());
        Sock savedSock = sockCaptor.getValue();
        assertEquals("blue", savedSock.getColor());
        assertEquals(100, savedSock.getCottonPart());
        assertEquals(50, savedSock.getQuantity());
    }

    @Test
    void registerSocksIncomeShouldThrowInvalidQuantityException() {
        String color = "green";
        int cottonPart = 60;
        int quantity = -5;

        assertThrows(InvalidQuantityException.class, () -> sockService.registerSocksIncome(color, cottonPart, quantity));
    }

    @Test
    void registerSocksIncomeShouldThrowInvalidCottonPartException() {
        String color = "red";
        int cottonPart = 150;
        int quantity = 50;

        assertThrows(InvalidCottonPartException.class, () -> sockService.registerSocksIncome(color, cottonPart, quantity));
    }

    @Test
    void registerSocksOutcomeShouldSuccessfullyRegistered() {
        String color = "red";
        int cottonPart = 50;
        int quantity = 5;
        Sock existingSock = new Sock(color, cottonPart, 10);
        when(sockRepository.findByColorAndCottonPart(color, cottonPart)).thenReturn(Optional.of(existingSock));
        sockService.registerSocksOutcome(color, cottonPart, quantity);

        assertEquals(5, existingSock.getQuantity());
        verify(sockRepository, times(1)).save(existingSock);
    }

    @Test
    void registerSocksOutcomeShouldThrowInsufficientStockException() {
        String color = "red";
        int cottonPart = 50;
        int quantity = 20;
        Sock existingSock = new Sock(color, cottonPart, 10);
        when(sockRepository.findByColorAndCottonPart(color, cottonPart)).thenReturn(Optional.of(existingSock));

        assertThrows(InsufficientSockQuantityException.class, () -> sockService.registerSocksOutcome(color, cottonPart, quantity));
    }

    @Test
    void getSocksCountShouldReturnSocksCount() {
        String color = "red";
        int cottonPart = 50;
        when(sockRepository.countByColorAndCottonPart(color, cottonPart)).thenReturn(10);
        int count = sockService.getSocksCount(color, "equal", cottonPart, null);

        assertEquals(10, count);
    }

    @Test
    void getSocksCountShouldThrowInvalidCottonPartException() {
        assertThrows(InvalidCottonPartException.class, () -> sockService.getSocksCount("red", "moreThan", -5, null));
    }

    @Test
    void getSocksCountShouldReturnCountUsingMaxCottonPartAndColorFilters(){
        String color = "red";
        int cottonPart = 12;
        int maxCottonPart = 50;
        when(sockRepository.countByColorAndCottonPartRange(color, cottonPart, maxCottonPart)).thenReturn(10);
        int count = sockService.getSocksCount(color, "equal", cottonPart, maxCottonPart);

        assertEquals(10, count);
    }

    @Test
    void getSocksCountShouldReturnCountUsingColorFilter(){
        String color = "red";
        when(sockRepository.countByColor(color)).thenReturn(10);
        int count = sockService.getSocksCount(color, null, null, null);

        assertEquals(10, count);
    }

    @Test
    void getSocksCountShouldReturnCountUsingCottonPartFilter(){
        int cottonPart = 50;
        when(sockRepository.countByCottonPart(cottonPart)).thenReturn(10);
        int count = sockService.getSocksCount(null, null, cottonPart, null);

        assertEquals(10, count);
    }

    @Test
    void getSocksCountShouldThrowIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> sockService.getSocksCount(null, null, null, null));
    }

    @Test
    void editSockShouldEditSuccessfully() {
        int sockId = 1;
        Sock existingSock = new Sock("blue", 100, 30);
        when(sockRepository.findById(sockId)).thenReturn(Optional.of(existingSock));
        sockService.editSock(sockId, "green", 80, 40);

        assertEquals("green", existingSock.getColor());
        assertEquals(80, existingSock.getCottonPart());
        assertEquals(40, existingSock.getQuantity());
        verify(sockRepository, times(1)).save(existingSock);
    }

    @Test
    void editSockShouldThrowSockNotFoundException() {
        int sockId = 1;
        when(sockRepository.findById(sockId)).thenReturn(Optional.empty());

        assertThrows(SockNotFoundException.class, () -> sockService.editSock(sockId, "green", 80, 40));
    }

    @Test
    void saveSocksBatchShouldSuccessfulSave() throws IOException {
        String csvContent = "red,50,10\nblue,60,15";
        MockMultipartFile file = new MockMultipartFile("file", "socks.csv", "text/csv", csvContent.getBytes());
        when(sockRepository.findByColorAndCottonPart("red", 50)).thenReturn(Optional.empty());
        when(sockRepository.findByColorAndCottonPart("blue", 60)).thenReturn(Optional.empty());
        sockService.saveSocksBatch(file);
        ArgumentCaptor<Sock> sockCaptor = ArgumentCaptor.forClass(Sock.class);
        verify(sockRepository, times(2)).save(sockCaptor.capture());
        List<Sock> capturedSocks = sockCaptor.getAllValues();

        assertEquals(2, capturedSocks.size());
        assertEquals("red", capturedSocks.get(0).getColor());
        assertEquals(50, capturedSocks.get(0).getCottonPart());
        assertEquals(10, capturedSocks.get(0).getQuantity());
        assertEquals("blue", capturedSocks.get(1).getColor());
        assertEquals(60, capturedSocks.get(1).getCottonPart());
        assertEquals(15, capturedSocks.get(1).getQuantity());
    }

    @Test
    void saveSocksBatchShouldThrowIllegalArgumentException(){
        String csvContent = "red,50";
        MockMultipartFile file = new MockMultipartFile("file", "socks.csv", "text/csv", csvContent.getBytes());

        assertThrows(IllegalArgumentException.class, () -> sockService.saveSocksBatch(file));
    }

}
