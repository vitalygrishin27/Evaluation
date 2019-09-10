package app.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "marks")
public class Mark {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id",nullable = false,unique = true)
    private Long id;

    @Column(name = "value")
    private int value;

    //связь с членом жюри User
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    @JoinColumn(name ="user_id")
    private User user;

    // связь с Performance
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    @JoinColumn(name ="performance_id")
    private Performance performance;


    //Связь с критерией
    @ManyToOne(optional = false,cascade = CascadeType.ALL)
    @JoinColumn(name ="criterion_id")
    private Criterion criterion;

    @Override
    public String toString() {
        return "Mark{" +
                "id=" + id +
                ", value=" + value +
                ", user=" + user +
                '}';
    }
}
