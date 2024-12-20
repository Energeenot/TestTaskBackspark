package Energeenot.TestTaskBackspark.repository;

import Energeenot.TestTaskBackspark.model.Sock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// ./gradlew test

@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
class SockRepositoryTest {

    @Autowired
    private SockRepository sockRepository;

    @BeforeEach
    void setUp() {
        sockRepository.save(new Sock("red", 50, 10));
        sockRepository.save(new Sock("red", 70, 20));
        sockRepository.save(new Sock("blue", 50, 15));
        sockRepository.save(new Sock("blue", 80, 25));
    }

    @AfterEach
    void tearDown() {
        sockRepository.deleteAll();
    }

    @Test
    void findByColorAndCottonPartShouldReturnCorrectQuantity() {
        Optional<Sock> sock = sockRepository.findByColorAndCottonPart("red", 50);
        assertThat(sock).isPresent();
        assertThat(sock.get().getQuantity()).isEqualTo(10);
    }

    @Test
    void countByColorAndCottonPartGreaterThanShouldReturnCorrectQuantity() {
        int count = sockRepository.countByColorAndCottonPartGreaterThan("red", 60);
        assertThat(count).isEqualTo(20);
    }

    @Test
    void countByCottonPartGreaterThanShouldReturnCorrectQuantity() {
        int count = sockRepository.countByCottonPartGreaterThan(60);
        assertThat(count).isEqualTo(45); // 20 + 25
    }

    @Test
    void countByColorAndCottonPartLessThanShouldReturnCorrectQuantity() {
        int count = sockRepository.countByColorAndCottonPartLessThan("blue", 60);
        assertThat(count).isEqualTo(15);
    }

    @Test
    void countByCottonPartLessThanShouldReturnCorrectQuantity() {
        int count = sockRepository.countByCottonPartLessThan(60);
        assertThat(count).isEqualTo(25);
    }

    @Test
    void countByColorAndCottonPartShouldReturnCorrectQuantity() {
        int count = sockRepository.countByColorAndCottonPart("blue", 80);
        assertThat(count).isEqualTo(25);
    }

    @Test
    void countByCottonPartShouldReturnCorrectQuantity() {
        int count = sockRepository.countByCottonPart(50);
        assertThat(count).isEqualTo(25);
    }

    @Test
    void countByColorShouldReturnCorrectQuantity() {
        int count = sockRepository.countByColor("red");
        assertThat(count).isEqualTo(30);
    }

    @Test
    void countByColorAndCottonPartRangeShouldReturnCorrectQuantity() {
        int count = sockRepository.countByColorAndCottonPartRange("red", 40, 70);
        assertThat(count).isEqualTo(30);
    }

    @Test
    void countByCottonPartRangeShouldReturnCorrectQuantity() {
        int count = sockRepository.countByCottonPartRange(40, 70);
        assertThat(count).isEqualTo(45);
    }
}
