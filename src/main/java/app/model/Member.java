package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id",nullable = false,unique = true)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "second_name")
    private String secondName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "office")
    private String office;

    @Column(name = "boss")
    private String boss;

    //связь с категорией
    @ManyToOne (optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    //связь с performance (номера)
    @OneToMany (mappedBy = "member",fetch = FetchType.EAGER)
    private Collection<Performance> performances;

}
