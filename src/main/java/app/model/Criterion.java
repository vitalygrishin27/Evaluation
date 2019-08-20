package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "criterions")
public class Criterion {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id",nullable = false,unique = true)
    private Long id;

    @Column(name = "name",unique = true,nullable = false)
    private String name;

    @OneToMany(mappedBy = "criterion",fetch = FetchType.LAZY)
    private Collection<Mark> marks;



}
