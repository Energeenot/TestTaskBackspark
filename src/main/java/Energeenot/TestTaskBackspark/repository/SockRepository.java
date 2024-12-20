package Energeenot.TestTaskBackspark.repository;

import Energeenot.TestTaskBackspark.model.Sock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SockRepository extends JpaRepository<Sock, Integer> {

    Optional<Sock> findByColorAndCottonPart(String color, int cottonPart);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sock s WHERE s.color = :color AND s.cottonPart > :cottonPart")
    int countByColorAndCottonPartGreaterThan(@Param("color") String color,@Param("cottonPart") Integer cottonPart);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sock s WHERE s.cottonPart > :cottonPart")
    int countByCottonPartGreaterThan(@Param("cottonPart") Integer cottonPart);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sock s WHERE s.color = :color AND s.cottonPart < :cottonPart")
    int countByColorAndCottonPartLessThan(@Param("color") String color,@Param("cottonPart") Integer cottonPart);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sock s WHERE s.cottonPart < :cottonPart")
    int countByCottonPartLessThan(@Param("cottonPart") Integer cottonPart);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sock s WHERE s.color = :color AND s.cottonPart = :cottonPart")
    int countByColorAndCottonPart(@Param("color") String color,@Param("cottonPart") Integer cottonPart);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sock s WHERE s.cottonPart = :cottonPart")
    int countByCottonPart(@Param("cottonPart") Integer cottonPart);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sock s WHERE s.color = :color")
    int countByColor(@Param("color") String color);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sock s WHERE s.color = :color AND s.cottonPart BETWEEN :cottonPart AND :maxCottonPart")
    int countByColorAndCottonPartRange(@Param("color") String color,@Param("cottonPart") Integer cottonPart,@Param("maxCottonPart") Integer maxCottonPart);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sock s WHERE s.cottonPart BETWEEN :cottonPart AND :maxCottonPart") //
    int countByCottonPartRange(Integer cottonPart, Integer maxCottonPart);
}
