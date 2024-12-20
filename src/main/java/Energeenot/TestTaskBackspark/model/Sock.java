package Energeenot.TestTaskBackspark.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "socks")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Sock {

    public Sock(String color, int cottonPart, int quantity) {
        this.color = color;
        this.cottonPart = cottonPart;
        this.quantity = quantity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, name = "color", nullable = false)
    private String color;

    @Column(name = "cotton_part", nullable = false)
    private int cottonPart;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}
