package com.example.user_service.repository;

import com.example.user_service.model.User;
import com.example.user_service.pojos.request.UserDetailEntityDTO;
import com.example.user_service.pojos.request.UserMailDTO;
import com.example.user_service.pojos.request.UserMedicineDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * This is a user repository class
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select u from User u where lower(u.userName) like lower(concat(?1,'%'))")
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,value = "userDetail_graph")
    public List<User> findByNameIgnoreCase(String userName);

    @Query("select u from User u where lower(u.email) like lower(?1)")
    public User findByMail(String email);

    @Query("select new com.example.user_service.pojos.request.UserMailDTO("+
            "u.userName, u.email, d.picPath) "+
            "from User as u  inner join u.userDetails as d where lower(u.email) like lower(?1)")
    public UserMailDTO findByMail1(String email);

    @Query("SELECT u from User u where u.userId = ?1")
    public User getUserById(String userId);

    @Query("select new com.example.user_service.pojos.request.UserMedicineDTO("+
            "m.medicineName, m.title, m.medicineDes, m.startDate,m.endDate, m.time, m.days) " +
            "from User as u inner join u.userMedicines as m where u.userId = ?1 ")
    public List<UserMedicineDTO> getUserMedicineById(String userId);

    @Query("select new com.example.user_service.pojos.request.UserDetailEntityDTO("+
            "u.userName, u.email, d.bio, d.age, d.userContact, d.gender, d.bloodGroup,d.maritalStatus," +
            "d.weight ) " +
            "from User as u inner join u.userDetails as d")
//    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,value = "userDetail_graph")
    Page<UserDetailEntityDTO> findAllUsers(Pageable pageable);


    @Query("select new com.example.user_service.pojos.request.UserDetailEntityDTO("+
            "u.userName, u.email, d.bio, d.age, d.userContact, d.gender, d.bloodGroup,d.maritalStatus," +
            "d.weight ) " +
            "from User as u inner join u.userDetails as d where u.userId = ?1 ")
    public UserDetailEntityDTO getUserById1(String  userId);

}