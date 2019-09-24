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
@Table(name = "configuration")
public class Configuration {

    @Id
    @Column(name = "id_configuration",nullable = false,unique = true)
    private Long id;

    @Column(name = "contest_name",nullable = false)
    private String contestName;
}
