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
@Table(name = "users_contacts")
public class UserContact {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id",nullable = false,unique = true)
    private Long id;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "second_name",nullable = true)
    private String secondName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(name = "office",nullable = true)
    private String office;

}
