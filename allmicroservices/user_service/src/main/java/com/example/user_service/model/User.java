package com.example.user_service.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


//@NamedNativeQuery(name = "UserEntity.getUserByIdCustom",
//query = "Select u.user_name as userName, u.email as email from user u where u.user_id = ?1",resultSetMapping = "Mapping.UserEntityDTO")
//@SqlResultSetMapping(name = "Mapping.UserEntityDTO",classes = @ConstructorResult(targetClass = UserEntityDTO.class,columns = {@ColumnResult(name = "userName"),@ColumnResult(name = "email")}))

/**
 * This is an entity for User
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user",
        indexes = @Index(name = "index_fn",columnList = "user_id,user_name,email"))
@NamedEntityGraph(name="userDetail_graph" , attributeNodes = @NamedAttributeNode(value = "userDetails"))
public class User {

  @Id
  @Column(name = "user_id",nullable = false)
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
          name = "UUID",
          strategy = "org.hibernate.id.UUIDGenerator"
  )
  private String userId;

  @Column(name = "user_name",nullable = false)
  private String userName;

  @Column(name = "email",nullable = false)
  private String email;

  @Column(name = "last_login",nullable = false)
  private LocalDateTime lastLogin;

  @Column(name = "created_at",nullable = false)
  private LocalDateTime createdAt;

  @OneToOne(
          cascade = CascadeType.ALL,
          mappedBy = "user",
          fetch = FetchType.LAZY
  )
  private UserDetails userDetails;


  @OneToMany(
          cascade = CascadeType.ALL,
          mappedBy = "user",
          fetch = FetchType.EAGER
  )
  @JsonIgnore
  private List<UserMedicines> userMedicines;


  public <D> User(D map) {
  }
}