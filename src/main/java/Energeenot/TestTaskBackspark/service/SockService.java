package Energeenot.TestTaskBackspark.service;

import Energeenot.TestTaskBackspark.exception.InsufficientSockQuantityException;
import Energeenot.TestTaskBackspark.exception.InvalidCottonPartException;
import Energeenot.TestTaskBackspark.exception.InvalidQuantityException;
import Energeenot.TestTaskBackspark.exception.SockNotFoundException;
import Energeenot.TestTaskBackspark.model.Sock;
import Energeenot.TestTaskBackspark.repository.SockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

@Service
public class SockService {

    private final SockRepository sockRepository;
    private static final Logger logger = LoggerFactory.getLogger(SockService.class);

    @Autowired
    public SockService(SockRepository sockRepository) {
        this.sockRepository = sockRepository;
    }

    @Transactional
    public void registerSocksIncome(String color, int cottonPart, int quantity) {
        logger.info("Registering socks income: color - {}, cottonPart = {}%, quantity = {}", color, cottonPart, quantity);
        if (quantity <= 0) {
            logger.warn("Invalid quantity: quantity = {}", quantity);
            throw new InvalidQuantityException("Quantity must be greater than 0");
        }
        if (cottonPart < 0 || cottonPart > 100) {
            logger.warn("Invalid cottonPart: cottonPart = {}", cottonPart);
            throw new InvalidCottonPartException("CottonPart must be between 0 and 100");
        }

        Sock sock = sockRepository.findByColorAndCottonPart(color, cottonPart)
                .orElse(new Sock(color, cottonPart, 0));
        sock.setQuantity(sock.getQuantity() + quantity);
        logger.info("Socks income registered successfully");
        sockRepository.save(sock);
    }

    @Transactional
    public void registerSocksOutcome(String color, int cottonPart, int quantity) {
        logger.info("Registering socks outcome: color - {}, cottonPart = {}%, quantity = {}", color, cottonPart, quantity);
        Sock sock = sockRepository.findByColorAndCottonPart(color, cottonPart)
                .orElseThrow(() -> new SockNotFoundException("Sock not found"));

        if (sock.getQuantity() < quantity) {
            logger.warn("The quantity they want to pick up is more than available in stock: quantity = {}, quantity in stock = {}", quantity, sock.getQuantity());
            throw new InsufficientSockQuantityException("Not enough socks in stock");
        }

        logger.info("Socks outcome registered successfully");
        sock.setQuantity(sock.getQuantity() - quantity);
        sockRepository.save(sock);
    }

    @Transactional
    public int getSocksCount(String color, String  comparison, Integer cottonPart, Integer maxCottonPart) {
        logger.info("Getting socks count");
        if ((cottonPart != null && (cottonPart < 0 || cottonPart > 100)) ||
                (maxCottonPart != null && (maxCottonPart < 0 || maxCottonPart > 100)) ||
                (cottonPart != null && maxCottonPart != null && cottonPart > maxCottonPart)) {
            logger.warn("Invalid cottonPart or maxCottonPart: cottonPart = {}, maxCottonPart = {}", cottonPart, maxCottonPart);
            throw new InvalidCottonPartException("Invalid cottonPart range. Must be between 0 and 100, and min <= max.");
        }

        if (cottonPart != null && maxCottonPart != null) {
            logger.info("Filtering by cottonPart range: {} - {}", cottonPart, maxCottonPart);
            return color != null
                    ? sockRepository.countByColorAndCottonPartRange(color, cottonPart, maxCottonPart)
                    : sockRepository.countByCottonPartRange(cottonPart, maxCottonPart);
        }
        if (cottonPart != null && comparison != null) {
            logger.info("Comparing cottonPart: {}, comparison: {}", cottonPart, comparison);
            return switch (comparison) {
                case "moreThan" -> color != null
                        ? sockRepository.countByColorAndCottonPartGreaterThan(color, cottonPart)
                        : sockRepository.countByCottonPartGreaterThan(cottonPart);
                case "lessThan" -> color != null
                        ? sockRepository.countByColorAndCottonPartLessThan(color, cottonPart)
                        : sockRepository.countByCottonPartLessThan(cottonPart);
                case "equal" -> color != null
                        ? sockRepository.countByColorAndCottonPart(color, cottonPart)
                        : sockRepository.countByCottonPart(cottonPart);
                default ->
                        throw new IllegalArgumentException("Invalid comparison operator. Use 'moreThan', 'lessThan', or 'equal'");
            };
        }else if (color != null) {
            logger.info("Get socks count: color = {}", color);
            return sockRepository.countByColor(color);
        }else if (cottonPart != null){
            logger.info("Get socks count: cottonPart = {}", cottonPart);
            return sockRepository.countByCottonPart(cottonPart);
        }else {
            logger.warn("At least one filter must be provided");
            throw new IllegalArgumentException("At least one filter must be provided");
        }
    }

    @Transactional
    public void editSock(int id, String color, Integer cottonPart, Integer quantity) {
        logger.info("Editing sock");
        Sock sock = sockRepository.findById(id)
                .orElseThrow(() -> new SockNotFoundException("Sock not found"));
        logger.info("Sock before editing: {}", sock);
        Optional.ofNullable(color).ifPresent(sock::setColor);
        Optional.ofNullable(cottonPart).ifPresent(sock::setCottonPart);
        Optional.ofNullable(quantity).ifPresent(sock::setQuantity);
        logger.info("Sock after editing: {}", sock);
        sockRepository.save(sock);
    }

    @Transactional
    public void saveSocksBatch(MultipartFile file) throws IOException {
        logger.info("Saving socks from CSV file");
        if (file.isEmpty()) {
            logger.warn("File is empty");
            throw new IllegalArgumentException("File is empty");
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while((line = reader.readLine()) != null){
                String[] fields = line.split(",");
                if (fields.length != 3) {
                    logger.warn("Invalid format for line: {}", line);
                    throw new IllegalArgumentException("Invalid CSV format. Each line must contain color, cottonPart, and quantity");
                }

                String color = fields[0].trim();
                int cottonPart = Integer.parseInt(fields[1].trim());
                int quantity = Integer.parseInt(fields[2].trim());

                if (cottonPart < 0 || cottonPart > 100) {
                    throw new InvalidCottonPartException("Invalid cottonPart range: " + cottonPart + " . Must be between 0 and 100");
                }
                if (quantity <= 0) {
                    throw  new InvalidQuantityException("Invalid quantity value: " + quantity);
                }

                Optional<Sock> existingSock = sockRepository.findByColorAndCottonPart(color, cottonPart);
                if (existingSock.isPresent()) {
                    Sock sock = existingSock.get();
                    sock.setQuantity(sock.getQuantity() + quantity);
                    sockRepository.save(sock);
                    logger.info("Updated existing sock: {}", sock);
                } else {
                    Sock newSock = new Sock(color, cottonPart, quantity);
                    sockRepository.save(newSock);
                    logger.info("Created new sock: {}", newSock);
                }
            }
        }
        logger.info("Socks saved");
    }
}
