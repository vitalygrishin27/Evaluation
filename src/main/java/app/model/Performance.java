package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collection;
import java.util.Comparator;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "performances")
public class Performance {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "performance_id",nullable = false,unique = true)
    private Long performanceId;

    @Column(name = "performance_name",nullable = false)
    private String performanceName;

    @Column(name = "turn_number")
    private int turnNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "performance",fetch = FetchType.LAZY)
    private Collection<Mark> marks;

    public static final Comparator<Performance> COMPARE_BY_TURN = new Comparator<Performance>() {
        @Override
        public int compare(Performance lhs, Performance rhs) {
            return lhs.getTurnNumber() - rhs.getTurnNumber();
        }
    };

    @Override
    public String toString() {
        return "Performance{" +
                "performanceId=" + performanceId +
                ", performanceName='" + performanceName + '\'' +
                '}';
    }
}
